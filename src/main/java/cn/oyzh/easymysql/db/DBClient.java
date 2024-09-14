package cn.oyzh.easymysql.db;

import cn.hutool.log.StaticLog;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.db.function.MysqlFunction;
import cn.oyzh.easymysql.db.procedure.MysqlProcedure;
import cn.oyzh.easymysql.db.query.MysqlExecuteResult;
import cn.oyzh.easymysql.db.query.MysqlExplainResult;
import cn.oyzh.easymysql.db.query.MysqlQueryResults;
import cn.oyzh.easymysql.db.record.MysqlDeleteRecordParam;
import cn.oyzh.easymysql.db.record.MysqlInsertRecordParam;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.record.MysqlRecordData;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.db.record.MysqlRecordPrimaryKey;
import cn.oyzh.easymysql.db.record.MysqlSelectRecordParam;
import cn.oyzh.easymysql.db.record.MysqlUpdateRecordParam;
import cn.oyzh.easymysql.db.table.MysqlChecks;
import cn.oyzh.easymysql.db.table.MysqlForeignKey;
import cn.oyzh.easymysql.db.table.MysqlIndex;
import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.db.table.MysqlTrigger;
import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.easymysql.exception.DBException;
import cn.oyzh.easymysql.exception.ReadonlyOperationException;
import cn.oyzh.easymysql.sql.DBSqlParser;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.common.ssh.SSHForwardInfo;
import cn.oyzh.fx.common.ssh.SSHForwarder;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2023/11/06
 */
@Accessors(fluent = true, chain = true)
public abstract class DBClient {

    /**
     * db信息
     */
    @Getter
    protected final MysqlInfo dbInfo;

    /**
     * ssh端口转发器
     */
    private SSHForwarder sshForwarder;

    /**
     * 数据库连接管理器
     */
    protected final DBConnectionManager connectionManager = new DBConnectionManager();

    protected final DBConnConfig connConfig = new DBConnConfig();

    public Connection connection() throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getServerConnection();
        if (connection == null || connection.isClosed()) {
            connection = this.initConnection(this.connConfig, null, this.dbInfo.getUser(), this.dbInfo.getPassword());
            this.connectionManager.setServerConnection(connection);
        }
        return connection;
    }

    public Connection connection(String dbName) throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getConnection(dbName);
        if (connection == null || connection.isClosed()) {
            connection = this.initConnection(this.connConfig, dbName, this.dbInfo.getUser(), this.dbInfo.getPassword());
            this.connectionManager.addConnection(dbName, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection connection(String dbName, String schema) throws SQLException, ClassNotFoundException {
        if (schema == null) {
            return this.connection(dbName);
        }
        Connection connection = this.connectionManager.getSchemaConnection(dbName, schema);
        if (connection == null || connection.isClosed()) {
            connection = this.initConnection(this.connConfig, dbName, this.dbInfo.getUser(), this.dbInfo.getPassword());
            this.connectionManager.addSchemaConnection(dbName, schema, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection functionConnection(String dbName, String schema) throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getFunctionConnection(dbName, schema);
        if (connection == null || connection.isClosed()) {
            connection = this.initConnection(this.connConfig, dbName, this.dbInfo.getUser(), this.dbInfo.getPassword());
            this.connectionManager.addFunctionConnection(dbName, schema, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection procedureConnection(String dbName, String schema) throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getProcedureConnection(dbName, schema);
        if (connection == null || connection.isClosed()) {
            connection = this.initConnection(this.connConfig, dbName, this.dbInfo.getUser(), this.dbInfo.getPassword());
            this.connectionManager.addProcedureConnection(dbName, schema, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection newConnection(String dbName) throws SQLException, ClassNotFoundException {
        Connection connection = this.initConnection(this.connConfig, dbName, this.dbInfo.getUser(), this.dbInfo.getPassword());
        connection.setAutoCommit(true);
        return connection;
    }

    /**
     * 连接状态
     */
    private final ReadOnlyObjectWrapper<DBConnState> state = new ReadOnlyObjectWrapper<>();

    /**
     * 属性列表
     */
    private Map<String, Object> properties;

    /**
     * 获取属性
     *
     * @param key 键
     * @param <T> 属性类型
     * @return 属性
     */
    protected <T> T getProperty(String key) {
        return this.properties == null || key == null ? null : (T) this.properties.get(key);
    }

    /**
     * 是否有此属性
     *
     * @param key 键
     * @return 结果
     */
    protected boolean hasProperty(String key) {
        return this.properties != null && this.properties.containsKey(key);
    }

    /**
     * 添加属性
     *
     * @param key   键
     * @param value 值
     */
    protected void putProperty(String key, Object value) {
        if (key != null && value != null) {
            if (this.properties == null) {
                this.properties = new HashMap<>();
            }
            this.properties.put(key, value);
        }
    }

    /**
     * 获取连接状态
     *
     * @return 连接状态
     */
    public DBConnState state() {
        return this.stateProperty().get();
    }

    /**
     * 连接状态属性
     *
     * @return 连接状态属性
     */
    public ReadOnlyObjectProperty<DBConnState> stateProperty() {
        return this.state.getReadOnlyProperty();
    }

    public DBClient(@NonNull MysqlInfo dbInfo) {
        this.dbInfo = dbInfo;
        if (dbInfo.isSSHForward()) {
            this.sshForwarder = new SSHForwarder(dbInfo.getSshInfo());
        }
        this.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case CLOSED -> DBEventUtil.connectionClosed(this);
                    case CONNECTED -> DBEventUtil.connectionConnected(this);
                }
            }
        });
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return this.dbInfo.isReadonly();
    }

    /**
     * 如果只读模式不支持操作，则抛出异常
     */
    public void throwReadonlyException() {
        if (this.isReadonly()) {
            throw new ReadonlyOperationException();
        }
    }

    /**
     * 连接数据库
     */
    public void start() {
        if (this.isConnected() || this.isConnecting()) {
            return;
        }
        // 初始化客户端
        this.initClient();
        try {
            // 开始连接时间
            final AtomicLong starTime = new AtomicLong();
            // 开始连接时间
            starTime.set(System.currentTimeMillis());
            // 更新连接状态
            this.state.set(DBConnState.CONNECTING);
            // 连接成功前阻塞线程
            if (this.connection().isValid(this.dbInfo.getConnectTimeOut())) {
                // 更新连接状态
                this.state.set(DBConnState.CONNECTED);
            } else {// 连接未成功则关闭
                this.close();
                if (this.state.get() == DBConnState.FAILED) {
                    this.state.set(null);
                } else {
                    this.state.set(DBConnState.FAILED);
                }
            }
        } catch (Exception ex) {
            this.state.set(DBConnState.FAILED);
            StaticLog.warn("dbClient start error", ex);
            throw new DBException(ex);
        }
    }

    /**
     * 初始化客户端
     */
    protected void initClient() {
        // 连接地址
        String ip;
        int port;
        // ssh端口转发
        if (this.dbInfo.isSSHForward()) {
            SSHForwardInfo forwardInfo = new SSHForwardInfo();
            forwardInfo.setHost(this.dbInfo.hostIp());
            forwardInfo.setPort(this.dbInfo.hostPort());
            // 连接信息
            ip = "127.0.0.1";
            port = this.sshForwarder.forward(forwardInfo);
        } else {// 直连
            // 连接信息
            ip = this.dbInfo.hostIp();
            port = this.dbInfo.hostPort();
        }
        this.connConfig.setHost(ip);
        this.connConfig.setPort(port);
    }

    protected abstract Connection initConnection(DBConnConfig connConfig, String dbName, String user, String password) throws ClassNotFoundException, SQLException;

    /**
     * 关闭连接
     */
    public void close() {
        try {
            this.connectionManager.destroy();
            // 销毁端口转发
            if (this.dbInfo.isSSHForward()) {
                this.sshForwarder.destroy();
            }
            StaticLog.info("dbClient closed.");
            this.state.set(DBConnState.CLOSED);
        } catch (Exception ex) {
            StaticLog.warn("dbClient close error.", ex);
        }
    }

    /**
     * db是否已连接
     *
     * @return 结果
     */
    public boolean isClosed() {
        return this.state() == DBConnState.CLOSED;
    }

    /**
     * db是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        DBConnState state = this.state.get();
        return state != null && state.isConnected();
    }

    /**
     * db是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.state() == DBConnState.CONNECTING;
    }


    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return this.dbInfo.getName();
    }


    /**
     * 添加状态监听器
     *
     * @param stateListener 状态监听器
     */
    public void addStateListener(ChangeListener<DBConnState> stateListener) {
        if (stateListener != null) {
            this.state.addListener(stateListener);
        }
    }

    public abstract List<String> engines();

    public abstract List<DBDatabase> databases();

    /**
     * 获取表数量
     *
     * @param dbName 库名称或者模式名称
     * @return 表数量
     */
    public int tableSize(String dbName, String schema) {
        try {
            int size = 0;
            Connection connection = this.connection(dbName, schema);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(dbName, schema, "%", TABLE_TYPES);
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                if (DBUtil.checkTableType(resultSet, dbName)) {
                    size++;
                }
            }
            DBUtil.close(resultSet);
            return size;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<MysqlTable> tables(String dbName) {
        return this.tables(dbName, null, false);
    }

    public List<MysqlTable> tables(String dbName, boolean full) {
        return this.tables(dbName, null, full);
    }

    public List<MysqlTable> tables(String dbName, String schema) {
        return this.tables(dbName, schema, false);
    }

    public abstract List<MysqlTable> tables(String dbName, String schema, boolean full);

    public MysqlTable table(String dbName, String tableName) {
        return this.table(dbName, tableName, false);
    }

    public abstract MysqlTable table(String dbName, String tableName, boolean full);

    /**
     * 获取视图数量
     *
     * @param dbName 库名称或者模式名称
     * @return 视图数量
     */
    public int viewSize(String dbName, String schema) {
        try {
            int size = 0;
            Connection connection = this.connection(dbName, schema);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(dbName, schema, null, VIEW_TYPES);
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                if (DBUtil.checkViewType(resultSet, dbName)) {
                    size++;
                }
            }
            DBUtil.close(resultSet);
            return size;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public abstract MysqlView view(String dbName, String viewName);

    public abstract List<MysqlView> views(String dbName);

    public abstract void dropView(String dbName, MysqlView view);

    public abstract boolean existView(String dbName, String viewName);

    public abstract void createView(String dbName, MysqlView view);

    public abstract void alertView(String dbName, MysqlView view);

    public abstract Long tableAutoIncrement(String dbName, String tableName);

    public long tableCount(String dbName, String tableName) {
        return this.tableCount(dbName, tableName, null);
    }

    public abstract long tableCount(String dbName, String tableName, List<MysqlRecordFilter> filters);

    public abstract List<MysqlIndex> indexes(String dbName, String tableName);

    public abstract MysqlChecks checks(String dbName, String tableName);

    public abstract List<MysqlForeignKey> foreignKeys(String dbName, String tableName);

    public abstract MysqlColumns tableColumns(String dbName, String schema, String tableName);

    public List<MysqlRecord> selectTableRecords(String dbName, String tableName, Long start, Long limit) {
        return this.selectTableRecords(dbName, tableName, start, limit, null, null, false);
    }

    public List<MysqlRecord> selectTableRecords(String dbName, String tableName, Long start, Long limit, List<MysqlRecordFilter> filters) {
        return this.selectTableRecords(dbName, tableName, start, limit, null, filters, false);
    }

    public abstract List<MysqlRecord> selectTableRecords(String dbName, String tableName, Long start, Long limit, MysqlColumns columns, List<MysqlRecordFilter> filters, boolean readonly);

    public abstract List<MysqlColumn> viewColumns(String dbName, String viewName);

    public abstract List<MysqlRecord> viewRecords(String dbName, String viewName, Long start, Long limit, List<MysqlRecordFilter> filters);

    public int insertRecord(String dbName, String tableName, MysqlRecordData recordData) {
        return this.insertRecord(dbName, tableName, recordData, null);
    }

    public abstract int insertRecord(String dbName, String tableName, MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey);

    public abstract int deleteRecord(String dbName, String tableName, MysqlRecordData recordData);

    public abstract int deleteRecord(String dbName, String tableName, MysqlRecordPrimaryKey primaryKey);

    public abstract int updateRecord(String dbName, String tableName, MysqlRecordData recordData, MysqlRecordData originalRecordData);

    public abstract int updateRecord(String dbName, String tableName, MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey);

    public abstract void createTable(String dbName, MysqlTable table);

    public abstract void alterTable(String dbName, MysqlTable table);

    public abstract boolean existTable(String dbName, String tableName);

    public abstract boolean renameTable(String dbName, String oldTableName, String newTableName);

    public abstract boolean clearTable(String dbName, String tableName);

    public abstract void truncateTable(String dbName, String schema, String tableName);

    public abstract boolean dropTable(String dbName, String tableName);

    public abstract List<String> charsets();

    public abstract List<String> collation(String charset);

    public abstract String columnCollation(String dbName, String tableName, String columnName);

    public abstract boolean existDatabase(String dbName);

    public abstract boolean createDatabase(DBDatabase database);

    public abstract boolean alterDatabase(DBDatabase database);

    public abstract String databaseCollation(String dbName);

    public abstract boolean dropDatabase(String dbName);

    public abstract boolean isUpdateOnCurrentTimestamp(String dbName, String tableName, String colName);

    public MysqlQueryResults<MysqlExecuteResult> executeSql(String dbName, String sql) {
        MysqlQueryResults<MysqlExecuteResult> results = new MysqlQueryResults<>();
        Connection connection = null;
        try {
            DBUtil.printSql(sql);
            DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
            List<String> list = parser.parseSql();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String execSql : list) {
                MysqlExecuteResult result = new MysqlExecuteResult();
                result.sql(execSql);
                try {
                    long startTime = System.nanoTime();
                    boolean isQuery = statement.execute(execSql);
                    if (isQuery) {
                        ResultSet resultSet = statement.getResultSet();
                        if (parser.isSingle() && parser.isSelect()) {
                            result.parseResult(resultSet, connection, false);
                            result.fullColumn(parser.isFullColumn());
                        } else {
                            result.parseResult(resultSet, connection);
                        }
                        DBUtil.close(resultSet);
                        result.success(true);
                    } else {
                        int updateCount = statement.getUpdateCount();
                        result.updateCount(updateCount);
                        result.success(true);
                    }
                    long endTime = System.nanoTime();
                    result.used(endTime - startTime);
                } catch (SQLException ex) {
                    result.msg(ex.toString());
                }
                results.addResult(result);
            }
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            results.parseError(ex);
        }
        return results;
    }

    public abstract MysqlQueryResults<MysqlExplainResult> explainSql(String dbName, String sql);

    public abstract MysqlExecuteResult executeSingleSql(String dbName, String sql);

    public abstract void executeSqlSimple(String dbName, String sql);

    public int insertBatch(String dbName, List<String> sqlList) {
        return this.insertBatch(dbName, sqlList, false);
    }

    public abstract int insertBatch(String dbName, List<String> sqlList, boolean parallel);

    public abstract DBDialect dialect();

    public abstract List<MysqlProcedure> procedures(String dbName);

    public abstract MysqlProcedure selectProcedure(String dbName, String produceName);

    public int procedureSize(String dbName, String schema) {
        int size = 0;
        try {
            Connection connection = this.procedureConnection(dbName, schema);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getProcedures(dbName, schema, "%");
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                if (DBUtil.checkProcedureType(resultSet, dbName)) {
                    size++;
                }
            }
            DBUtil.close(resultSet);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return size;
    }

    public abstract void dropProcedure(String dbName, MysqlProcedure procedure);

    public abstract void createProcedure(String dbName, MysqlProcedure procedure);

    public abstract void alertProcedure(String dbName, MysqlProcedure procedure);

    public abstract void createFunction(String dbName, MysqlFunction function);

    public abstract void dropFunction(String dbName, MysqlFunction function);

    public abstract List<MysqlFunction> functions(String dbName);

    public int functionSize(String dbName, String schema) {
        int size = 0;
        try {
            Connection connection = this.functionConnection(dbName, schema);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getFunctions(dbName, schema, "%");
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                if (DBUtil.checkFunctionType(resultSet, dbName)) {
                    size++;
                }
            }
            DBUtil.close(resultSet);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return size;
    }

    public abstract MysqlFunction selectFunction(String dbName, String functionName);

    public abstract void alertFunction(String dbName, MysqlFunction function);

    public abstract List<MysqlTrigger> triggers(String dbName);

    public abstract List<MysqlTrigger> triggers(String dbName, String tableName);

    public abstract MysqlRecord selectRecord(String dbName, String tableName, MysqlRecordPrimaryKey primaryKey);

    public abstract String selectVersion();

    public abstract String selectClientCharacter();

    public abstract void dropEvent(String dbName, MysqlEvent event);

    public abstract void createEvent(String dbName, MysqlEvent event);

    public abstract void alertEvent(String dbName, MysqlEvent event);

    public abstract MysqlEvent selectEvent(String dbName, String eventName);

    public abstract Integer eventSize(String dbName);

    public abstract List<MysqlEvent> events(String dbName);

    public abstract boolean isSupportFeature(DBFeature feature);

    public boolean isSupportCheckFeature() {
        return this.isSupportFeature(DBFeature.CHECK);
    }

    public boolean isSupportEventFeature() {
        return this.isSupportFeature(DBFeature.EVENT);
    }

    public static final String[] TABLE_TYPES = new String[]{"TABLE", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};

    public static final String[] VIEW_TYPES = new String[]{"VIEW"};

    public List<MysqlTable> selectTables(String dbName, String schema) {
        try {
            Connection connection = this.connection(dbName, schema);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(dbName, schema, "%", TABLE_TYPES);
            List<MysqlTable> tables = new ArrayList<>();
            while (resultSet.next()) {
                if (DBUtil.checkTableType(resultSet, dbName)) {
                    MysqlTable table = new MysqlTable();
                    table.setDbName(dbName);
                    table.setSchema(schema);
                    String tableName = resultSet.getString("TABLE_NAME");
                    String remarks = resultSet.getString("REMARKS");
                    table.setName(tableName);
                    table.setComment(remarks);
                    tables.add(table);
                }
            }
            DBUtil.close(resultSet);
            return tables;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlColumns selectColumns(MysqlSelectColumnParam param) {
        try {
            String dbName = param.dbName();
            String schema = param.schema();
            String tableName = param.tableName();
            Connection connection = this.connection(dbName);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(dbName, schema, tableName, null);
            DBUtil.printMetaData(resultSet);
            MysqlColumns columns = new MysqlColumns();
            while (resultSet.next()) {
                String remarks = resultSet.getString("REMARKS");
                String typeName = resultSet.getString("TYPE_NAME");
                String columnDef = resultSet.getString("COLUMN_DEF");
                Integer columnSize = resultSet.getInt("COLUMN_SIZE");
                String columnName = resultSet.getString("COLUMN_NAME");
                String isNullable = resultSet.getString("IS_NULLABLE");
                Integer decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
                Integer ordinalPosition = resultSet.getInt("ORDINAL_POSITION");
                String isAutoincrement = resultSet.getString("IS_AUTOINCREMENT");
                MysqlColumn column = new MysqlColumn();
                column.setDbName(dbName);
                column.setSchema(schema);
                column.setType(typeName);
                column.setSize(columnSize);
                column.setName(columnName);
                column.setComment(remarks);
                column.setTableName(tableName);
                column.setDigits(decimalDigits);
                column.setDefaultValue(columnDef);
                column.setPosition(ordinalPosition);
                column.setNullable("YES".equalsIgnoreCase(isNullable));
                column.setAutoIncrement("YES".equalsIgnoreCase(isAutoincrement));
                columns.add(column);
            }
            DBUtil.close(resultSet);
            return columns;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public abstract List<MysqlRecord> selectTableRecords(MysqlSelectRecordParam param);

    public abstract long selectTableCount(MysqlSelectRecordParam param);

    public abstract int insertRecord(MysqlInsertRecordParam param);

    public abstract int deleteRecord(MysqlDeleteRecordParam param);

    public abstract int updateRecord(MysqlUpdateRecordParam param);

}

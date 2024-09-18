package cn.oyzh.easymysql.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easymysql.condition.MysqlConditionUtil;
import cn.oyzh.easymysql.db.check.MysqlCheck;
import cn.oyzh.easymysql.db.check.MysqlChecks;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKey;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.db.function.MysqlFunction;
import cn.oyzh.easymysql.db.index.MysqlIndex;
import cn.oyzh.easymysql.db.index.MysqlIndexes;
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
import cn.oyzh.easymysql.db.routine.MysqlRoutineParam;
import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.db.table.MysqlTableAlertParam;
import cn.oyzh.easymysql.db.table.MysqlTableCreateParam;
import cn.oyzh.easymysql.db.table.MysqlTableSelectParam;
import cn.oyzh.easymysql.db.trigger.MysqlTrigger;
import cn.oyzh.easymysql.db.trigger.MysqlTriggers;
import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.easymysql.exception.DBException;
import cn.oyzh.easymysql.exception.ReadonlyOperationException;
import cn.oyzh.easymysql.generator.event.EventAlertSqlGenerator;
import cn.oyzh.easymysql.generator.event.EventCreateSqlGenerator;
import cn.oyzh.easymysql.generator.routine.DBFunctionSqlGenerator;
import cn.oyzh.easymysql.generator.routine.DBProcedureSqlGenerator;
import cn.oyzh.easymysql.generator.table.MysqlTableAlertSqlGenerator;
import cn.oyzh.easymysql.generator.table.MysqlTableCreateSqlGenerator;
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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
public class DBClient {

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

    protected Connection initConnection(DBConnConfig connConfig, String dbName, String user, String password) throws ClassNotFoundException, SQLException {
        // 加载JDBC驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        String host = connConfig.getConnectionString(this.dialect());
        if (dbName != null) {
            host += dbName;
        }
        // 创建数据库连接
        return DriverManager.getConnection(host, user, password);
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

    /**
     * 获取表数量
     *
     * @param dbName 库名称或者模式名称
     * @return 表数量
     */
    public int tableSize(String dbName) {
        try {
            int size = 0;
            Connection connection = this.connection(dbName);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, dbName, "%", TABLE_TYPES);
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

    /**
     * 获取视图数量
     *
     * @param dbName 库名称或者模式名称
     * @return 视图数量
     */
    public int viewSize(String dbName) {
        try {
            int size = 0;
            Connection connection = this.connection(dbName);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, dbName, "%", VIEW_TYPES);
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

    public int insertBatch(String dbName, List<String> sqlList) {
        return this.insertBatch(dbName, sqlList, false);
    }

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

    public void alertFunction(String dbName, MysqlFunction function) {
        try {
            String sql = "DROP FUNCTION IF EXISTS " + DBUtil.wrap(dbName, function.getName(), this.dialect());
            DBUtil.printSql(sql);
            Connection connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            sql = DBFunctionSqlGenerator.INSTANCE.generate(function);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<MysqlTrigger> triggers(String dbName) {
        try {
            String sql = """
                        SELECT 
                            TRIGGER_NAME,ACTION_STATEMENT,ACTION_TIMING,EVENT_MANIPULATION,EVENT_OBJECT_TABLE
                        FROM 
                            INFORMATION_SCHEMA.TRIGGERS 
                        WHERE 
                            TRIGGER_SCHEMA = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            List<MysqlTrigger> list = new ArrayList<>();
            while (resultSet.next()) {
                MysqlTrigger trigger = new MysqlTrigger();
                String name = resultSet.getString("TRIGGER_NAME");
                String timing = resultSet.getString("ACTION_TIMING");
                String tableName = resultSet.getString("EVENT_OBJECT_TABLE");
                String manipulation = resultSet.getString("EVENT_MANIPULATION");
                String actionStatement = resultSet.getString("ACTION_STATEMENT");
                trigger.setName(name);
                trigger.setTableName(tableName);
                trigger.setDefinition(actionStatement);
                trigger.setPolicy(timing, manipulation);
                list.add(trigger);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public MysqlTriggers triggers(String dbName, String tableName) {
        try {
            String sql = """
                        SELECT 
                            TRIGGER_NAME,ACTION_STATEMENT,ACTION_TIMING,EVENT_MANIPULATION
                        FROM 
                            INFORMATION_SCHEMA.TRIGGERS
                        WHERE 
                            TRIGGER_SCHEMA = ?
                        AND 
                            EVENT_OBJECT_TABLE = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            MysqlTriggers list = new MysqlTriggers();
            while (resultSet.next()) {
                MysqlTrigger trigger = new MysqlTrigger();
                String name = resultSet.getString("TRIGGER_NAME");
                String timing = resultSet.getString("ACTION_TIMING");
                String manipulation = resultSet.getString("EVENT_MANIPULATION");
                String actionStatement = resultSet.getString("ACTION_STATEMENT");
                trigger.setName(name);
                trigger.setDefinition(actionStatement);
                trigger.setPolicy(timing, manipulation);
                list.add(trigger);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String selectVersion() {
        if (this.hasProperty("version")) {
            return this.getProperty("version");
        }
        String version = "";
        try {
            Connection conn = this.connection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT VERSION()");
            if (resultSet.next()) {
                version = resultSet.getString(1);
            }
            this.putProperty("version", version);
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public void dropEvent(String dbName, MysqlEvent event) {
        try {
            String sql = "DROP EVENT " + DBUtil.wrap(event.getDbName(), event.getName());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void createEvent(String dbName, MysqlEvent event) {
        try {
            String sql = EventCreateSqlGenerator.generate(this.dialect(), event);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void alertEvent(String dbName, MysqlEvent event) {
        try {
            String sql = EventAlertSqlGenerator.generate(this.dialect(), event);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlEvent selectEvent(String dbName, String eventName) {
        MysqlEvent event = new MysqlEvent();
        try {
            String sql = "SELECT * FROM `INFORMATION_SCHEMA`.`EVENTS` WHERE `EVENT_SCHEMA` = ? AND `EVENT_NAME` = ?";
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, eventName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                Date ends = resultSet.getDate("ENDS");
                Date starts = resultSet.getDate("STARTS");
                String status = resultSet.getString("STATUS");
                String type = resultSet.getString("EVENT_TYPE");
                Date executeAt = resultSet.getDate("EXECUTE_AT");
                String comment = resultSet.getString("EVENT_COMMENT");
                int intervalValue = resultSet.getInt("INTERVAL_VALUE");
                String onCompletion = resultSet.getString("ON_COMPLETION");
                String definition = resultSet.getString("EVENT_DEFINITION");
                String intervalField = resultSet.getString("INTERVAL_FIELD");
                String createDefinition = this.showCreateEvent(dbName, eventName);
                event.setName(eventName);
                event.setType(type);
                event.setEnds(ends);
                event.setStarts(starts);
                event.setDbName(dbName);
                event.setStatus(status);
                event.setComment(comment);
                event.setDefinition(definition);
                event.setOnCompletion(status);
                event.setExecuteAt(executeAt);
                event.setOnCompletion(onCompletion);
                event.setIntervalValue(intervalValue);
                event.setIntervalField(intervalField);
                event.setCreateDefinition(createDefinition);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return event;
    }

    public Integer eventSize(String dbName) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM `INFORMATION_SCHEMA`.`EVENTS` WHERE `EVENT_SCHEMA` = ?";
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return count;
    }

    public List<MysqlEvent> events(String dbName) {
        List<MysqlEvent> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM `INFORMATION_SCHEMA`.`EVENTS` WHERE `EVENT_SCHEMA` = ?";
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                MysqlEvent event = new MysqlEvent();
                Date ends = resultSet.getDate("ENDS");
                Date starts = resultSet.getDate("STARTS");
                String status = resultSet.getString("STATUS");
                String name = resultSet.getString("EVENT_NAME");
                String type = resultSet.getString("EVENT_TYPE");
                Date executeAt = resultSet.getDate("EXECUTE_AT");
                String comment = resultSet.getString("EVENT_COMMENT");
                int intervalValue = resultSet.getInt("INTERVAL_VALUE");
                String onCompletion = resultSet.getString("ON_COMPLETION");
                String definition = resultSet.getString("EVENT_DEFINITION");
                String intervalField = resultSet.getString("INTERVAL_FIELD");
                String createDefinition = this.showCreateEvent(dbName, name);
                event.setName(name);
                event.setType(type);
                event.setEnds(ends);
                event.setStarts(starts);
                event.setDbName(dbName);
                event.setStatus(status);
                event.setComment(comment);
                event.setDefinition(definition);
                event.setOnCompletion(status);
                event.setExecuteAt(executeAt);
                event.setOnCompletion(onCompletion);
                event.setIntervalValue(intervalValue);
                event.setIntervalField(intervalField);
                event.setCreateDefinition(createDefinition);
                list.add(event);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return list;
    }

    public boolean isSupportFeature(DBFeature feature) {
        try {
            if (feature == DBFeature.EVENT) {
                return true;
            }
            // 检查约束
            if (feature == DBFeature.CHECK) {
                // 最低支持版本8.0.16
                String version = this.selectVersion();
                String[] arr = version.split("\\.");
                if (Integer.parseInt(arr[0]) < 8) {
                    return false;
                }
                return Integer.parseInt(arr[2]) >= 16;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isSupportCheckFeature() {
        return this.isSupportFeature(DBFeature.CHECK);
    }

    public boolean isSupportEventFeature() {
        return this.isSupportFeature(DBFeature.EVENT);
    }

    public static final String[] TABLE_TYPES = new String[]{"TABLE", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};

    public static final String[] VIEW_TYPES = new String[]{"VIEW"};

    public List<MysqlTable> selectTables(String dbName) {
        MysqlTableSelectParam param = new MysqlTableSelectParam();
        param.dbName(dbName);
        return this.selectTables(param);
    }

    public List<MysqlTable> selectTables(MysqlTableSelectParam param) {
        try {
            String dbName = param.dbName();
            List<MysqlTable> tables = new ArrayList<>();
            Connection connection = this.connection(dbName);
            if (param.full()) {
                String sql = "SELECT `AUTO_INCREMENT`, `ROW_FORMAT`, `TABLE_COLLATION`, `TABLE_NAME`, `TABLE_COMMENT`, `ENGINE` FROM information_schema.TABLES WHERE `TABLE_SCHEMA` = ? AND `TABLE_TYPE` != 'VIEW'";
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, dbName);
                ResultSet resultSet = statement.executeQuery();
                DBUtil.printMetaData(resultSet);
                while (resultSet.next()) {
                    MysqlTable table = new MysqlTable();
                    String tableEngine = resultSet.getString("ENGINE");
                    String tableName = resultSet.getString("TABLE_NAME");
                    String rowFormat = resultSet.getString("ROW_FORMAT");
                    Long autoIncrement = resultSet.getLong("AUTO_INCREMENT");
                    String tableComment = resultSet.getString("TABLE_COMMENT");
                    String tableCollation = resultSet.getString("TABLE_COLLATION");
                    String showCreateTable = this.showCreateTable(dbName, tableName);
                    table.setDbName(dbName);
                    table.setName(tableName);
                    table.setEngine(tableEngine);
                    table.setRowFormat(rowFormat);
                    table.setComment(tableComment);
                    table.setAutoIncrement(autoIncrement);
                    table.setCreateDefinition(showCreateTable);
                    table.setCharsetAndCollation(tableCollation);
                    tables.add(table);
                }
                DBUtil.close(resultSet);
                DBUtil.close(statement);
            } else {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet resultSet = metaData.getTables(null, null, "%", TABLE_TYPES);
                while (resultSet.next()) {
                    if (DBUtil.checkTableType(resultSet, dbName)) {
                        MysqlTable table = new MysqlTable();
                        table.setDbName(dbName);
                        String remarks = resultSet.getString("REMARKS");
                        String tableName = resultSet.getString("TABLE_NAME");
                        table.setName(tableName);
                        table.setComment(remarks);
                        tables.add(table);
                    }
                }
                DBUtil.close(resultSet);
            }


            return tables;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlColumns selectColumns(MysqlSelectColumnParam param) {
        try {
            String dbName = param.dbName();
            String tableName = param.tableName();
            MysqlColumns columns = new MysqlColumns();
            String sql = "SHOW FULL COLUMNS FROM " + DBUtil.wrap(dbName, tableName, this.dialect());
            PreparedStatement statement = this.connection().prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            int position = 0;
            while (resultSet.next()) {
                String key = resultSet.getString("Key");
                String type = resultSet.getString("Type");
                String field = resultSet.getString("Field");
                Object def = resultSet.getObject("Default");
                String extra = resultSet.getString("Extra");
                String nullable = resultSet.getString("Null");
                String comment = resultSet.getString("Comment");
                String collation = resultSet.getString("COLLATION");

                MysqlColumn column = new MysqlColumn();
                column.parseKey(key);
                column.parseType(type);
                column.parseExtra(extra);
                column.parseCollation(collation);

                column.setName(field);
                column.setDbName(dbName);
                column.setComment(comment);
                column.setDefaultValue(def);
                column.setPosition(position++);
                column.setTableName(tableName);
                column.setNullable("yes".equalsIgnoreCase(nullable));
                columns.add(column);
            }
            DBUtil.close(resultSet);
            // 返回排序后的数据
            return new MysqlColumns(columns.sortOfPosition());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<MysqlRecord> selectRecords(MysqlSelectRecordParam param) {
        try {
            Connection connection = this.connection(param.dbName(), param.schema());
            StringBuilder builder = new StringBuilder("SELECT * FROM ");
            builder.append(DBUtil.wrap(param.dbName(), param.tableName(), this.dialect()));
            String filterCondition = MysqlConditionUtil.buildCondition(param.filters());
            if (StrUtil.isNotBlank(filterCondition)) {
                builder.append(" WHERE ").append(filterCondition);
            }
            if (param.hasPageControl()) {
                builder.append(" LIMIT ")
                        .append(param.start())
                        .append(",")
                        .append(param.limit());
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            DBUtil.printMetaData(resultSet);
            List<MysqlRecord> records = new ArrayList<>();
            List<MysqlColumn> columns;
            if (param.columns() != null) {
                columns = param.columns();
            } else {
                columns = DBHelper.parseColumns(resultSet);
            }
            while (resultSet.next()) {
                MysqlRecord record = new MysqlRecord(param.readonly());
                for (MysqlColumn column : columns) {
                    Object data = resultSet.getObject(column.getName());
                    // 获取几何值
                    if (column.supportGeometry()) {
                        data = DBHelper.getGeometryString(connection, data);
                    }
                    record.putValue(column, data);
                }
                records.add(record);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return records;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public long selectRecordCount(MysqlSelectRecordParam param) {
        long count = 0;
        try {
            Connection connection = this.connection(param.dbName(), param.schema());
            StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM");
            builder.append(DBUtil.wrap(param.dbName(), param.tableName(), this.dialect()));
            String filterCondition = MysqlConditionUtil.buildCondition(param.filters());
            if (StrUtil.isNotBlank(filterCondition)) {
                builder.append(" WHERE ").append(filterCondition);
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return count;
    }

    public int insertRecord(MysqlInsertRecordParam param) {
        if (param == null || param.record() == null) {
            return 0;
        }
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ")
                    .append(DBUtil.wrap(param.dbName(), param.tableName(), this.dialect()))
                    .append("(");
            for (String column : param.record().columns()) {
                builder.append(DBUtil.wrap(column, this.dialect())).append(",");
            }
            builder.append(")");
            builder.append(" VALUES(");
            for (String column : param.record().columns()) {
                if (param.record().isTypeGeometry(column)) {
                    builder.append("ST_GeomFromText(?),");
                } else {
                    builder.append("?,");
                }
            }
            builder.append(")");
            String sql = builder.toString();
            sql = sql.replaceAll(",\\)", ")");
            DBUtil.printSql(sql);
            Connection connection = this.connection(param.dbName(), param.schema());
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            for (String colName : param.record().columns()) {
                DBUtil.setVal(statement, param.record().value(colName), index++);
            }
            int count = statement.executeUpdate();
            MysqlRecordPrimaryKey primaryKey = param.primaryKey();
            // 处理自动递增值
            if (primaryKey != null && primaryKey.shouldReturnData()) {
                primaryKey.setReturnData(DBHelper.lastInsertId(connection));
            }
            DBUtil.close(statement);
            return count;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public int deleteRecord(MysqlDeleteRecordParam param) {
        try {
            int updateCount;
            String dbName = param.dbName();
            String schema = param.schema();
            String tableName = param.tableName();
            Connection connection = this.connection(dbName, schema);
            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM ")
                    .append(DBUtil.wrap(schema, tableName, this.dialect()))
                    .append(" WHERE ");
            if (param.primaryKey() == null) {
                MysqlRecordData recordData = param.record();
                boolean first = true;
                for (String colName : recordData.columns()) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(" AND ");
                    }
                    builder.append(DBUtil.wrap(colName, this.dialect()))
                            .append(" = ?");
                }
                String sql = builder.toString();
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                int index = 1;
                // 设置参数
                for (String colName : param.record().columns()) {
                    DBUtil.setVal(statement, param.record().value(colName), index++);
                }
                updateCount = DBUtil.executeUpdate(statement);
            } else {
                MysqlRecordPrimaryKey primaryKey = param.primaryKey();
                builder.append(DBUtil.wrap(primaryKey.getColumnName(), this.dialect()))
                        .append(" = ?");
                String sql = builder.toString();
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                DBUtil.setVal(statement, primaryKey.originalData(), 1);
                updateCount = DBUtil.executeUpdate(statement);
                DBUtil.close(statement);
            }
            return updateCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public int updateRecord(MysqlUpdateRecordParam param) {
        try {
            int updateCount;
            String dbName = param.dbName();
            String schema = param.schema();
            String tableName = param.tableName();
            MysqlRecordData recordData = param.updateRecord();
            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE ")
                    .append(DBUtil.wrap(schema, tableName, this.dialect()))
                    .append(" SET ");
            for (String column : recordData.columns()) {
                if (recordData.isTypeGeometry(column)) {
                    builder.append(DBUtil.wrap(column, this.dialect())).append(" = ST_GeomFromText(?),");
                } else {
                    builder.append(DBUtil.wrap(column, this.dialect())).append(" = ?,");
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(" WHERE ");
            Connection connection = this.connection(dbName, schema);
            if (param.primaryKey() == null) {
                MysqlRecordData originalRecordData = param.record();
                // 参数
                boolean first = true;
                for (String column : originalRecordData.columns()) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(" AND ");
                    }
                    builder.append(DBUtil.wrap(column, this.dialect())).append(" = ?");
                }
                int index = 1;
                String sql = builder.toString();
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                // 设置值
                for (String colName : recordData.columns()) {
                    DBUtil.setVal(statement, recordData.value(colName), index++);
                }
                // 设置参数
                for (String colName : originalRecordData.columns()) {
                    DBUtil.setVal(statement, originalRecordData.value(colName), index++);
                }
                updateCount = DBUtil.executeUpdate(statement);
                DBUtil.close(statement);
            } else {
                MysqlRecordPrimaryKey primaryKey = param.primaryKey();
                builder.append(" WHERE ").append(DBUtil.wrap(primaryKey.getColumnName(), this.dialect())).append(" = ?");
                String sql = builder.toString();
                DBUtil.printInfo(sql, recordData);
                PreparedStatement statement = connection.prepareStatement(sql);
                int index = 1;
                // 设置值
                for (String colName : recordData.columns()) {
                    DBUtil.setVal(statement, recordData.value(colName), index++);
                }
                // 设置参数
                DBUtil.setVal(statement, primaryKey.originalData(), index);
                updateCount = DBUtil.executeUpdate(statement);
                DBUtil.close(statement);
            }
            return updateCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateTable(String dbName, String tableName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SHOW CREATE TABLE " + DBUtil.wrap(tableName);
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString(2);
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateView(String dbName, String viewName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SHOW CREATE VIEW " + DBUtil.wrap(viewName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString("Create View");
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateFunction(String dbName, String functionName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SHOW CREATE FUNCTION " + DBUtil.wrap(functionName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString("Create Function");
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateProcedure(String dbName, String procedureName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SHOW CREATE PROCEDURE " + DBUtil.wrap(procedureName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString("Create Procedure");
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateTrigger(String dbName, String triggerName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SHOW CREATE TRIGGER " + DBUtil.wrap(triggerName);
            Statement statement = connection.createStatement();
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery(sql);
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString("Sql Original Statement");
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateEvent(String dbName, String eventName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SHOW CREATE EVENT " + DBUtil.wrap(eventName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString("Create Event");
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<String> engines() {
        if (this.hasProperty("engines")) {
            return this.getProperty("engines");
        }
        try {
            List<String> engines = new ArrayList<>();
            String sql = "SELECT ENGINE FROM information_schema.ENGINES WHERE SUPPORT = 'YES' OR SUPPORT = 'DEFAULT'";
            Statement statement = this.connection().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                engines.add(resultSet.getString(1));
            }
            DBUtil.close(statement);
            this.putProperty("engines", engines);
            return engines;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public List<DBDatabase> databases() {
        try {
            Statement statement = this.connection().createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW DATABASES");
            List<DBDatabase> list = new ArrayList<>();
            while (resultSet.next()) {
                DBDatabase databases = new DBDatabase();
                String dbName = resultSet.getString(1);
                databases.setName(dbName);
                databases.setCharsetAndCollation(this.databaseCollation(dbName));
                list.add(databases);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlTable selectTable(String dbName, String tableName) {
        MysqlTableSelectParam param = new MysqlTableSelectParam();
        param.dbName(dbName);
        param.tableName(tableName);
        return this.selectTable(param);
    }

    public MysqlTable selectTable(MysqlTableSelectParam param) {
        try {
            String dbName = param.dbName();
            String tableName = param.tableName();
            MysqlTable table = new MysqlTable();
            table.setDbName(dbName);
            table.setName(tableName);
            Connection connection = this.connection(dbName);
            if (param.full()) {
                String sql = "SELECT `AUTO_INCREMENT`, `ROW_FORMAT`, `TABLE_COLLATION`, `TABLE_COMMENT`, `ENGINE` FROM information_schema.TABLES WHERE `TABLE_SCHEMA` = ? AND `TABLE_NAME` = ?  AND `TABLE_TYPE` = 'BASE TABLE'";
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, dbName);
                statement.setString(2, tableName);
                ResultSet resultSet = statement.executeQuery();
                DBUtil.printMetaData(resultSet);
                String showCreateTable = this.showCreateTable(dbName, tableName);
                while (resultSet.next()) {
                    String tableEngine = resultSet.getString("ENGINE");
                    String rowFormat = resultSet.getString("ROW_FORMAT");
                    Long autoIncrement = resultSet.getLong("AUTO_INCREMENT");
                    String tableComment = resultSet.getString("TABLE_COMMENT");
                    String tableCollation = resultSet.getString("TABLE_COLLATION");
                    table.setEngine(tableEngine);
                    table.setRowFormat(rowFormat);
                    table.setComment(tableComment);
                    table.setAutoIncrement(autoIncrement);
                    table.setCreateDefinition(showCreateTable);
                    table.setCharsetAndCollation(tableCollation);
                }
                DBUtil.close(resultSet);
                DBUtil.close(statement);
            } else {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet resultSet = metaData.getTables(null, null, tableName, TABLE_TYPES);
                while (resultSet.next()) {
                    if (DBUtil.checkTableType(resultSet, dbName)) {
                        String remarks = resultSet.getString("REMARKS");
                        table.setComment(remarks);
                    }
                }
                DBUtil.close(resultSet);
            }
            return table;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlTable selectFullTable(MysqlTableSelectParam param) {
        try {
            String dbName = param.dbName();
            String tableName = param.tableName();
            MysqlTable table = new MysqlTable();
            table.setDbName(dbName);
            table.setName(tableName);
            Connection connection = this.connection(dbName);
            String sql = "SELECT `AUTO_INCREMENT`, `ROW_FORMAT`, `TABLE_COLLATION`, `TABLE_COMMENT`, `ENGINE` FROM information_schema.TABLES WHERE `TABLE_SCHEMA` = ? AND `TABLE_NAME` = ?  AND `TABLE_TYPE` = 'BASE TABLE'";
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            String showCreateTable = this.showCreateTable(dbName, tableName);
            while (resultSet.next()) {
                String tableEngine = resultSet.getString("ENGINE");
                String rowFormat = resultSet.getString("ROW_FORMAT");
                Long autoIncrement = resultSet.getLong("AUTO_INCREMENT");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                String tableCollation = resultSet.getString("TABLE_COLLATION");
                table.setEngine(tableEngine);
                table.setRowFormat(rowFormat);
                table.setComment(tableComment);
                table.setAutoIncrement(autoIncrement);
                table.setCreateDefinition(showCreateTable);
                table.setCharsetAndCollation(tableCollation);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return table;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlView view(String dbName, String viewName) {
        try {
            String sql = "SELECT `TABLE_NAME`, `TABLE_COMMENT` FROM information_schema.`TABLES` WHERE `TABLE_SCHEMA` = ? AND `TABLE_NAME` = ? AND `TABLE_TYPE` = 'VIEW'";
            DBUtil.printSql(sql);
            Connection connection = this.connection();
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, viewName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            MysqlView view = new MysqlView();
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                Map<String, String> info = DBHelper.getViewInfo(connection, dbName, tableName);
                view.setDbName(dbName);
                view.setName(tableName);
                view.setComment(tableComment);
                view.setDefiner(info.get("DEFINER"));
                view.setAlgorithm(info.get("ALGORITHM"));
                view.setDefinition(info.get("DEFINITION"));
                view.setCheckOption(info.get("CHECK_OPTION"));
                view.setSecurityType(info.get("SECURITY_TYPE"));
                view.setUpdatable(StrUtil.equalsIgnoreCase("YES", info.get("UPDATABLE")));
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return view;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<MysqlView> views(String dbName) {
        try {
            List<MysqlView> list = new ArrayList<>();
            String sql = "SELECT `TABLE_NAME`, `TABLE_COMMENT` FROM information_schema.`TABLES` WHERE `TABLE_SCHEMA` = ? AND `TABLE_TYPE` = 'VIEW'";
            DBUtil.printSql(sql);
            Connection connection = this.connection();
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                MysqlView view = new MysqlView();
                String tableName = resultSet.getString("TABLE_NAME");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                Map<String, String> info = DBHelper.getViewInfo(connection, dbName, tableName);
                view.setDbName(dbName);
                view.setName(tableName);
                view.setComment(tableComment);
                view.setDefiner(info.get("DEFINER"));
                view.setAlgorithm(info.get("ALGORITHM"));
                view.setDefinition(info.get("DEFINITION"));
                view.setCheckOption(info.get("CHECK_OPTION"));
                view.setSecurityType(info.get("SECURITY_TYPE"));
                view.setUpdatable(StrUtil.equalsIgnoreCase("YES", info.get("UPDATABLE")));
                list.add(view);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropView(String dbName, MysqlView view) {
        try {
            String sql = "DROP VIEW IF EXISTS " + DBUtil.wrap(view.getDbName(), view.getName());
            Statement statement = this.connection(dbName).createStatement();
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public boolean existView(String dbName, String viewName) {
        boolean result;
        try {
            DatabaseMetaData metaData = this.connection(dbName).getMetaData();
            ResultSet resultSet = metaData.getTables(null, dbName, viewName, new String[]{"VIEW"});
            result = resultSet.next();
            DBUtil.close(resultSet);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
        return result;
    }

    public void createView(String dbName, MysqlView view) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "CREATE ";
            if (StrUtil.isNotBlank(view.getAlgorithm())) {
                sql += " ALGORITHM = " + view.getAlgorithm();
            }
            if (StrUtil.isNotBlank(view.getDefiner())) {
                sql += " DEFINER = " + view.getDefiner();
            }
            if (StrUtil.isNotBlank(view.getSecurityType())) {
                sql += " SQL SECURITY " + view.getSecurityType();
            }
            sql = sql + " VIEW " + DBUtil.wrap(dbName, view.getName()) + " AS " + view.getDefinition();
            if (view.hasCheckOption()) {
                sql += " WITH " + view.getCheckOption() + " CHECK OPTION";
            }
            DBUtil.printSql(sql);
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public void alertView(String dbName, MysqlView view) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "CREATE OR REPLACE ";
            if (StrUtil.isNotBlank(view.getAlgorithm())) {
                sql += " ALGORITHM = " + view.getAlgorithm();
            }
            if (StrUtil.isNotBlank(view.getDefiner())) {
                sql += " DEFINER = " + view.getDefiner();
            }
            if (StrUtil.isNotBlank(view.getSecurityType())) {
                sql += " SQL SECURITY " + view.getSecurityType();
            }
            sql = sql + " VIEW " + DBUtil.wrap(dbName, view.getName()) + " AS " + view.getDefinition();
            if (view.hasCheckOption()) {
                sql += " WITH " + view.getCheckOption() + " CHECK OPTION";
            }
            DBUtil.printSql(sql);
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public MysqlIndexes indexes(String dbName, String tableName) {
        try {
            Connection connection = this.connection();
            Statement statement = connection.createStatement();
            String sql = "SHOW INDEX FROM " + DBUtil.wrap(dbName, tableName);
            DBUtil.printSql(sql);
            ResultSet resultSet = statement.executeQuery(sql);
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            Map<String, MysqlIndex> indexMap = new HashMap<>();
            while (resultSet.next()) {
                String keyName = resultSet.getString("Key_name");
                // 主键类型的跳过
                if ("Primary".equalsIgnoreCase(keyName)) {
                    continue;
                }
                MysqlIndex tableIndex = indexMap.get(keyName);
                String columnName = resultSet.getString("Column_name");
                if (tableIndex == null) {
                    int noneUnique = resultSet.getInt("Non_unique");
                    int seqInIndex = resultSet.getInt("Seq_in_index");
                    String indexType = resultSet.getString("Index_type");
                    String indexComment = resultSet.getString("Index_comment");
                    tableIndex = new MysqlIndex();
                    tableIndex.setName(keyName);
                    tableIndex.setSeqIndex(seqInIndex);
                    tableIndex.setComment(indexComment);
                    tableIndex.type(indexType, noneUnique);
                    indexMap.put(keyName, tableIndex);
                }
                int subPart = resultSet.getInt("Sub_Part");
                tableIndex.addColumn(columnName, subPart);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return new MysqlIndexes(indexMap.values());
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public MysqlChecks checks(String dbName, String tableName) {
        if (!this.isSupportCheckFeature()) {
            return null;
        }
        try {
            String sql = """
                    SELECT
                        CHECK_CLAUSE AS 'CLAUSE',
                        CONSTRAINT_NAME AS 'NAME',
                        TABLE_NAME AS 'TABLE_NAME',
                        CONSTRAINT_SCHEMA AS 'DB_NAME'
                    FROM 
                        information_schema.CHECK_CONSTRAINTS
                    WHERE  
                        CONSTRAINT_SCHEMA = ?
                    AND 
                        TABLE_NAME = ?;
                    """;
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            MysqlChecks checks = new MysqlChecks();
            while (resultSet.next()) {
                MysqlCheck check = new MysqlCheck();
                String name = resultSet.getString("NAME");
                String clause = resultSet.getString("CLAUSE");
                check.setName(name);
                check.setClause(clause);
                check.setDbName(dbName);
                check.setTableName(tableName);
                checks.add(check);
            }
            DBUtil.close(resultSet);
            return checks;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlForeignKeys foreignKeys(String dbName, String tableName) {
        try {
            // 查询外键
            String sql = """
                    SELECT
                        a.COLUMN_NAME AS 'FKCOLUMN_NAME',
                        a.REFERENCED_TABLE_SCHEMA AS 'PKTABLE_CAT',
                        a.REFERENCED_TABLE_NAME AS 'PKTABLE_NAME',
                        a.REFERENCED_COLUMN_NAME AS 'PKCOLUMN_NAME',
                        a.CONSTRAINT_NAME AS 'FK_NAME',
                        a1.UPDATE_RULE,
                        a1.DELETE_RULE 
                    FROM
                        information_schema.KEY_COLUMN_USAGE a
                    JOIN 
                        information_schema.REFERENTIAL_CONSTRAINTS a1 
                    ON 
                        a.CONSTRAINT_NAME = a1.CONSTRAINT_NAME 
                    WHERE
                        a.REFERENCED_TABLE_SCHEMA = ?
                    AND 
                        a.TABLE_NAME = ? 
                    AND 
                        a.REFERENCED_TABLE_NAME IS NOT NULL;
                    """;
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            Map<String, MysqlForeignKey> foreignKeyMap = new HashMap<>();
            while (resultSet.next()) {
                String fkName = resultSet.getString("FK_NAME");
                String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                String pkColumnName = resultSet.getString("PKCOLUMN_NAME");
                MysqlForeignKey foreignKey = foreignKeyMap.get(fkName);
                if (foreignKey == null) {
                    String pkTableName = resultSet.getString("PKTABLE_NAME");
                    String pkTableCat = resultSet.getString("PKTABLE_CAT");
                    String updateRule = resultSet.getString("UPDATE_RULE");
                    String deleteRule = resultSet.getString("DELETE_RULE");
                    foreignKey = new MysqlForeignKey();
                    foreignKey.setName(fkName);
                    foreignKey.setUpdatePolicy(updateRule == null ? null : updateRule.toUpperCase());
                    foreignKey.setDeletePolicy(deleteRule == null ? null : deleteRule.toUpperCase());
                    foreignKey.setPrimaryKeyTable(pkTableName);
                    foreignKey.setPrimaryKeyDatabase(pkTableCat);
                    foreignKeyMap.put(fkName, foreignKey);
                }
                foreignKey.addColumn(fkColumnName);
                foreignKey.addPrimaryKeyColumn(pkColumnName);
            }
            DBUtil.close(resultSet);
            return new MysqlForeignKeys(foreignKeyMap.values());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<MysqlColumn> viewColumns(String dbName, String viewName) {
        try {
            if (StrUtil.isBlank(viewName)) {
                return Collections.emptyList();
            }
            String sql = """
                    SELECT
                        a.EXTRA as COLUMN_EXTRA,
                        a.COLUMN_KEY as COLUMN_KEY,
                        a.COLUMN_COMMENT as REMARKS,
                        a.COLUMN_TYPE as COLUMN_TYPE,
                        a.COLUMN_NAME as COLUMN_NAME,
                        a.IS_NULLABLE as IS_NULLABLE,
                        a.COLUMN_DEFAULT as COLUMN_DEF,
                        a.COLLATION_NAME as COLLATION_NAME,
                        a.CHARACTER_SET_NAME as CHARSET_NAME,
                        a.ORDINAL_POSITION as ORDINAL_POSITION
                    FROM
                        INFORMATION_SCHEMA.`COLUMNS` a
                    WHERE
                        a.TABLE_SCHEMA = ?
                    AND
                        a.TABLE_NAME = ?
                    """;
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, viewName);
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            Map<String, MysqlColumn> columns = new HashMap<>();
            while (resultSet.next()) {
                Object def = resultSet.getObject("COLUMN_DEF");
                String remarks = resultSet.getString("REMARKS");
                int position = resultSet.getInt("ORDINAL_POSITION");
                String nullable = resultSet.getString("IS_NULLABLE");
                String columnKey = resultSet.getString("COLUMN_KEY");
                String columnType = resultSet.getString("COLUMN_TYPE");
                String columnName = resultSet.getString("COLUMN_NAME");
                String charsetName = resultSet.getString("CHARSET_NAME");
                String columnExtra = resultSet.getString("COLUMN_EXTRA");
                String collationName = resultSet.getString("COLLATION_NAME");
                MysqlColumn column = new MysqlColumn();
                column.initColumn(columnType, columnExtra);
                column.setDbName(dbName);
                column.setName(columnName);
                column.setComment(remarks);
                column.setDefaultValue(def);
                column.setPosition(position);
                column.setCharset(charsetName);
                column.setTableName(viewName);
                column.setCollation(collationName);
                column.setNullable("yes".equalsIgnoreCase(nullable));
                column.setPrimaryKey("pri".equalsIgnoreCase(columnKey));
                columns.put(columnName, column);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);

            sql = "SELECT * FROM " + DBUtil.wrap(dbName, viewName) + " LIMIT 1";
            PreparedStatement statement1 = this.connection().prepareStatement(sql);
            ResultSet resultSet1 = statement1.executeQuery();
            MysqlColumns dbColumns = DBHelper.parseColumns(resultSet1);
            DBUtil.close(resultSet1);
            DBUtil.close(statement1);

            // 初始化状态
            for (MysqlColumn value : columns.values()) {
                MysqlColumn dbColumn = dbColumns.column(value.getName());
                if (dbColumn != null) {
                    value.setNullable(dbColumn.isNullable());
                    value.setAutoIncrement(dbColumn.isAutoIncrement());
                }
                value.initStatus();
            }
            // 返回排序后的数据
            return CollUtil.sort(columns.values(), Comparator.comparingInt(MysqlColumn::getPosition));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<MysqlRecord> viewRecords(String dbName, String viewName, Long start, Long limit, List<MysqlRecordFilter> filters) {
        try {
            Connection connection = this.connection(dbName);
            StringBuilder builder = new StringBuilder("SELECT * FROM ");
            builder.append(DBUtil.wrap(dbName, viewName));
            String filterCondition = MysqlConditionUtil.buildCondition(filters);
            if (StrUtil.isNotBlank(filterCondition)) {
                builder.append(" WHERE ").append(filterCondition);
            }
            if (start != null && limit != null) {
                builder.append(" LIMIT ").append(start).append(",").append(limit);
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            DBUtil.printMetaData(resultSet);
            List<MysqlRecord> records = new ArrayList<>();
            boolean updatable = DBHelper.isViewUpdatable(connection, dbName, viewName);
            MysqlColumns columns = DBHelper.parseColumns(resultSet);
            while (resultSet.next()) {
                MysqlRecord record = new MysqlRecord(!updatable);
                for (MysqlColumn column : columns) {
                    Object data = resultSet.getObject(column.getName());
                    // 获取几何值
                    if (column.supportGeometry()) {
                        data = DBHelper.getGeometryString(connection, data);
                    }
                    record.putValue(column, data);
                }
                records.add(record);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return records;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public void createTable(MysqlTableCreateParam param) {
        Connection connection = null;
        try {
            String dbName = param.dbName();
            connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            String sql = MysqlTableCreateSqlGenerator.generateSql(param);
            DBUtil.printSql(sql);
            List<String> sqlList = DBSqlParser.parseSql(sql, this.dialect());
            connection.setAutoCommit(false);
            for (String sqlStr : sqlList) {
                statement.executeUpdate(sqlStr);
            }
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    public void alertTable(MysqlTableAlertParam param) {
        Connection connection = null;
        try {
            String dbName = param.table().getDbName();
            connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            String sql = MysqlTableAlertSqlGenerator.generateSql(param);
            DBUtil.printSql(sql);
            List<String> sqlList = DBSqlParser.parseSql(sql, this.dialect());
            connection.setAutoCommit(false);
            for (String sqlStr : sqlList) {
                statement.executeUpdate(sqlStr);
            }
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    public boolean existTable(String dbName, String tableName) {
        boolean result;
        try {
            DatabaseMetaData metaData = this.connection(dbName).getMetaData();
            ResultSet resultSet = metaData.getTables(null, dbName, tableName, TABLE_TYPES);
            result = resultSet.next();
            DBUtil.close(resultSet);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
        return result;
    }

    public void renameTable(String dbName, String oldTableName, String newTableName) {
        try {
            StringBuilder builder = new StringBuilder("RENAME TABLE ");
            builder.append(DBUtil.wrap(dbName, oldTableName, this.dialect()))
                    .append(" TO ")
                    .append(DBUtil.wrap(dbName, newTableName, this.dialect()));
            String sql = builder.toString();
            Connection connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            DBUtil.printSql(sql);
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void clearTable(String dbName, String tableName) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "DELETE FROM " + DBUtil.wrap(dbName, tableName, this.dialect());
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void truncateTable(String dbName, String tableName) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "TRUNCATE TABLE " + DBUtil.wrap(dbName, tableName, this.dialect());
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropTable(String dbName, String tableName) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "DROP TABLE " + DBUtil.wrap(dbName, tableName, this.dialect());
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<String> charsets() {
        if (this.hasProperty("charsets")) {
            return this.getProperty("charsets");
        }
        try {
            List<String> charsets = new ArrayList<>();
            Statement statement = this.connection().createStatement();
            String sql = "SELECT CHARACTER_SET_NAME FROM INFORMATION_SCHEMA.CHARACTER_SETS;";
            DBUtil.printSql(sql);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                charsets.add(resultSet.getString(1));
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            this.putProperty("charsets", charsets);
            return charsets;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public List<String> collation(String charset) {
        Map<String, List<String>> collations = this.getProperty("collation");
        if (collations == null) {
            collations = new HashMap<>();
            this.putProperty("collations", collations);
        }
        charset = charset.toUpperCase();
        if (collations.containsKey(charset)) {
            return collations.get(charset.toUpperCase());
        }
        try {
            String sql = "SELECT COLLATION_NAME FROM INFORMATION_SCHEMA.COLLATIONS WHERE CHARACTER_SET_NAME = ?;";
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, charset);
            ResultSet resultSet = statement.executeQuery();
            List<String> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            collations.put(charset, list);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public boolean existDatabase(String dbName) {
        boolean result = false;
        try {
            DatabaseMetaData metaData = this.connection().getMetaData();
            // 执行查询操作，检查数据库是否存在
            ResultSet resultSet = metaData.getCatalogs();
            while (resultSet.next()) {
                String catalogName = resultSet.getString("TABLE_CAT");
                if (catalogName.equals(dbName)) {
                    result = true;
                    break;
                }
            }
            DBUtil.close(resultSet);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
        return result;
    }

    public void createDatabase(DBDatabase database) {
        try {
            StringBuilder builder = new StringBuilder("CREATE DATABASE ");
            builder.append(DBUtil.wrap(database.getName(), this.dialect()));
            if (database.getCharset() != null) {
                builder.append(" CHARACTER SET ").append(DBUtil.wrapData(database.getCharset()));
            }
            if (database.getCollation() != null) {
                builder.append(" COLLATE ").append(DBUtil.wrapData(database.getCollation()));
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = this.connection().createStatement();
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public boolean alterDatabase(DBDatabase database) {
        try {
            // 无变化
            if (database.getCharset() == null && database.getCollation() == null) {
                return true;
            }
            StringBuilder builder = new StringBuilder("ALTER DATABASE ").append(DBUtil.wrap(database.getName()));
            if (database.getCharset() != null) {
                builder.append(" CHARACTER SET ").append(DBUtil.wrapData(database.getCharset()));
            }
            if (database.getCollation() != null) {
                builder.append(" COLLATE ").append(DBUtil.wrapData(database.getCollation()));
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = this.connection().createStatement();
            statement.execute(sql);
            DBUtil.close(statement);
            return true;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public String databaseCollation(String dbName) {
        String collation = null;
        try {
            String sql = "SELECT DEFAULT_COLLATION_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?;";
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                collation = resultSet.getString(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
        return collation;
    }

    public boolean dropDatabase(String dbName) {
        try {
            String sql = "DROP DATABASE " + DBUtil.wrap(dbName);
            DBUtil.printSql(sql);
            Statement statement = this.connection().createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
            return true;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public MysqlQueryResults<MysqlExplainResult> explainSql(String dbName, String sql) {
        MysqlQueryResults<MysqlExplainResult> results = new MysqlQueryResults<>();
        Connection connection = null;
        try {
            DBUtil.printSql(sql);
            DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
            List<String> list = parser.parseSql();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String execSql : list) {
                MysqlExplainResult result = new MysqlExplainResult();
                try {
                    execSql = "EXPLAIN " + execSql.stripLeading();
                    result.sql(execSql);
                    long startTime = System.nanoTime();
                    ResultSet resultSet = statement.executeQuery(execSql);
                    result.parseResult(resultSet, connection);
                    result.used(System.nanoTime() - startTime);
                    DBUtil.close(resultSet);
                    result.success(true);
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

    public MysqlExecuteResult executeSingleSql(String dbName, String sql) {
        Connection connection = null;
        MysqlExecuteResult result = new MysqlExecuteResult();
        result.sql(sql);
        try {
            DBUtil.printSql(sql);
            DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
            String execSql = parser.parseSingleSql();
            connection = this.connection(dbName);
            Statement statement = connection.createStatement();
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
                    connection.setAutoCommit(false);
                    int updateCount = statement.getUpdateCount();
                    connection.commit();
                    result.updateCount(updateCount);
                    result.success(true);
                }
                long endTime = System.nanoTime();
                result.used(endTime - startTime);
            } catch (SQLException ex) {
                result.msg(ex.getMessage());
            }
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
        }
        return result;
    }

    public void executeSqlSimple(String dbName, String sql) {
        Connection connection = null;
        try {
            DBUtil.printSql(sql);
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(sql);
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    public int insertBatch(String dbName, List<String> sqlList, boolean parallel) {
        Connection connection = null;
        int result = 0;
        try {
            connection = parallel ? this.newConnection(dbName) : this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String sql : sqlList) {
                statement.addBatch(sql);
            }
            int[] results = statement.executeBatch();
            connection.commit();
            DBUtil.close(statement);
            if (parallel) {
                DBUtil.close(connection);
            }
            for (int i : results) {
                result += i;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);

        }
        return result;
    }

    public DBDialect dialect() {
        return DBDialect.MYSQL;
    }

    public List<MysqlFunction> functions(String dbName) {
        try {
            List<MysqlFunction> list = new ArrayList<>();
            String sql = """
                    SELECT
                        `ROUTINE_NAME`,
                        `SECURITY_TYPE`,
                        `SQL_DATA_ACCESS`,
                        `ROUTINE_DEFINITION`
                    FROM
                        `INFORMATION_SCHEMA`.`ROUTINES` 
                    WHERE
                        `ROUTINE_SCHEMA` = ?
                    AND
                        `ROUTINE_TYPE` = 'FUNCTION'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                MysqlFunction function = new MysqlFunction();
                String name = resultSet.getString("ROUTINE_NAME");
                List<MysqlRoutineParam> params = DBHelper.listFunctionParam(this.connection(), dbName, name);
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                String createDefinition = DBHelper.getFunctionDefinition(this.connection(dbName), name);
                function.setName(name);
                function.setDbName(dbName);
                function.setParams(params);
                function.setDefinition(definition);
                function.setSecurityType(securityType);
                function.setCharacteristic(sqlDataAccess);
                function.setCreateDefinition(createDefinition);
                list.add(function);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public List<MysqlProcedure> procedures(String dbName) {
        try {
            List<MysqlProcedure> list = new ArrayList<>();
            String sql = """
                    SELECT
                        `ROUTINE_NAME`,
                        `SECURITY_TYPE`,
                        `SQL_DATA_ACCESS`,
                        `ROUTINE_DEFINITION`
                    FROM
                        `INFORMATION_SCHEMA`.`ROUTINES` 
                    WHERE
                        `ROUTINE_SCHEMA` = ?
                    AND
                        `ROUTINE_TYPE` = 'PROCEDURE'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                MysqlProcedure procedure = new MysqlProcedure();
                String name = resultSet.getString("ROUTINE_NAME");
                String createDefinition = this.showCreateProcedure(dbName, name);
                List<MysqlRoutineParam> params = DBHelper.listProcedureParam(this.connection(), dbName, name);
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                procedure.setName(name);
                procedure.setDbName(dbName);
                procedure.setParams(params);
                procedure.setDefinition(definition);
                procedure.setSecurityType(securityType);
                procedure.setCharacteristic(sqlDataAccess);
                procedure.setCreateDefinition(createDefinition);
                list.add(procedure);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public MysqlProcedure selectProcedure(String dbName, String produceName) {
        try {
            String sql = """
                    SELECT
                        `SECURITY_TYPE`,
                        `SQL_DATA_ACCESS`,
                        `ROUTINE_DEFINITION`
                    FROM
                        `INFORMATION_SCHEMA`.`ROUTINES` 
                    WHERE
                        `ROUTINE_SCHEMA` = ?
                    AND   
                        `ROUTINE_NAME` = ?
                    AND
                        `ROUTINE_TYPE` = 'PROCEDURE'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, produceName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            MysqlProcedure procedure = new MysqlProcedure();
            procedure.setDbName(dbName);
            procedure.setName(produceName);
            // 遍历结果集
            while (resultSet.next()) {
                String createDefinition = this.showCreateProcedure(dbName, produceName);
                List<MysqlRoutineParam> params = DBHelper.listProcedureParam(this.connection(), dbName, produceName);
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                procedure.setDbName(dbName);
                procedure.setParams(params);
                procedure.setDefinition(definition);
                procedure.setSecurityType(securityType);
                procedure.setCharacteristic(sqlDataAccess);
                procedure.setCreateDefinition(createDefinition);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return procedure;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }


    public void dropProcedure(String dbName, MysqlProcedure routine) {
        try {
            String sql = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(dbName, routine.getName());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void createProcedure(String dbName, MysqlProcedure procedure) {
        try {
            String sql = DBProcedureSqlGenerator.INSTANCE.generate(procedure);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void alertProcedure(String dbName, MysqlProcedure procedure) {
        try {
            String sql = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(dbName, procedure.getName());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            sql = DBProcedureSqlGenerator.INSTANCE.generate(procedure);
            DBUtil.printSql(sql);
            Statement statement1 = this.connection(dbName).createStatement();
            statement1.executeUpdate(sql);
            DBUtil.close(statement1);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropFunction(String dbName, MysqlFunction function) {
        try {
            String sql = "DROP function IF EXISTS " + DBUtil.wrap(dbName, function.getName());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlFunction selectFunction(String dbName, String functionName) {
        try {
            String sql = """
                    SELECT
                        `SECURITY_TYPE`,
                        `SQL_DATA_ACCESS`,
                        `ROUTINE_DEFINITION`
                    FROM
                        `INFORMATION_SCHEMA`.`ROUTINES` 
                    WHERE
                        `ROUTINE_SCHEMA` = ?
                    AND   
                        `ROUTINE_NAME` = ?
                    AND
                        `ROUTINE_TYPE` = 'FUNCTION'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, functionName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            MysqlFunction function = new MysqlFunction();
            function.setDbName(dbName);
            function.setName(functionName);
            // 遍历结果集
            while (resultSet.next()) {
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                List<MysqlRoutineParam> params = DBHelper.listFunctionParam(this.connection(), dbName, functionName);
                String createDefinition = this.showCreateFunction(dbName, functionName);
                function.setDbName(dbName);
                function.setParams(params);
                function.setDefinition(definition);
                function.setSecurityType(securityType);
                function.setCharacteristic(sqlDataAccess);
                function.setCreateDefinition(createDefinition);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return function;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void createFunction(String dbName, MysqlFunction function) {
        try {
            String sql = DBFunctionSqlGenerator.INSTANCE.generate(function);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public MysqlRecord selectRecord(MysqlSelectRecordParam param) {
        try {

            String dbName = param.dbName();
            String tableName = param.tableName();
            Connection connection = this.connection(dbName);
            MysqlRecordPrimaryKey primaryKey = param.primaryKey();
            StringBuilder builder = new StringBuilder("SELECT * FROM ");
            builder.append(DBUtil.wrap(dbName, tableName, DBDialect.MYSQL))
                    .append(" WHERE ")
                    .append(DBUtil.wrap(primaryKey.getColumnName(), DBDialect.MYSQL))
                    .append(" = ?");
            String sql = builder.toString();
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, primaryKey.data());
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            MysqlRecord record = new MysqlRecord();
            MysqlColumns columns = DBHelper.parseColumns(resultSet);
            while (resultSet.next()) {
                for (MysqlColumn column : columns) {
                    Object data = resultSet.getObject(column.getName());
                    // 获取几何值
                    if (column.supportGeometry()) {
                        data = DBHelper.getGeometryString(connection, data);
                    }
                    record.putValue(column, data);
                }
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return record;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public String selectClientCharacter() {
        String character = "";
        try {
            Connection conn = this.connection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SHOW VARIABLES LIKE 'character_set_%'");
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                if ("character_set_client".equalsIgnoreCase(name)) {
                    character = resultSet.getString(2);
                    break;
                }
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return character;
    }

    public boolean existPrimaryKey(String dbName, String tableName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SHOW INDEX FROM " + DBUtil.wrap(dbName, tableName, this.dialect()) + " WHERE Key_name = 'PRIMARY'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            boolean exist = resultSet.next();
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return exist;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }
}

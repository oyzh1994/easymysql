package cn.oyzh.easymysql.handler;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.MysqlConnect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 运行sql文件处理器
 *
 * @author oyzh
 * @since 2024/08/29
 */
public abstract class DataRunSqlFileHandler extends DataHandler {

    /**
     * 库名称
     */
    protected String dbName;

    /**
     * sql文件
     */
    protected File sqlFile;

    /**
     * db客户端
     */
    protected MysqlClient dbClient;

    /**
     * 连接信息
     */
    protected MysqlConnect dbInfo;

    /**
     * 插入限制
     */
    protected int insertLimit = 5000;

    /**
     * 批量限制
     */
    protected int batchLimit = 250;

    /**
     * 遇到错误时继续
     */
    protected boolean continueWithErrors = true;

    /**
     * 方言
     */
    private DBDialect dialect;

    /**
     * 构造方法
     *
     * @param dbClient db客户端
     * @param dbName   库名称
     */
    public DataRunSqlFileHandler(MysqlClient dbClient, String dbName) {
        this.dbClient = dbClient;
        this.dbName = dbName;
    }

    /**
     * 设置sql文件
     *
     * @param sqlFile sql文件
     * @return 当前对象
     */
    public DataRunSqlFileHandler sqlFile(File sqlFile) {
        this.sqlFile = sqlFile;
        return this;
    }

    /**
     * 运行sql文件
     *
     * @throws Exception 异常
     */
    public abstract void runSqlFile() throws Exception ;

    /**
     * 插入集合
     */
    protected List<String> insertList;

    /**
     * 添加插入sql
     *
     * @param sql 插入sql
     */
    protected void addInsertSql(String sql) {
        if (StringUtil.isNotBlank(sql)) {
            if (this.insertList == null) {
                this.insertList = new ArrayList<>();
            }
            this.insertList.add(sql);
            if (this.insertList.size() >= this.insertLimit) {
                this.doBatchInsert();
            }
        }
    }

    /**
     * 执行批量插入
     */
    protected void doBatchInsert() {
        if (CollectionUtil.isNotEmpty(this.insertList)) {
            try {
                if (this.insertList.size() <= this.batchLimit) {
                    this.doBatchInsert(this.insertList, false);
                } else {
                    List<List<String>> lists = CollectionUtil.split(this.insertList, this.batchLimit);
                    List<Runnable> tasks = new ArrayList<>();
                    for (List<String> list : lists) {
                        tasks.add(() -> this.doBatchInsert(list, true));
                    }
                    ThreadUtil.submit(tasks);
                }
            } finally {
                this.insertList.clear();
            }
        }
    }

    /**
     * 执行批量插入
     *
     * @param sqlList  sql列表
     * @param parallel 是否并发
     */
    protected void doBatchInsert(List<String> sqlList, boolean parallel) {
        try {
            int result = this.dbClient.insertBatch(this.dbName, sqlList, parallel);
            this.processedIncr(result);
        } catch (Exception ex) {
            this.processedDecr(sqlList.size());
            throw ex;
        }
    }

    /**
     * 创建新的处理器
     *
     * @param dbClient db客户端
     * @param dbName   数据库
     * @return 运行sql文件处理器
     */
    public static DataRunSqlFileHandler newHandler(MysqlClient dbClient, String dbName) {
        DataRunSqlFileHandler handler = switch (dbClient.dialect()) {
            case MYSQL -> new MysqlDataRunSqlFileHandler(dbClient, dbName);
            default -> null;
        };
        if (handler != null) {
            handler.setDialect(dbClient.dialect());
        }
        return handler;
    }

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置库名称
     *
     * @param dbName 库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取sql文件
     *
     * @return sql文件
     */
    public File getSqlFile() {
        return sqlFile;
    }

    /**
     * 设置sql文件
     *
     * @param sqlFile sql文件
     */
    public void setSqlFile(File sqlFile) {
        this.sqlFile = sqlFile;
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public MysqlClient getDbClient() {
        return dbClient;
    }

    /**
     * 设置db客户端
     *
     * @param dbClient db客户端
     */
    public void setDbClient(MysqlClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public MysqlConnect getDbInfo() {
        return dbInfo;
    }

    /**
     * 设置连接信息
     *
     * @param dbInfo 连接信息
     * @return 当前对象
     */
    public DataRunSqlFileHandler setDbInfo(MysqlConnect dbInfo) {
        this.dbInfo = dbInfo;
        return this;
    }

    /**
     * 获取插入限制
     *
     * @return 插入限制
     */
    public int getInsertLimit() {
        return insertLimit;
    }

    /**
     * 设置插入限制
     *
     * @param insertLimit 插入限制
     */
    public void setInsertLimit(int insertLimit) {
        this.insertLimit = insertLimit;
    }

    /**
     * 获取批量限制
     *
     * @return 批量限制
     */
    public int getBatchLimit() {
        return batchLimit;
    }

    /**
     * 设置批量限制
     *
     * @param batchLimit 批量限制
     */
    public void setBatchLimit(int batchLimit) {
        this.batchLimit = batchLimit;
    }

    /**
     * 是否遇到错误时继续
     *
     * @return 结果
     */
    public boolean isContinueWithErrors() {
        return continueWithErrors;
    }

    /**
     * 设置遇到错误时继续
     *
     * @param continueWithErrors 遇到错误时继续
     */
    public void setContinueWithErrors(boolean continueWithErrors) {
        this.continueWithErrors = continueWithErrors;
    }

    /**
     * 获取方言
     *
     * @return 方言
     */
    public DBDialect getDialect() {
        return dialect;
    }

    /**
     * 设置方言
     *
     * @param dialect 方言
     */
    public void setDialect(DBDialect dialect) {
        this.dialect = dialect;
    }
}


package cn.oyzh.easymysql.handler.runfile;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.handler.DataHandler;
import cn.oyzh.easymysql.handler.runfile.MariaDataRunSqlFileHandler;
import cn.oyzh.easymysql.handler.runfile.MysqlDataRunSqlFileHandler;
import cn.oyzh.fx.common.thread.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/08/29
 */
@Slf4j
public abstract class DataRunSqlFileHandler extends DataHandler {

    /**
     * 库名称
     */
    @Getter
    protected String dbName;

    /**
     * sql文件
     */
    protected File sqlFile;

    /**
     * db客户端
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    protected DBClient dbClient;

    /**
     * 连接信息
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected DBInfo dbInfo;

    /**
     * 插入限制
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected int insertLimit = 5000;

    /**
     * 批量限制
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected int batchLimit = 250;

    /**
     * 遇到错误时继续
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected boolean continueWithErrors = true;

    /**
     * 方言
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    private DBDialect dialect;

    public DataRunSqlFileHandler(DBClient dbClient, String dbName) {
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
        if (StrUtil.isNotBlank(sql)) {
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
        if (CollUtil.isNotEmpty(this.insertList)) {
            try {
                if (this.insertList.size() <= this.batchLimit) {
                    this.doBatchInsert(this.insertList, false);
                } else {
                    List<List<String>> lists = CollUtil.split(this.insertList, this.batchLimit);
                    List<Runnable> tasks = new ArrayList<>();
                    for (List<String> list : lists) {
                        tasks.add(() -> this.doBatchInsert(list, true));
                    }
                    ThreadUtil.submitVirtual(tasks);
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
     * @return DataDumpHandler
     */
    public static DataRunSqlFileHandler newHandler(DBClient dbClient, String dbName) {
        DataRunSqlFileHandler handler = switch (dbClient.dialect()) {
            case MYSQL -> new MysqlDataRunSqlFileHandler(dbClient, dbName);
            case MARIADB -> new MariaDataRunSqlFileHandler(dbClient, dbName);
            default -> null;
        };
        if (handler != null) {
            handler.dialect(dbClient.dialect());
        }
        return handler;
    }
}


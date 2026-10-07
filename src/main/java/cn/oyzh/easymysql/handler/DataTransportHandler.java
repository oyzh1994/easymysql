package cn.oyzh.easymysql.handler;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.fx.data.DataTransportEvent;
import cn.oyzh.easymysql.fx.data.DataTransportFunction;
import cn.oyzh.easymysql.fx.data.DataTransportProcedure;
import cn.oyzh.easymysql.fx.data.DataTransportTable;
import cn.oyzh.easymysql.fx.data.DataTransportTrigger;
import cn.oyzh.easymysql.fx.data.DataTransportView;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输处理器
 *
 * @author oyzh
 * @since 2024/09/06
 */
public abstract class DataTransportHandler extends DataHandler {

    /**
     * 来源客户端
     */
    protected MysqlClient sourceClient;

    /**
     * 目标客户端
     */
    protected MysqlClient targetClient;

    /**
     * 来源库
     */
    protected String sourceDatabase;

    /**
     * 目标库
     */
    protected String targetDatabase;

    /**
     * 查询限制
     */
    protected int selectLimit = 5000;

    /**
     * 批量限制
     */
    protected int batchLimit = 250;

    /**
     * 视图
     */
    protected List<DataTransportView> views;

    /**
     * 表
     */
    protected List<DataTransportTable> tables;

    /**
     * 触发器
     */
    protected List<DataTransportTrigger> triggers;

    /**
     * 函数
     */
    protected List<DataTransportFunction> functions;

    /**
     * 过程
     */
    protected List<DataTransportProcedure> procedures;

    /**
     * 事件
     */
    protected List<DataTransportEvent> events;

    /**
     * 方言
     */
    private DBDialect dialect;

    /**
     * 执行传输
     */
    public abstract void doTransport() throws Exception;

    /**
     * 插入集合
     */
    protected List<String> insertList;

    /**
     * 添加插入sql
     *
     * @param sqlList sql列表
     */
    protected void addInsertSql(List<String> sqlList) {
        if (CollectionUtil.isNotEmpty(sqlList)) {
            if (this.insertList == null) {
                this.insertList = new ArrayList<>();
            }
            this.insertList.addAll(sqlList);
            if (this.insertList.size() >= this.batchLimit) {
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
            int result = this.targetClient.insertBatch(this.targetDatabase, sqlList, parallel);
            this.processedIncr(result);
        } catch (Exception ex) {
            this.processedDecr(sqlList.size());
            throw ex;
        }
    }

    /**
     * 创建新的处理器
     *
     * @param dialect 方言
     * @return DataTransportHandler
     */
    public static DataTransportHandler newHandler(DBDialect dialect) {
        DataTransportHandler handler = switch (dialect) {
            case MYSQL -> new MysqlDataTransportHandler();
            default -> null;
        };
        if (handler != null) {
            handler.setDialect(dialect);
        }
        return handler;
    }

    /**
     * 获取来源客户端
     *
     * @return 来源客户端
     */
    public MysqlClient getSourceClient() {
        return sourceClient;
    }

    /**
     * 设置来源客户端
     *
     * @param sourceClient 来源客户端
     */
    public void setSourceClient(MysqlClient sourceClient) {
        this.sourceClient = sourceClient;
    }

    /**
     * 获取目标客户端
     *
     * @return 目标客户端
     */
    public MysqlClient getTargetClient() {
        return targetClient;
    }

    /**
     * 设置目标客户端
     *
     * @param targetClient 目标客户端
     */
    public void setTargetClient(MysqlClient targetClient) {
        this.targetClient = targetClient;
    }

    /**
     * 获取来源库
     *
     * @return 来源库
     */
    public String getSourceDatabase() {
        return sourceDatabase;
    }

    /**
     * 设置来源库
     *
     * @param sourceDatabase 来源库
     */
    public void setSourceDatabase(String sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    /**
     * 获取目标库
     *
     * @return 目标库
     */
    public String getTargetDatabase() {
        return targetDatabase;
    }

    /**
     * 设置目标库
     *
     * @param targetDatabase 目标库
     */
    public void setTargetDatabase(String targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    /**
     * 获取查询限制
     *
     * @return 查询限制
     */
    public int getSelectLimit() {
        return selectLimit;
    }

    /**
     * 设置查询限制
     *
     * @param selectLimit 查询限制
     */
    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
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
     * 获取视图列表
     *
     * @return 视图列表
     */
    public List<DataTransportView> getViews() {
        return views;
    }

    /**
     * 设置视图列表
     *
     * @param views 视图列表
     */
    public void setViews(List<DataTransportView> views) {
        this.views = views;
    }

    /**
     * 获取表列表
     *
     * @return 表列表
     */
    public List<DataTransportTable> getTables() {
        return tables;
    }

    /**
     * 设置表列表
     *
     * @param tables 表列表
     */
    public void setTables(List<DataTransportTable> tables) {
        this.tables = tables;
    }

    /**
     * 获取触发器列表
     *
     * @return 触发器列表
     */
    public List<DataTransportTrigger> getTriggers() {
        return triggers;
    }

    /**
     * 设置触发器列表
     *
     * @param triggers 触发器列表
     */
    public void setTriggers(List<DataTransportTrigger> triggers) {
        this.triggers = triggers;
    }

    /**
     * 获取函数列表
     *
     * @return 函数列表
     */
    public List<DataTransportFunction> getFunctions() {
        return functions;
    }

    /**
     * 设置函数列表
     *
     * @param functions 函数列表
     */
    public void setFunctions(List<DataTransportFunction> functions) {
        this.functions = functions;
    }

    /**
     * 获取过程列表
     *
     * @return 过程列表
     */
    public List<DataTransportProcedure> getProcedures() {
        return procedures;
    }

    /**
     * 设置过程列表
     *
     * @param procedures 过程列表
     */
    public void setProcedures(List<DataTransportProcedure> procedures) {
        this.procedures = procedures;
    }

    /**
     * 获取事件列表
     *
     * @return 事件列表
     */
    public List<DataTransportEvent> getEvents() {
        return events;
    }

    /**
     * 设置事件列表
     *
     * @param events 事件列表
     */
    public void setEvents(List<DataTransportEvent> events) {
        this.events = events;
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


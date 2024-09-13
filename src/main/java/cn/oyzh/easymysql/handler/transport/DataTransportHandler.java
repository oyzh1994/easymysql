package cn.oyzh.easymysql.handler.transport;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.fx.data.DataTransportEvent;
import cn.oyzh.easymysql.fx.data.DataTransportFunction;
import cn.oyzh.easymysql.fx.data.DataTransportProcedure;
import cn.oyzh.easymysql.fx.data.DataTransportTable;
import cn.oyzh.easymysql.fx.data.DataTransportTrigger;
import cn.oyzh.easymysql.fx.data.DataTransportView;
import cn.oyzh.easymysql.handler.DataHandler;
import cn.oyzh.fx.common.thread.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/06
 */
@Slf4j
public abstract class DataTransportHandler extends DataHandler {

    /**
     * 来源客户端
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected DBClient sourceClient;

    /**
     * 目标客户端
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected DBClient targetClient;

    /**
     * 来源库
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected String sourceDatabase;

    /**
     * 目标库
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected String targetDatabase;

    /**
     * 查询限制
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected int selectLimit = 5000;

    /**
     * 批量限制
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected int batchLimit = 250;

    /**
     * 视图
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected List<DataTransportView> views;

    /**
     * 表
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected List<DataTransportTable> tables;

    /**
     * 触发器
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected List<DataTransportTrigger> triggers;

    /**
     * 函数
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected List<DataTransportFunction> functions;

    /**
     * 过程
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected List<DataTransportProcedure> procedures;

    /**
     * 事件
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected List<DataTransportEvent> events;

    /**
     * 方言
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
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
        if (CollUtil.isNotEmpty(sqlList)) {
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
            handler.dialect(dialect);
        }
        return handler;
    }
}


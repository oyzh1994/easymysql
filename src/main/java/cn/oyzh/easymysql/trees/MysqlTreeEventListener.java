package cn.oyzh.easymysql.trees;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.event.MysqlDatabaseAddedEvent;
import cn.oyzh.easymysql.event.MysqlEventAddedEvent;
import cn.oyzh.easymysql.event.MysqlEventAlertedEvent;
import cn.oyzh.easymysql.event.MysqlFunctionAddedEvent;
import cn.oyzh.easymysql.event.MysqlFunctionAlertedEvent;
import cn.oyzh.easymysql.event.MysqlProcedureAddedEvent;
import cn.oyzh.easymysql.event.MysqlProcedureAlertedEvent;
import cn.oyzh.easymysql.event.MysqlQueryAddedEvent;
import cn.oyzh.easymysql.event.MysqlTableAddedEvent;
import cn.oyzh.easymysql.event.MysqlTableAlertedEvent;
import cn.oyzh.easymysql.event.MysqlViewAddedEvent;
import cn.oyzh.easymysql.event.MysqlViewAlertedEvent;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.fx.plus.event.EventListener;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author oyzh
 * @since 2024-09-12
 */
@Component
public class MysqlTreeEventListener implements EventListener {

    @PostConstruct
    private void init(){
        EventListener.super.register();
    }

    @PreDestroy
    private void destroy(){
        EventListener.super.unregister();
    }

    /**
     * 查询新增事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onQueryAdded(MysqlQueryAddedEvent event) {
        event.item().getQueryTypeChild().addChild(event.data());
    }

    /**
     * 视图新增事件
     *
     * @param event 事件
     */
    @Subscribe
    private void viewAdded(MysqlViewAddedEvent event) {
        MysqlDatabaseTreeItem dbItem = event.data();
        if (dbItem != null) {
            dbItem.getViewTypeChild().reloadChild();
        }
    }

    /**
     * 视图变更事件
     *
     * @param event 事件
     */
    @Subscribe
    private void viewAlerted(MysqlViewAlertedEvent event) {
        String viewName = event.data();
        for (MysqlViewTreeItem viewItem : event.dbItem().getViewChild()) {
            if (StrUtil.equalsIgnoreCase(viewName, viewItem.viewName())) {
                viewItem.reloadChild();
                break;
            }
        }
    }


    /**
     * 表添加事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onTableAdded(MysqlTableAddedEvent event) {
        MysqlDatabaseTreeItem dbItem = event.data();
        if (dbItem != null) {
            dbItem.getTableTypeChild().reloadChild();
        }
    }

    /**
     * 表修改事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onTableAlerted(MysqlTableAlertedEvent event) {
        String tableName = event.data();
        for (MysqlTableTreeItem tableItem : event.dbItem().getTableChild()) {
            if (StrUtil.equalsIgnoreCase(tableName, tableItem.tableName())) {
                tableItem.reloadChild();
                break;
            }
        }
    }

    /**
     * 过程添加事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onProcedureAdded(MysqlProcedureAddedEvent event) {
        MysqlDatabaseTreeItem dbItem = event.data();
        if (dbItem != null) {
            dbItem.getProcedureTypeChild().reloadChild();
        }
    }

    /**
     * 过程修改事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onProcedureAlerted(MysqlProcedureAlertedEvent event) {
        String procedureName = event.data();
        for (MysqlProcedureTreeItem procedureItem : event.dbItem().getProcedureChild()) {
            if (StrUtil.equalsIgnoreCase(procedureName, procedureItem.procedureName())) {
                procedureItem.reloadChild();
                break;
            }
        }
    }

    /**
     * 函数添加事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onFunctionAdded(MysqlFunctionAddedEvent event) {
        MysqlDatabaseTreeItem dbItem = event.data();
        if (dbItem != null) {
            dbItem.getFunctionTypeChild().reloadChild();
        }
    }

    /**
     * 函数修改事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onFunctionAlerted(MysqlFunctionAlertedEvent event) {
        String functionName = event.data();
        for (MysqlFunctionTreeItem functionItem : event.dbItem().getFunctionChild()) {
            if (StrUtil.equalsIgnoreCase(functionName, functionItem.functionName())) {
                functionItem.reloadChild();
                break;
            }
        }
    }

    /**
     * 事件添加事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onEventAdded(MysqlEventAddedEvent event) {
        MysqlDatabaseTreeItem dbItem = event.data();
        if (dbItem != null) {
            dbItem.getEventTypeChild().reloadChild();
        }
    }

    /**
     * 事件修改事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onEventAlerted(MysqlEventAlertedEvent event) {
        String functionName = event.data();
        for (MysqlEventTreeItem eventTreeItem : event.dbItem().getEventChild()) {
            if (StrUtil.equalsIgnoreCase(functionName, eventTreeItem.eventName())) {
                eventTreeItem.reloadChild();
                break;
            }
        }
    }

    /**
     * 数据库新增事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onDatabaseAdded(MysqlDatabaseAddedEvent event) {
        DBConnectTreeItem connectItem = event.connectItem();
        if (connectItem != null) {
            connectItem.reloadChild();
        }
    }

}

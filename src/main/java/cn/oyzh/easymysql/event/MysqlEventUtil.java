package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.event.connect.DBAddConnectEvent;
import cn.oyzh.easymysql.event.connect.DBConnectionClosedEvent;
import cn.oyzh.easymysql.event.connect.DBConnectionConnectedEvent;
import cn.oyzh.easymysql.event.connect.MysqlConnectAddedEvent;
import cn.oyzh.easymysql.event.connect.MysqlConnectDeletedEvent;
import cn.oyzh.easymysql.event.connect.MysqlConnectUpdatedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseAddedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseClosedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseDroppedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseUpdatedEvent;
import cn.oyzh.easymysql.event.event.MysqlEventAddedEvent;
import cn.oyzh.easymysql.event.event.MysqlEventAlertedEvent;
import cn.oyzh.easymysql.event.event.MysqlEventDesignEvent;
import cn.oyzh.easymysql.event.event.MysqlEventRenamedEvent;
import cn.oyzh.easymysql.event.function.MysqlFunctionAddedEvent;
import cn.oyzh.easymysql.event.function.MysqlFunctionAlertedEvent;
import cn.oyzh.easymysql.event.function.MysqlFunctionDesignEvent;
import cn.oyzh.easymysql.event.group.DBAddGroupEvent;
import cn.oyzh.easymysql.event.procedure.MysqlProcedureAddedEvent;
import cn.oyzh.easymysql.event.procedure.MysqlProcedureAlertedEvent;
import cn.oyzh.easymysql.event.procedure.MysqlProcedureDesignEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryAddEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryAddedEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryDeletedEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryOpenEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryRenamedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableAddedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableAlertedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableClearedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableDesignEvent;
import cn.oyzh.easymysql.event.table.MysqlTableDroppedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableFilteredEvent;
import cn.oyzh.easymysql.event.table.MysqlTableOpenEvent;
import cn.oyzh.easymysql.event.table.MysqlTableRenamedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableTruncatedEvent;
import cn.oyzh.easymysql.event.tree.MysqlTreeItemChangedEvent;
import cn.oyzh.easymysql.event.view.MysqlViewAddedEvent;
import cn.oyzh.easymysql.event.view.MysqlViewAlertedEvent;
import cn.oyzh.easymysql.event.view.MysqlViewDesignEvent;
import cn.oyzh.easymysql.event.view.MysqlViewFilteredEvent;
import cn.oyzh.easymysql.event.view.MysqlViewOpenEvent;
import cn.oyzh.easymysql.event.view.MysqlViewRenamedEvent;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.event.MysqlEvent;
import cn.oyzh.easymysql.mysql.function.MysqlFunction;
import cn.oyzh.easymysql.mysql.procedure.MysqlProcedure;
import cn.oyzh.easymysql.mysql.record.MysqlRecordFilter;
import cn.oyzh.easymysql.mysql.table.MysqlTable;
import cn.oyzh.easymysql.mysql.view.MysqlView;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.event.MysqlEventTreeItem;
import cn.oyzh.easymysql.trees.query.MysqlQueryTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * redis事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
public class MysqlEventUtil {

    public static void tableOpen(MysqlTableTreeItem item, MysqlDatabaseTreeItem dbItem) {
        MysqlTableOpenEvent event = new MysqlTableOpenEvent();
        event.data(item);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    // public static void recordDelete() {
    //     EventUtil.post(new MysqlRecordDeleteEvent());
    // }

    public static void tableAdded(MysqlDatabaseTreeItem item) {
        MysqlTableAddedEvent event = new MysqlTableAddedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void tableAlerted(String tableName, MysqlDatabaseTreeItem dbItem) {
        MysqlTableAlertedEvent event = new MysqlTableAlertedEvent();
        event.data(tableName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void procedureAdded(MysqlDatabaseTreeItem dbItem) {
        MysqlProcedureAddedEvent event = new MysqlProcedureAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void procedureAlerted(String procedureName, MysqlDatabaseTreeItem dbItem) {
        MysqlProcedureAlertedEvent event = new MysqlProcedureAlertedEvent();
        event.data(procedureName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void eventAdded(MysqlDatabaseTreeItem dbItem) {
        MysqlEventAddedEvent event = new MysqlEventAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void eventAlerted(String eventName, MysqlDatabaseTreeItem dbItem) {
        MysqlEventAlertedEvent event = new MysqlEventAlertedEvent();
        event.data(eventName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void functionAdded(MysqlDatabaseTreeItem dbItem) {
        MysqlFunctionAddedEvent event = new MysqlFunctionAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void functionAlerted(String functionName, MysqlDatabaseTreeItem dbItem) {
        MysqlFunctionAlertedEvent event = new MysqlFunctionAlertedEvent();
        event.data(functionName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void tableRenamed(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableRenamedEvent event = new MysqlTableRenamedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void viewRenamed(MysqlViewTreeItem viewItem, MysqlDatabaseTreeItem dbItem) {
        MysqlViewRenamedEvent event = new MysqlViewRenamedEvent();
        event.setDbItem(dbItem);
        event.data(viewItem);
        EventUtil.post(event);
    }

    public static void eventRenamed(MysqlEventTreeItem viewItem, MysqlDatabaseTreeItem dbItem) {
        MysqlEventRenamedEvent event = new MysqlEventRenamedEvent();
        event.setDbItem(dbItem);
        event.data(viewItem);
        EventUtil.post(event);
    }

    public static void tableCleared(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableClearedEvent event = new MysqlTableClearedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void tableFiltered(MysqlTableTreeItem item, List<MysqlRecordFilter> filters) {
        MysqlTableFilteredEvent event = new MysqlTableFilteredEvent();
        event.data(item);
        event.setFilters(filters);
        EventUtil.post(event);
    }

    public static void viewFiltered(MysqlViewTreeItem item, List<MysqlRecordFilter> filters) {
        MysqlViewFilteredEvent event = new MysqlViewFilteredEvent();
        event.data(item);
        event.setFilters(filters);
        EventUtil.post(event);
    }

    public static void tableTruncated(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableTruncatedEvent event = new MysqlTableTruncatedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void tableDropped(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableDroppedEvent event = new MysqlTableDroppedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void databaseClosed(MysqlDatabaseTreeItem dbItem) {
        MysqlDatabaseClosedEvent event = new MysqlDatabaseClosedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void databaseAdded(DBConnectTreeItem connectItem, DBDatabase database) {
        MysqlDatabaseAddedEvent event = new MysqlDatabaseAddedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    public static void databaseUpdated(DBConnectTreeItem connectItem, DBDatabase database) {
        MysqlDatabaseUpdatedEvent event = new MysqlDatabaseUpdatedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    public static void databaseDropped(MysqlDatabaseTreeItem dbItem) {
        MysqlDatabaseDroppedEvent event = new MysqlDatabaseDroppedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void queryAdd(MysqlDatabaseTreeItem item) {
        MysqlQueryAddEvent event = new MysqlQueryAddEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void queryAdded(MysqlQuery query, MysqlDatabaseTreeItem item) {
        MysqlQueryAddedEvent event = new MysqlQueryAddedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void queryDeleted(MysqlQueryTreeItem item) {
        MysqlQueryDeletedEvent event = new MysqlQueryDeletedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void queryOpen(MysqlQuery query, MysqlDatabaseTreeItem item) {
        MysqlQueryOpenEvent event = new MysqlQueryOpenEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void queryRenamed(MysqlQuery query, MysqlDatabaseTreeItem item) {
        MysqlQueryRenamedEvent event = new MysqlQueryRenamedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void viewOpen(MysqlViewTreeItem item, MysqlDatabaseTreeItem dbItem) {
        MysqlViewOpenEvent event = new MysqlViewOpenEvent();
        event.data(item);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designFunction(MysqlFunction function, MysqlDatabaseTreeItem dbItem) {
        MysqlFunctionDesignEvent event = new MysqlFunctionDesignEvent();
        event.data(function);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designProcedure(MysqlProcedure procedure, MysqlDatabaseTreeItem dbItem) {
        MysqlProcedureDesignEvent event = new MysqlProcedureDesignEvent();
        event.data(procedure);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designEvent(MysqlEvent event, MysqlDatabaseTreeItem dbItem) {
        MysqlEventDesignEvent event1 = new MysqlEventDesignEvent();
        event1.data(event);
        event1.setDbItem(dbItem);
        EventUtil.post(event1);
    }

    public static void viewAlerted(String viewName, MysqlDatabaseTreeItem dbItem) {
        MysqlViewAlertedEvent event = new MysqlViewAlertedEvent();
        event.data(viewName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designView(MysqlView dbView, MysqlDatabaseTreeItem dbItem) {
        MysqlViewDesignEvent event = new MysqlViewDesignEvent();
        event.data(dbView);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void viewAdded(MysqlDatabaseTreeItem dbItem) {
        MysqlViewAddedEvent event = new MysqlViewAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void infoDeleted(MysqlConnect info) {
        MysqlConnectDeletedEvent event = new MysqlConnectDeletedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    public static void designTable(MysqlTable table, MysqlDatabaseTreeItem dbItem) {
        MysqlTableDesignEvent event = new MysqlTableDesignEvent();
        event.data(table);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 连接关闭事件
     *
     * @param client redis客户端
     */
    public static void connectionClosed(MysqlClient client) {
        DBConnectionClosedEvent event = new DBConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接成功事件
     *
     * @param client redis客户端
     */
    public static void connectionConnected(MysqlClient client) {
        DBConnectionConnectedEvent event = new DBConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    // /**
    //  * 终端打开事件
    //  */
    // public static void terminalOpen() {
    //     terminalOpen(null);
    // }
    //
    // /**
    //  * 终端打开事件
    //  *
    //  * @param info redis信息
    //  */
    // public static void terminalOpen(MysqlConnect info) {
    //     DBTerminalOpenEvent event = new DBTerminalOpenEvent();
    //     event.data(info);
    //     EventUtil.post(event);
    // }
    //
    // /**
    //  * 终端关闭事件
    //  *
    //  * @param info redis信息
    //  */
    // public static void terminalClose(MysqlConnect info) {
    //     DBTerminalCloseEvent event = new DBTerminalCloseEvent();
    //     event.data(info);
    //     EventUtil.post(event);
    // }

//    /**
//     * 树节点变化事件
//     */
//    public static void treeChildChanged() {
//        EventUtil.postDelay(new TreeChildChangedEvent(), 100);
//    }

    // public static void recordDelete(MysqlRecord record) {
    //     RecordDeleteEvent event = new RecordDeleteEvent();
    //     event.data(record);
    //     EventUtil.post(event);
    // }

    /**
     * 连接已修改事件
     *
     * @param connect DB信息
     */
    public static void connectUpdated(MysqlConnect connect) {
        MysqlConnectUpdatedEvent event = new MysqlConnectUpdatedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    public static void addConnect() {
        EventUtil.post(new DBAddConnectEvent());
    }

    public static void addGroup() {
        EventUtil.post(new DBAddGroupEvent());
    }

    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    public static void connectAdded(MysqlConnect connect) {
        MysqlConnectAddedEvent event = new MysqlConnectAddedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    public static void connectDeleted(MysqlConnect connect) {
        MysqlConnectDeletedEvent event = new MysqlConnectDeletedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 布局1
     */
    public static void layout1() {
        EventUtil.post(new Layout1Event());
    }

    /**
     * 布局2
     */
    public static void layout2() {
        EventUtil.post(new Layout2Event());
    }

    /**
     * 节点选中事件
     *
     * @param item 节点
     */
    public static void treeItemChanged(TreeItem<?> item) {
        MysqlTreeItemChangedEvent event = new MysqlTreeItemChangedEvent();
        event.data(item);
        EventUtil.post(event);
    }

}

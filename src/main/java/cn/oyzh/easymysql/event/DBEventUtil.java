package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.connect.DBAddConnectEvent;
import cn.oyzh.easymysql.event.connect.DBConnectionClosedEvent;
import cn.oyzh.easymysql.event.connect.DBConnectionConnectedEvent;
import cn.oyzh.easymysql.event.connect.DBInfoAddedEvent;
import cn.oyzh.easymysql.event.connect.DBInfoDeletedEvent;
import cn.oyzh.easymysql.event.connect.DBInfoUpdatedEvent;
import cn.oyzh.easymysql.event.group.DBAddGroupEvent;
import cn.oyzh.easymysql.event.terminal.DBTerminalCloseEvent;
import cn.oyzh.easymysql.event.terminal.DBTerminalOpenEvent;
import cn.oyzh.easymysql.event.tree.MysqlTreeItemChangedEvent;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;
import lombok.experimental.UtilityClass;

/**
 * redis事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
@UtilityClass
public class DBEventUtil {

    /**
     * 连接关闭事件
     *
     * @param client redis客户端
     */
    public static void connectionClosed(DBClient client) {
        DBConnectionClosedEvent event = new DBConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接成功事件
     *
     * @param client redis客户端
     */
    public static void connectionConnected(DBClient client) {
        DBConnectionConnectedEvent event = new DBConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 终端打开事件
     */
    public static void terminalOpen() {
        terminalOpen(null);
    }

    /**
     * 终端打开事件
     *
     * @param info redis信息
     */
    public static void terminalOpen(MysqlConnect info) {
        DBTerminalOpenEvent event = new DBTerminalOpenEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 终端关闭事件
     *
     * @param info redis信息
     */
    public static void terminalClose(MysqlConnect info) {
        DBTerminalCloseEvent event = new DBTerminalCloseEvent();
        event.data(info);
        EventUtil.post(event);
    }

//    /**
//     * 搜索开始事件
//     */
//    public static void searchStart(DBSearchParam searchParam) {
//        DBSearchStartEvent event = new DBSearchStartEvent();
//        event.data(searchParam);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 搜索结束事件
//     */
//    public static void searchFinish(DBSearchParam searchParam) {
//        DBSearchFinishEvent event = new DBSearchFinishEvent();
//        event.data(searchParam);
//        EventUtil.post(event);
//    }

    /**
     * 树节点过滤事件
     */
    public static void treeChildFilter() {
        EventUtil.post(new TreeChildFilterEvent());
    }

    /**
     * 树节点变化事件
     */
    public static void treeChildChanged() {
        EventUtil.postDelay(new TreeChildChangedEvent(), 100);
    }

    /**
     * 过滤主页事件
     */
    public static void filterMain() {
        EventUtil.post(new DBFilterMainEvent());
    }
    //
    // public static void tableOpen(MysqlTableTreeItem item, MysqlDatabaseTreeItem dbItem) {
    //     MysqlTableOpenEvent event = new MysqlTableOpenEvent();
    //     event.data(item);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }

    // public static void rowAdded(DBTableTreeItem item) {
    //     DBRowAddedEvent event = new DBRowAddedEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void rowUpdated(DBTableTreeItem item) {
    //     DBRowUpdatedEvent event = new DBRowUpdatedEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }

    public static void recordDelete() {
        EventUtil.post(new RecordDeleteEvent());
    }

    // public static void rowDeleted(DBTableTreeItem item) {
    //     DBRowDeletedEvent event = new DBRowDeletedEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }

    // public static void columnAdded(DBColumn column) {
    //     DBColumnAddedEvent event = new DBColumnAddedEvent();
    //     event.data(column);
    //     EventUtil.post(event);
    // }
    //
    // public static void columnUpdated(DBColumn column) {
    //     DBColumnUpdatedEvent event = new DBColumnUpdatedEvent();
    //     event.data(column);
    //     EventUtil.post(event);
    // }

    // public static void tableAdded(MysqlDatabaseTreeItem item) {
    //     MysqlTableAddedEvent event = new MysqlTableAddedEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableAlerted(String tableName, MysqlDatabaseTreeItem dbItem) {
    //     MysqlTableAlertedEvent event = new MysqlTableAlertedEvent();
    //     event.data(tableName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void procedureAdded(MysqlDatabaseTreeItem dbItem) {
    //     MysqlProcedureAddedEvent event = new MysqlProcedureAddedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void procedureAlerted(String procedureName, MysqlDatabaseTreeItem dbItem) {
    //     MysqlProcedureAlertedEvent event = new MysqlProcedureAlertedEvent();
    //     event.data(procedureName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void eventAdded(MysqlDatabaseTreeItem dbItem) {
    //     MysqlEventAddedEvent event = new MysqlEventAddedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void eventAlerted(String eventName, MysqlDatabaseTreeItem dbItem) {
    //     MysqlEventAlertedEvent event = new MysqlEventAlertedEvent();
    //     event.data(eventName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void functionAdded(MysqlDatabaseTreeItem dbItem) {
    //     MysqlFunctionAddedEvent event = new MysqlFunctionAddedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void functionAlerted(String functionName, MysqlDatabaseTreeItem dbItem) {
    //     MysqlFunctionAlertedEvent event = new MysqlFunctionAlertedEvent();
    //     event.data(functionName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableRenamed(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
    //     MysqlTableRenamedEvent event = new MysqlTableRenamedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableCleared(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
    //     MysqlTableClearedEvent event = new MysqlTableClearedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void schemaFiltered(DBTableSchemaTreeItem<?> treeItem, List<DBRecordFilter> filters) {
    //     DBSchemaFilteredEvent event = new DBSchemaFilteredEvent();
    //     event.data(treeItem);
    //     event.filters(filters);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableTruncated(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
    //     MysqlTableTruncatedEvent event = new MysqlTableTruncatedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableDropped(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
    //     MysqlTableDroppedEvent event = new MysqlTableDroppedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }

    // public static void indexAdded(DBIndex index) {
    //     DBIndexAddedEvent event = new DBIndexAddedEvent();
    //     event.data(index);
    //     EventUtil.post(event);
    // }
    //
    // public static void indexUpdated(DBIndex index) {
    //     DBIndexUpdatedEvent event = new DBIndexUpdatedEvent();
    //     event.data(index);
    //     EventUtil.post(event);
    // }
    //
    // public static void foreignKeyAdded(DBForeignKey foreignKey) {
    //     DBForeignKeyAddedEvent event = new DBForeignKeyAddedEvent();
    //     event.data(foreignKey);
    //     EventUtil.post(event);
    // }
    //
    // public static void foreignKeyUpdated(DBForeignKey foreignKey) {
    //     DBForeignKeyUpdatedEvent event = new DBForeignKeyUpdatedEvent();
    //     event.data(foreignKey);
    //     EventUtil.post(event);
    // }

    // public static void databaseClosed(MysqlDatabaseTreeItem dbItem) {
    //     MysqlDatabaseClosedEvent event = new MysqlDatabaseClosedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void databaseAdded(DBConnectTreeItem connectItem, DBDatabase database) {
    //     MysqlDatabaseAddedEvent event = new MysqlDatabaseAddedEvent();
    //     event.data(database);
    //     event.connectItem(connectItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void databaseUpdated(DBConnectTreeItem connectItem, DBDatabase database) {
    //     MysqlDatabaseUpdatedEvent event = new MysqlDatabaseUpdatedEvent();
    //     event.data(database);
    //     event.connectItem(connectItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void databaseDropped(MysqlDatabaseTreeItem dbItem) {
    //     MysqlDatabaseDroppedEvent event = new MysqlDatabaseDroppedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }

    /**
     * 连接已修改事件
     *
     * @param info DB信息
     */
    public static void infoUpdated(MysqlConnect info) {
        DBInfoUpdatedEvent event = new DBInfoUpdatedEvent();
        event.data(info);
        EventUtil.post(event);
    }
    //
    // public static void queryAdd(MysqlDatabaseTreeItem item) {
    //     MysqlQueryAddEvent event = new MysqlQueryAddEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }

    // public static void queryAdded(DBQuery query, MysqlDatabaseTreeItem item) {
    //     MysqlQueryAddedEvent event = new MysqlQueryAddedEvent();
    //     event.data(query);
    //     event.item(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void queryDeleted(MysqlQueryTreeItem item) {
    //     MysqlQueryDeletedEvent event = new MysqlQueryDeletedEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void queryOpen(DBQuery query, MysqlDatabaseTreeItem item) {
    //     MysqlQueryOpenEvent event = new MysqlQueryOpenEvent();
    //     event.data(query);
    //     event.item(item);
    //     EventUtil.post(event);
    // }

    public static void addConnect() {
        EventUtil.post(new DBAddConnectEvent());
    }

    public static void addGroup() {
        EventUtil.post(new DBAddGroupEvent());
    }

    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    public static void leftCollapse() {
        EventUtil.post(new DBLeftCollapseEvent());
    }

    public static void leftExtend() {
        EventUtil.post(new DBLeftExtendEvent());
    }

    public static void infoAdded(MysqlConnect dbInfo) {
        DBInfoAddedEvent event = new DBInfoAddedEvent();
        event.data(dbInfo);
        EventUtil.post(event);
    }

//    public static void searchFire() {
//        EventUtil.post(new DBSearchFireEvent());
//    }

    // public static void viewOpen(MysqlViewTreeItem item) {
    //     MysqlViewOpenEvent event = new MysqlViewOpenEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void designFunction(DBFunction function, MysqlDatabaseTreeItem dbItem) {
    //     MysqlFunctionDesignEvent event = new MysqlFunctionDesignEvent();
    //     event.data(function);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }

    // public static void procedureOpen(DBProcedureTreeItem item, DBDatabaseTreeItem dbItem) {
    //     DBProcedureOpenEvent event = new DBProcedureOpenEvent();
    //     event.data(item);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }

    // public static void designProcedure(DBProcedure procedure, MysqlDatabaseTreeItem dbItem) {
    //     MysqlProcedureDesignEvent event = new MysqlProcedureDesignEvent();
    //     event.data(procedure);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void designEvent(DBEvent event, MysqlDatabaseTreeItem dbItem) {
    //     MysqlEventDesignEvent event1 = new MysqlEventDesignEvent();
    //     event1.data(event);
    //     event1.dbItem(dbItem);
    //     EventUtil.post(event1);
    // }

    // public static void routineUpdated(DBDatabaseTreeItem dbItem) {
    //     DBRoutineUpdatedEvent event = new DBRoutineUpdatedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }

    // public static void produceChanged(String produceName, DBDatabaseTreeItem dbItem) {
    //     DBProduceChangedEvent event = new DBProduceChangedEvent();
    //     event.data(produceName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void functionChanged(String functionName, DBDatabaseTreeItem dbItem) {
    //     DBFunctionChangedEvent event = new DBFunctionChangedEvent();
    //     event.data(functionName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }

    // public static void functionAdd(DBFunction function, DBDatabaseTreeItem dbItem) {
    //     DBFunctionAddEvent event = new DBFunctionAddEvent();
    //     event.data(function);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }

    // public static void viewAlerted(String viewName, MysqlDatabaseTreeItem dbItem) {
    //     MysqlViewAlertedEvent event = new MysqlViewAlertedEvent();
    //     event.data(viewName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void designView(DBView dbView, MysqlDatabaseTreeItem dbItem) {
    //     MysqlViewDesignEvent event = new MysqlViewDesignEvent();
    //     event.data(dbView);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void viewAdded(MysqlDatabaseTreeItem dbItem) {
    //     MysqlViewAddedEvent event = new MysqlViewAddedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }

    public static void infoDeleted(MysqlConnect info) {
        DBInfoDeletedEvent event = new DBInfoDeletedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    // public static void designTable(DBTable table, MysqlDatabaseTreeItem dbItem) {
    //     MysqlTableDesignEvent event = new MysqlTableDesignEvent();
    //     event.data(table);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void designView(DBView dbView, MariadbDatabaseTreeItem dbItem) {
    //     MariadbViewDesignEvent event = new MariadbViewDesignEvent();
    //     event.data(dbView);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void viewOpen(MariadbViewTreeItem item) {
    //     MariadbViewOpenEvent event = new MariadbViewOpenEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void designEvent(DBEvent event, MariadbDatabaseTreeItem dbItem) {
    //     MariadbEventDesignEvent event1 = new MariadbEventDesignEvent();
    //     event1.data(event);
    //     event1.dbItem(dbItem);
    //     EventUtil.post(event1);
    // }
    //
    // public static void databaseDropped(MariadbDatabaseTreeItem dbItem) {
    //     MariadbDatabaseDroppedEvent event = new MariadbDatabaseDroppedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void databaseClosed(MariadbDatabaseTreeItem dbItem) {
    //     MariadbDatabaseClosedEvent event = new MariadbDatabaseClosedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void queryAdd(MariadbDatabaseTreeItem item) {
    //     MariadbQueryAddEvent event = new MariadbQueryAddEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void queryAdded(DBQuery query, MariadbDatabaseTreeItem item) {
    //     MariadbQueryAddedEvent event = new MariadbQueryAddedEvent();
    //     event.data(query);
    //     event.item(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void queryDeleted(MariadbQueryTreeItem item) {
    //     MariadbQueryDeletedEvent event = new MariadbQueryDeletedEvent();
    //     event.data(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void queryOpen(DBQuery query, MariadbDatabaseTreeItem item) {
    //     MariadbQueryOpenEvent event = new MariadbQueryOpenEvent();
    //     event.data(query);
    //     event.item(item);
    //     EventUtil.post(event);
    // }
    //
    // public static void designFunction(DBFunction function, MariadbDatabaseTreeItem dbItem) {
    //     MariadbFunctionDesignEvent event = new MariadbFunctionDesignEvent();
    //     event.data(function);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void designTable(DBTable table, MariadbDatabaseTreeItem dbItem) {
    //     MariadbTableDesignEvent event = new MariadbTableDesignEvent();
    //     event.data(table);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableTruncated(MariadbTableTreeItem tableItem, MariadbDatabaseTreeItem dbItem) {
    //     MariadbTableTruncatedEvent event = new MariadbTableTruncatedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableDropped(MariadbTableTreeItem tableItem, MariadbDatabaseTreeItem dbItem) {
    //     MariadbTableDroppedEvent event = new MariadbTableDroppedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableRenamed(MariadbTableTreeItem tableItem, MariadbDatabaseTreeItem dbItem) {
    //     MariadbTableRenamedEvent event = new MariadbTableRenamedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableCleared(MariadbTableTreeItem tableItem, MariadbDatabaseTreeItem dbItem) {
    //     MariadbTableClearedEvent event = new MariadbTableClearedEvent();
    //     event.dbItem(dbItem);
    //     event.data(tableItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void tableOpen(MariadbTableTreeItem item, MariadbDatabaseTreeItem dbItem) {
    //     MariadbTableOpenEvent event = new MariadbTableOpenEvent();
    //     event.data(item);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void designProcedure(DBProcedure procedure, MariadbDatabaseTreeItem dbItem) {
    //     MariadbProcedureDesignEvent event = new MariadbProcedureDesignEvent();
    //     event.data(procedure);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void eventAdded(MariadbDatabaseTreeItem dbItem) {
    //     MariadbEventAddedEvent event = new MariadbEventAddedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void eventAlerted(String eventName, MariadbDatabaseTreeItem dbItem) {
    //     MariadbEventAlertedEvent event = new MariadbEventAlertedEvent();
    //     event.data(eventName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void functionAdded(MariadbDatabaseTreeItem dbItem) {
    //     MariadbFunctionAddedEvent event = new MariadbFunctionAddedEvent();
    //     event.data(dbItem);
    //     EventUtil.post(event);
    // }
    //
    // public static void functionAlerted(String functionName, MariadbDatabaseTreeItem dbItem) {
    //     MariadbFunctionAlertedEvent event = new MariadbFunctionAlertedEvent();
    //     event.data(functionName);
    //     event.dbItem(dbItem);
    //     EventUtil.post(event);
    // }


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

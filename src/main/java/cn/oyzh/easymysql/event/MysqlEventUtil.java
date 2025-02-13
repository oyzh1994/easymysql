package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.db.function.MysqlFunction;
import cn.oyzh.easymysql.db.procedure.MysqlProcedure;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.event.connect.DBInfoDeletedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseAddedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseClosedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseDroppedEvent;
import cn.oyzh.easymysql.event.database.MysqlDatabaseUpdatedEvent;
import cn.oyzh.easymysql.event.event.MysqlEventAddedEvent;
import cn.oyzh.easymysql.event.event.MysqlEventAlertedEvent;
import cn.oyzh.easymysql.event.event.MysqlEventDesignEvent;
import cn.oyzh.easymysql.event.function.MysqlFunctionAddedEvent;
import cn.oyzh.easymysql.event.function.MysqlFunctionAlertedEvent;
import cn.oyzh.easymysql.event.function.MysqlFunctionDesignEvent;
import cn.oyzh.easymysql.event.procedure.MysqlProcedureAddedEvent;
import cn.oyzh.easymysql.event.procedure.MysqlProcedureAlertedEvent;
import cn.oyzh.easymysql.event.procedure.MysqlProcedureDesignEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryAddEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryAddedEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryDeletedEvent;
import cn.oyzh.easymysql.event.query.MysqlQueryOpenEvent;
import cn.oyzh.easymysql.event.table.MysqlTableAddedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableAlertedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableClearedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableDesignEvent;
import cn.oyzh.easymysql.event.table.MysqlTableDroppedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableFilteredEvent;
import cn.oyzh.easymysql.event.table.MysqlTableOpenEvent;
import cn.oyzh.easymysql.event.table.MysqlTableRenamedEvent;
import cn.oyzh.easymysql.event.table.MysqlTableTruncatedEvent;
import cn.oyzh.easymysql.event.view.MysqlViewAddedEvent;
import cn.oyzh.easymysql.event.view.MysqlViewAlertedEvent;
import cn.oyzh.easymysql.event.view.MysqlViewDesignEvent;
import cn.oyzh.easymysql.event.view.MysqlViewFilteredEvent;
import cn.oyzh.easymysql.event.view.MysqlViewOpenEvent;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.query.MysqlQueryTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.event.EventUtil;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * redis事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
@UtilityClass
public class MysqlEventUtil {

    public static void tableOpen(MysqlTableTreeItem item, MysqlDatabaseTreeItem dbItem) {
        MysqlTableOpenEvent event = new MysqlTableOpenEvent();
        event.data(item);
        event.dbItem(dbItem);
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
        event.dbItem(dbItem);
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
        event.dbItem(dbItem);
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
        event.dbItem(dbItem);
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
        event.dbItem(dbItem);
        EventUtil.post(event);
    }

    public static void tableRenamed(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableRenamedEvent event = new MysqlTableRenamedEvent();
        event.dbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void tableCleared(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableClearedEvent event = new MysqlTableClearedEvent();
        event.dbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void tableFiltered(MysqlTableTreeItem item, List<MysqlRecordFilter> filters) {
        MysqlTableFilteredEvent event = new MysqlTableFilteredEvent();
        event.data(item);
        event.filters(filters);
        EventUtil.post(event);
    }

    public static void viewFiltered(MysqlViewTreeItem item, List<MysqlRecordFilter> filters) {
        MysqlViewFilteredEvent event = new MysqlViewFilteredEvent();
        event.data(item);
        event.filters(filters);
        EventUtil.post(event);
    }

    public static void tableTruncated(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableTruncatedEvent event = new MysqlTableTruncatedEvent();
        event.dbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void tableDropped(MysqlTableTreeItem tableItem, MysqlDatabaseTreeItem dbItem) {
        MysqlTableDroppedEvent event = new MysqlTableDroppedEvent();
        event.dbItem(dbItem);
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
        event.connectItem(connectItem);
        EventUtil.post(event);
    }

    public static void databaseUpdated(DBConnectTreeItem connectItem, DBDatabase database) {
        MysqlDatabaseUpdatedEvent event = new MysqlDatabaseUpdatedEvent();
        event.data(database);
        event.connectItem(connectItem);
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
        event.item(item);
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
        event.item(item);
        EventUtil.post(event);
    }

    public static void viewOpen(MysqlViewTreeItem item) {
        MysqlViewOpenEvent event = new MysqlViewOpenEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void designFunction(MysqlFunction function, MysqlDatabaseTreeItem dbItem) {
        MysqlFunctionDesignEvent event = new MysqlFunctionDesignEvent();
        event.data(function);
        event.dbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designProcedure(MysqlProcedure procedure, MysqlDatabaseTreeItem dbItem) {
        MysqlProcedureDesignEvent event = new MysqlProcedureDesignEvent();
        event.data(procedure);
        event.dbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designEvent(MysqlEvent event, MysqlDatabaseTreeItem dbItem) {
        MysqlEventDesignEvent event1 = new MysqlEventDesignEvent();
        event1.data(event);
        event1.dbItem(dbItem);
        EventUtil.post(event1);
    }

    public static void viewAlerted(String viewName, MysqlDatabaseTreeItem dbItem) {
        MysqlViewAlertedEvent event = new MysqlViewAlertedEvent();
        event.data(viewName);
        event.dbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designView(MysqlView dbView, MysqlDatabaseTreeItem dbItem) {
        MysqlViewDesignEvent event = new MysqlViewDesignEvent();
        event.data(dbView);
        event.dbItem(dbItem);
        EventUtil.post(event);
    }

    public static void viewAdded(MysqlDatabaseTreeItem dbItem) {
        MysqlViewAddedEvent event = new MysqlViewAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void infoDeleted(MysqlConnect info) {
        DBInfoDeletedEvent event = new DBInfoDeletedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    public static void designTable(String tableName, MysqlDatabaseTreeItem dbItem) {
        MysqlTableDesignEvent event = new MysqlTableDesignEvent();
        event.data(tableName);
        event.dbItem(dbItem);
        EventUtil.post(event);
    }

}

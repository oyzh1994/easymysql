// package cn.oyzh.easymysql.trees;
//
// import cn.oyzh.common.util.StringUtil;
// import cn.oyzh.easymysql.event.database.MysqlDatabaseAddedEvent;
// import cn.oyzh.easymysql.event.event.MysqlEventAddedEvent;
// import cn.oyzh.easymysql.event.event.MysqlEventAlertedEvent;
// import cn.oyzh.easymysql.event.function.MysqlFunctionAddedEvent;
// import cn.oyzh.easymysql.event.function.MysqlFunctionAlertedEvent;
// import cn.oyzh.easymysql.event.procedure.MysqlProcedureAddedEvent;
// import cn.oyzh.easymysql.event.procedure.MysqlProcedureAlertedEvent;
// import cn.oyzh.easymysql.event.query.MysqlQueryAddedEvent;
// import cn.oyzh.easymysql.event.view.MysqlViewAddedEvent;
// import cn.oyzh.easymysql.event.view.MysqlViewAlertedEvent;
// import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
// import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
// import cn.oyzh.easymysql.trees.event.MysqlEventTreeItem;
// import cn.oyzh.easymysql.trees.function.MysqlFunctionTreeItem;
// import cn.oyzh.easymysql.trees.procedure.MysqlProcedureTreeItem;
// import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
// import cn.oyzh.event.EventListener;
// import cn.oyzh.event.EventSubscribe;
//
// /**
//  * @author oyzh
//  * @since 2024-09-12
//  */
// public class MysqlTreeEventListener implements EventListener {
//
// //    @PostConstruct
// //    private void init(){
// //        EventListener.super.register();
// //    }
// //
// //    @PreDestroy
// //    private void destroy(){
// //        EventListener.super.unregister();
// //    }
//
//     /**
//      * 查询新增事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onQueryAdded(MysqlQueryAddedEvent event) {
//         event.getDbItem().getQueryTypeChild().addChild(event.data());
//     }
//
//     /**
//      * 视图新增事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void viewAdded(MysqlViewAddedEvent event) {
//         MysqlDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getViewTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 视图变更事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void viewAlerted(MysqlViewAlertedEvent event) {
//         String viewName = event.data();
//         for (MysqlViewTreeItem viewItem : event.getDbItem().getViewChild()) {
//             if (StringUtil.equalsIgnoreCase(viewName, viewItem.viewName())) {
//                 viewItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//
//     // /**
//     //  * 表添加事件
//     //  *
//     //  * @param event 事件
//     //  */
//     // @EventSubscribe
//     // private void onTableAdded(MysqlTableAddedEvent event) {
//     //     MysqlDatabaseTreeItem dbItem = event.data();
//     //     if (dbItem != null) {
//     //         dbItem.getTableTypeChild().reloadChild();
//     //     }
//     // }
//
//     // /**
//     //  * 表修改事件
//     //  *
//     //  * @param event 事件
//     //  */
//     // @EventSubscribe
//     // private void onTableAlerted(MysqlTableAlertedEvent event) {
//     //     String tableName = event.data();
//     //     for (MysqlTableTreeItem tableItem : event.getDbItem().getTableChild()) {
//     //         if (StringUtil.equalsIgnoreCase(tableName, tableItem.tableName())) {
//     //             tableItem.reloadChild();
//     //             break;
//     //         }
//     //     }
//     // }
//
//     /**
//      * 过程添加事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onProcedureAdded(MysqlProcedureAddedEvent event) {
//         MysqlDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getProcedureTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 过程修改事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onProcedureAlerted(MysqlProcedureAlertedEvent event) {
//         String procedureName = event.data();
//         for (MysqlProcedureTreeItem procedureItem : event.getDbItem().getProcedureChild()) {
//             if (StringUtil.equalsIgnoreCase(procedureName, procedureItem.procedureName())) {
//                 procedureItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//     /**
//      * 函数添加事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onFunctionAdded(MysqlFunctionAddedEvent event) {
//         MysqlDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getFunctionTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 函数修改事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onFunctionAlerted(MysqlFunctionAlertedEvent event) {
//         String functionName = event.data();
//         for (MysqlFunctionTreeItem functionItem : event.getDbItem().getFunctionChild()) {
//             if (StringUtil.equalsIgnoreCase(functionName, functionItem.functionName())) {
//                 functionItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//     /**
//      * 事件添加事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onEventAdded(MysqlEventAddedEvent event) {
//         MysqlDatabaseTreeItem dbItem = event.data();
//         if (dbItem != null) {
//             dbItem.getEventTypeChild().reloadChild();
//         }
//     }
//
//     /**
//      * 事件修改事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onEventAlerted(MysqlEventAlertedEvent event) {
//         String functionName = event.data();
//         for (MysqlEventTreeItem eventTreeItem : event.getDbItem().getEventChild()) {
//             if (StringUtil.equalsIgnoreCase(functionName, eventTreeItem.eventName())) {
//                 eventTreeItem.reloadChild();
//                 break;
//             }
//         }
//     }
//
//     /**
//      * 数据库新增事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void onDatabaseAdded(MysqlDatabaseAddedEvent event) {
//         DBConnectTreeItem connectItem = event.getConnectItem();
//         if (connectItem != null) {
//             connectItem.reloadChild();
//         }
//     }
//
// }

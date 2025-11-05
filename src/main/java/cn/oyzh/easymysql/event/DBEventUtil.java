//package cn.oyzh.easymysql.event;
//
//import cn.oyzh.easymysql.mysql.MysqlClient;
//import cn.oyzh.easymysql.domain.MysqlConnect;
//import cn.oyzh.easymysql.event.connect.DBAddConnectEvent;
//import cn.oyzh.easymysql.event.connect.DBConnectionClosedEvent;
//import cn.oyzh.easymysql.event.connect.DBConnectionConnectedEvent;
//import cn.oyzh.easymysql.event.connect.MysqlConnectAddedEvent;
//import cn.oyzh.easymysql.event.connect.MysqlConnectDeletedEvent;
//import cn.oyzh.easymysql.event.connect.MysqlConnectUpdatedEvent;
//import cn.oyzh.easymysql.event.group.DBAddGroupEvent;
//import cn.oyzh.easymysql.event.record.RecordDeleteEvent;
//import cn.oyzh.easymysql.event.terminal.DBTerminalCloseEvent;
//import cn.oyzh.easymysql.event.terminal.DBTerminalOpenEvent;
//import cn.oyzh.easymysql.event.tree.MysqlTreeItemChangedEvent;
//import cn.oyzh.event.EventUtil;
//import cn.oyzh.fx.gui.event.Layout1Event;
//import cn.oyzh.fx.gui.event.Layout2Event;
//import cn.oyzh.fx.plus.changelog.ChangelogEvent;
//import javafx.scene.control.TreeItem;
//import lombok.experimental.UtilityClass;
//
///**
// * redis事件工具
// *
// * @author oyzh
// * @since 2023/11/20
// */
//@UtilityClass
//public class DBEventUtil {
//
//    /**
//     * 连接关闭事件
//     *
//     * @param client redis客户端
//     */
//    public static void connectionClosed(MysqlClient client) {
//        DBConnectionClosedEvent event = new DBConnectionClosedEvent();
//        event.data(client);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 连接成功事件
//     *
//     * @param client redis客户端
//     */
//    public static void connectionConnected(MysqlClient client) {
//        DBConnectionConnectedEvent event = new DBConnectionConnectedEvent();
//        event.data(client);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 终端打开事件
//     */
//    public static void terminalOpen() {
//        terminalOpen(null);
//    }
//
//    /**
//     * 终端打开事件
//     *
//     * @param info redis信息
//     */
//    public static void terminalOpen(MysqlConnect info) {
//        DBTerminalOpenEvent event = new DBTerminalOpenEvent();
//        event.data(info);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 终端关闭事件
//     *
//     * @param info redis信息
//     */
//    public static void terminalClose(MysqlConnect info) {
//        DBTerminalCloseEvent event = new DBTerminalCloseEvent();
//        event.data(info);
//        EventUtil.post(event);
//    }
//
////    /**
////     * 树节点变化事件
////     */
////    public static void treeChildChanged() {
////        EventUtil.postDelay(new TreeChildChangedEvent(), 100);
////    }
//
//    public static void recordDelete() {
//        EventUtil.post(new RecordDeleteEvent());
//    }
//
//    /**
//     * 连接已修改事件
//     *
//     * @param connect DB信息
//     */
//    public static void connectUpdated(MysqlConnect connect) {
//        MysqlConnectUpdatedEvent event = new MysqlConnectUpdatedEvent();
//        event.data(connect);
//        EventUtil.post(event);
//    }
//
//    public static void addConnect() {
//        EventUtil.post(new DBAddConnectEvent());
//    }
//
//    public static void addGroup() {
//        EventUtil.post(new DBAddGroupEvent());
//    }
//
//    public static void changelog() {
//        EventUtil.post(new ChangelogEvent());
//    }
//
//    public static void connectAdded(MysqlConnect connect) {
//        MysqlConnectAddedEvent event = new MysqlConnectAddedEvent();
//        event.data(connect);
//        EventUtil.post(event);
//    }
//
//    public static void connectDeleted(MysqlConnect connect) {
//        MysqlConnectDeletedEvent event = new MysqlConnectDeletedEvent();
//        event.data(connect);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 布局1
//     */
//    public static void layout1() {
//        EventUtil.post(new Layout1Event());
//    }
//
//    /**
//     * 布局2
//     */
//    public static void layout2() {
//        EventUtil.post(new Layout2Event());
//    }
//
//    /**
//     * 节点选中事件
//     *
//     * @param item 节点
//     */
//    public static void treeItemChanged(TreeItem<?> item) {
//        MysqlTreeItemChangedEvent event = new MysqlTreeItemChangedEvent();
//        event.data(item);
//        EventUtil.post(event);
//    }
//
//}

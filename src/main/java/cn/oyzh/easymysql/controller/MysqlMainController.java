package cn.oyzh.easymysql.controller;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.domain.MysqlPageInfo;
import cn.oyzh.easymysql.domain.MysqlSetting;
import cn.oyzh.easymysql.event.connect.DBInfoUpdatedEvent;
import cn.oyzh.easymysql.event.DBLeftCollapseEvent;
import cn.oyzh.easymysql.event.DBLeftExtendEvent;
import cn.oyzh.easymysql.fx.DBMsgTextArea;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.query.MysqlQueryTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.easymysql.store.DBPageInfoStore;
import cn.oyzh.easymysql.store.DBSettingStore;
import cn.oyzh.easymysql.tabs.DBTabPane;
import cn.oyzh.easymysql.trees.DBTreeView;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.util.Collections;
import java.util.List;


/**
 * db主页
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class MysqlMainController extends ParentStageController {

    /**
     * 配置对象
     */
    private final MysqlSetting setting = DBSettingStore.SETTING;

    /**
     * 当前激活的db信息
     */
    private MysqlConnect info;

    /**
     * 左侧db树
     */
    @FXML
    public DBTreeView tree;

    /**
     * 左侧组件
     */
    @FXML
    private FlexTabPane tabPaneLeft;

    /**
     * 大小调整增强
     */
    private ResizeEnhance resizeEnhance;

    // /**
    //  * 倒序排序
    //  */
    // private boolean ascSort;

    /**
     * 节点排序(正序)
     */
    @FXML
    private SVGGlyph sortAsc;

    /**
     * 节点排序(倒序)
     */
    @FXML
    private SVGGlyph sortDesc;

    /**
     * 仅看收藏键
     */
    @FXML
    private FlexCheckBox onlyCollect;

    /**
     * db切换面板
     */
    @FXML
    public DBTabPane tabPane;

    /**
     * 页面信息
     */
    private final MysqlPageInfo pageInfo = DBPageInfoStore.PAGE_INFO;

    /**
     * 页面信息储存
     */
    private final DBPageInfoStore pageInfoStore = DBPageInfoStore.INSTANCE;

    /**
     * 消息文本框
     */
    @FXML
    private DBMsgTextArea msgArea;

    /**
     * 搜索Controller
     */
    @FXML
    private SearchController searchController;

    /**
     * 对子节点排序，正序
     */
    @FXML
    private void sortAsc() {
        this.sortAsc.disappear();
        this.sortDesc.display();
        this.tree.sortAsc();
    }

    /**
     * 对子节点排序，倒序
     */
    @FXML
    private void sortDesc() {
        this.sortDesc.disappear();
        this.sortAsc.display();
        this.tree.sortDesc();
    }

    /**
     * db信息修改事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onInfoUpdate(DBInfoUpdatedEvent event) {
        if (this.info == event.data()) {
            this.stage.appendTitle(" (" + event.data().getName() + ")");
        }
    }

    /**
     * 刷新窗口标题
     *
     * @param info db信息
     */
    private void flushViewTitle(MysqlConnect info) {
        if (info != null) {
            this.stage.appendTitle(" (" + info.getName() + ")");
        } else {
            this.stage.restoreTitle();
        }
        this.info = info;
    }

    /**
     * 树节点变化事件
     *
     * @param item 节点
     */
    private void treeItemChanged(TreeItem<?> item) {
        if (item instanceof MysqlTableTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof MysqlQueryTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof MysqlDatabaseTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof DBConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else {
            this.flushViewTitle(null);
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 注册事件处理
        EventUtil.register(this.tree);
        EventUtil.register(this.tabPane);
        EventUtil.register(this.msgArea);

        // 初始化过滤
        // this.tree.itemFilter(this.treeItemFilter);
        // this.treeItemFilter.initFilters();
        this.filter();

        // 设置上次保存的页面拉伸
        if (this.setting.isRememberPageResize()) {
            this.resizeMainLeft(this.pageInfo.getMainLeftWidth());
        }
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this.tree);
        EventUtil.unregister(this.tabPane);
        EventUtil.unregister(this.msgArea);
        // 关闭连接
        this.tree.closeConnects();
        // 保存页面拉伸
        this.savePageResize();
        // 取消F5按键监听
        KeyListener.unListenReleased(this.tree, KeyCode.F5);
        KeyListener.unListenReleased(this.tabPane, KeyCode.F5);
    }

    /**
     * 左侧组件重新布局
     *
     * @param newWidth 新宽度
     */
    private void resizeMainLeft(Double newWidth) {
        if (newWidth != null && !Double.isNaN(newWidth)) {
            // 设置组件宽
            this.tabPaneLeft.setRealWidth(newWidth);
            this.tabPane.setLayoutX(newWidth);
            this.tabPane.setFlexWidth("100% - " + newWidth);
            this.tabPaneLeft.parentAutosize();
        }
    }

    @Override
    public void onSystemExit() {
        // 保存页面拉伸
        this.savePageResize();
    }

    /**
     * 保存页面拉伸
     */
    private void savePageResize() {
        if (this.setting.isRememberPageResize()) {
            this.pageInfo.setMainLeftWidth(this.tabPaneLeft.getMinWidth());
            this.pageInfoStore.update(this.pageInfo);
        }
    }

    @Override
    protected void bindListeners() {
        // 左侧栏业务
        this.onlyCollect.selectedChanged((obs, o, n) -> {
            if (n) {
            } else {
            }
            this.filter();
        });

        this.sortAsc.managedBindVisible();
        this.sortDesc.managedBindVisible();
        this.tree.selectItemChanged(this::treeItemChanged);
        // 文件拖拽初始化
        this.stage.initDragFile(this.tree.dragContent(), this.tree.root()::dragFile);
        // 拖动改变db树大小处理
        this.resizeEnhance = new ResizeEnhance(this.tabPaneLeft, Cursor.DEFAULT);
        this.resizeEnhance.minWidth(390d);
        this.resizeEnhance.maxWidth(800d);
        this.resizeEnhance.triggerThreshold(8d);
        this.resizeEnhance.mouseDragged(event -> {
            double sceneX = event.getSceneX();
            if (this.resizeEnhance.resizeWidthAble(sceneX)) {
                // 左侧组件重新布局
                this.resizeMainLeft(sceneX);
            }
        });
        // 初始化拉伸事件
        this.tree.setOnMouseMoved(this.resizeEnhance.mouseMoved());
        this.resizeEnhance.initResizeEvent();


        // 监听F5按键
        KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
        KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
    }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.tree.scrollTo(this.tree.getSelectedItem());
    }

    /**
     * 展开左侧事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void leftExtend(DBLeftExtendEvent event) {
        this.tabPaneLeft.display();
        double w = this.tabPaneLeft.getMinWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.tabPaneLeft.parentAutosize();
        JulLog.info("LEFT_EXTEND.");
    }

    /**
     * 收缩左侧事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void leftCollapse(DBLeftCollapseEvent event) {
        this.tabPaneLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.tabPaneLeft.parentAutosize();
        JulLog.info("LEFT_COLLAPSE.");
    }

    @Override
    public List<SubStageController> getSubControllers() {
        return Collections.singletonList(this.searchController);
    }

    /**
     * 当前活跃的db树节点
     *
     * @return db树节点
     */
    public TreeItem<?> activeItem() {
        return tree.getSelectedItem();
    }

    /**
     * 执行过滤
     */
    private void filter() {
        TaskManager.startDelay("db:tree:filter", () -> {
            this.tree.disable();
            if (this.onlyCollect.isSelected()) {
                this.tree.itemFilter().setOnlyCollect(true);
            } else {
                this.tree.itemFilter().setOnlyCollect(false);
            }
            this.tree.filter();
            this.tree.enable();
        }, 100);
    }

    /**
     * 清空消息
     */
    @FXML
    private void clearMsg() {
        this.msgArea.clear();
    }

    public void openTerminal(MouseEvent mouseEvent) {

    }

    // /**
    //  * 处理操作消息
    //  */
    // @EventGroup(value = DBEventGroups.KEY_ACTION, async = true, verbose = true)
    // @EventGroup(value = DBEventGroups.INFO_ACTION, async = true, verbose = true)
    // @EventGroup(value = DBEventGroups.CONNECTION_ACTION, async = true, verbose = true)
    // private void onActionMsg(Event<EventMsg> event) {
    //     if (event.data() instanceof EventMsgFormatter formatter) {
    //         String formatMsg = formatter.formatMsg();
    //         if (formatMsg != null) {
    //             this.msgArea.appendLine(String.format("%s %s", Const.DATE_TIME_FORMAT.format(System.currentTimeMillis()), formatMsg));
    //         }
    //     }
    // }
}

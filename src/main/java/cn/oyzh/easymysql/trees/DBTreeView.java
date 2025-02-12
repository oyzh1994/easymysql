package cn.oyzh.easymysql.trees;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easymysql.controller.info.MysqlInfoAddController;
import cn.oyzh.easymysql.event.DBAddConnectEvent;
import cn.oyzh.easymysql.event.DBAddGroupEvent;
import cn.oyzh.easymysql.event.DBInfoAddedEvent;
import cn.oyzh.easymysql.event.DBInfoUpdatedEvent;
import cn.oyzh.easymysql.event.DBSearchFinishEvent;
import cn.oyzh.easymysql.event.DBSearchStartEvent;
import cn.oyzh.easymysql.event.TreeChildFilterEvent;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.easymysql.trees.group.DBGroupTreeItem;
import cn.oyzh.easymysql.trees.root.DBRootTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * db树
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class DBTreeView extends RichTreeView implements FXEventListener {

    /**
     * 搜索中标志位
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private volatile boolean searching;

    @Override
    public DBTreeItemFilter itemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null) {
            this.itemFilter = SpringUtil.getBean(DBTreeItemFilter.class);
        }
        return (DBTreeItemFilter) this.itemFilter;
    }

    public DBTreeView() {
        this.dragContent = "db_tree_drag";
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new DBTreeCell());
        super.root(new DBRootTreeItem(this));
        this.root().extend();
    }

    @Override
    public DBRootTreeItem root() {
        return (DBRootTreeItem) this.getRoot();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (DBConnectTreeItem treeItem : this.root().getConnectedItems()) {
            ThreadUtil.startVirtual(treeItem::closeConnect);
        }
    }

    /**
     * 搜索开始事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onSearchStart(DBSearchStartEvent event) {
        this.searching = true;
        this.filter();
    }

    /**
     * 搜索结束事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onSearchFinish(DBSearchFinishEvent event) {
        this.searching = false;
        this.filter();
    }

    /**
     * 树节点过滤事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onTreeChildFilter(TreeChildFilterEvent event) {
        this.filter();
    }

    /**
     * 连接修改事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onInfoUpdate(DBInfoUpdatedEvent event) {
        f1:
        for (TreeItem<?> item : this.root().getRealChildren()) {
            if (item instanceof DBConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == event.data()) {
                    connectTreeItem.value(event.data());
                    break;
                }
            } else if (item instanceof DBGroupTreeItem groupTreeItem) {
                for (DBConnectTreeItem connectTreeItem : groupTreeItem.getConnectItems()) {
                    if (connectTreeItem.value() == event.data()) {
                        connectTreeItem.value(event.data());
                        break f1;
                    }
                }
            }
        }
    }

    /**
     * 添加连接事件
     *
     * @param event 事件
     */
    @Subscribe
    private void addConnect(DBAddConnectEvent event) {
        StageManager.showStage(MysqlInfoAddController.class, this.window());
    }

    /**
     * 添加分组事件
     *
     * @param event 事件
     */
    @Subscribe
    public void addGroup(DBAddGroupEvent event) {
        this.root().addGroup();
    }

    /**
     * 连接新增事件
     *
     * @param event 事件
     */
    @Subscribe
    private void infoAdded(DBInfoAddedEvent event) {
        this.root().addConnect(event.data());
    }

    /**
     * 连接变更事件
     *
     * @param event 事件
     */
    @Subscribe
    private void infoUpdated(DBInfoUpdatedEvent event) {
        this.root().infoUpdate(event.data());
    }
}

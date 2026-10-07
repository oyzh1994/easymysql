package cn.oyzh.easymysql.trees.query;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.store.MysqlQueryStore;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItemFilter;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * db树查询类型节点
 *
 * @author oyzh
 * @since 2024/01/31
 */
public class MysqlQueriesTreeItem extends DBTreeItem<MysqlQueriesTreeItemValue> {

    /**
     * 构造查询类型树节点
     *
     * @param treeView 树视图
     */
    public MysqlQueriesTreeItem(RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.setValue(new MysqlQueriesTreeItemValue(this));
    }

    @Override
    public MysqlDatabaseTreeItem parent() {
        return (MysqlDatabaseTreeItem) super.parent();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addQuery = MenuItemHelper.addQuery( this::addQuery);
        items.add(addQuery);
        FXMenuItem reload = MenuItemHelper.refreshData( this::reloadChild);
        items.add(reload);
        return items;
    }

    /**
     * 新增查询
     */
    private void addQuery() {
        MysqlEventUtil.queryAdd(this.parent());
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        this.setLoaded(true);
                        this.setLoading(true);
                        List<MysqlQuery> dbQueries = MysqlQueryStore.INSTANCE.list(this.info().getId(), this.dbName());
                        List<TreeItem<?>> list = new ArrayList<>();
                        for (MysqlQuery query : dbQueries) {
                            list.add(new MysqlQueryTreeItem(query, this.getTreeView()));
                        }
                        this.setChild(list);
                    })
                    .onFinish(() -> this.setLoading(false))
                    .onSuccess(this::expend)
                    .onError(ex -> {
                        this.setLoaded(false);
                        MessageBox.exception(ex);
                    })
                    .build();
            this.startWaiting(task);
        }
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    /**
     * 添加查询子节点
     *
     * @param query 查询
     */
    public void addChild(MysqlQuery query) {
        this.addChild(new MysqlQueryTreeItem(query, this.getTreeView()));
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.parent().dbName();
    }

    /**
     * 获取数据库客户端
     *
     * @return 数据库客户端
     */
    public MysqlClient client() {
        return this.parent().client();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public MysqlConnect info() {
        return this.parent().info();
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    //@Override
    //public synchronized void doFilter(RichTreeItemFilter itemFilter) {
    //    super.doFilter(itemFilter);
    //    this.refresh();
    //}

    /**
     * 获取查询数量
     *
     * @return 查询数量
     */
    public Integer querySize() {
        List<MysqlQuery> dbQueries = MysqlQueryStore.INSTANCE.list(this.info().getId(), this.dbName());
        return dbQueries == null ? 0 : dbQueries.size();
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public MysqlConnect dbConnect() {
        return this.parent().dbConnect();
    }

    /**
     * 添加查询
     *
     * @param query 查询
     */
    public void addQuery(MysqlQuery query) {
        this.addChild(new MysqlQueryTreeItem(query, this.getTreeView()));
        this.sortChild(this.isSortAsc());
    }
}

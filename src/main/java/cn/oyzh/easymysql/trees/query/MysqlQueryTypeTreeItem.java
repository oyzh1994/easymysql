package cn.oyzh.easymysql.trees.query;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.store.DBQueryStore;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * db树查询类型节点
 *
 * @author oyzh
 * @since 2024/01/31
 */
public class MysqlQueryTypeTreeItem extends DBTreeItem<MysqlQueryTypeTreeItemValue> {

    /**
     * 值
     */
    @Getter
    @Accessors(fluent = true, chain = false)
    private final String value;

    /**
     * 子节点加载标志位
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private boolean nodeLoaded;

    /**
     * 父节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final MysqlDatabaseTreeItem dbItem;

    public MysqlQueryTypeTreeItem(MysqlDatabaseTreeItem dbItem) {
        super(dbItem.getTreeView());
        super.setFilterable(true);
        this.dbItem = dbItem;
        this.value = "查询";
        this.setValue(new MysqlQueryTypeTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.loadChild();
            this.flushLocal();
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addQuery = MenuItemHelper.addQuery("12", this::addQuery);
        FXMenuItem reload = MenuItemHelper.refreshData("12", this::reloadChild);
        items.add(reload);
        items.add(addQuery);
        return items;
    }

    private void addQuery() {
        MysqlEventUtil.queryAdd(this.dbItem);
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    /**
     * 加载子节点
     */
    public void loadChild() {
        if (!this.isWaiting() && (!this.nodeLoaded)) {
            this.nodeLoaded = true;
            this._loadChild();
            this.extend();
        }
    }

    @Override
    public void reloadChild() {
        this.nodeLoaded = false;
        this._loadChild();
    }

    public void addChild(@NonNull MysqlQuery query) {
        this.addChild(new MysqlQueryTreeItem(query, this));
    }

    /**
     * 加载子节点实际业务
     */
    private void _loadChild() {
        List<MysqlQuery> dbQueries = DBQueryStore.INSTANCE.list(this.dbItem.info().getId(), this.dbItem.dbName());
        List<TreeItem<?>> list = new ArrayList<>();
        for (MysqlQuery query : dbQueries) {
            list.add(new MysqlQueryTreeItem(query, this));
        }
        this.setChild(list);
    }

    public String dbName() {
        return dbItem.dbName();
    }

    public DBClient client() {
        return dbItem.client();
    }

    /**
     * 刷新值
     */
    private void flushValue() {
        this.getValue().flushGraphicColor();
        this.getValue().flushNum();
    }

    public MysqlInfo info() {
        return dbItem.info();
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.loadChild();
    }

    @Override
    public synchronized void doFilter(RichTreeItemFilter itemFilter) {
        super.doFilter(itemFilter);
        this.flushValue();
    }

    public Integer querySize() {
        List<MysqlQuery> dbQueries = DBQueryStore.INSTANCE.list(this.dbItem.info().getId(), this.dbItem.dbName());
        return dbQueries == null ? 0 : dbQueries.size();
    }


}

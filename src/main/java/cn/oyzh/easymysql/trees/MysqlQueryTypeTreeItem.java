package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.domain.DBQuery;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.store.DBQueryStore;
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
public class MysqlQueryTypeTreeItem extends DBTreeItem<MysqlQueryTypeTreeItem.MysqlQueryTypeTreeItemValue> {

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

    public void addChild(@NonNull DBQuery query) {
        this.addChild(new MysqlQueryTreeItem(query, this));
    }

    /**
     * 加载子节点实际业务
     */
    private void _loadChild() {
        List<DBQuery> dbQueries = DBQueryStore.INSTANCE.list(this.dbItem.info().getId(), this.dbItem.dbName());
        List<TreeItem<?>> list = new ArrayList<>();
        for (DBQuery query : dbQueries) {
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

    public DBInfo info() {
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
        List<DBQuery> dbQueries = DBQueryStore.INSTANCE.list(this.dbItem.info().getId(), this.dbItem.dbName());
        return dbQueries == null ? 0 : dbQueries.size();
    }

    /**
     * db树表类型值
     *
     * @author oyzh
     * @since 2023/12/08
     */
    public static class MysqlQueryTypeTreeItemValue extends DBTreeItemValue {

        private final MysqlQueryTypeTreeItem item;

        public MysqlQueryTypeTreeItemValue(MysqlQueryTypeTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.name(item.value());
        }

        @Override
        public void flushGraphic() {
            if (this.graphic() == null) {
                SVGGlyph glyph = new SVGGlyph("/font/query2.svg", 12);
                glyph.disableTheme();
                this.graphic(glyph);
            }
        }

        @Override
        public void flushGraphicColor() {
            SVGGlyph glyph = (SVGGlyph) this.graphic();
            if (this.item.isChildEmpty()) {
                if (ThemeManager.isDarkMode()) {
                    glyph.setColor(Color.WHITE);
                } else {
                    glyph.setColor(Color.BLACK);
                }
            } else {
                glyph.setColor(Color.GREEN);
            }
        }

        /**
         * 刷新节点数量
         */
        public void flushNum() {
            try {
                Integer size = this.item.querySize();
                // 寻找组件
                FXText text = (FXText) this.lookup("#num");
                if (size == null) {
                    this.removeChild(text);
                } else {
                    if (text == null) {
                        text = new FXText();
                        this.addChild(text);
                        text.setId("num");
                        text.setFill(Color.valueOf("#228B22"));
                        HBox.setMargin(text, new Insets(0, 0, 0, 3));
                    }
                    text.setTextExt("(" + size + ")");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

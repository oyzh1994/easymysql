package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.controller.data.DBDataExportController;
import cn.oyzh.easymysql.controller.data.DBDataImportController;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * db树表类型节点
 *
 * @author oyzh
 * @since 2023/12/08
 */
public class MysqlTableTypeTreeItem extends DBTreeItem<MysqlTableTypeTreeItem.MysqlTableTypeTreeItemValue> {

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

    public MysqlTableTypeTreeItem(MysqlDatabaseTreeItem dbItem) {
        super(dbItem.getTreeView());
        super.setFilterable(true);
        this.dbItem = dbItem;
        this.value = I18nHelper.table();
        this.setValue(new MysqlTableTypeTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.loadChild();
            this.flushLocal();
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem reload = MenuItemHelper.reloadData("12", this::reloadChild);
        FXMenuItem add = MenuItemHelper.addTable("12", this::addTable);
        FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
        FXMenuItem importData = MenuItemHelper.importData("12", this::importData);
        items.add(add);
        items.add(reload);
        items.add(exportData);
        items.add(importData);
        return items;
    }

    /**
     * 导出数据
     */
    private void exportData() {
        StageAdapter fxView = StageManager.parseStage(DBDataExportController.class, this.window());
        fxView.setProp("dumpType", 2);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    /**
     * 导入数据
     */
    private void importData() {
        StageAdapter fxView = StageManager.parseStage(DBDataImportController.class, this.window());
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    private void addTable() {
        DBTable dbTable = new DBTable();
        dbTable.setDbName(this.dbName());
        MysqlEventUtil.designTable(dbTable, this.dbItem());
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

    /**
     * 加载子节点实际业务
     */
    private void _loadChild() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    List<DBTable> tables = this.client().tables(this.dbName());
                    // 无数据直接更新列表
                    if (this.isChildEmpty()) {
                        List<TreeItem<?>> list = new ArrayList<>();
                        for (DBTable table : tables) {
                            list.add(new MysqlTableTreeItem(table, this));
                        }
                        this.setChild(list);
                    } else {// 有数据则执行删除、新增、更新操作
                        ObservableList children = this.getRichChildren();
                        ObservableList<MysqlTableTreeItem> list = children;
                        List<MysqlTableTreeItem> delList = new ArrayList<>();
                        List<MysqlTableTreeItem> addList = new ArrayList<>();
                        // 删除
                        for (MysqlTableTreeItem item : list) {
                            if (tables.parallelStream().noneMatch(f -> f.compare(item.value()))) {
                                delList.add(item);
                            }
                        }
                        // 新增
                        for (DBTable table : tables) {
                            if (list.parallelStream().noneMatch(item -> table.compare(item.value()))) {
                                addList.add(new MysqlTableTreeItem(table, this));
                            }
                        }
                        // 更新
                        for (MysqlTableTreeItem item : list) {
                            if (!addList.contains(item) && !delList.contains(item)) {
                                tables.parallelStream().filter(f -> f.compare(item.value())).findFirst().ifPresent(f -> item.value().copy(f));
                            }
                        }
                        list.removeAll(delList);
                        list.addAll(addList);
                    }
                })
                .onError(ex -> {
                    this.nodeLoaded = false;
                    MessageBox.exception(ex);
                })
                .onSuccess(this::flushValue)
                .onFinish(this::stopWaiting)
                .build();
        // 执行业务
        this.startWaiting(task);
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

    public Integer tableSize() {
        return this.dbItem.tableSize();
    }

    public DBInfo info() {
        return this.dbItem.info();
    }

    public String infoName() {
        return this.dbItem.infoName();
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

    /**
     * db树表类型值
     *
     * @author oyzh
     * @since 2023/12/08
     */
    public static class MysqlTableTypeTreeItemValue extends DBTreeItemValue {

        private final MysqlTableTypeTreeItem item;

        public MysqlTableTypeTreeItemValue(MysqlTableTypeTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.name(item.value());
        }

        @Override
        public void flushGraphic() {
            if (this.graphic() == null) {
                SVGGlyph glyph = new SVGGlyph("/font/table2.svg", 12);
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
                Integer size = this.item.tableSize();
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

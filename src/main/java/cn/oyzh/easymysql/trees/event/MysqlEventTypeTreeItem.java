package cn.oyzh.easymysql.trees.event;

import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventTypeTreeItem extends DBTreeItem<MysqlEventTypeTreeItem.MysqlEventTypeTreeItemValue> {

    /**
     * 值
     */
    @Getter
    @Accessors(fluent = true, chain = false)
    private final String value;

    /**
     * 父节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final MysqlDatabaseTreeItem dbItem;

    public MysqlEventTypeTreeItem(MysqlDatabaseTreeItem dbItem) {
        super(dbItem.getTreeView());
        super.setFilterable(true);
        this.dbItem = dbItem;
        this.value = I18nHelper.event();
        this.setValue(new MysqlEventTypeTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.loadChild();
            this.flushLocal();
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem add = MenuItemHelper.addEvent("12", this::add);
        FXMenuItem reload = MenuItemHelper.refreshData("12", this::reloadChild);
        items.add(reload);
        items.add(add);
        return items;
    }

    private void add() {
        MysqlEvent event = new MysqlEvent();
        event.setDbName(this.dbName());
        MysqlEventUtil.designEvent(event, this.dbItem);
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    /**
     * 加载子节点
     */
    public void loadChild() {
        if (!this.isWaiting() && !this.loaded && !this.loading) {
            this.reloadChild();
            this.extend();
        }
    }

    @Override
    public void reloadChild() {
        if (this.loading) {
            return;
        }
        this.loaded = true;
        this.loading = true;
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    List<MysqlEvent> events = this.client().events(this.dbName());
                    // 无数据直接更新列表
                    if (this.isChildEmpty()) {
                        List<TreeItem<?>> list = new ArrayList<>();
                        for (MysqlEvent event : events) {
                            list.add(new MysqlEventTreeItem(event, this));
                        }
                        this.setChild(list);
                    } else {// 有数据则执行删除、新增、更新操作
                        ObservableList children = this.getRichChildren();
                        ObservableList<MysqlEventTreeItem> list = children;
                        List<MysqlEventTreeItem> delList = new ArrayList<>();
                        List<MysqlEventTreeItem> addList = new ArrayList<>();
                        // 删除
                        for (MysqlEventTreeItem item : list) {
                            if (events.parallelStream().noneMatch(f -> f.compare(item.value()))) {
                                delList.add(item);
                            }
                        }
                        // 新增
                        for (MysqlEvent f : events) {
                            if (list.parallelStream().noneMatch(item -> f.compare(item.value()))) {
                                addList.add(new MysqlEventTreeItem(f, this));
                            }
                        }
                        // 更新
                        for (MysqlEventTreeItem item : list) {
                            if (!addList.contains(item) && !delList.contains(item)) {
                                events.parallelStream().filter(f -> f.compare(item.value())).findFirst().ifPresent(f -> item.value().copy(f));
                            }
                        }
                        list.removeAll(delList);
                        list.addAll(addList);
                    }
                })
                .onError(ex -> {
                    this.loaded = false;
                    MessageBox.exception(ex);
                })
                .onSuccess(this::flushValue)
                .onFinish(() -> {
                    this.loading = false;
                    this.stopWaiting();
                })
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

    public MysqlConnect info() {
        return dbItem.info();
    }

    public String infoName() {
        return dbItem.infoName();
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

    public Integer eventSize() {
        return this.client().eventSize(this.dbName());
    }

    /**
     * @author oyzh
     * @since 2024/09/09
     */
    public static class MysqlEventTypeTreeItemValue extends DBTreeItemValue {

        private final MysqlEventTypeTreeItem item;

        public MysqlEventTypeTreeItemValue(MysqlEventTypeTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.name(item.value());
        }

        @Override
        public void flushGraphic() {
            SVGGlyph glyph = (SVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new SVGGlyph("/font/event.svg", "12");
                glyph.disableTheme();
                this.graphic(glyph);
            }
        }

        @Override
        public void flushGraphicColor() {
            SVGGlyph glyph = (SVGGlyph) this.graphic();
            if (this.item.isChildEmpty()) {
                glyph.setColor((Paint) null);
            } else {
                glyph.setColor(Color.GREEN);
            }
        }

        /**
         * 刷新节点数量
         */
        public void flushNum() {
            try {
                Integer size = this.item.eventSize();
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

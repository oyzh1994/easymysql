package cn.oyzh.easymysql.trees.function;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.routine.MysqlFunction;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.plus.controls.svg.FunctionSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
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
 * db树函数类型节点
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlFunctionTypeTreeItem extends DBTreeItem<MysqlFunctionTypeTreeItemValue> {

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

    public MysqlFunctionTypeTreeItem(MysqlDatabaseTreeItem dbItem) {
        super(dbItem.getTreeView());
        super.setFilterable(true);
        this.dbItem = dbItem;
        this.value = I18nHelper.function();
        this.setValue(new MysqlFunctionTypeTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.loadChild();
            this.flushLocal();
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem add = MenuItemHelper.addFunction("12", this::add);
        FXMenuItem reload = MenuItemHelper.refreshData("12", this::reloadChild);
        items.add(add);
        items.add(reload);
        return items;
    }

    private void add() {
        MysqlFunction function = new MysqlFunction();
        function.setDbName(this.dbName());
        MysqlEventUtil.designFunction(function, this.dbItem);
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    /**
     * 加载子节点
     */
    public void loadChild() {
        if (!this.loaded && !this.loading) {
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
                    List<MysqlFunction> functions = this.client().functions(this.dbName());
                    // 无数据直接更新列表
                    if (this.isChildEmpty()) {
                        List<TreeItem<?>> list = new ArrayList<>();
                        for (MysqlFunction function : functions) {
                            list.add(new MysqlFunctionTreeItem(function, this));
                        }
                        this.setChild(list);
                    } else {// 有数据则执行删除、新增、更新操作
                        ObservableList children = this.getRichChildren();
                        ObservableList<MysqlFunctionTreeItem> list = children;
                        List<MysqlFunctionTreeItem> delList = new ArrayList<>();
                        List<MysqlFunctionTreeItem> addList = new ArrayList<>();
                        // 删除
                        for (MysqlFunctionTreeItem item : list) {
                            if (functions.parallelStream().noneMatch(f -> f.compare(item.value()))) {
                                delList.add(item);
                            }
                        }
                        // 新增
                        for (MysqlFunction f : functions) {
                            if (list.parallelStream().noneMatch(item -> f.compare(item.value()))) {
                                addList.add(new MysqlFunctionTreeItem(f, this));
                            }
                        }
                        // 更新
                        for (MysqlFunctionTreeItem item : list) {
                            if (!addList.contains(item) && !delList.contains(item)) {
                                functions.parallelStream().filter(f -> f.compare(item.value())).findFirst().ifPresent(f -> item.value().copy(f));
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
        BackgroundService.submitFXLater(() -> {
            this.getValue().flushGraphicColor();
            this.getValue().flushNum();
        });
    }

    public MysqlInfo info() {
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

    public Integer functionSize() {
        return this.client().functionSize(this.dbName(), null);
    }


}

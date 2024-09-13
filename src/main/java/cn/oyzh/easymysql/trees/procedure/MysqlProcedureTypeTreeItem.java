package cn.oyzh.easymysql.trees.procedure;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.procedure.MysqlProcedure;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * db树过程类型节点
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlProcedureTypeTreeItem extends DBTreeItem<MysqlProcedureTypeTreeItemValue> {

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

    public MysqlProcedureTypeTreeItem(MysqlDatabaseTreeItem dbItem) {
        super(dbItem.getTreeView());
        super.setFilterable(true);
        this.dbItem = dbItem;
        this.value = I18nHelper.procedure();
        this.setValue(new MysqlProcedureTypeTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.loadChild();
            this.flushLocal();
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem add = MenuItemHelper.addProcedure("12", this::add);
        FXMenuItem reload = MenuItemHelper.refreshData("12", this::reloadChild);
        items.add(reload);
        items.add(add);
        return items;
    }

    private void add() {
        MysqlProcedure procedure = new MysqlProcedure();
        procedure.setDbName(this.dbName());
        MysqlEventUtil.designProcedure(procedure, this.dbItem);
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
                    List<MysqlProcedure> procedures = this.client().procedures(this.dbName());
                    // 无数据直接更新列表
                    if (this.isChildEmpty()) {
                        List<TreeItem<?>> list = new ArrayList<>();
                        for (MysqlProcedure procedure : procedures) {
                            list.add(new MysqlProcedureTreeItem(procedure, this));
                        }
                        this.setChild(list);
                    } else {// 有数据则执行删除、新增、更新操作
                        ObservableList children = this.getRichChildren();
                        ObservableList<MysqlProcedureTreeItem> list = children;
                        List<MysqlProcedureTreeItem> delList = new ArrayList<>();
                        List<MysqlProcedureTreeItem> addList = new ArrayList<>();
                        // 删除
                        for (MysqlProcedureTreeItem item : list) {
                            if (procedures.parallelStream().noneMatch(f -> f.compare(item.value()))) {
                                delList.add(item);
                            }
                        }
                        // 新增
                        for (MysqlProcedure f : procedures) {
                            if (list.parallelStream().noneMatch(item -> f.compare(item.value()))) {
                                addList.add(new MysqlProcedureTreeItem(f, this));
                            }
                        }
                        // 更新
                        for (MysqlProcedureTreeItem item : list) {
                            if (!addList.contains(item) && !delList.contains(item)) {
                                procedures.parallelStream().filter(f -> f.compare(item.value())).findFirst().ifPresent(f -> item.value().copy(f));
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

    public Integer procedureSize() {
        return this.client().procedureSize(this.dbName(), null);
    }


}

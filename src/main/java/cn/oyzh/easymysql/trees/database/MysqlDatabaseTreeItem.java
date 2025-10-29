package cn.oyzh.easymysql.trees.database;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easymysql.controller.data.MysqlDataDumpController;
import cn.oyzh.easymysql.controller.data.MysqlRunSqlFileController;
import cn.oyzh.easymysql.controller.database.MysqlDatabaseInfoController;
import cn.oyzh.easymysql.controller.database.MysqlDatabaseUpdateController;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.check.MysqlChecks;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.db.function.MysqlFunction;
import cn.oyzh.easymysql.db.index.MysqlIndexes;
import cn.oyzh.easymysql.db.procedure.MysqlProcedure;
import cn.oyzh.easymysql.db.query.MysqlExecuteResult;
import cn.oyzh.easymysql.db.query.MysqlExplainResult;
import cn.oyzh.easymysql.db.query.MysqlQueryResults;
import cn.oyzh.easymysql.db.record.MysqlDeleteRecordParam;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.record.MysqlSelectRecordParam;
import cn.oyzh.easymysql.db.table.MysqlAlertTableParam;
import cn.oyzh.easymysql.db.table.MysqlCreateTableParam;
import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.db.table.MysqlSelectTableParam;
import cn.oyzh.easymysql.db.trigger.MysqlTriggers;
import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.easymysql.trees.event.MysqlEventTreeItem;
import cn.oyzh.easymysql.trees.event.MysqlEventsTreeItem;
import cn.oyzh.easymysql.trees.function.MysqlFunctionTreeItem;
import cn.oyzh.easymysql.trees.function.MysqlFunctionsTreeItem;
import cn.oyzh.easymysql.trees.procedure.MysqlProcedureTreeItem;
import cn.oyzh.easymysql.trees.procedure.MysqlProceduresTreeItem;
import cn.oyzh.easymysql.trees.query.MysqlQueriesTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTablesTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewsTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemFilter;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * db树database节点
 *
 * @author oyzh
 * @since 2023/12/12
 */
public class MysqlDatabaseTreeItem extends DBTreeItem<MysqlDatabaseTreeItemValue> {

    /**
     * 当前值
     */
    private final DBDatabase value;

    public DBDatabase value() {
        return value;
    }

    public MysqlDatabaseTreeItem(DBDatabase database, RichTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        super.setFilterable(true);
        this.value = database;
        this.setValue(new MysqlDatabaseTreeItemValue(this));
    }

    @Override
    public DBConnectTreeItem parent() {
        return (DBConnectTreeItem) super.parent();
    }

    public String dbName() {
        return this.value.getName();
    }

    public String userName() {
        return this.info().getUser();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (!this.isChildEmpty()) {
            FXMenuItem closeDB = MenuItemHelper.closeDatabase("10", this::closeDB);
            items.add(closeDB);
        }
        FXMenuItem editDB = MenuItemHelper.editDatabase("11", this::editDB);
        FXMenuItem dropDB = MenuItemHelper.deleteDatabase("12", this::dropDB);
        FXMenuItem dumpData = MenuItemHelper.dumpData("12", this::dump);
        FXMenuItem runSqlFile = MenuItemHelper.runSqlFile("12", this::runSqlFile);
        FXMenuItem dbInfo = MenuItemHelper.databaseInfo("12", this::dbInfo);
        items.add(editDB);
        items.add(dropDB);
        items.add(dumpData);
        items.add(runSqlFile);
        items.add(dbInfo);
        return items;
    }

    /**
     * 运行sql文件
     */
    private void runSqlFile() {
        StageAdapter fxView = StageManager.parseStage(MysqlRunSqlFileController.class, this.window());
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    /**
     * 转储
     */
    private void dump() {
        StageAdapter fxView = StageManager.parseStage(MysqlDataDumpController.class, this.window());
        fxView.setProp("dumpType", 1);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    private void dbInfo() {
        StageAdapter fxView = StageManager.parseStage(MysqlDatabaseInfoController.class, this.window());
        fxView.setProp("dbItem", this);
        fxView.display();
    }

    private void dropDB() {
        if (MessageBox.confirm("确定删除库" + this.dbName() + "？")) {
            if (this.parent().dropDatabase(this.dbName())) {
                this.remove();
                MysqlEventUtil.databaseDropped(this);
            } else {
                MessageBox.warn("删除库失败！");
            }
        }
    }

    /**
     * 编辑数据库
     */
    public void editDB() {
        StageAdapter fxView = StageManager.parseStage(MysqlDatabaseUpdateController.class, this.window());
        fxView.setProp("database", this.value);
        fxView.setProp("connectItem", this.parent());
        fxView.display();
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        this.clearChild();
        this.collapse();
        this.setLoaded(false);
        MysqlEventUtil.databaseClosed(this);
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            this.setLoaded(true);
            this.setLoading(true);
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        List<TreeItem<?>> typeItems = new ArrayList<>();
                        typeItems.add(new MysqlTablesTreeItem(this.getTreeView()));
                        typeItems.add(new MysqlViewsTreeItem(this.getTreeView()));
                        typeItems.add(new MysqlFunctionsTreeItem(this.getTreeView()));
                        typeItems.add(new MysqlProceduresTreeItem(this.getTreeView()));
                        typeItems.add(new MysqlEventsTreeItem(this.getTreeView()));
                        typeItems.add(new MysqlQueriesTreeItem(this.getTreeView()));
                        super.setChild(typeItems);
                    })
                    .onSuccess(this::expend)
                    .onError(ex -> {
                        this.setLoaded(false);
                        MessageBox.error(ex.getMessage());
                    })
                    .onFinish(() -> this.setLoading(false))
                    .build();
            super.startWaiting(task);
        }

    }

    /**
     * 获取表类型子节点
     *
     * @return 表类型子节点
     */
    public MysqlTablesTreeItem getTableTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MysqlTablesTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取表节点列表
     *
     * @return 表节点列表
     */
    public List<MysqlTableTreeItem> getTableChild() {
        List<MysqlTableTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getTableTypeChild().richChildren()) {
            if (child instanceof MysqlTableTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    /**
     * 获取查询类型子节点
     *
     * @return 查询类型子节点
     */
    public MysqlQueriesTreeItem getQueryTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MysqlQueriesTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取函数类型子节点
     *
     * @return 函数类型子节点
     */
    public MysqlFunctionsTreeItem getFunctionTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MysqlFunctionsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取函数节点列表
     *
     * @return 过程节点列表
     */
    public List<MysqlFunctionTreeItem> getFunctionChild() {
        List<MysqlFunctionTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getFunctionTypeChild().richChildren()) {
            if (child instanceof MysqlFunctionTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    /**
     * 获取过程类型子节点
     *
     * @return 过程类型子节点
     */
    public MysqlProceduresTreeItem getProcedureTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MysqlProceduresTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取过程节点列表
     *
     * @return 过程节点列表
     */
    public List<MysqlProcedureTreeItem> getProcedureChild() {
        List<MysqlProcedureTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getProcedureTypeChild().richChildren()) {
            if (child instanceof MysqlProcedureTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    public MysqlEventsTreeItem getEventTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MysqlEventsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    public List<MysqlEventTreeItem> getEventChild() {
        List<MysqlEventTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getEventTypeChild().richChildren()) {
            if (child instanceof MysqlEventTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    /**
     * 获取查询类型子节点
     *
     * @return 查询类型子节点
     */
    public MysqlViewsTreeItem getViewTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MysqlViewsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取视图节点列表
     *
     * @return 视图节点列表
     */
    public List<MysqlViewTreeItem> getViewChild() {
        List<MysqlViewTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getViewTypeChild().richChildren()) {
            if (child instanceof MysqlViewTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public DBClient client() {
        return this.parent().getClient();
    }

    /**
     * 获取db信息
     *
     * @return db信息
     */
    public MysqlConnect info() {
        return this.parent().value();
    }

    public Integer tableSize() {
        try {
            return this.client().tableSize(this.dbName());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
        return 0;
    }

    public Integer viewSize() {
        return this.client().viewSize(this.dbName());
    }

    public String infoName() {
        return this.info().getName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    public void createTable(MysqlTable table, MysqlColumns columns, MysqlIndexes indexes, MysqlForeignKeys foreignKeys, MysqlTriggers triggers, MysqlChecks checks) {
        MysqlCreateTableParam param = new MysqlCreateTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        this.client().createTable(param);
    }

    public void createTable(MysqlCreateTableParam param) {
        this.client().createTable(param);
    }

    public MysqlCreateTableParam createTableParam(MysqlTable table, MysqlColumns columns, MysqlIndexes indexes, MysqlForeignKeys foreignKeys, MysqlTriggers triggers, MysqlChecks checks) {
        MysqlCreateTableParam param = new MysqlCreateTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        return param;
    }

    public void alterTable(MysqlTable table, MysqlColumns columns, MysqlIndexes indexes, MysqlForeignKeys foreignKeys, MysqlTriggers triggers, MysqlChecks checks) {
        MysqlAlertTableParam param = new MysqlAlertTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        param.setExistPrimaryKey(this.existPrimaryKey(table.getName()));
        this.client().alertTable(param);
    }

    public void alterTable(MysqlAlertTableParam param) {
        this.client().alertTable(param);
    }

    public MysqlAlertTableParam alterTableParam(MysqlTable table, MysqlColumns columns, MysqlIndexes indexes, MysqlForeignKeys foreignKeys, MysqlTriggers triggers, MysqlChecks checks) {
        MysqlAlertTableParam param = new MysqlAlertTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        param.setExistPrimaryKey(this.existPrimaryKey(table.getName()));
        return param;
    }

    public boolean existPrimaryKey(String tableName) {
        return this.client().existPrimaryKey(this.dbName(), tableName);
    }

    public MysqlTable selectFullTable(String tableName) {
        MysqlSelectTableParam param = new MysqlSelectTableParam();
        param.setDbName(this.dbName());
        param.setTableName(tableName);
        return this.client().selectFullTable(param);
    }

    public boolean existTable(String tableName) {
        return this.client().existTable(this.dbName(), tableName);
    }

    public void renameTable(String oldTableName, String newTableName) {
        this.client().renameTable(this.dbName(), oldTableName, newTableName);
    }

    public void clearTable(String tableName) {
        this.client().clearTable(this.dbName(), tableName);
    }

    public void truncateTable(String tableName) {
        this.client().truncateTable(this.dbName(), tableName);
    }

    public void dropTable(String tableName) {
        this.client().dropTable(this.dbName(), tableName);
    }

    public MysqlQueryResults<MysqlExecuteResult> executeSql(String sql) {
        return this.client().executeSql(this.dbName(), sql);
    }

    public MysqlExecuteResult executeSingleSql(String sql) {
        return this.client().executeSingleSql(this.dbName(), sql);
    }

    public MysqlQueryResults<MysqlExplainResult> explainSql(String sql) {
        return this.client().explainSql(this.dbName(), sql);
    }

    public void createFunction(MysqlFunction function) {
        this.client().createFunction(this.dbName(), function);
    }

    public void dropFunction(MysqlFunction function) {
        this.client().dropFunction(this.dbName(), function);
    }

    public MysqlProcedure selectProcedure(String procedureName) {
        return this.client().selectProcedure(this.dbName(), procedureName);
    }

    public void alertProcedure(MysqlProcedure procedure) {
        this.client().alertProcedure(this.dbName(), procedure);
    }

    public void createProcedure(MysqlProcedure procedure) {
        this.client().createProcedure(this.dbName(), procedure);
    }

    public void dropProcedure(MysqlProcedure procedure) {
        this.client().dropProcedure(this.dbName(), procedure);
    }

    public MysqlFunction selectFunction(String functionName) {
        return this.client().selectFunction(this.dbName(), functionName);
    }

    public void alertFunction(MysqlFunction function) {
        this.client().alertFunction(this.dbName(), function);
    }

    public MysqlView selectView(String viewName) {
        return this.client().view(this.dbName(), viewName);
    }

    public MysqlTable selectTable(String tableName) {
        return this.client().selectTable(this.dbName(), tableName);
    }

    public void createView(MysqlView view) {
        this.client().createView(this.dbName(), view);
    }

    public void alertView(MysqlView view) {
        this.client().alertView(this.dbName(), view);
    }

    public void dropView(MysqlView view) {
        this.client().dropView(this.dbName(), view);
    }

    public boolean existView(String viewName) {
        return this.client().existView(this.dbName(), viewName);
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    @Override
    public synchronized void doFilter(RichTreeItemFilter itemFilter) {
        super.doFilter(itemFilter);
        this.refresh();
    }

    public MysqlEvent selectEvent(String eventName) {
        return this.client().selectEvent(this.dbName(), eventName);
    }

    public void alertEvent(MysqlEvent event) {
        this.client().alertEvent(this.dbName(), event);
    }

    public void createEvent(MysqlEvent event) {
        this.client().createEvent(this.dbName(), event);
    }

    public void dropEvent(MysqlEvent event) {
        this.client().dropEvent(this.dbName(), event);
    }

    public boolean isSupportCheckFeature() {
        return this.client().isSupportCheckFeature();
    }

    public DBDialect dialect() {
        return this.client().dialect();
    }

    public int deleteRecord(MysqlDeleteRecordParam param) {
        return this.client().deleteRecord(param);
    }

    public MysqlChecks checks(String tableName) {
        return this.client().checks(this.dbName(), tableName);
    }

    public MysqlTriggers triggers(String tableName) {
        return this.client().triggers(this.dbName(), tableName);
    }

    public MysqlColumns columns(String tableName) {
        MysqlSelectColumnParam param = new MysqlSelectColumnParam();
        param.setDbName(this.dbName());
        param.setTableName(tableName);
        return this.client().selectColumns(param);
    }

    public MysqlIndexes indexes(String tableName) {
        return this.client().indexes(this.dbName(), tableName);
    }

    public MysqlForeignKeys foreignKeys(String tableName) {
        return this.client().foreignKeys(this.dbName(), tableName);
    }

    public MysqlRecord selectRecord(MysqlSelectRecordParam param) {
        return this.client().selectRecord(param);
    }

    public MysqlConnect dbConnect() {
        return this.client().getDbConnect();
    }

    public void cloneTable(String tableName, boolean includeRecord) {
        this.client().cloneTable(this.dbName(), tableName, includeRecord);
    }
}

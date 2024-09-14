package cn.oyzh.easymysql.trees.database;

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
import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.db.table.MysqlTableAlertParam;
import cn.oyzh.easymysql.db.table.MysqlTableCreateParam;
import cn.oyzh.easymysql.db.table.MysqlTableSelectParam;
import cn.oyzh.easymysql.db.trigger.MysqlTriggers;
import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.easymysql.trees.event.MysqlEventTreeItem;
import cn.oyzh.easymysql.trees.event.MysqlEventTypeTreeItem;
import cn.oyzh.easymysql.trees.function.MysqlFunctionTreeItem;
import cn.oyzh.easymysql.trees.function.MysqlFunctionTypeTreeItem;
import cn.oyzh.easymysql.trees.procedure.MysqlProcedureTreeItem;
import cn.oyzh.easymysql.trees.procedure.MysqlProcedureTypeTreeItem;
import cn.oyzh.easymysql.trees.query.MysqlQueryTypeTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTypeTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTypeTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

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
    @Getter
    @Accessors(chain = true, fluent = true)
    private final DBDatabase value;

    /**
     * 父节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected DBConnectTreeItem parent;

    public MysqlDatabaseTreeItem(DBDatabase database, DBConnectTreeItem parent) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.value = database;
        this.setValue(new MysqlDatabaseTreeItemValue(this));
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
            if (this.parent.dropDatabase(this.dbName())) {
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
        fxView.setProp("connectItem", this.parent);
        fxView.display();
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        this.clearChild();
        this.collapse();
        MysqlEventUtil.databaseClosed(this);
    }

    /**
     * 初始化类型
     */
    private void initTypes() {
        List<TreeItem<?>> typeItems = new ArrayList<>();
        typeItems.add(new MysqlTableTypeTreeItem(this));
        typeItems.add(new MysqlViewTypeTreeItem(this));
        typeItems.add(new MysqlFunctionTypeTreeItem(this));
        typeItems.add(new MysqlProcedureTypeTreeItem(this));
        typeItems.add(new MysqlEventTypeTreeItem(this));
        typeItems.add(new MysqlQueryTypeTreeItem(this));
        super.setChild(typeItems);
    }

    /**
     * 获取表类型子节点
     *
     * @return 表类型子节点
     */
    public MysqlTableTypeTreeItem getTableTypeChild() {
        for (RichTreeItem<?> child : this.getRichChildren()) {
            if (child instanceof MysqlTableTypeTreeItem treeItem) {
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
        for (RichTreeItem<?> child : this.getTableTypeChild().getRichChildren()) {
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
    public MysqlQueryTypeTreeItem getQueryTypeChild() {
        for (RichTreeItem<?> child : this.getRichChildren()) {
            if (child instanceof MysqlQueryTypeTreeItem treeItem) {
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
    public MysqlFunctionTypeTreeItem getFunctionTypeChild() {
        for (RichTreeItem<?> child : this.getRichChildren()) {
            if (child instanceof MysqlFunctionTypeTreeItem treeItem) {
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
        for (RichTreeItem<?> child : this.getFunctionTypeChild().getRichChildren()) {
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
    public MysqlProcedureTypeTreeItem getProcedureTypeChild() {
        for (RichTreeItem<?> child : this.getRichChildren()) {
            if (child instanceof MysqlProcedureTypeTreeItem treeItem) {
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
        for (RichTreeItem<?> child : this.getProcedureTypeChild().getRichChildren()) {
            if (child instanceof MysqlProcedureTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    public MysqlEventTypeTreeItem getEventTypeChild() {
        for (RichTreeItem<?> child : this.getRichChildren()) {
            if (child instanceof MysqlEventTypeTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    public List<MysqlEventTreeItem> getEventChild() {
        List<MysqlEventTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getEventTypeChild().getRichChildren()) {
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
    public MysqlViewTypeTreeItem getViewTypeChild() {
        for (RichTreeItem<?> child : this.getRichChildren()) {
            if (child instanceof MysqlViewTypeTreeItem treeItem) {
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
        for (RichTreeItem<?> child : this.getViewTypeChild().getRichChildren()) {
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
        return this.parent.client();
    }

    /**
     * 获取db信息
     *
     * @return db信息
     */
    public MysqlInfo info() {
        return this.parent.value();
    }

    public Integer tableSize() {
        return this.client().tableSize(this.dbName());
    }

    public Integer viewSize() {
        return this.client().viewSize(this.dbName());
    }

    public String infoName() {
        return this.info().getName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (this.isChildEmpty()) {
            this.initTypes();
            this.extend();
        }
    }

    public void createTable(MysqlTable table, MysqlColumns columns, MysqlIndexes indexes, MysqlForeignKeys foreignKeys, MysqlTriggers triggers, MysqlChecks checks) {
        MysqlTableCreateParam param = new MysqlTableCreateParam();
        param.table(table);
        param.checks(checks);
        param.columns(columns);
        param.indexes(indexes);
        param.triggers(triggers);
        param.foreignKeys(foreignKeys);
        this.client().createTable(param);
    }

    public void alterTable(MysqlTable table, MysqlColumns columns, MysqlIndexes indexes, MysqlForeignKeys foreignKeys, MysqlTriggers triggers, MysqlChecks checks) {
        MysqlTableAlertParam param = new MysqlTableAlertParam();
        param.table(table);
        param.checks(checks);
        param.columns(columns);
        param.indexes(indexes);
        param.triggers(triggers);
        param.foreignKeys(foreignKeys);
        param.existPrimaryKey(this.existPrimaryKey(table.getName()));
        this.client().alertTable(param);
    }

    public boolean existPrimaryKey(String tableName) {
        return this.client().existPrimaryKey(this.dbName(), tableName);
    }

    public MysqlTable selectFullTable(String tableName) {
        MysqlTableSelectParam param = new MysqlTableSelectParam();
        param.dbName(this.dbName());
        param.tableName(tableName);
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
        this.flushValue();
    }

    /**
     * 刷新值
     */
    private void flushValue() {
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
        return this.columns(tableName, false);
    }

    public MysqlColumns fullColumns(String tableName) {
        return this.columns(tableName, true);
    }

    public MysqlColumns columns(String tableName,boolean full) {
        MysqlSelectColumnParam param = new MysqlSelectColumnParam();
        param.dbName(this.dbName());
        param.tableName(tableName);
        param.full(full);
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
}

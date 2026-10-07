package cn.oyzh.easymysql.trees.database;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easymysql.controller.data.MysqlDataDumpController;
import cn.oyzh.easymysql.controller.data.MysqlRunSqlFileController;
import cn.oyzh.easymysql.controller.database.MysqlDatabaseUpdateController;
import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.check.MysqlChecks;
import cn.oyzh.easymysql.mysql.column.MysqlColumns;
import cn.oyzh.easymysql.mysql.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.mysql.event.MysqlEvent;
import cn.oyzh.easymysql.mysql.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.mysql.function.MysqlFunction;
import cn.oyzh.easymysql.mysql.index.MysqlIndexes;
import cn.oyzh.easymysql.mysql.procedure.MysqlProcedure;
import cn.oyzh.easymysql.mysql.query.MysqlExecuteResult;
import cn.oyzh.easymysql.mysql.query.MysqlExplainResult;
import cn.oyzh.easymysql.mysql.query.MysqlQueryResults;
import cn.oyzh.easymysql.mysql.record.MysqlDeleteRecordParam;
import cn.oyzh.easymysql.mysql.record.MysqlRecord;
import cn.oyzh.easymysql.mysql.record.MysqlSelectRecordParam;
import cn.oyzh.easymysql.mysql.table.MysqlAlertTableParam;
import cn.oyzh.easymysql.mysql.table.MysqlCreateTableParam;
import cn.oyzh.easymysql.mysql.table.MysqlSelectTableParam;
import cn.oyzh.easymysql.mysql.table.MysqlTable;
import cn.oyzh.easymysql.mysql.trigger.MysqlTriggers;
import cn.oyzh.easymysql.mysql.view.MysqlView;
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
import cn.oyzh.easymysql.trees.terminal.MysqlTerminalTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewsTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
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

    /**
     * 获取数据库
     *
     * @return 数据库
     */
    public DBDatabase value() {
        return value;
    }

    /**
     * 构造数据库树节点
     *
     * @param database 数据库
     * @param treeView 树视图
     */
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

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String dbName() {
        return this.value.getName();
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String userName() {
        return this.info().getUser();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (!this.isChildEmpty()) {
            FXMenuItem closeDB = MenuItemHelper.closeDatabase( this::closeDB);
            items.add(closeDB);
        }
        FXMenuItem editDB = MenuItemHelper.editDatabase( this::editDB);
        items.add(editDB);
        FXMenuItem dropDB = MenuItemHelper.deleteDatabase( this::delete);
        items.add(dropDB);
        FXMenuItem dumpData = MenuItemHelper.dumpData( this::dump);
        items.add(dumpData);
        FXMenuItem runSqlFile = MenuItemHelper.runSqlFile( this::runSqlFile);
        items.add(runSqlFile);
        // FXMenuItem dbInfo = MenuItemHelper.databaseInfo( this::dbInfo);
        // items.add(dbInfo);
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

    // private void dbInfo() {
    //     StageAdapter fxView = StageManager.parseStage(MysqlDatabaseInfoController.class, this.window());
    //     fxView.setProp("dbItem", this);
    //     fxView.display();
    // }

    @Override
    public void delete() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    if (MessageBox.confirm(I18nHelper.deleteDatabase() + "[" + this.dbName() + "]")) {
                        if (this.parent().dropDatabase(this.dbName())) {
                            MysqlEventUtil.databaseDropped(this);
                            super.remove();
                        } else {
                            MessageBox.warn(I18nHelper.operationFail());
                        }
                    }
                })
                .onSuccess(super::refresh)
                .build();
        super.startWaiting(task);
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
                        typeItems.add(new MysqlTerminalTreeItem(this.getTreeView()));
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
     * @return 函数节点列表
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

    /**
     * 获取事件类型子节点
     *
     * @return 事件类型子节点
     */
    public MysqlEventsTreeItem getEventTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MysqlEventsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取事件节点列表
     *
     * @return 事件节点列表
     */
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
     * 获取视图类型子节点
     *
     * @return 视图类型子节点
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
    public MysqlClient client() {
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

    /**
     * 获取表数量
     *
     * @return 表数量
     */
    public Integer tableSize() {
        try {
            return this.client().tableSize(this.dbName());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
        return 0;
    }

    /**
     * 获取视图数量
     *
     * @return 视图数量
     */
    public Integer viewSize() {
        return this.client().viewSize(this.dbName());
    }

    /**
     * 获取连接信息名称
     *
     * @return 连接信息名称
     */
    public String infoName() {
        return this.info().getName();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
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

    /**
     * 创建表
     *
     * @param table       表
     * @param columns     字段
     * @param indexes     索引
     * @param foreignKeys 外键
     * @param triggers    触发器
     * @param checks      检查约束
     */
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

    /**
     * 创建表
     *
     * @param param 创建表参数
     */
    public void createTable(MysqlCreateTableParam param) {
        this.client().createTable(param);
    }

    /**
     * 构造创建表参数
     *
     * @param table       表
     * @param columns     字段
     * @param indexes     索引
     * @param foreignKeys 外键
     * @param triggers    触发器
     * @param checks      检查约束
     * @return 创建表参数
     */
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

    /**
     * 修改表
     *
     * @param table       表
     * @param columns     字段
     * @param indexes     索引
     * @param foreignKeys 外键
     * @param triggers    触发器
     * @param checks      检查约束
     */
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

    /**
     * 修改表
     *
     * @param param 修改表参数
     */
    public void alterTable(MysqlAlertTableParam param) {
        this.client().alertTable(param);
    }

    /**
     * 构造修改表参数
     *
     * @param table       表
     * @param columns     字段
     * @param indexes     索引
     * @param foreignKeys 外键
     * @param triggers    触发器
     * @param checks      检查约束
     * @return 修改表参数
     */
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

    /**
     * 是否存在主键
     *
     * @param tableName 表名称
     * @return 结果
     */
    public boolean existPrimaryKey(String tableName) {
        return this.client().existPrimaryKey(this.dbName(), tableName);
    }

    /**
     * 查询完整表信息
     *
     * @param tableName 表名称
     * @return 表
     */
    public MysqlTable selectFullTable(String tableName) {
        MysqlSelectTableParam param = new MysqlSelectTableParam();
        param.setDbName(this.dbName());
        param.setTableName(tableName);
        return this.client().selectFullTable(param);
    }

    /**
     * 是否存在表
     *
     * @param tableName 表名称
     * @return 结果
     */
    @Deprecated
    public boolean existTable(String tableName) {
        return this.client().existTable(this.dbName(), tableName);
    }

    /**
     * 重命名表
     *
     * @param oldTableName 原表名称
     * @param newTableName 新表名称
     */
    public void renameTable(String oldTableName, String newTableName) {
        this.client().renameTable(this.dbName(), oldTableName, newTableName);
    }

    /**
     * 重命名事件
     *
     * @param oldEventName 事件名称
     * @param newEventName 新事件名称
     */
    public void renameEvent(String oldEventName, String newEventName) {
        this.client().renameEvent(this.dbName(), oldEventName, newEventName);
    }

    /**
     * 清空表
     *
     * @param tableName 表名称
     */
    public void clearTable(String tableName) {
        this.client().clearTable(this.dbName(), tableName);
    }

    /**
     * 截断表
     *
     * @param tableName 表名称
     */
    public void truncateTable(String tableName) {
        this.client().truncateTable(this.dbName(), tableName);
    }

    /**
     * 删除表
     *
     * @param tableName 表名称
     */
    public void dropTable(String tableName) {
        this.client().dropTable(this.dbName(), tableName);
    }

    /**
     * 执行sql
     *
     * @param sql sql语句
     * @return 执行结果
     */
    public MysqlQueryResults<MysqlExecuteResult> executeSql(String sql) {
        return this.client().executeSql(this.dbName(), sql);
    }

    /**
     * 执行单条sql
     *
     * @param sql sql语句
     * @return 执行结果
     */
    public MysqlExecuteResult executeSingleSql(String sql) {
        return this.client().executeSingleSql(this.dbName(), sql);
    }

    /**
     * 解释sql
     *
     * @param sql sql语句
     * @return 解释结果
     */
    public MysqlQueryResults<MysqlExplainResult> explainSql(String sql) {
        return this.client().explainSql(this.dbName(), sql);
    }

    /**
     * 创建函数
     *
     * @param function 函数
     */
    public void createFunction(MysqlFunction function) {
        this.client().createFunction(this.dbName(), function);
    }

    /**
     * 删除函数
     *
     * @param function 函数
     */
    public void dropFunction(MysqlFunction function) {
        this.client().dropFunction(this.dbName(), function);
    }

    /**
     * 查询过程
     *
     * @param procedureName 过程名称
     * @return 过程
     */
    public MysqlProcedure selectProcedure(String procedureName) {
        return this.client().selectProcedure(this.dbName(), procedureName);
    }

    /**
     * 修改过程
     *
     * @param procedure 过程
     */
    public void alertProcedure(MysqlProcedure procedure) {
        this.client().alertProcedure(this.dbName(), procedure);
    }

    /**
     * 创建过程
     *
     * @param procedure 过程
     */
    public void createProcedure(MysqlProcedure procedure) {
        this.client().createProcedure(this.dbName(), procedure);
    }

    /**
     * 删除过程
     *
     * @param procedure 过程
     */
    public void dropProcedure(MysqlProcedure procedure) {
        this.client().dropProcedure(this.dbName(), procedure);
    }

    /**
     * 查询函数
     *
     * @param functionName 函数名称
     * @return 函数
     */
    public MysqlFunction selectFunction(String functionName) {
        return this.client().selectFunction(this.dbName(), functionName);
    }

    /**
     * 修改函数
     *
     * @param function 函数
     */
    public void alertFunction(MysqlFunction function) {
        this.client().alertFunction(this.dbName(), function);
    }

    /**
     * 查询视图
     *
     * @param viewName 视图名称
     * @return 视图
     */
    public MysqlView selectView(String viewName) {
        return this.client().view(this.dbName(), viewName);
    }

    /**
     * 查询表
     *
     * @param tableName 表名称
     * @return 表
     */
    public MysqlTable selectTable(String tableName) {
        return this.client().selectTable(this.dbName(), tableName);
    }

    /**
     * 创建视图
     *
     * @param view 视图
     */
    public void createView(MysqlView view) {
        this.client().createView(this.dbName(), view);
    }

    /**
     * 修改视图
     *
     * @param view 视图
     */
    public void alertView(MysqlView view) {
        this.client().alertView(this.dbName(), view);
    }

    /**
     * 删除视图
     *
     * @param view 视图
     */
    public void dropView(MysqlView view) {
        this.client().dropView(this.dbName(), view);
    }

    /**
     * 是否存在视图
     *
     * @param viewName 视图名称
     * @return 结果
     */
    public boolean existView(String viewName) {
        return this.client().existView(this.dbName(), viewName);
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    //@Override
    //public synchronized void doFilter(RichTreeItemFilter itemFilter) {
    //    super.doFilter(itemFilter);
    //    this.refresh();
    //}

    /**
     * 查询事件
     *
     * @param eventName 事件名称
     * @return 事件
     */
    public MysqlEvent selectEvent(String eventName) {
        return this.client().selectEvent(this.dbName(), eventName);
    }

    /**
     * 修改事件
     *
     * @param event 事件
     */
    public void alertEvent(MysqlEvent event) {
        this.client().alertEvent(this.dbName(), event);
    }

    /**
     * 创建事件
     *
     * @param event 事件
     */
    public void createEvent(MysqlEvent event) {
        this.client().createEvent(this.dbName(), event);
    }

    /**
     * 删除事件
     *
     * @param event 事件
     */
    public void dropEvent(MysqlEvent event) {
        this.client().dropEvent(this.dbName(), event);
    }

    /**
     * 是否支持检查约束功能
     *
     * @return 结果
     */
    public boolean isSupportCheckFeature() {
        return this.client().isSupportCheckFeature();
    }

    /**
     * 获取数据库方言
     *
     * @return 数据库方言
     */
    public DBDialect dialect() {
        return this.client().dialect();
    }

    /**
     * 删除记录
     *
     * @param param 删除记录参数
     * @return 结果
     */
    public int deleteRecord(MysqlDeleteRecordParam param) {
        return this.client().deleteRecord(param);
    }

    /**
     * 获取检查约束
     *
     * @param tableName 表名称
     * @return 检查约束
     */
    public MysqlChecks checks(String tableName) {
        return this.client().checks(this.dbName(), tableName);
    }

    /**
     * 获取触发器
     *
     * @param tableName 表名称
     * @return 触发器
     */
    public MysqlTriggers triggers(String tableName) {
        return this.client().triggers(this.dbName(), tableName);
    }

    /**
     * 获取字段
     *
     * @param tableName 表名称
     * @return 字段
     */
    public MysqlColumns columns(String tableName) {
        MysqlSelectColumnParam param = new MysqlSelectColumnParam();
        param.setDbName(this.dbName());
        param.setTableName(tableName);
        return this.client().selectColumns(param);
    }

    /**
     * 获取索引
     *
     * @param tableName 表名称
     * @return 索引
     */
    public MysqlIndexes indexes(String tableName) {
        return this.client().indexes(this.dbName(), tableName);
    }

    /**
     * 获取外键
     *
     * @param tableName 表名称
     * @return 外键
     */
    public MysqlForeignKeys foreignKeys(String tableName) {
        return this.client().foreignKeys(this.dbName(), tableName);
    }

    /**
     * 查询记录
     *
     * @param param 查询记录参数
     * @return 记录
     */
    public MysqlRecord selectRecord(MysqlSelectRecordParam param) {
        return this.client().selectRecord(param);
    }

    /**
     * 获取数据库连接信息
     *
     * @return 数据库连接信息
     */
    public MysqlConnect dbConnect() {
        return this.client().getDbConnect();
    }

    /**
     * 克隆表
     *
     * @param tableName     表名称
     * @param includeRecord 是否包含记录
     * @return 结果
     */
    public String cloneTable(String tableName, boolean includeRecord) {
        return this.client().cloneTable(this.dbName(), tableName, includeRecord);
    }
}

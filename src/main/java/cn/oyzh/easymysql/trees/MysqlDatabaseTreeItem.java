package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.controller.data.DBDataDumpController;
import cn.oyzh.easymysql.controller.data.DBRunSqlFileController;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.event.DBEvent;
import cn.oyzh.easymysql.db.query.DBExecuteResult;
import cn.oyzh.easymysql.db.query.DBExplainResult;
import cn.oyzh.easymysql.db.query.DBQueryResults;
import cn.oyzh.easymysql.db.routine.DBFunction;
import cn.oyzh.easymysql.db.routine.DBProcedure;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.db.view.DBView;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.controller.database.MysqlDatabaseInfoController;
import cn.oyzh.easymysql.controller.database.MysqlDatabaseUpdateController;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
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
 * db树database节点
 *
 * @author oyzh
 * @since 2023/12/12
 */
public class MysqlDatabaseTreeItem extends DBTreeItem<MysqlDatabaseTreeItem.MysqlDatabaseTreeItemValue> {

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
        StageAdapter fxView = StageManager.parseStage(DBRunSqlFileController.class, this.window());
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    /**
     * 转储
     */
    private void dump() {
        StageAdapter fxView = StageManager.parseStage(DBDataDumpController.class, this.window());
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
    public DBInfo info() {
        return this.parent.value();
    }

    public Integer tableSize() {
        return this.client().tableSize(this.dbName(), null);
    }

    public Integer viewSize() {
        return this.client().viewSize(this.dbName(), null);
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

    public void createTable(DBTable table) {
        this.client().createTable(this.dbName(), table);
    }

    public void alterTable(DBTable table) {
        this.client().alterTable(this.dbName(), table);
    }

    public DBTable selectTable(String tableName) {
        return this.selectTable(tableName, true);
    }

    public DBTable selectTable(String tableName, boolean full) {
        return this.client().table(this.dbName(), tableName, full);
    }

    public boolean existTable(String tableName) {
        return this.client().existTable(this.dbName(), tableName);
    }

    public boolean renameTable(String oldTableName, String newTableName) {
        return this.client().renameTable(this.dbName(), oldTableName, newTableName);
    }

    public boolean clearTable(String tableName) {
        return this.client().clearTable(this.dbName(), tableName);
    }

    public void truncateTable(String tableName) {
        this.client().truncateTable(this.dbName(), null, tableName);
    }

    public boolean dropTable(String tableName) {
        return this.client().dropTable(this.dbName(), tableName);
    }

    public DBQueryResults<DBExecuteResult> executeSql(String sql) {
        return this.client().executeSql(this.dbName(), sql);
    }

    public DBExecuteResult executeSingleSql(String sql) {
        return this.client().executeSingleSql(this.dbName(), sql);
    }

    public DBQueryResults<DBExplainResult> explainSql(String sql) {
        return this.client().explainSql(this.dbName(), sql);
    }

    public void createFunction(DBFunction function) {
        this.client().createFunction(this.dbName(), function);
    }

    public void dropFunction(DBFunction function) {
        this.client().dropFunction(this.dbName(), function);
    }

    public DBProcedure selectProcedure(String procedureName) {
        return this.client().selectProcedure(this.dbName(), procedureName);
    }

    public void alertProcedure(DBProcedure procedure) {
        this.client().alertProcedure(this.dbName(), procedure);
    }

    public void createProcedure(DBProcedure procedure) {
        this.client().createProcedure(this.dbName(), procedure);
    }

    public void dropProcedure(DBProcedure procedure) {
        this.client().dropProcedure(this.dbName(), procedure);
    }

    public DBFunction selectFunction(String functionName) {
        return this.client().selectFunction(this.dbName(), functionName);
    }

    public void alertFunction(DBFunction function) {
        this.client().alertFunction(this.dbName(), function);
    }

    public DBView selectView(String viewName) {
        return this.client().view(this.dbName(), viewName);
    }

    public void createView(DBView view) {
        this.client().createView(this.dbName(), view);
    }

    public void alertView(DBView view) {
        this.client().alertView(this.dbName(), view);
    }

    public void dropView(DBView view) {
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

    public DBEvent selectEvent(String eventName) {
        return this.client().selectEvent(this.dbName(), eventName);
    }

    public void alertEvent(DBEvent event) {
        this.client().alertEvent(this.dbName(), event);
    }

    public void createEvent(DBEvent event) {
        this.client().createEvent(this.dbName(), event);
    }

    public void dropEvent(DBEvent event) {
        this.client().dropEvent(this.dbName(), event);
    }

    public boolean isSupportCheckFeature() {
        return this.client().isSupportCheckFeature();
    }

    public DBDialect dialect() {
        return this.client().dialect();
    }

    /**
     * database值
     *
     * @author oyzh
     * @since 2023/12/20
     */
    public static class MysqlDatabaseTreeItemValue extends DBTreeItemValue {

        /**
         * db树database节点
         */
        private final MysqlDatabaseTreeItem item;

        public MysqlDatabaseTreeItemValue(MysqlDatabaseTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.flushGraphicColor();
            this.name(item.dbName());
        }

        @Override
        public void flushGraphic() {
            SVGGlyph glyph = (SVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new SVGGlyph("/font/database2.svg", "12");
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
                Integer tableSize = this.item.tableSize();
                // 寻找组件
                FXText text = (FXText) this.lookup("#num");
                if (tableSize == null) {
                    this.removeChild(text);
                } else {
                    if (text == null) {
                        text = new FXText();
                        this.addChild(text);
                        text.setId("num");
                        text.setFill(Color.valueOf("#228B22"));
                        HBox.setMargin(text, new Insets(0, 0, 0, 3));
                    }
                    text.setText("(" + tableSize + ")");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

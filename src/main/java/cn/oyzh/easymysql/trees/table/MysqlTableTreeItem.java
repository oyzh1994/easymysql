package cn.oyzh.easymysql.trees.table;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.controller.data.MysqlDataDumpController;
import cn.oyzh.easymysql.controller.data.MysqlDataExportController;
import cn.oyzh.easymysql.controller.table.MysqlTableInfoController;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.check.MysqlChecks;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.easymysql.mysql.column.MysqlColumns;
import cn.oyzh.easymysql.mysql.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.mysql.foreignKey.MysqlForeignKey;
import cn.oyzh.easymysql.mysql.index.MysqlIndex;
import cn.oyzh.easymysql.mysql.record.MysqlDeleteRecordParam;
import cn.oyzh.easymysql.mysql.record.MysqlInsertRecordParam;
import cn.oyzh.easymysql.mysql.record.MysqlRecord;
import cn.oyzh.easymysql.mysql.record.MysqlRecordData;
import cn.oyzh.easymysql.mysql.record.MysqlRecordFilter;
import cn.oyzh.easymysql.mysql.record.MysqlRecordPrimaryKey;
import cn.oyzh.easymysql.mysql.record.MysqlSelectRecordParam;
import cn.oyzh.easymysql.mysql.record.MysqlUpdateRecordParam;
import cn.oyzh.easymysql.mysql.table.MysqlSelectTableParam;
import cn.oyzh.easymysql.mysql.table.MysqlTable;
import cn.oyzh.easymysql.mysql.trigger.MysqlTrigger;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.util.DBI18nHelper;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.svg.glyph.CopySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * db树表节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MysqlTableTreeItem extends DBTreeItem<MysqlTableTreeItemValue> {

    /**
     * 当前值
     */
    private final MysqlTable value;

    /**
     * 构造表树节点
     *
     * @param table 表
     * @param treeView 树视图
     */
    public MysqlTableTreeItem(MysqlTable table, RichTreeView treeView) {
        super(treeView);
        this.value = table;
        this.setValue(new MysqlTableTreeItemValue(this));
    }

    @Override
    public MysqlTablesTreeItem parent() {
        return (MysqlTablesTreeItem) super.parent();
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    public MysqlClient client() {
        return this.parent().client();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.parent().dbName();
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        return this.value.getName();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public MysqlConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openTable = MenuItemHelper.openTable( this::onPrimaryDoubleClick);
        items.add(openTable);
        FXMenuItem updateTable = MenuItemHelper.designTable( this::designTable);
        items.add(updateTable);
        FXMenuItem renameTable = MenuItemHelper.renameTable( this::rename);
        items.add(renameTable);
        FXMenuItem clearTable = MenuItemHelper.clearTableData( this::clearTableData);
        items.add(clearTable);
        FXMenuItem truncateTable = MenuItemHelper.truncateTable( this::truncateTable);
        items.add(truncateTable);
        FXMenuItem dropTable = MenuItemHelper.deleteTable( this::delete);
        items.add(dropTable);
        items.add(MenuItemHelper.separator());
        FXMenuItem dumpTable = MenuItemHelper.dumpData( this::dump);
        items.add(dumpTable);
        FXMenuItem exportTable = MenuItemHelper.exportData( this::export);
        items.add(exportTable);
        FXMenuItem tableInfo = MenuItemHelper.tableInfo( this::tableInfo);
        items.add(tableInfo);

        // 克隆表
        Menu cloneTable = MenuItemHelper.menu(I18nHelper.cloneTable(), new CopySVGGlyph("12"));
        MenuItem clone1 = MenuItemHelper.menuItem(DBI18nHelper.tableTip3(), () -> this.cloneTable(true));
        MenuItem clone2 = MenuItemHelper.menuItem(DBI18nHelper.tableTip4(), () -> this.cloneTable(false));
        cloneTable.getItems().addAll(clone1, clone2);

        items.add(cloneTable);
        return items;
    }

    /**
     * 克隆表
     *
     * @param includeRecord 是否包含记录
     */
    private void cloneTable(boolean includeRecord) {
        StageManager.showMask(() -> this.doCloneTable(includeRecord));
    }

    /**
     * 执行克隆表
     *
     * @param includeRecord 是否包含记录
     */
    private void doCloneTable(boolean includeRecord) {
        try {
            String cloneTable = this.dbItem().cloneTable(this.tableName(), includeRecord);
            MysqlTable mysqlTable = this.dbItem().selectTable(cloneTable);
            this.dbItem().getTableTypeChild().addTable(mysqlTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 转储
     */
    private void dump() {
        StageAdapter fxView = StageManager.parseStage(MysqlDataDumpController.class, this.window());
        fxView.setProp("dumpType", 2);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.setProp("tableName", this.tableName());
        fxView.display();
    }

    /**
     * 导出
     */
    private void export() {
        StageAdapter fxView = StageManager.parseStage(MysqlDataExportController.class, this.window());
        fxView.setProp("dumpType", 2);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.setProp("tableName", this.tableName());
        fxView.display();
    }

    /**
     * 设计表
     */
    private void designTable() {
        this.reloadChild();
        MysqlEventUtil.designTable(this.value, this.dbItem());
    }

    /**
     * 截断表
     */
    private void truncateTable() {
        if (MessageBox.confirm(I18nHelper.truncateTable() + "[" + this.tableName() + "]")) {
            try {
                this.dbItem().truncateTable(this.tableName());
                MysqlEventUtil.tableTruncated(this, this.dbItem());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    /**
     * 清空表
     */
    private void clearTableData() {
        try {
            if (MessageBox.confirm(I18nHelper.clearTableData() + "[" + this.tableName() + "]")) {
                this.dbItem().clearTable(this.tableName());
                MysqlEventUtil.tableCleared(this, this.dbItem());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void delete() {
        try {
            if (MessageBox.confirm(I18nHelper.deleteTable() + "[" + this.tableName() + "]")) {
                this.dbItem().dropTable(this.tableName());
                MysqlEventUtil.tableDropped(this, this.dbItem());
                this.remove();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 查看表信息
     */
    private void tableInfo() {
        StageAdapter fxView = StageManager.parseStage(MysqlTableInfoController.class, this.window());
        fxView.setProp("tableItem", this);
        fxView.display();
    }

    @Override
    public void rename() {
        try {
            // if (!MessageBox.confirm(DBI18nHelper.tableTip2())) {
            //     return;
            // }
            String tableName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.value.getName());
            // 名称为null或者跟当前名称相同，则忽略
            if (tableName == null || Objects.equals(tableName, this.value.getName())) {
                return;
            }
            // 检查名称
            if (StringUtil.isBlank(tableName)) {
                MessageBox.warn(I18nHelper.pleaseInputContent());
                return;
            }
            // if (this.dbItem().existTable(tableName)) {
            //     MessageBox.warn(I18nHelper.table() + " " + tableName + I18nHelper.alreadyExists());
            //     return;
            // }
            String oldName = this.value.getName();
            // 修改名称
            this.dbItem().renameTable(oldName, tableName);
            this.value.setName(tableName);
            this.refresh();
            MysqlEventUtil.tableRenamed(this, this.dbItem());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public MysqlDatabaseTreeItem dbItem() {
        if (this.parent() == null) {
            return null;
        }
        return this.parent().parent();
    }

    /**
     * 分页查询记录
     *
     * @param pageNo  页码
     * @param limit   每页数量
     * @param filters 过滤条件
     * @param columns 列
     * @return 分页记录
     */
    public Paging<MysqlRecord> recordPage(long pageNo, long limit, List<MysqlRecordFilter> filters, List<MysqlColumn> columns) {
        MysqlSelectRecordParam param = new MysqlSelectRecordParam();
        param.setLimit(limit);
        param.setFilters(filters);
        param.setColumns(columns);
        param.setDbName(this.dbName());
        param.setStart(pageNo * limit);
        param.setTableName(this.tableName());
        List<MysqlRecord> rows = this.client().selectRecords(param);
        long count = this.client().selectRecordCount(param);
        Paging<MysqlRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return parent().infoName();
    }

    /**
     * 获取列信息
     *
     * @return 列信息
     */
    public MysqlColumns columns() {
        return this.client().selectColumns(new MysqlSelectColumnParam(this.dbName(), this.tableName()));
    }

    /**
     * 获取索引列表
     *
     * @return 索引列表
     */
    public List<MysqlIndex> indexes() {
        return this.client().indexes(this.dbName(), this.tableName());
    }

    /**
     * 获取检查信息
     *
     * @return 检查信息
     */
    public MysqlChecks checks() {
        return this.client().checks(this.dbName(), this.tableName());
    }

    /**
     * 获取外键列表
     *
     * @return 外键列表
     */
    public List<MysqlForeignKey> foreignKeys() {
        return this.client().foreignKeys(this.dbName(), this.tableName());
    }

    /**
     * 获取触发器列表
     *
     * @return 触发器列表
     */
    public List<MysqlTrigger> triggers() {
        return this.client().triggers(this.dbName(), this.tableName());
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.tableOpen(this, this.dbItem());
    }

    /**
     * 列缓存
     */
    private MysqlColumns columns;

    /**
     * 获取主键列，优先返回自动递增列
     *
     * @return 主键列
     */
    public MysqlColumn getPrimaryKey() {
        if (columns == null) {
            columns = this.columns();
        }
        MysqlColumn dbColumn = null;
        for (MysqlColumn column : this.columns.primaryKeys()) {
            if (column.isAutoIncrement()) {
                dbColumn = column;
                break;
            }
        }
        if (dbColumn == null) {
            for (MysqlColumn column : this.columns.primaryKeys()) {
                return column;
            }
        }
        return dbColumn;
    }

    @Override
    public void loadChild() {
        try {
            MysqlSelectTableParam param = new MysqlSelectTableParam();
            param.setFull(true);
            param.setDbName(this.dbName());
            param.setTableName(this.tableName());
            MysqlTable table = this.client().selectTable(param);
            if (table != null) {
                this.value.copy(table);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    /**
     * 是否存在主键
     *
     * @return 是否存在主键
     */
    public boolean hasPrimaryKey() {
        if (columns == null) {
            columns = this.columns();
        }
        return this.columns.primaryKeys().isEmpty();
    }

    /**
     * 新增记录
     *
     * @param recordData 记录数据
     * @return 影响行数
     */
    public int insertRecord(MysqlRecordData recordData) {
        return this.insertRecord(recordData, null);
    }

    /**
     * 新增记录
     *
     * @param recordData 记录数据
     * @param primaryKey 主键
     * @return 影响行数
     */
    public int insertRecord(MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey) {
        MysqlInsertRecordParam param = new MysqlInsertRecordParam();
        param.setRecord(recordData);
        param.setDbName(this.dbName());
        param.setPrimaryKey(primaryKey);
        param.setTableName(this.tableName());
        return this.client().insertRecord(param);
    }

    /**
     * 删除记录
     *
     * @param recordData 记录数据
     * @return 影响行数
     */
    public int deleteRecord(MysqlRecordData recordData) {
        MysqlDeleteRecordParam param = new MysqlDeleteRecordParam();
        param.setDbName(this.dbName());
        param.setTableName(this.tableName());
        param.setRecord(recordData);
        return this.client().deleteRecord(param);
    }

    /**
     * 删除记录
     *
     * @param primaryKey 主键
     * @return 影响行数
     */
    public int deleteRecord(MysqlRecordPrimaryKey primaryKey) {
        MysqlDeleteRecordParam param = new MysqlDeleteRecordParam();
        param.setDbName(this.dbName());
        param.setTableName(this.tableName());
        param.setPrimaryKey(primaryKey);
        return this.client().deleteRecord(param);
    }

    /**
     * 查询记录
     *
     * @param primaryKey 主键
     * @return 记录
     */
    public MysqlRecord selectRecord(MysqlRecordPrimaryKey primaryKey) {
        MysqlSelectRecordParam param = new MysqlSelectRecordParam();
        param.setDbName(this.dbName());
        param.setTableName(this.tableName());
        param.setPrimaryKey(primaryKey);
        return this.client().selectRecord(param);
    }

    /**
     * 更新记录
     *
     * @param recordData 记录数据
     * @param primaryKey 主键
     * @return 影响行数
     */
    public int updateRecord(MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey) {
        MysqlUpdateRecordParam param = new MysqlUpdateRecordParam();
        param.setDbName(this.dbName());
        param.setTableName(this.tableName());
        param.setPrimaryKey(primaryKey);
        param.setUpdateRecord(recordData);
        return this.client().updateRecord(param);
    }

    /**
     * 更新记录
     *
     * @param recordData         记录数据
     * @param originalRecordData 原始记录数据
     * @return 影响行数
     */
    public int updateRecord(MysqlRecordData recordData, MysqlRecordData originalRecordData) {
        MysqlUpdateRecordParam param = new MysqlUpdateRecordParam();
        param.setDbName(this.dbName());
        param.setTableName(this.tableName());
        param.setUpdateRecord(recordData);
        param.setRecord(originalRecordData);
        return this.client().updateRecord(param);
    }

    /**
     * 获取当前值
     *
     * @return 表
     */
    public MysqlTable value() {
        return value;
    }
}

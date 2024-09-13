package cn.oyzh.easymysql.trees;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.controller.data.DBDataDumpController;
import cn.oyzh.easymysql.controller.data.DBDataExportController;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.record.DBRecord;
import cn.oyzh.easymysql.db.record.DBRecordData;
import cn.oyzh.easymysql.db.record.DBRecordFilter;
import cn.oyzh.easymysql.db.record.DBRecordPrimaryKey;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.easymysql.db.table.DBForeignKey;
import cn.oyzh.easymysql.db.table.DBForeignKeys;
import cn.oyzh.easymysql.db.table.DBIndex;
import cn.oyzh.easymysql.db.table.DBIndexes;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.db.table.DBTrigger;
import cn.oyzh.easymysql.db.table.DBTriggers;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.controller.MysqlTableInfoController;
import cn.oyzh.easymysql.module.mysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.util.DBI18nHelper;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * db树表节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MysqlTableTreeItem extends DBTreeItem<MysqlTableTreeItem.MysqlTableTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final DBTable value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlTableTypeTreeItem parent;

    public MysqlTableTreeItem(DBTable table, MysqlTableTypeTreeItem parent) {
        super(parent.getTreeView());
        this.parent = parent;
        this.value = table;
        this.setValue(new MysqlTableTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.flushLocal();
        });
    }

    public DBClient client() {
        return this.parent.client();
    }

    public String dbName() {
        return this.parent.dbName();
    }

    public String tableName() {
        return this.value.getName();
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public DBInfo info() {
        return this.parent.info();
    }

    public DBIndexes tableIndexes() {
        // if (this.value.getIndexes() == null) {
        this.value.setIndexes(new DBIndexes(this.indexes()));
        // }
        return this.value.indexes();
    }

    public DBColumns tableColumns() {
        // if (this.value.getColumns() == null) {
        this.value.setColumns(new DBColumns(this.columns()));
        // }
        return this.value.columns();
    }

    public DBTriggers tableTriggers() {
        // if (this.value.getTriggers() == null) {
        this.value.setTriggers(new DBTriggers(this.triggers()));
        // }
        return this.value.triggers();
    }

    public DBForeignKeys tableForeignKeys() {
        // if (this.value.getForeignKeys() == null) {
        this.value.setForeignKeys(new DBForeignKeys(this.foreignKeys()));
        // }
        return this.value.foreignKeys();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openTable = MenuItemHelper.openTable("12", this::onPrimaryDoubleClick);
        FXMenuItem updateTable = MenuItemHelper.designTable("12", this::designTable);
        FXMenuItem renameTable = MenuItemHelper.renameTable("12", this::rename);
        FXMenuItem clearTable = MenuItemHelper.clearTable("12", this::clearTable);
        FXMenuItem truncateTable = MenuItemHelper.truncateTable("12", this::truncateTable);
        FXMenuItem dropTable = MenuItemHelper.deleteTable("12", this::dropTable);
        FXMenuItem dumpTable = MenuItemHelper.dumpData("12", this::dump);
        FXMenuItem exportTable = MenuItemHelper.exportData("12", this::export);
        FXMenuItem tableInfo = MenuItemHelper.tableInfo("12", this::tableInfo);
        items.add(openTable);
        items.add(updateTable);
        items.add(renameTable);
        items.add(clearTable);
        items.add(truncateTable);
        items.add(dropTable);
        items.add(dumpTable);
        items.add(exportTable);
        items.add(tableInfo);
        return items;
    }

    /**
     * 转储
     */
    private void dump() {
        StageAdapter fxView = StageManager.parseStage(DBDataDumpController.class, this.window());
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
        StageAdapter fxView = StageManager.parseStage(DBDataExportController.class, this.window());
        fxView.setProp("dumpType", 2);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.setProp("tableName", this.tableName());
        fxView.display();
    }

    private void designTable() {
        this.reloadChild();
        MysqlEventUtil.designTable(this.value, this.dbItem());
    }

    private void truncateTable() {
        if (MessageBox.confirm(I18nHelper.truncateTable() + " " + this.tableName() + "?")) {
            try {
                this.dbItem().truncateTable(this.tableName());
                MysqlEventUtil.tableTruncated(this, this.dbItem());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    private void clearTable() {
        if (MessageBox.confirm("确定清空表数据？")) {
            if (this.dbItem().clearTable(this.tableName())) {
                MysqlEventUtil.tableCleared(this, this.dbItem());
            } else {
                MessageBox.warn("清空表数据失败！");
            }
        }
    }

    private void dropTable() {
        if (MessageBox.confirm("确定删除表" + this.tableName() + "？")) {
            if (this.dbItem().dropTable(this.tableName())) {
                this.remove();
                MysqlEventUtil.tableDropped(this, this.dbItem());
            } else {
                MessageBox.warn("删除表失败！");
            }
        }
    }

    private void tableInfo() {
        StageAdapter fxView = StageManager.parseStage(MysqlTableInfoController.class, this.window());
        fxView.setProp("tableItem", this);
        fxView.display();
    }

    @Override
    public void rename() {
        try {
            if (!MessageBox.confirm(DBI18nHelper.tableTip2())) {
                return;
            }
            String tableName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.value.getName());
            // 名称为null或者跟当前名称相同，则忽略
            if (tableName == null || Objects.equals(tableName, this.value.getName())) {
                return;
            }
            // 检查名称
            if (StrUtil.isBlank(tableName)) {
                MessageBox.warn(I18nHelper.pleaseInputContent());
                return;
            }
            if (this.dbItem().existTable(tableName)) {
                MessageBox.warn(I18nHelper.table() + " " + tableName + I18nHelper.alreadyExists());
                return;
            }
            String oldName = this.value.getName();
            // 修改名称
            if (this.dbItem().renameTable(oldName, tableName)) {
                this.value.setName(tableName);
                this.getValue().flushText();
                MysqlEventUtil.tableRenamed(this, this.dbItem());
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    public MysqlDatabaseTreeItem dbItem() {
        return this.parent.dbItem();
    }

    public Paging<DBRecord> recordPage(long pageNo, long limit, List<DBRecordFilter> filters) {
        List<DBRecord> rows = this.client().selectTableRecords(this.dbName(), this.tableName(), pageNo * limit, limit, filters);
        long count = this.client().tableCount(dbName(), this.tableName(), filters);
        Paging<DBRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    public String infoName() {
        return parent.infoName();
    }

    public List<DBColumn> columns() {
        return this.client().tableColumns(this.dbName(), null, this.tableName());
    }

    public List<DBIndex> indexes() {
        return this.client().indexes(this.dbName(), this.tableName());
    }

    public List<DBForeignKey> foreignKeys() {
        return this.client().foreignKeys(this.dbName(), this.tableName());
    }

    public List<DBTrigger> triggers() {
        return this.client().triggers(this.dbName(), this.tableName());
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.tableOpen(this, this.dbItem());
    }

    /**
     * 获取主键列，优先返回自动递增列
     *
     * @return 主键列
     */
    public DBColumn getPrimaryKey() {
        if (this.value.getColumns() == null) {
            this.tableColumns();
        }
        DBColumn dbColumn = null;
        for (DBColumn column : this.value.primaryKeys()) {
            if (column.isAutoIncrement()) {
                dbColumn = column;
                break;
            }
        }
        if (dbColumn == null) {
            for (DBColumn column : this.value.primaryKeys()) {
                return column;
            }
        }
        return dbColumn;
    }

    @Override
    public void reloadChild() {
        try {
            DBTable table = this.client().table(this.dbName(), this.tableName(), true);
            if (table != null) {
                this.value.copy(table);
            }
            super.reloadChild();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasPrimaryKey() {
        return this.value.hasPrimaryKey();
    }

    @Override
    public boolean supportFilter() {
        return true;
    }

    public int insertRecord(DBRecordData recordData) {
        return this.insertRecord(recordData, null);
    }

    public int insertRecord(DBRecordData recordData, DBRecordPrimaryKey primaryKey) {
        return this.client().insertRecord(this.dbName(), this.tableName(), recordData, primaryKey);
    }

    public int deleteRecord(DBRecordData recordData) {
        return this.client().deleteRecord(this.dbName(), this.tableName(), recordData);
    }

    public int deleteRecord(DBRecordPrimaryKey primaryKey) {
        return this.client().deleteRecord(this.dbName(), this.tableName(), primaryKey);
    }

    public DBRecord selectRecord(DBRecordPrimaryKey primaryKey) {
        return this.client().selectRecord(this.dbName(), this.tableName(), primaryKey);
    }

    public int updateRecord(DBRecordData recordData, DBRecordPrimaryKey primaryKey) {
        return this.client().updateRecord(this.dbName(), this.tableName(), recordData, primaryKey);
    }

    public int updateRecord(DBRecordData recordData, DBRecordData originalRecordData) {
        return this.client().updateRecord(this.dbName(), this.tableName(), recordData, originalRecordData);
    }

    /**
     * db树表节点值
     *
     * @author oyzh
     * @since 2023/12/22
     */
    @Accessors(chain = true, fluent = true)
    public static class MysqlTableTreeItemValue extends DBTreeItemValue {

        /**
         * db树表节点
         */
        private final MysqlTableTreeItem item;

        public MysqlTableTreeItemValue(MysqlTableTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.flushGraphicColor();
            this.flushText();
        }

        @Override
        public void flushGraphic() {
            SVGGlyph glyph = (SVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new SVGGlyph("/font/table2.svg", "12");
                this.graphic(glyph);
            }
        }

        @Override
        public String name() {
            return this.item.tableName();
        }
    }
}

package cn.oyzh.easymysql.trees.table;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.controller.data.MysqlDataDumpController;
import cn.oyzh.easymysql.controller.data.MysqlDataExportController;
import cn.oyzh.easymysql.controller.table.MysqlTableInfoController;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.record.MysqlRecordData;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.db.record.MysqlRecordPrimaryKey;
import cn.oyzh.easymysql.db.record.MysqlSelectRecordParam;
import cn.oyzh.easymysql.db.table.MysqlChecks;
import cn.oyzh.easymysql.db.table.MysqlForeignKey;
import cn.oyzh.easymysql.db.table.MysqlIndex;
import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.db.table.MysqlTrigger;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.util.DBI18nHelper;
import cn.oyzh.fx.common.dto.Paging;
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
public class MysqlTableTreeItem extends DBTreeItem<MysqlTableTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final MysqlTable value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlTableTypeTreeItem parent;

    public MysqlTableTreeItem(MysqlTable table, MysqlTableTypeTreeItem parent) {
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
    public MysqlInfo info() {
        return this.parent.info();
    }

    // public MysqlIndexes tableIndexes() {
    //     // if (this.value.getIndexes() == null) {
    //     this.value.setIndexes(new MysqlIndexes(this.indexes()));
    //     // }
    //     return this.value.indexes();
    // }

    // public MysqlColumns tableColumns() {
    //     return new MysqlColumns(this.columns());
    // }

    // public MysqlTriggers tableTriggers() {
    //     // if (this.value.getTriggers() == null) {
    //     this.value.setTriggers(new MysqlTriggers(this.triggers()));
    //     // }
    //     return this.value.triggers();
    // }
    //
    // public MysqlForeignKeys tableForeignKeys() {
    //     // if (this.value.getForeignKeys() == null) {
    //     this.value.setForeignKeys(new MysqlForeignKeys(this.foreignKeys()));
    //     // }
    //     return this.value.foreignKeys();
    // }

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
        try {
            if (MessageBox.confirm("确定清空表数据？")) {
                this.dbItem().clearTable(this.tableName());
                MysqlEventUtil.tableCleared(this, this.dbItem());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    private void dropTable() {
        try {
            if (MessageBox.confirm("确定删除表" + this.tableName() + "？")) {
                this.dbItem().dropTable(this.tableName());
                this.remove();
                MysqlEventUtil.tableDropped(this, this.dbItem());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
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
            this.dbItem().renameTable(oldName, tableName);
            this.value.setName(tableName);
            this.getValue().flushText();
            MysqlEventUtil.tableRenamed(this, this.dbItem());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    public MysqlDatabaseTreeItem dbItem() {
        return this.parent.dbItem();
    }

    public Paging<MysqlRecord> recordPage(long pageNo, long limit, List<MysqlRecordFilter> filters, List<MysqlColumn> columns) {
        MysqlSelectRecordParam param = new MysqlSelectRecordParam();
        param.limit(limit);
        param.filters(filters);
        param.columns(columns);
        param.dbName(this.dbName());
        param.start(pageNo * limit);
        param.tableName(this.tableName());
        List<MysqlRecord> rows = this.client().selectRecords(param);
        long count = this.client().selectRecordCount(param);
        Paging<MysqlRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    public String infoName() {
        return parent.infoName();
    }

    public MysqlColumns columns() {
        return this.client().selectColumns(new MysqlSelectColumnParam(this.dbName(), this.tableName()));
    }

    public List<MysqlIndex> indexes() {
        return this.client().indexes(this.dbName(), this.tableName());
    }

    public MysqlChecks checks() {
        return this.client().checks(this.dbName(), this.tableName());
    }

    public List<MysqlForeignKey> foreignKeys() {
        return this.client().foreignKeys(this.dbName(), this.tableName());
    }

    public List<MysqlTrigger> triggers() {
        return this.client().triggers(this.dbName(), this.tableName());
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.tableOpen(this, this.dbItem());
    }


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
    public void reloadChild() {
        try {
            MysqlTable table = this.client().table(this.dbName(), this.tableName(), true);
            if (table != null) {
                this.value.copy(table);
            }
            super.reloadChild();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasPrimaryKey() {
        if (columns == null) {
            columns = this.columns();
        }
        return this.columns.primaryKeys().isEmpty();
    }

    @Override
    public boolean supportFilter() {
        return true;
    }

    public int insertRecord(MysqlRecordData recordData) {
        return this.insertRecord(recordData, null);
    }

    public int insertRecord(MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey) {
        return this.client().insertRecord(this.dbName(), this.tableName(), recordData, primaryKey);
    }

    public int deleteRecord(MysqlRecordData recordData) {
        return this.client().deleteRecord(this.dbName(), this.tableName(), recordData);
    }

    public int deleteRecord(MysqlRecordPrimaryKey primaryKey) {
        return this.client().deleteRecord(this.dbName(), this.tableName(), primaryKey);
    }

    public MysqlRecord selectRecord(MysqlRecordPrimaryKey primaryKey) {
        return this.client().selectRecord(this.dbName(), this.tableName(), primaryKey);
    }

    public int updateRecord(MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey) {
        return this.client().updateRecord(this.dbName(), this.tableName(), recordData, primaryKey);
    }

    public int updateRecord(MysqlRecordData recordData, MysqlRecordData originalRecordData) {
        return this.client().updateRecord(this.dbName(), this.tableName(), recordData, originalRecordData);
    }


}

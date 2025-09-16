package cn.oyzh.easymysql.db.table;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.check.MysqlChecks;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.db.index.MysqlIndexes;
import cn.oyzh.easymysql.db.trigger.MysqlTriggers;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-14
 */
public class MysqlTableAlertParam {

    private MysqlTable table;

    private MysqlChecks checks;

    private MysqlColumns columns;

    private MysqlIndexes indexes;

    private MysqlTriggers triggers;

    private MysqlForeignKeys foreignKeys;

    // private MysqlPrimaryKeys primaryKeys;

    /**
     * 是否存在主键
     */
    private boolean existPrimaryKey;

    public String dbName() {
        return this.table.getDbName();
    }

    public boolean hasColumns() {
        return CollUtil.isNotEmpty(this.columns);
    }

    public List<MysqlColumn> primaryKeys() {
        return this.columns.primaryKeys();
    }

    public boolean hasIndex() {
        return CollUtil.isNotEmpty(this.indexes);
    }

    public boolean hasForeignKey() {
        return CollUtil.isNotEmpty(this.foreignKeys);
    }

    public boolean hasCheck() {
        return CollUtil.isNotEmpty(this.checks);
    }

    public boolean hasTrigger() {
        return CollUtil.isNotEmpty(this.triggers);
    }

    public boolean primaryKeyChanged() {
        if (this.hasColumns()) {
            for (MysqlColumn column : columns) {
                if (column.isPrimaryKeyChanged()) {
                    return true;
                }
                if (column.isCreated() && column.isPrimaryKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean columnChanged() {
        if (this.hasColumns()) {
            for (MysqlColumn column : this.columns) {
                if (column.isDeleted()) {
                    return true;
                }
                if (column.isCreated()) {
                    return true;
                }
                if (column.isColumnChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String tableName() {
        return this.table.getName();
    }

    public void setTableName(String tableName) {
        this.table.setName(tableName);
    }


    public MysqlTable getTable() {
        return table;
    }

    public void setTable(MysqlTable table) {
        this.table = table;
    }

    public MysqlChecks getChecks() {
        return checks;
    }

    public void setChecks(MysqlChecks checks) {
        this.checks = checks;
    }

    public MysqlColumns getColumns() {
        return columns;
    }

    public void setColumns(MysqlColumns columns) {
        this.columns = columns;
    }

    public MysqlIndexes getIndexes() {
        return indexes;
    }

    public void setIndexes(MysqlIndexes indexes) {
        this.indexes = indexes;
    }

    public MysqlTriggers getTriggers() {
        return triggers;
    }

    public void setTriggers(MysqlTriggers triggers) {
        this.triggers = triggers;
    }

    public MysqlForeignKeys getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(MysqlForeignKeys foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public boolean isExistPrimaryKey() {
        return existPrimaryKey;
    }

    public void setExistPrimaryKey(boolean existPrimaryKey) {
        this.existPrimaryKey = existPrimaryKey;
    }
}

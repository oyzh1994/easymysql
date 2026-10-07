package cn.oyzh.easymysql.mysql.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.check.MysqlChecks;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.easymysql.mysql.column.MysqlColumns;
import cn.oyzh.easymysql.mysql.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.mysql.index.MysqlIndexes;
import cn.oyzh.easymysql.mysql.trigger.MysqlTriggers;

import java.util.List;

/**
 * 修改表参数
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class MysqlAlertTableParam {

    /**
     * 表
     */
    private MysqlTable table;

    /**
     * 检查器集合
     */
    private MysqlChecks checks;

    /**
     * 字段集合
     */
    private MysqlColumns columns;

    /**
     * 索引集合
     */
    private MysqlIndexes indexes;

    /**
     * 触发器集合
     */
    private MysqlTriggers triggers;

    /**
     * 外键集合
     */
    private MysqlForeignKeys foreignKeys;

    // private MysqlPrimaryKeys primaryKeys;

    /**
     * 是否存在主键
     */
    private boolean existPrimaryKey;

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String dbName() {
        return this.table.getDbName();
    }

    /**
     * 是否存在字段
     *
     * @return 是否存在
     */
    public boolean hasColumns() {
        return CollectionUtil.isNotEmpty(this.columns);
    }

    /**
     * 获取主键字段列表
     *
     * @return 主键字段列表
     */
    public List<MysqlColumn> primaryKeys() {
        return this.columns.primaryKeys();
    }

    /**
     * 是否存在索引
     *
     * @return 是否存在
     */
    public boolean hasIndex() {
        return CollectionUtil.isNotEmpty(this.indexes);
    }

    /**
     * 是否存在外键
     *
     * @return 是否存在
     */
    public boolean hasForeignKey() {
        return CollectionUtil.isNotEmpty(this.foreignKeys);
    }

    /**
     * 是否存在检查器
     *
     * @return 是否存在
     */
    public boolean hasCheck() {
        return CollectionUtil.isNotEmpty(this.checks);
    }

    /**
     * 是否存在触发器
     *
     * @return 是否存在
     */
    public boolean hasTrigger() {
        return CollectionUtil.isNotEmpty(this.triggers);
    }

    /**
     * 主键是否变更
     *
     * @return 是否变更
     */
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

    /**
     * 字段是否变更
     *
     * @return 是否变更
     */
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

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        return this.table.getName();
    }

    /**
     * 设置表名称
     *
     * @param tableName 表名称
     */
    public void setTableName(String tableName) {
        this.table.setName(tableName);
    }

    /**
     * 获取表
     *
     * @return 表
     */
    public MysqlTable getTable() {
        return table;
    }

    /**
     * 设置表
     *
     * @param table 表
     */
    public void setTable(MysqlTable table) {
        this.table = table;
    }

    /**
     * 获取检查器集合
     *
     * @return 检查器集合
     */
    public MysqlChecks getChecks() {
        return checks;
    }

    /**
     * 设置检查器集合
     *
     * @param checks 检查器集合
     */
    public void setChecks(MysqlChecks checks) {
        this.checks = checks;
    }

    /**
     * 获取字段集合
     *
     * @return 字段集合
     */
    public MysqlColumns getColumns() {
        return columns;
    }

    /**
     * 设置字段集合
     *
     * @param columns 字段集合
     */
    public void setColumns(MysqlColumns columns) {
        this.columns = columns;
    }

    /**
     * 获取索引集合
     *
     * @return 索引集合
     */
    public MysqlIndexes getIndexes() {
        return indexes;
    }

    /**
     * 设置索引集合
     *
     * @param indexes 索引集合
     */
    public void setIndexes(MysqlIndexes indexes) {
        this.indexes = indexes;
    }

    /**
     * 获取触发器集合
     *
     * @return 触发器集合
     */
    public MysqlTriggers getTriggers() {
        return triggers;
    }

    /**
     * 设置触发器集合
     *
     * @param triggers 触发器集合
     */
    public void setTriggers(MysqlTriggers triggers) {
        this.triggers = triggers;
    }

    /**
     * 获取外键集合
     *
     * @return 外键集合
     */
    public MysqlForeignKeys getForeignKeys() {
        return foreignKeys;
    }

    /**
     * 设置外键集合
     *
     * @param foreignKeys 外键集合
     */
    public void setForeignKeys(MysqlForeignKeys foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    /**
     * 是否存在主键
     *
     * @return 是否存在
     */
    public boolean isExistPrimaryKey() {
        return existPrimaryKey;
    }

    /**
     * 设置是否存在主键
     *
     * @param existPrimaryKey 是否存在主键
     */
    public void setExistPrimaryKey(boolean existPrimaryKey) {
        this.existPrimaryKey = existPrimaryKey;
    }
}

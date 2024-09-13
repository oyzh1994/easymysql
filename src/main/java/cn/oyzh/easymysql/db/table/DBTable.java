package cn.oyzh.easymysql.db.table;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.db.table.DBChecks;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.easymysql.db.table.DBForeignKey;
import cn.oyzh.easymysql.db.table.DBForeignKeys;
import cn.oyzh.easymysql.db.table.DBIndexes;
import cn.oyzh.easymysql.db.table.DBTrigger;
import cn.oyzh.easymysql.db.table.DBTriggers;
import cn.oyzh.fx.common.util.ObjectComparator;
import cn.oyzh.fx.common.util.ObjectCopier;
import javafx.beans.property.SimpleStringProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * db表
 *
 * @author oyzh
 * @since 2024/01/16
 */
@EqualsAndHashCode(callSuper = true)
public class DBTable extends DBObjectStatus implements ObjectCopier<DBTable>, ObjectComparator<DBTable> {

    @Getter
    @Setter
    private boolean hasPrimaryKey;

    /**
     * 行格式
     */
    @Getter
    private String rowFormat;

    /**
     * 自动递增值
     */
    @Getter
    private Long autoIncrement;

    /**
     * 表创建定义
     */
    @Getter
    @Setter
    private String createDefinition;

    /**
     * 索引
     */
    @Getter
    @Setter
    private DBIndexes indexes;

    /**
     * 触发器
     */
    @Getter
    @Setter
    private DBTriggers triggers;

    /**
     * 外键
     */
    @Getter
    @Setter
    private DBForeignKeys foreignKeys;

    /**
     * 引擎
     */
    @Getter
    private String engine;

    /**
     * 字符集
     */
    @Getter
    private String charset;

    /**
     * 排序规则
     */
    @Getter
    private String collation;

    /**
     * 检查器
     */
    @Getter
    @Setter
    private DBChecks checks;

    public void setEngine(String engine) {
        this.engine = engine;
        super.putOriginalData("engine", engine);
    }

    public boolean isEngineChanged() {
        return super.checkOriginalData("engine", this.engine);
    }

    public void setCharset(String charset) {
        this.charset = charset;
        super.putOriginalData("charset", charset);
    }

    public boolean isCharsetChanged() {
        return super.checkOriginalData("charset", this.charset);
    }

    public void setCollation(String collation) {
        this.collation = collation;
        super.putOriginalData("collation", collation);
    }

    public boolean isCollationChanged() {
        return super.checkOriginalData("collation", this.collation);
    }

    public void setRowFormat(String rowFormat) {
        this.rowFormat = rowFormat;
        super.putOriginalData("rowFormat", rowFormat);
        // this.updateChanged();
    }

    public boolean isRowFormatChanged() {
        return super.checkOriginalData("rowFormat", this.rowFormat);
    }

    public void setAutoIncrement(Long autoIncrement) {
        this.autoIncrement = autoIncrement;
        super.putOriginalData("autoIncrement", autoIncrement);
    }

    public boolean isAutoIncrementChanged() {
        return super.checkOriginalData("autoIncrement", this.autoIncrement);
    }

    public boolean hasIndex() {
        return this.indexes != null && !this.indexes.isEmpty();
    }

    public boolean hasForeignKey() {
        return CollUtil.isNotEmpty(this.foreignKeys);
    }

    public boolean hasCheck() {
        return CollUtil.isNotEmpty(this.checks);
    }

    public boolean hasCharset() {
        return this.getCharset() != null;
    }

    public boolean hasCollation() {
        return this.getCollation() != null;
    }

    public boolean hasEngine() {
        return this.getEngine() != null;
    }

    public void setCharsetAndCollation(String collation) {
        if (StrUtil.isNotBlank(collation)) {
            String charset = collation.split("_")[0];
            this.setCharset(charset);
            this.setCollation(collation);
        }
    }

    public boolean hasTrigger() {
        return this.triggers != null && !this.triggers.isEmpty();
    }

    public DBIndexes indexes() {
        if (this.indexes == null) {
            this.indexes = new DBIndexes();
        }
        return this.indexes;
    }

    public DBTriggers triggers() {
        if (this.triggers == null) {
            this.triggers = new DBTriggers();
        }
        return this.triggers;
    }

    public DBForeignKeys foreignKeys() {
        if (this.foreignKeys == null) {
            this.foreignKeys = new DBForeignKeys();
        }
        return this.foreignKeys;
    }

    public DBChecks checks() {
        if (this.checks == null) {
            this.checks = new DBChecks();
        }
        return this.checks;
    }


    public boolean hasAutoIncrement() {
        return this.getAutoIncrement() != null;
    }

    @Override
    public void copy(DBTable table) {
        if (table != null) {
            this.setEngine(table.getEngine());
            this.setChecks(table.getChecks());
            this.setComment(table.getComment());
            this.setColumns(table.getColumns());
            this.setIndexes(table.getIndexes());
            this.setCharset(table.getCharset());
            this.setTriggers(table.getTriggers());
            this.setRowFormat(table.getRowFormat());
            this.setCollation(table.getCollation());
            this.setForeignKeys(table.getForeignKeys());
            this.setHasPrimaryKey(table.isHasPrimaryKey());
            this.setAutoIncrement(table.getAutoIncrement());
            this.setCreateDefinition(table.getCreateDefinition());
        }
    }

    public boolean isInnoDB() {
        return "innodb".equalsIgnoreCase(this.getEngine());
    }

    public boolean hasRowFormat() {
        return StrUtil.isNotBlank(this.getRowFormat());
    }

    public void removeIndex(DBIndex index) {
        if (index != null && this.indexes != null) {
            this.indexes().remove(index);
        }
    }

    public void removeTrigger(DBTrigger trigger) {
        if (trigger != null && this.triggers != null) {
            this.triggers().remove(trigger);
        }
    }

    public void removeForeignKey(DBForeignKey foreignKey) {
        if (foreignKey != null && this.foreignKeys != null) {
            this.foreignKeys().remove(foreignKey);
        }
    }

    public void removeCheck(DBCheck check) {
        if (check != null && this.checks != null) {
            this.checks().remove(check);
        }
    }

    /**
     * 库名称
     */
    @Setter
    @Getter
    private String dbName;

    /**
     * 模式名称
     */
    @Setter
    @Getter
    private String schema;

    /**
     * 表字段
     */
    @Setter
    @Getter
    protected DBColumns columns;

    /**
     * 表名称
     */
    private SimpleStringProperty nameProperty;

    /**
     * 表注释
     */
    private SimpleStringProperty commentProperty;

    public SimpleStringProperty nameProperty() {
        if (this.nameProperty == null) {
            this.nameProperty = new SimpleStringProperty();
        }
        return this.nameProperty;
    }

    public void setName(String name) {
        this.nameProperty().setValue(name);
    }

    public String getName() {
        return this.nameProperty == null ? null : this.nameProperty.get();
    }

    public SimpleStringProperty commentProperty() {
        if (this.commentProperty == null) {
            this.commentProperty = new SimpleStringProperty();
        }
        return this.commentProperty;
    }

    public void setComment(String comment) {
        this.commentProperty().setValue(comment);
    }

    public String getComment() {
        return this.commentProperty == null ? null : this.commentProperty.get();
    }

    public boolean primaryKeyChanged() {
        if (this.hasColumns()) {
            boolean b1 = this.columns.primaryKeyChanged();
            if (b1) {
                return true;
            }
            for (DBColumn column : this.columns.createdList()) {
                if (column.isPrimaryKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<DBColumn> primaryKeys() {
        if (this.hasColumns()) {
            return this.columns.primaryKeys();
        }
        return Collections.emptyList();
    }

    public boolean hasPrimaryKey() {
        return CollUtil.isNotEmpty(this.primaryKeys());
    }

    public boolean hasColumns() {
        return this.columns != null && !this.columns.isEmpty();
    }

    public boolean hasComment() {
        return this.getComment() != null;
    }

    public DBColumns columns() {
        if (this.columns == null) {
            this.columns = new DBColumns();
        }
        return this.columns;
    }

    @Override
    public boolean compare(DBTable table) {
        if (table == null) {
            return false;
        }
        if (table == this) {
            return true;
        }
        if (!StrUtil.equals(this.getName(), table.getName())) {
            return false;
        }
        return StrUtil.equals(this.getDbName(), table.getDbName());
    }

    public void removeColumn(DBColumn column) {
        if (column != null && this.columns != null) {
            this.columns().remove(column);
        }
    }

    /**
     * 是否新数据
     *
     * @return 结果
     */

    public boolean isNew() {
        return StrUtil.isBlank(this.getName());
    }
}


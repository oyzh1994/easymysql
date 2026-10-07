package cn.oyzh.easymysql.mysql.table;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import javafx.beans.property.SimpleStringProperty;

/**
 * db表
 *
 * @author oyzh
 * @since 2024/01/16
 */
public class MysqlTable extends DBObjectStatus implements ObjectCopier<MysqlTable>, ObjectComparator<MysqlTable> {

    /**
     * 是否存在主键
     */
    private boolean hasPrimaryKey;

    /**
     * 行格式
     */
    private String rowFormat;

    /**
     * 自动递增值
     */
    private Long autoIncrement;

    /**
     * 表创建定义
     */
    private String createDefinition;

    // /**
    //  * 索引
    //  */
    // @Getter
    // @Setter
    // private MysqlIndexes indexes;

    // /**
    //  * 触发器
    //  */
    // @Getter
    // @Setter
    // private MysqlTriggers triggers;

    // /**
    //  * 外键
    //  */
    // @Getter
    // @Setter
    // private MysqlForeignKeys foreignKeys;

    /**
     * 引擎
     */
    private String engine;

    /**
     * 字符集
     */
    private String charset;

    /**
     * 排序规则
     */
    private String collation;

    // /**
    //  * 检查器
    //  */
    // @Getter
    // @Setter
    // private MysqlChecks checks;

    /**
     * 设置引擎
     *
     * @param engine 引擎
     */
    public void setEngine(String engine) {
        this.engine = engine;
        super.putOriginalData("engine", engine);
    }

    /**
     * 判断引擎是否变更
     *
     * @return 是否变更
     */
    public boolean isEngineChanged() {
        return super.checkOriginalData("engine", this.engine);
    }

    /**
     * 设置字符集
     *
     * @param charset 字符集
     */
    public void setCharset(String charset) {
        this.charset = charset;
        super.putOriginalData("charset", charset);
    }

    /**
     * 判断字符集是否变更
     *
     * @return 是否变更
     */
    public boolean isCharsetChanged() {
        return super.checkOriginalData("charset", this.charset);
    }

    /**
     * 设置排序规则
     *
     * @param collation 排序规则
     */
    public void setCollation(String collation) {
        this.collation = collation;
        super.putOriginalData("collation", collation);
    }

    /**
     * 判断排序规则是否变更
     *
     * @return 是否变更
     */
    public boolean isCollationChanged() {
        return super.checkOriginalData("collation", this.collation);
    }

    /**
     * 设置行格式
     *
     * @param rowFormat 行格式
     */
    public void setRowFormat(String rowFormat) {
        this.rowFormat = rowFormat;
        super.putOriginalData("rowFormat", rowFormat);
        // this.updateChanged();
    }

    /**
     * 判断行格式是否变更
     *
     * @return 是否变更
     */
    public boolean isRowFormatChanged() {
        return super.checkOriginalData("rowFormat", this.rowFormat);
    }

    /**
     * 设置自动递增值
     *
     * @param autoIncrement 自动递增值
     */
    public void setAutoIncrement(Long autoIncrement) {
        this.autoIncrement = autoIncrement;
        super.putOriginalData("autoIncrement", autoIncrement);
    }

    /**
     * 判断自动递增值是否变更
     *
     * @return 是否变更
     */
    public boolean isAutoIncrementChanged() {
        return super.checkOriginalData("autoIncrement", this.autoIncrement);
    }

    // public boolean hasIndex() {
    //     return this.indexes != null && !this.indexes.isEmpty();
    // }
    //
    // public boolean hasForeignKey() {
    //     return CollUtil.isNotEmpty(this.foreignKeys);
    // }

    // public boolean hasCheck() {
    //     return CollUtil.isNotEmpty(this.checks);
    // }

    /**
     * 是否存在字符集
     *
     * @return 是否存在
     */
    public boolean hasCharset() {
        return StringUtil.isNotBlank(this.charset);
    }

    /**
     * 是否存在排序规则
     *
     * @return 是否存在
     */
    public boolean hasCollation() {
        return StringUtil.isNotBlank(this.collation);
    }

    /**
     * 是否存在引擎
     *
     * @return 是否存在
     */
    public boolean hasEngine() {
        return this.getEngine() != null;
    }

    /**
     * 根据排序规则同时设置字符集与排序规则
     *
     * @param collation 排序规则
     */
    public void setCharsetAndCollation(String collation) {
        if (StringUtil.isNotBlank(collation)) {
            String charset = collation.split("_")[0];
            this.setCharset(charset);
            this.setCollation(collation);
        }
    }

    // public boolean hasTrigger() {
    //     return this.triggers != null && !this.triggers.isEmpty();
    // }
    //
    // public MysqlIndexes indexes() {
    //     if (this.indexes == null) {
    //         this.indexes = new MysqlIndexes();
    //     }
    //     return this.indexes;
    // }
    //
    // public MysqlTriggers triggers() {
    //     if (this.triggers == null) {
    //         this.triggers = new MysqlTriggers();
    //     }
    //     return this.triggers;
    // }
    //
    // public MysqlForeignKeys foreignKeys() {
    //     if (this.foreignKeys == null) {
    //         this.foreignKeys = new MysqlForeignKeys();
    //     }
    //     return this.foreignKeys;
    // }

    // public MysqlChecks checks() {
    //     if (this.checks == null) {
    //         this.checks = new MysqlChecks();
    //     }
    //     return this.checks;
    // }


    /**
     * 是否存在自动递增值
     *
     * @return 是否存在
     */
    public boolean hasAutoIncrement() {
        return this.getAutoIncrement() != null;
    }

    @Override
    public void copy(MysqlTable table) {
        if (table != null) {
            this.setEngine(table.getEngine());
            this.setComment(table.getComment());
            this.setCharset(table.getCharset());
            this.setRowFormat(table.getRowFormat());
            this.setCollation(table.getCollation());
            this.setHasPrimaryKey(table.isHasPrimaryKey());
            this.setAutoIncrement(table.getAutoIncrement());
            this.setCreateDefinition(table.getCreateDefinition());
        }
    }

    /**
     * 是否InnoDB引擎
     *
     * @return 是否InnoDB
     */
    public boolean isInnoDB() {
        return "innodb".equalsIgnoreCase(this.getEngine());
    }

    /**
     * 是否存在行格式
     *
     * @return 是否存在
     */
    public boolean hasRowFormat() {
        return StringUtil.isNotBlank(this.getRowFormat());
    }

    // public void removeIndex(MysqlIndex index) {
    //     if (index != null && this.indexes != null) {
    //         this.indexes().remove(index);
    //     }
    // }
    //
    // public void removeTrigger(MysqlTrigger trigger) {
    //     if (trigger != null && this.triggers != null) {
    //         this.triggers().remove(trigger);
    //     }
    // }
    //
    // public void removeForeignKey(MysqlForeignKey foreignKey) {
    //     if (foreignKey != null && this.foreignKeys != null) {
    //         this.foreignKeys().remove(foreignKey);
    //     }
    // }

    // public void removeCheck(MysqlCheck check) {
    //     if (check != null && this.checks != null) {
    //         this.checks().remove(check);
    //     }
    // }

    /**
     * 库名称
     */
    private String dbName;

    /**
     * 模式名称
     */
    private String schema;

    // /**
    //  * 表字段
    //  */
    // @Setter
    // @Getter
    // protected MysqlColumns columns;

    /**
     * 表名称
     */
    private SimpleStringProperty nameProperty;

    /**
     * 表注释
     */
    private SimpleStringProperty commentProperty;

    /**
     * 获取名称属性
     *
     * @return 名称属性
     */
    public SimpleStringProperty nameProperty() {
        if (this.nameProperty == null) {
            this.nameProperty = new SimpleStringProperty();
        }
        return this.nameProperty;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.nameProperty().setValue(name);
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return this.nameProperty == null ? null : this.nameProperty.get();
    }

    /**
     * 获取注释属性
     *
     * @return 注释属性
     */
    public SimpleStringProperty commentProperty() {
        if (this.commentProperty == null) {
            this.commentProperty = new SimpleStringProperty();
        }
        return this.commentProperty;
    }

    /**
     * 设置注释
     *
     * @param comment 注释
     */
    public void setComment(String comment) {
        this.commentProperty().setValue(comment);
    }

    /**
     * 获取注释
     *
     * @return 注释
     */
    public String getComment() {
        return this.commentProperty == null ? null : this.commentProperty.get();
    }

    // public boolean primaryKeyChanged() {
    //     if (this.hasColumns()) {
    //         boolean b1 = this.columns.primaryKeyChanged();
    //         if (b1) {
    //             return true;
    //         }
    //         for (MysqlColumn column : this.columns.createdList()) {
    //             if (column.isPrimaryKey()) {
    //                 return true;
    //             }
    //         }
    //     }
    //     return false;
    // }
    //
    // public List<MysqlColumn> primaryKeys() {
    //     if (this.hasColumns()) {
    //         return this.columns.primaryKeys();
    //     }
    //     return Collections.emptyList();
    // }

    // public boolean hasPrimaryKey() {
    //     return CollUtil.isNotEmpty(this.primaryKeys());
    // }

    // public boolean hasColumns() {
    //     return this.columns != null && !this.columns.isEmpty();
    // }

    /**
     * 是否存在注释
     *
     * @return 是否存在
     */
    public boolean hasComment() {
        return this.getComment() != null;
    }

    // public MysqlColumns columns() {
    //     if (this.columns == null) {
    //         this.columns = new MysqlColumns();
    //     }
    //     return this.columns;
    // }

    @Override
    public boolean compare(MysqlTable table) {
        if (table == null) {
            return false;
        }
        if (table == this) {
            return true;
        }
        if (!StringUtil.equals(this.getName(), table.getName())) {
            return false;
        }
        return StringUtil.equals(this.getDbName(), table.getDbName());
    }

    // public void removeColumn(MysqlColumn column) {
    //     if (column != null && this.columns != null) {
    //         this.columns().remove(column);
    //     }
    // }

    /**
     * 是否新数据
     *
     * @return 结果
     */

    public boolean isNew() {
        return StringUtil.isBlank(this.getName());
    }

    /**
     * 是否存在主键
     *
     * @return 是否存在
     */
    public boolean isHasPrimaryKey() {
        return hasPrimaryKey;
    }

    /**
     * 设置是否存在主键
     *
     * @param hasPrimaryKey 是否存在主键
     */
    public void setHasPrimaryKey(boolean hasPrimaryKey) {
        this.hasPrimaryKey = hasPrimaryKey;
    }

    /**
     * 获取行格式
     *
     * @return 行格式
     */
    public String getRowFormat() {
        return rowFormat;
    }

    /**
     * 获取自动递增值
     *
     * @return 自动递增值
     */
    public Long getAutoIncrement() {
        return autoIncrement;
    }

    /**
     * 获取创建定义
     *
     * @return 创建定义
     */
    public String getCreateDefinition() {
        return createDefinition;
    }

    /**
     * 设置创建定义
     *
     * @param createDefinition 创建定义
     */
    public void setCreateDefinition(String createDefinition) {
        this.createDefinition = createDefinition;
    }

    /**
     * 获取引擎
     *
     * @return 引擎
     */
    public String getEngine() {
        return engine;
    }

    /**
     * 获取字符集
     *
     * @return 字符集
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 获取排序规则
     *
     * @return 排序规则
     */
    public String getCollation() {
        return collation;
    }

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置库名称
     *
     * @param dbName 库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取模式名称
     *
     * @return 模式名称
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置模式名称
     *
     * @param schema 模式名称
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }


}





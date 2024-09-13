package cn.oyzh.easymysql.db.view;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.fx.common.util.ObjectComparator;
import cn.oyzh.fx.common.util.ObjectCopier;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * db视图
 *
 * @author oyzh
 * @since 2024/06/28
 */
public class DBView extends DBObjectStatus implements ObjectCopier<DBView>, ObjectComparator<DBView> {

    /**
     * 定义者
     */
    @Getter
    @Setter
    private String definer;

    /**
     * 算法
     */
    @Getter
    @Setter
    private String algorithm;

    /**
     * 是否可变更
     */
    @Getter
    @Setter
    private boolean updatable;

    /**
     * 检查选项
     */
    @Getter
    @Setter
    private String checkOption;

    /**
     * 安全性
     */
    @Getter
    @Setter
    private String securityType;

    /**
     * 视图定义
     */
    private SimpleStringProperty definitionProperty;

    public SimpleStringProperty definitionProperty() {
        if (this.definitionProperty == null) {
            this.definitionProperty = new SimpleStringProperty();
        }
        return this.definitionProperty;
    }

    public void setDefinition(String definition) {
        this.definitionProperty().setValue(definition);
    }

    public String getDefinition() {
        return this.definitionProperty == null ? null : this.definitionProperty.get();
    }

    @Override
    public void copy(DBView f) {
        if (f != null) {
            this.setComment(f.getComment());
            this.setColumns(f.getColumns());
            this.setDefiner(f.getDefiner());
            this.setAlgorithm(f.getAlgorithm());
            this.setDefinition(f.getDefinition());
            this.setCheckOption(f.getCheckOption());
            this.setSecurityType(f.getSecurityType());
        }
    }

    public boolean hasCheckOption() {
        return StrUtil.isNotBlank(this.checkOption) && !StrUtil.equalsIgnoreCase(this.checkOption, "NONE");
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
    public boolean compare(DBView view) {
        if (view == null) {
            return false;
        }
        if (view == this) {
            return true;
        }
        if (!StrUtil.equals(this.getName(), view.getName())) {
            return false;
        }
        return StrUtil.equals(this.getDbName(), view.getDbName());
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

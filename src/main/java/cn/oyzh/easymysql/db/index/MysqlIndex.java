package cn.oyzh.easymysql.db.index;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.fx.table.DBIndexFieldTextFiled;
import cn.oyzh.easymysql.fx.table.DBIndexMethodComboBox;
import cn.oyzh.easymysql.fx.table.DBIndexTypeComboBox;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.common.util.ObjectCopier;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.util.TableViewUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * db表索引
 *
 * @author oyzh
 * @since 2024/01/24
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlIndex extends DBObjectStatus implements ObjectCopier<MysqlIndex> {

    /**
     * 索引顺序
     */
    @Getter
    @Setter
    private int seqIndex;

    /**
     * 类型
     * 1. normal
     * 2. unique
     * 3. fulltext
     * 4. spatial
     */
    @Getter
    private String type;

    /**
     * 方式
     * 1. null|空字符串
     * 2. btree
     * 3. hash
     */
    @Getter
    private String method;

    /**
     * 注释
     */
    @Getter
    private String comment;

    /**
     * 名称
     */
    @Getter
    private String name;

    /**
     * 字段列表
     */
    @Getter
    private List<IndexColumn> columns;

    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    public void addColumn(String column, int subPart) {
        if (this.columns == null) {
            this.setColumns(new ArrayList<>());
        }
        this.columns.add(new IndexColumn(column, subPart));
    }

    public boolean isUnique() {
        return StrUtil.equalsIgnoreCase(this.getMethod(), "UNIQUE");
    }

    // public boolean isPrimary() {
    //     return StrUtil.equalsIgnoreCase(this.getName(), "PRIMARY");
    // }

    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }
    //
    // public ClearableTextField getNameControl() {
    //     ClearableTextField textField = new ClearableTextField();
    //     textField.setPromptText(I18nHelper.pleaseInputName());
    //     textField.addTextChangeListener((observable, oldValue, newValue) -> {
    //         // if (!StrUtil.equalsIgnoreCase(newValue, this.name)) {
    //         //     if (this.oldName == null) {
    //         //         this.oldName = this.name;
    //         //     }
    //         //     this.name = newValue;
    //         //     this.setChanged(true);
    //         // }
    //         this.setName(newValue);
    //     });
    //     if (this.name != null) {
    //         textField.setText(this.name);
    //     }
    //     TableViewUtil.rowOnCtrlS(textField);
    //     TableViewUtil.selectRowOnMouseClicked(textField);
    //     return textField;
    // }

    public void setColumns(List<IndexColumn> columns) {
        this.columns = columns;
        super.putOriginalData("columns", columns);
    }

    // public DBIndexFieldTextFiled getColumnControl() {
    //     List<MysqlColumn> columnList = CacheHelper.get("columnList");
    //     DBIndexFieldTextFiled textField = new DBIndexFieldTextFiled(this, columnList, this.columns);
    //     textField.addTextChangeListener((observable, oldValue, newValue) -> this.setColumns(textField.getColumns()));
    //     textField.setFlexWidth("100% - 12");
    //     TableViewUtil.rowOnCtrlS(textField);
    //     TableViewUtil.selectRowOnMouseClicked(textField);
    //     return textField;
    // }

    public void setType(String type) {
        this.type = type;
        super.putOriginalData("type", type);
    }

    // public DBIndexTypeComboBox getTypeControl() {
    //     DBIndexTypeComboBox comboBox = new DBIndexTypeComboBox();
    //     comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
    //         // if (!StrUtil.equalsIgnoreCase(newValue, this.type)) {
    //         //     this.type = newValue;
    //         //     this.setChanged(true);
    //         // }
    //         this.setType(newValue);
    //     });
    //     comboBox.selectFirstIfNull(this.type);
    //     TableViewUtil.rowOnCtrlS(comboBox);
    //     TableViewUtil.selectRowOnMouseClicked(comboBox);
    //     return comboBox;
    // }

    public void setMethod(String method) {
        this.method = method;
        super.putOriginalData("method", method);
    }

    // public DBIndexMethodComboBox getMethodControl() {
    //     DBIndexMethodComboBox comboBox = new DBIndexMethodComboBox();
    //     comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
    //         // if (!StrUtil.equalsIgnoreCase(newValue, this.method)) {
    //         //     this.method = newValue;
    //         //     this.setChanged(true);
    //         // }
    //         this.setMethod(newValue);
    //     });
    //     comboBox.selectFirstIfNull(this.method);
    //     TableViewUtil.rowOnCtrlS(comboBox);
    //     TableViewUtil.selectRowOnMouseClicked(comboBox);
    //     return comboBox;
    // }

    public void setComment(String comment) {
        this.comment = comment;
        super.putOriginalData("comment", comment);
    }

    // public ClearableTextField getCommentControl() {
    //     ClearableTextField textField = new ClearableTextField();
    //     textField.setPromptText(I18nHelper.pleaseInputComment());
    //     textField.addTextChangeListener((observable, oldValue, newValue) -> {
    //         // if (!StrUtil.equalsIgnoreCase(newValue, this.comment)) {
    //         //     this.comment = newValue;
    //         //     this.setChanged(true);
    //         // }
    //         this.setComment(newValue);
    //     });
    //     if (this.comment != null) {
    //         textField.setText(this.comment);
    //     }
    //     TableViewUtil.rowOnCtrlS(textField);
    //     textField.setFlexWidth("100% - 12");
    //     TableViewUtil.selectRowOnMouseClicked(textField);
    //     return textField;
    // }

    public void type(String type, int noneUnique) {
        if (StrUtil.equalsIgnoreCase(type, "HASH") && noneUnique == 0) {
            this.setType("UNIQUE");
            this.setMethod("HASH");
        } else if (StrUtil.equalsIgnoreCase(type, "HASH") && noneUnique == 1) {
            this.setType("NORMAL");
            this.setMethod("HASH");
        } else if (StrUtil.equalsIgnoreCase(type, "BTREE") && noneUnique == 0) {
            this.setType("UNIQUE");
            this.setMethod("BTREE");
        } else if (StrUtil.equalsIgnoreCase(type, "BTREE") && noneUnique == 1) {
            this.setType("NORMAL");
            this.setMethod("BTREE");
        } else if (StrUtil.equalsIgnoreCase(type, "fulltext")) {
            this.setType("FULLTEXT");
            this.setMethod("");
        } else if (StrUtil.equalsIgnoreCase(type, "spatial")) {
            this.setType("SPATIAL");
            this.setMethod("");
        } else {
            this.setType("NORMAL");
            this.setMethod("BTREE");
        }
    }

    public String typeName() {
        if (this.type == null || "NORMAL".equalsIgnoreCase(this.type)) {
            return null;
        }
        return this.type.toUpperCase();
    }

    public String methodName() {
        return StrUtil.emptyToNull(this.method);
    }

    @Override
    public void copy(MysqlIndex t1) {
        if (t1 != null) {
            this.name = t1.name;
            this.type = t1.type;
            this.method = t1.method;
            this.comment = t1.comment;
            this.columns = t1.columns;
            this.seqIndex = t1.seqIndex;
        }
    }

    /**
     * 索引字段
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexColumn {

        /**
         * 字段名
         */
        private String columnName;

        /**
         * 子部分
         */
        private int subPart;

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof IndexColumn column) {
                return column.subPart == this.subPart && StrUtil.equals(this.columnName, column.columnName);
            }
            return false;
        }
    }
}

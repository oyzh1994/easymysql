package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.fx.DBDatabaseComboBox;
import cn.oyzh.easymysql.fx.table.DBFieldTextFiled;
import cn.oyzh.easymysql.fx.table.DBForeignKeyPolicyComboBox;
import cn.oyzh.easymysql.fx.table.DBTableComboBox;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.FlexTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.util.TableViewUtil;
import javafx.beans.property.SimpleStringProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * db表外键
 *
 * @author oyzh
 * @since 2024/01/25
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlForeignKey extends DBObjectStatus {

    /**
     * 外键名称
     */
    @Getter
    private String name;

    /**
     * 外键字段列表
     */
    @Getter
    private List<String> columns;

    /**
     * 引用库名称
     */
    private SimpleStringProperty primaryKeyDatabaseProperty;

    /**
     * 引用表名称
     */
    private SimpleStringProperty primaryKeyTableProperty;

    /**
     * 引用字段列表
     */
    @Getter
    private List<String> primaryKeyColumns;

    /**
     * 外键删除策略
     */
    @Getter
    private String deletePolicy;

    /**
     * 外键更新策略
     */
    @Getter
    private String updatePolicy;

    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    public SimpleStringProperty primaryKeyDatabaseProperty() {
        if (this.primaryKeyDatabaseProperty == null) {
            this.primaryKeyDatabaseProperty = new SimpleStringProperty();
        }
        return this.primaryKeyDatabaseProperty;
    }

    public SimpleStringProperty primaryKeyTableProperty() {
        if (this.primaryKeyTableProperty == null) {
            this.primaryKeyTableProperty = new SimpleStringProperty();
        }
        return this.primaryKeyTableProperty;
    }

    public void setDeletePolicy(String deletePolicy) {
        this.deletePolicy = deletePolicy;
        super.putOriginalData("deletePolicy", deletePolicy);
    }

    public void setUpdatePolicy(String updatePolicy) {
        this.updatePolicy = updatePolicy;
        super.putOriginalData("updatePolicy", updatePolicy);
    }

    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    public FlexTextField getNameControl() {
        try {
            ClearableTextField textField = new ClearableTextField();
            textField.setPromptText(I18nHelper.pleaseInputName());
            textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
            if (this.name != null) {
                textField.setText(this.name);
            }
            TableViewUtil.rowOnCtrlS(textField);
            TableViewUtil.selectRowOnMouseClicked(textField);
            return textField;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
        super.putOriginalData("columns", columns);
    }

    public DBFieldTextFiled getColumnControl() {
        try {
            List<MysqlColumn> columnList = CacheHelper.get("columnList");
            if (columnList == null) {
                columnList = new ArrayList<>();
            }
            DBFieldTextFiled textField = new DBFieldTextFiled(columnList, this.columns);
            textField.addTextChangeListener((observable, oldValue, newValue) -> this.setColumns(textField.getSelectedColumns()));
            textField.setFlexWidth("100% - 12");
            TableViewUtil.rowOnCtrlS(textField);
            TableViewUtil.selectRowOnMouseClicked(textField);
            return textField;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setPrimaryKeyDatabase(String primaryKeyDatabase) {
        this.primaryKeyDatabaseProperty().set(primaryKeyDatabase);
        super.putOriginalData("primaryKeyDatabase", primaryKeyDatabase);
    }

    public String getPrimaryKeyDatabase() {
        String dbName = null;
        if (this.primaryKeyDatabaseProperty != null) {
            dbName = this.primaryKeyDatabaseProperty.get();
        }
        if (dbName == null) {
            dbName = CacheHelper.get("dbName");
        }
        return dbName;
    }

    public DBDatabaseComboBox getPrimaryKeyDatabaseControl() {
        try {
            DBDatabaseComboBox comboBox = new DBDatabaseComboBox();
            comboBox.init(CacheHelper.get("dbClient"));
            comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setPrimaryKeyDatabase(newValue));
            comboBox.selectFirstIfNull(this.getPrimaryKeyDatabase());
            TableViewUtil.rowOnCtrlS(comboBox);
            TableViewUtil.selectRowOnMouseClicked(comboBox);
            return comboBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setPrimaryKeyTable(String primaryKeyTable) {
        this.primaryKeyTableProperty().set(primaryKeyTable);
        super.putOriginalData("primaryKeyTable", primaryKeyTable);
    }

    public String getPrimaryKeyTable() {
        if (this.primaryKeyTableProperty == null) {
            return null;
        }
        return this.primaryKeyTableProperty.get();
    }

    public DBTableComboBox getPrimaryKeyTableControl() {
        try {
            DBTableComboBox comboBox = new DBTableComboBox();
            DBClient dbClient = CacheHelper.get("dbClient");
            comboBox.init(this.getPrimaryKeyDatabase(), dbClient);
            comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setPrimaryKeyTable(newValue));
            comboBox.selectFirstIfNull(this.getPrimaryKeyTable());
            this.primaryKeyDatabaseProperty().addListener((observable, oldValue, newValue) -> {
                comboBox.init(this.getPrimaryKeyDatabase(), dbClient);
                comboBox.selectFirst();
            });
            TableViewUtil.rowOnCtrlS(comboBox);
            TableViewUtil.selectRowOnMouseClicked(comboBox);
            return comboBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public DBForeignKeyPolicyComboBox getDeletePolicyControl() {
        try {
            DBForeignKeyPolicyComboBox comboBox = new DBForeignKeyPolicyComboBox();
            comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setDeletePolicy(newValue));
            comboBox.selectFirstIfNull(this.deletePolicy);
            TableViewUtil.rowOnCtrlS(comboBox);
            TableViewUtil.selectRowOnMouseClicked(comboBox);
            return comboBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setPrimaryKeyColumns(List<String> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
        super.putOriginalData("primaryKeyColumns", primaryKeyColumns);
    }

    public DBFieldTextFiled getPrimaryKeyColumnControl() {
        try {
            DBFieldTextFiled textField = new DBFieldTextFiled();
            textField.addTextChangeListener((observable, oldValue, newValue) -> this.setPrimaryKeyColumns(textField.getSelectedColumns()));
            textField.setFlexWidth("100% - 12");
            Runnable func = () -> {
                textField.clear();
                String dbName = this.getPrimaryKeyDatabase();
                String tableName = this.getPrimaryKeyTable();
                DBClient client = CacheHelper.get("dbClient");
                textField.setColumns(client.selectColumns(new MysqlSelectColumnParam(dbName, tableName)));
                textField.setSelectedColumns(this.primaryKeyColumns);
            };
            this.primaryKeyTableProperty().addListener((observable, oldValue, newValue) -> func.run());
            func.run();
            TableViewUtil.rowOnCtrlS(textField);
            TableViewUtil.selectRowOnMouseClicked(textField);
            return textField;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public DBForeignKeyPolicyComboBox getUpdatePolicyControl() {
        try {
            DBForeignKeyPolicyComboBox comboBox = new DBForeignKeyPolicyComboBox();
            comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setUpdatePolicy(newValue));
            comboBox.selectFirstIfNull(this.updatePolicy);
            TableViewUtil.rowOnCtrlS(comboBox);
            TableViewUtil.selectRowOnMouseClicked(comboBox);
            return comboBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void addColumn(String columnName) {
        if (this.columns == null) {
            this.setColumns(new ArrayList<>());
        }
        this.columns.add(columnName);
    }

    public void addPrimaryKeyColumn(String columnName) {
        if (this.primaryKeyColumns == null) {
            this.setPrimaryKeyColumns(new ArrayList<>());
        }
        this.primaryKeyColumns.add(columnName);
    }
}

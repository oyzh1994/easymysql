package cn.oyzh.easymysql.mysql.foreignKey;

import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * db表外键
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class MysqlForeignKey extends DBObjectStatus implements ObjectCopier<MysqlForeignKey> {

    /**
     * 外键名称
     */
    private String name;

    /**
     * 外键字段列表
     */
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
    private List<String> primaryKeyColumns;

    /**
     * 外键删除策略
     */
    private String deletePolicy;

    /**
     * 外键更新策略
     */
    private String updatePolicy;

    /**
     * 获取原始名称
     *
     * @return 原始名称
     */
    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    /**
     * 获取引用库名称属性
     *
     * @return 引用库名称属性
     */
    public SimpleStringProperty primaryKeyDatabaseProperty() {
        if (this.primaryKeyDatabaseProperty == null) {
            this.primaryKeyDatabaseProperty = new SimpleStringProperty();
        }
        return this.primaryKeyDatabaseProperty;
    }

    /**
     * 获取引用表名称属性
     *
     * @return 引用表名称属性
     */
    public SimpleStringProperty primaryKeyTableProperty() {
        if (this.primaryKeyTableProperty == null) {
            this.primaryKeyTableProperty = new SimpleStringProperty();
        }
        return this.primaryKeyTableProperty;
    }

    /**
     * 设置外键删除策略
     *
     * @param deletePolicy 外键删除策略
     */
    public void setDeletePolicy(String deletePolicy) {
        this.deletePolicy = deletePolicy;
        super.putOriginalData("deletePolicy", deletePolicy);
    }

    /**
     * 设置外键更新策略
     *
     * @param updatePolicy 外键更新策略
     */
    public void setUpdatePolicy(String updatePolicy) {
        this.updatePolicy = updatePolicy;
        super.putOriginalData("updatePolicy", updatePolicy);
    }

    /**
     * 设置外键名称
     *
     * @param name 外键名称
     */
    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    // public FXTextField getNameControl() {
    //     try {
    //         ClearableTextField textField = new ClearableTextField();
    //         textField.setPromptText(I18nHelper.pleaseInputName());
    //         textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
    //         if (this.name != null) {
    //             textField.setText(this.name);
    //         }
    //         TableViewUtil.rowOnCtrlS(textField);
    //         TableViewUtil.selectRowOnMouseClicked(textField);
    //         return textField;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }

    /**
     * 设置外键字段列表
     *
     * @param columns 外键字段列表
     */
    public void setColumns(List<String> columns) {
        this.columns = columns;
        super.putOriginalData("columns", columns);
    }

    // public MysqlFieldTextFiled getColumnControl() {
    //     try {
    //         List<MysqlColumn> columnList = CacheHelper.get("columnList");
    //         if (columnList == null) {
    //             columnList = new ArrayList<>();
    //         }
    //         MysqlFieldTextFiled textField = new MysqlFieldTextFiled(columnList, this.columns);
    //         textField.addTextChangeListener((observable, oldValue, newValue) -> this.setColumns(textField.getSelectedColumns()));
    //         textField.setFlexWidth("100% - 12");
    //         TableViewUtil.rowOnCtrlS(textField);
    //         TableViewUtil.selectRowOnMouseClicked(textField);
    //         return textField;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }

    /**
     * 设置引用库名称
     *
     * @param primaryKeyDatabase 引用库名称
     */
    public void setPrimaryKeyDatabase(String primaryKeyDatabase) {
        this.primaryKeyDatabaseProperty().set(primaryKeyDatabase);
        super.putOriginalData("primaryKeyDatabase", primaryKeyDatabase);
    }

    /**
     * 获取引用库名称
     *
     * @return 引用库名称
     */
    public String getPrimaryKeyDatabase() {
        String dbName = null;
        if (this.primaryKeyDatabaseProperty != null) {
            dbName = this.primaryKeyDatabaseProperty.get();
        }
        //if (dbName == null) {
        //    dbName = CacheHelper.get("mysql:dbName");
        //}
        return dbName;
    }

    // public DBDatabaseComboBox getPrimaryKeyDatabaseControl() {
    //     try {
    //         DBDatabaseComboBox comboBox = new DBDatabaseComboBox();
    //         comboBox.init(CacheHelper.get("dbClient"));
    //         comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setPrimaryKeyDatabase(newValue));
    //         comboBox.selectFirstIfNull(this.getPrimaryKeyDatabase());
    //         TableViewUtil.rowOnCtrlS(comboBox);
    //         TableViewUtil.selectRowOnMouseClicked(comboBox);
    //         return comboBox;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }

    /**
     * 设置引用表名称
     *
     * @param primaryKeyTable 引用表名称
     */
    public void setPrimaryKeyTable(String primaryKeyTable) {
        this.primaryKeyTableProperty().set(primaryKeyTable);
        super.putOriginalData("primaryKeyTable", primaryKeyTable);
    }

    /**
     * 获取引用表名称
     *
     * @return 引用表名称
     */
    public String getPrimaryKeyTable() {
        if (this.primaryKeyTableProperty == null) {
            return null;
        }
        return this.primaryKeyTableProperty.get();
    }

    // public MysqlTableComboBox getPrimaryKeyTableControl() {
    //     try {
    //         MysqlTableComboBox comboBox = new MysqlTableComboBox();
    //         MysqlClient dbClient = CacheHelper.get("dbClient");
    //         comboBox.init(this.getPrimaryKeyDatabase(), dbClient);
    //         comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setPrimaryKeyTable(newValue));
    //         comboBox.selectFirstIfNull(this.getPrimaryKeyTable());
    //         this.primaryKeyDatabaseProperty().addListener((observable, oldValue, newValue) -> {
    //             comboBox.init(this.getPrimaryKeyDatabase(), dbClient);
    //             comboBox.selectFirst();
    //         });
    //         TableViewUtil.rowOnCtrlS(comboBox);
    //         TableViewUtil.selectRowOnMouseClicked(comboBox);
    //         return comboBox;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }

    // public MysqlForeignKeyPolicyComboBox getDeletePolicyControl() {
    //     try {
    //         MysqlForeignKeyPolicyComboBox comboBox = new MysqlForeignKeyPolicyComboBox();
    //         comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setDeletePolicy(newValue));
    //         comboBox.selectFirstIfNull(this.deletePolicy);
    //         TableViewUtil.rowOnCtrlS(comboBox);
    //         TableViewUtil.selectRowOnMouseClicked(comboBox);
    //         return comboBox;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }

    /**
     * 设置引用字段列表
     *
     * @param primaryKeyColumns 引用字段列表
     */
    public void setPrimaryKeyColumns(List<String> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
        super.putOriginalData("primaryKeyColumns", primaryKeyColumns);
    }

    // public MysqlFieldTextFiled getPrimaryKeyColumnControl() {
    //     try {
    //         MysqlFieldTextFiled textField = new MysqlFieldTextFiled();
    //         textField.addTextChangeListener((observable, oldValue, newValue) -> this.setPrimaryKeyColumns(textField.getSelectedColumns()));
    //         textField.setFlexWidth("100% - 12");
    //         Runnable func = () -> {
    //             textField.clear();
    //             String dbName = this.getPrimaryKeyDatabase();
    //             String tableName = this.getPrimaryKeyTable();
    //             MysqlClient client = CacheHelper.get("dbClient");
    //             textField.setColumns(client.selectColumns(new MysqlSelectColumnParam(dbName, tableName)));
    //             textField.setSelectedColumns(this.primaryKeyColumns);
    //         };
    //         this.primaryKeyTableProperty().addListener((observable, oldValue, newValue) -> func.run());
    //         func.run();
    //         TableViewUtil.rowOnCtrlS(textField);
    //         TableViewUtil.selectRowOnMouseClicked(textField);
    //         return textField;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }
    //
    // public MysqlForeignKeyPolicyComboBox getUpdatePolicyControl() {
    //     try {
    //         MysqlForeignKeyPolicyComboBox comboBox = new MysqlForeignKeyPolicyComboBox();
    //         comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setUpdatePolicy(newValue));
    //         comboBox.selectFirstIfNull(this.updatePolicy);
    //         TableViewUtil.rowOnCtrlS(comboBox);
    //         TableViewUtil.selectRowOnMouseClicked(comboBox);
    //         return comboBox;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }

    /**
     * 添加外键字段
     *
     * @param columnName 字段名称
     */
    public void addColumn(String columnName) {
        if (this.columns == null) {
            this.setColumns(new ArrayList<>());
        }
        this.columns.add(columnName);
    }

    /**
     * 添加引用字段
     *
     * @param columnName 字段名称
     */
    public void addPrimaryKeyColumn(String columnName) {
        if (this.primaryKeyColumns == null) {
            this.setPrimaryKeyColumns(new ArrayList<>());
        }
        this.primaryKeyColumns.add(columnName);
    }

    @Override
    public void copy(MysqlForeignKey t1) {
        if (t1 != null) {
            this.name = t1.name;
            this.columns = t1.columns;
            this.deletePolicy = t1.deletePolicy;
            this.updatePolicy = t1.updatePolicy;
            this.primaryKeyColumns = t1.primaryKeyColumns;
            this.setPrimaryKeyTable(t1.getPrimaryKeyTable());
            this.setPrimaryKeyDatabase(t1.getPrimaryKeyDatabase());
        }
    }

    /**
     * 是否无效外键（名称、字段、引用表或引用库为空）
     *
     * @return 是否无效
     */
    public boolean isInvalid() {
        return StringUtil.isBlank(this.name) || CollectionUtil.isEmpty(this.primaryKeyColumns) || CollectionUtil.isEmpty(this.columns)
                || StringUtil.isBlank(this.getPrimaryKeyTable()) || StringUtil.isBlank(this.getPrimaryKeyDatabase());
    }

    /**
     * 获取外键名称
     *
     * @return 外键名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取外键字段列表
     *
     * @return 外键字段列表
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * 获取外键删除策略
     *
     * @return 外键删除策略
     */
    public String getDeletePolicy() {
        return deletePolicy;
    }

    /**
     * 获取外键更新策略
     *
     * @return 外键更新策略
     */
    public String getUpdatePolicy() {
        return updatePolicy;
    }

    /**
     * 获取引用字段列表
     *
     * @return 引用字段列表
     */
    public List<String> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }
}

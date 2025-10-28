package cn.oyzh.easymysql.db.foreignKey;

import cn.oyzh.common.cache.CacheHelper;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.fx.DBDatabaseComboBox;
import cn.oyzh.easymysql.fx.table.DBFieldTextFiled;
import cn.oyzh.easymysql.fx.table.DBForeignKeyPolicyComboBox;
import cn.oyzh.easymysql.fx.table.DBTableComboBox;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.controls.text.field.FXTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class MysqlForeignKeyControl extends MysqlForeignKey {

    public FXTextField getNameControl() {
        try {
            ClearableTextField textField = new ClearableTextField();
            textField.setPromptText(I18nHelper.pleaseInputName());
            if (StringUtil.isEmpty(this.getName())) {
                this.setName(DBUtil.genForeignKeyName());
            }
            textField.setText(this.getName());
            textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
            TableViewUtil.rowOnCtrlS(textField);
            TableViewUtil.selectRowOnMouseClicked(textField);
            return textField;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

    public DBFieldTextFiled getColumnControl() {
        try {
            List<MysqlColumn> columnList = CacheHelper.get("columnList");
            if (columnList == null) {
                columnList = new ArrayList<>();
            }
            DBFieldTextFiled textField = new DBFieldTextFiled(columnList, this.getColumns());
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
            comboBox.selectFirstIfNull(this.getDeletePolicy());
            TableViewUtil.rowOnCtrlS(comboBox);
            TableViewUtil.selectRowOnMouseClicked(comboBox);
            return comboBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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
                textField.setSelectedColumns(this.getPrimaryKeyColumns());
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
            comboBox.selectFirstIfNull(this.getUpdatePolicy());
            TableViewUtil.rowOnCtrlS(comboBox);
            TableViewUtil.selectRowOnMouseClicked(comboBox);
            return comboBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static MysqlForeignKeyControl of(MysqlForeignKey foreignKey) {
        MysqlForeignKeyControl control = new MysqlForeignKeyControl();
        control.copy(foreignKey);
        return control;
    }

    public static List<MysqlForeignKeyControl> of(List<MysqlForeignKey> foreignKeys) {
        List<MysqlForeignKeyControl> controls = new ArrayList<>();
        for (MysqlForeignKey foreignKey : foreignKeys) {
            controls.add(of(foreignKey));
        }
        return controls;
    }


}

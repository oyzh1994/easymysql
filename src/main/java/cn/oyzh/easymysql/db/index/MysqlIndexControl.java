package cn.oyzh.easymysql.db.index;

import cn.oyzh.common.cache.CacheHelper;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.fx.table.DBIndexFieldTextFiled;
import cn.oyzh.easymysql.fx.table.DBIndexMethodComboBox;
import cn.oyzh.easymysql.fx.table.DBIndexTypeComboBox;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/14
 */
public class MysqlIndexControl extends MysqlIndex {

    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setText(this.getName());
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public DBIndexFieldTextFiled getColumnControl() {
        List<MysqlColumn> columnList = CacheHelper.get("columnList");
        DBIndexFieldTextFiled textField = new DBIndexFieldTextFiled(this, columnList, this.getColumns());
        textField.setFlexWidth("100% - 12");
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setColumns(textField.getColumns()));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public DBIndexTypeComboBox getTypeControl() {
        DBIndexTypeComboBox comboBox = new DBIndexTypeComboBox();
        comboBox.selectFirstIfNull(this.getType());
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public DBIndexMethodComboBox getMethodControl() {
        DBIndexMethodComboBox comboBox = new DBIndexMethodComboBox();
        comboBox.selectFirstIfNull(this.getMethod());
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setMethod(newValue));
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public ClearableTextField getCommentControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setFlexWidth("100% - 12");
        textField.setPromptText(I18nHelper.pleaseInputComment());
        textField.setText(this.getComment());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setComment(newValue));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public static MysqlIndexControl of(MysqlIndex index) {
        MysqlIndexControl control = new MysqlIndexControl();
        control.copy(index);
        return control;
    }

    public static List<MysqlIndexControl> of(List<MysqlIndex> indices) {
        List<MysqlIndexControl> controls = new ArrayList<>();
        for (MysqlIndex index : indices) {
            controls.add(of(index));
        }
        return controls;
    }
}

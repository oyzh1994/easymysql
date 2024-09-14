package cn.oyzh.easymysql.db.index;

import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.fx.table.DBIndexFieldTextFiled;
import cn.oyzh.easymysql.fx.table.DBIndexMethodComboBox;
import cn.oyzh.easymysql.fx.table.DBIndexTypeComboBox;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.util.TableViewUtil;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/14
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlIndexControl extends MysqlIndex {

    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        textField.setText(this.getName());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public DBIndexFieldTextFiled getColumnControl() {
        List<MysqlColumn> columnList = CacheHelper.get("columnList");
        DBIndexFieldTextFiled textField = new DBIndexFieldTextFiled(this, columnList, this.getColumns());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setColumns(textField.getColumns()));
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public DBIndexTypeComboBox getTypeControl() {
        DBIndexTypeComboBox comboBox = new DBIndexTypeComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        comboBox.selectFirstIfNull(this.getType());
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public DBIndexMethodComboBox getMethodControl() {
        DBIndexMethodComboBox comboBox = new DBIndexMethodComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setMethod(newValue));
        comboBox.selectFirstIfNull(this.getMethod());
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public ClearableTextField getCommentControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputComment());
        textField.addTextChangeListener((observable, oldValue, newValue) -> {
            this.setComment(newValue);
        });
        textField.setText(this.getComment());
        TableViewUtil.rowOnCtrlS(textField);
        textField.setFlexWidth("100% - 12");
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public static MysqlIndexControl of(MysqlIndex column) {
        MysqlIndexControl control = new MysqlIndexControl();
        control.copy(column);
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

package cn.oyzh.easymysql.db.column;

import cn.oyzh.easymysql.fx.table.DBFiledTypeComboBox;
import cn.oyzh.easymysql.popups.DBColumnConfigPopupController;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.ConfigurationSVGGlyph;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.util.TableViewUtil;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/14
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlColumnControl extends MysqlColumn {

    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        textField.setText(this.getName());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public ClearableTextField getCommentControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputComment());
        if (this.getComment() != null) {
            textField.setText(this.getComment());
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setComment(newValue));
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public NumberTextField getSizeControl() {
        NumberTextField textField = new NumberTextField();
        if (this.getSize() != null) {
            textField.setValue(this.getSize());
        } else if (this.supportSize() && this.isCreated() && this.suggestSize() != null) {
            textField.setValue(this.getSize());
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setSize(textField.getIntValue()));
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public NumberTextField getDigitsControl() {
        NumberTextField textField = new NumberTextField();
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setDigits(textField.getIntValue()));
        textField.setValue(this.getDigits());
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public DBFiledTypeComboBox getTypeControl() {
        DBFiledTypeComboBox comboBox = new DBFiledTypeComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        comboBox.selectFirstIfNull(this.getType());
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public FlexCheckBox getNullableControl() {
        FlexCheckBox checkBox = new FlexCheckBox();
        checkBox.selectedChanged((observable, oldValue, newValue) -> this.setNullable(newValue));
        checkBox.setSelected(this.isNullable());
        // 监听主键值变化
        this.primaryKeyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                checkBox.setSelected(false);
            }
        });
        TableViewUtil.rowOnCtrlS(checkBox);
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    public FlexCheckBox getPrimaryKeyControl() {
        FlexCheckBox checkBox = new FlexCheckBox();
        checkBox.selectedChanged((observable, oldValue, newValue) -> this.setPrimaryKey(newValue));
        checkBox.setSelected(this.isPrimaryKey());
        TableViewUtil.rowOnCtrlS(checkBox);
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    public ConfigurationSVGGlyph getConfigControl() {
        ConfigurationSVGGlyph glyph = new ConfigurationSVGGlyph();
        glyph.setOnMousePrimaryClicked(event -> {
            PopupAdapter popup = PopupManager.parsePopup(DBColumnConfigPopupController.class);
            popup.setProp("dbColumn", this);
            popup.setProp("dbClient", CacheHelper.get("dbClient"));
            popup.showPopup(glyph);
        });
        TableViewUtil.selectRowOnMouseClicked(glyph);
        return glyph;
    }

    public static MysqlColumnControl of(MysqlColumn column) {
        MysqlColumnControl control = new MysqlColumnControl();
        control.copy(column);
        return control;
    }

    public static List<MysqlColumnControl> of(List<MysqlColumn> columns) {
        List<MysqlColumnControl> controls = new ArrayList<>();
        for (MysqlColumn column : columns) {
            controls.add(of(column));
        }
        return controls;
    }
}

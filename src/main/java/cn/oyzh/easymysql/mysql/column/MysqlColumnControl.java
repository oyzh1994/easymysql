package cn.oyzh.easymysql.mysql.column;

import cn.oyzh.easymysql.fx.table.MysqlFiledTypeComboBox;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * MySQL字段控件
 *
 * @author oyzh
 * @since 2024/09/14
 */
public class MysqlColumnControl extends MysqlColumn {

    /**
     * 获取名称控件
     *
     * @return 名称控件
     */
    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.setText(this.getName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取注释控件
     *
     * @return 注释控件
     */
    public ClearableTextField getCommentControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputComment());
        textField.setFlexWidth("100% - 12");
        textField.setText(this.getComment());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setComment(newValue));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取大小控件
     *
     * @return 大小控件
     */
    public NumberTextField getSizeControl() {
        NumberTextField textField = new NumberTextField();
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        if (this.getSize() != null) {
            textField.setValue(this.getSize());
        } else if (this.supportSize() && this.isCreated() && this.suggestSize() != null) {
            textField.setValue(this.getSize());
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setSize(textField.getIntValue()));
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取小数位控件
     *
     * @return 小数位控件
     */
    public NumberTextField getDigitsControl() {
        NumberTextField textField = new NumberTextField();
        textField.setFlexWidth("100% - 12");
        textField.setValue(this.getDigits());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setDigits(textField.getIntValue()));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取类型控件
     *
     * @return 类型控件
     */
    public MysqlFiledTypeComboBox getTypeControl() {
        MysqlFiledTypeComboBox comboBox = new MysqlFiledTypeComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        comboBox.selectFirstIfNull(this.getType());
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取可为null控件
     *
     * @return 可为null控件
     */
    public FXCheckBox getNullableControl() {
        FXCheckBox checkBox = new FXCheckBox();
        checkBox.setSelected(this.isNullable());
        checkBox.selectedChanged((observable, oldValue, newValue) -> this.setNullable(newValue));
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

    /**
     * 获取主键控件
     *
     * @return 主键控件
     */
    public FXCheckBox getPrimaryKeyControl() {
        FXCheckBox checkBox = new FXCheckBox();
        checkBox.setSelected(this.isPrimaryKey());
        checkBox.selectedChanged((observable, oldValue, newValue) -> {
            this.setPrimaryKey(newValue);
        });
        TableViewUtil.rowOnCtrlS(checkBox);
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    // public ConfigurationSVGGlyph getConfigControl() {
    //     ConfigurationSVGGlyph glyph = new ConfigurationSVGGlyph();
    //     glyph.setOnMousePrimaryClicked(event -> {
    //         PopupAdapter popup = PopupManager.parsePopup(DBColumnConfigPopupController.class);
    //         popup.setProp("dbColumn", this);
    //         popup.setProp("dbClient", CacheHelper.get("dbClient"));
    //         popup.showPopup(glyph);
    //     });
    //     TableViewUtil.selectRowOnMouseClicked(glyph);
    //     return glyph;
    // }

    /**
     * 根据字段构建字段控件
     *
     * @param column 字段
     * @return 字段控件
     */
    public static MysqlColumnControl of(MysqlColumn column) {
        MysqlColumnControl control = new MysqlColumnControl();
        control.copy(column);
        return control;
    }

    /**
     * 根据字段列表构建字段控件列表
     *
     * @param columns 字段列表
     * @return 字段控件列表
     */
    public static List<MysqlColumnControl> of(List<MysqlColumn> columns) {
        List<MysqlColumnControl> controls = new ArrayList<>();
        for (MysqlColumn column : columns) {
            controls.add(of(column));
        }
        return controls;
    }
}

package cn.oyzh.easymysql.db.trigger;

import cn.oyzh.easymysql.fx.table.DBTriggerPolicyComboBox;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.EnlargeTextFiled;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/14
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlTriggerControl extends MysqlTrigger {

    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> {
            this.setName(newValue);
        });
        textField.setText(this.getName());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public DBTriggerPolicyComboBox getPolicyControl() {
        DBTriggerPolicyComboBox comboBox = new DBTriggerPolicyComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
            this.setPolicy(newValue);
        });
        comboBox.selectFirstIfNull(this.getPolicy());
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public EnlargeTextFiled getDefinitionControl() {
        EnlargeTextFiled textField = new EnlargeTextFiled();
        textField.setPromptText(I18nHelper.pleaseInputContent());
        textField.setText(this.getDefinition());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setDefinition(newValue));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public static MysqlTriggerControl of(MysqlTrigger trigger) {
        MysqlTriggerControl control = new MysqlTriggerControl();
        control.copy(trigger);
        return control;
    }

    public static List<MysqlTriggerControl> of(List<MysqlTrigger> triggers) {
        List<MysqlTriggerControl> controls = new ArrayList<>();
        for (MysqlTrigger trigger : triggers) {
            controls.add(of(trigger));
        }
        return controls;
    }
}

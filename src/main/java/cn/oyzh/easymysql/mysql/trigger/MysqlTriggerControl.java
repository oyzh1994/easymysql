package cn.oyzh.easymysql.mysql.trigger;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.fx.table.MysqlTriggerPolicyComboBox;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.EnlargeTextFiled;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 表触发器控件
 *
 * @author oyzh
 * @since 2024/09/14
 */
public class MysqlTriggerControl extends MysqlTrigger {

    /**
     * 获取名称控件
     *
     * @return 名称控件
     */
    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        if (StringUtil.isEmpty(this.getName())) {
            this.setName(DBUtil.genTriggerName());
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> {
            this.setName(newValue);
        });
        textField.setText(this.getName());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取策略控件
     *
     * @return 策略控件
     */
    public MysqlTriggerPolicyComboBox getPolicyControl() {
        MysqlTriggerPolicyComboBox comboBox = new MysqlTriggerPolicyComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
            this.setPolicy(newValue);
        });
        comboBox.selectFirstIfNull(this.getPolicy());
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取定义控件
     *
     * @return 定义控件
     */
    public EnlargeTextFiled getDefinitionControl() {
        EnlargeTextFiled textField = new EnlargeTextFiled();
        textField.setPromptText(I18nHelper.pleaseInputContent());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setDefinition(newValue));
        textField.setText(this.getDefinition());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 由触发器构建控件
     *
     * @param trigger 触发器
     * @return 触发器控件
     */
    public static MysqlTriggerControl of(MysqlTrigger trigger) {
        MysqlTriggerControl control = new MysqlTriggerControl();
        control.copy(trigger);
        return control;
    }

    /**
     * 由触发器列表构建控件列表
     *
     * @param triggers 触发器列表
     * @return 触发器控件列表
     */
    public static List<MysqlTriggerControl> of(List<MysqlTrigger> triggers) {
        List<MysqlTriggerControl> controls = new ArrayList<>();
        for (MysqlTrigger trigger : triggers) {
            controls.add(of(trigger));
        }
        return controls;
    }
}

package cn.oyzh.easymysql.mysql.check;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查约束编辑控件
 *
 * @author oyzh
 * @since 2024/09/11
 */
public class MysqlCheckControl extends MysqlCheck {

    /**
     * 获取名称控件
     *
     * @return 名称控件
     */
    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        if (StringUtil.isEmpty(this.getName())) {
            this.setName(DBUtil.genCheckName());
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        textField.setText(this.getName());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取子语句控件
     *
     * @return 子语句控件
     */
    public ClearableTextField getClauseControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setClause(newValue));
        textField.setText(this.getClause());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 将检查约束转换为控件
     *
     * @param check 检查约束
     * @return 检查约束控件
     */
    public static MysqlCheckControl of(MysqlCheck check) {
        MysqlCheckControl control = new MysqlCheckControl();
        control.copy(check);
        return control;
    }

    /**
     * 将检查约束列表转换为控件列表
     *
     * @param checks 检查约束列表
     * @return 检查约束控件列表
     */
    public static List<MysqlCheckControl> of(List<MysqlCheck> checks) {
        List<MysqlCheckControl> controls = new ArrayList<>();
        for (MysqlCheck check : checks) {
            controls.add(of(check));
        }
        return controls;
    }
}

package cn.oyzh.easymysql.mysql.index;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.fx.table.MysqlIndexFieldTextFiled;
import cn.oyzh.easymysql.fx.table.MysqlIndexMethodComboBox;
import cn.oyzh.easymysql.fx.table.MysqlIndexTypeComboBox;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * MySQL索引控件
 *
 * @author oyzh
 * @since 2024/09/14
 */
public class MysqlIndexControl extends MysqlIndex {

    /**
     * 字段列表
     */
    private List<MysqlColumn> columnList;

    /**
     * 设置字段列表
     *
     * @param columnList 字段列表
     */
    public void setColumnList(List<MysqlColumn> columnList) {
        this.columnList = columnList;
    }

    /**
     * 获取名称控件
     *
     * @return 名称控件
     */
    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        if (StringUtil.isEmpty(this.getName())) {
            this.setName(DBUtil.genIndexName());
        }
        textField.setText(this.getName());
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取字段控件
     *
     * @return 字段控件
     */
    public MysqlIndexFieldTextFiled getColumnControl() {
        //List<MysqlColumn> columnList = CacheHelper.get("mysql:columnList");
        if (this.columnList == null) {
            this.columnList = null;
        }
        MysqlIndexFieldTextFiled textField = new MysqlIndexFieldTextFiled(this, this.columnList, this.getColumns());
        textField.setFlexWidth("100% - 12");
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setColumns(textField.getColumns()));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取类型控件
     *
     * @return 类型控件
     */
    public MysqlIndexTypeComboBox getTypeControl() {
        MysqlIndexTypeComboBox comboBox = new MysqlIndexTypeComboBox();
        comboBox.selectFirstIfNull(this.getType());
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        // 初始化数据
        this.setType(comboBox.getValue());
        return comboBox;
    }

    /**
     * 获取方式控件
     *
     * @return 方式控件
     */
    public MysqlIndexMethodComboBox getMethodControl() {
        MysqlIndexMethodComboBox comboBox = new MysqlIndexMethodComboBox();
        comboBox.selectFirstIfNull(this.getMethod());
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setMethod(newValue));
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取注释控件
     *
     * @return 注释控件
     */
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

    /**
     * 根据索引构建索引控件
     *
     * @param index 索引
     * @return 索引控件
     */
    public static MysqlIndexControl of(MysqlIndex index) {
        MysqlIndexControl control = new MysqlIndexControl();
        control.copy(index);
        return control;
    }

    /**
     * 根据索引列表构建索引控件列表
     *
     * @param indices 索引列表
     * @return 索引控件列表
     */
    public static List<MysqlIndexControl> of(List<MysqlIndex> indices) {
        List<MysqlIndexControl> controls = new ArrayList<>();
        for (MysqlIndex index : indices) {
            controls.add(of(index));
        }
        return controls;
    }
}

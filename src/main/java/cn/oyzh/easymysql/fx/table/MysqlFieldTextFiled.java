package cn.oyzh.easymysql.fx.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.easymysql.popups.MysqlColumnFieldPopupController;
import cn.oyzh.fx.gui.text.field.ChooseTextField;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.i18n.I18nHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * db字段文本框
 *
 * @author oyzh
 * @since 2024/7/10
 */
public class MysqlFieldTextFiled extends ChooseTextField {

    {
        super.setAction(this::initPopup);
        this.setPromptText(I18nHelper.pleaseSelectField());
    }

    /**
     * 构造db字段文本框
     */
    public MysqlFieldTextFiled() {
    }

    /**
     * 字段列表
     */
    private List<MysqlColumn> columns;

    /**
     * 已选中的字段名称列表
     */
    private List<String> selectedColumns;

    /**
     * 构造db字段文本框
     *
     * @param columns         字段列表
     * @param selectedColumns 已选中的字段名称列表
     */
    public MysqlFieldTextFiled(List<MysqlColumn> columns, List<String> selectedColumns) {
        this.columns = columns;
        this.setSelectedColumns(selectedColumns);
    }

    /**
     * 弹窗组件
     */
    private PopupAdapter popup;

    /**
     * 初始化弹窗
     */
    protected void initPopup() {
        this.popup = PopupManager.parsePopup(MysqlColumnFieldPopupController.class);
        this.popup.setProp("columns", this.columns);
        this.popup.setProp("selectedColumns", this.selectedColumns);
        this.popup.setProp("onSubmit", (Runnable) () -> {
            MysqlColumnListView listView = this.listView();
            if (listView != null) {
                this.selectedColumns = listView.getSelectedColumnNames();
            }
            this.initText();
        });
        this.popup.showPopup(this);
    }

    /**
     * 设置字段列表
     *
     * @param columns 字段列表
     */
    public void setColumns(List<MysqlColumn> columns) {
        this.columns = columns;
        MysqlColumnListView listView = this.listView();
        if (listView != null) {
            listView.init(columns);
        }
        this.initText();
    }

    /**
     * 设置已选中的字段名称列表
     *
     * @param selectedColumns 已选中的字段名称列表
     */
    public void setSelectedColumns(List<String> selectedColumns) {
        this.selectedColumns = selectedColumns;
        MysqlColumnListView listView = this.listView();
        if (listView != null) {
            listView.select(selectedColumns);
        }
        this.initText();
    }

    /**
     * 获取已选中的字段名称列表
     *
     * @return 已选中的字段名称列表
     */
    public List<String> getSelectedColumns() {
        return Objects.requireNonNullElse(this.selectedColumns, Collections.emptyList());
    }

    /**
     * 初始化文本
     */
    protected void initText() {
        String text = "";
        if (CollectionUtil.isNotEmpty(this.selectedColumns)) {
            text = CollectionUtil.join(this.selectedColumns, ",");
        }
        this.setText(text);
        this.setTipText(text);
    }

    /**
     * 获取列表视图
     *
     * @return 列表视图
     */
    protected MysqlColumnListView listView() {
        if (this.popup != null && this.popup.content() != null) {
            return (MysqlColumnListView) this.popup.content().lookup("#listView");
        }
        return null;
    }
}

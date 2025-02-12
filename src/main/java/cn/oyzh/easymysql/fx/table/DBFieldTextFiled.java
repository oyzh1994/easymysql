package cn.oyzh.easymysql.fx.table;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.popups.DBColumnFieldPopupController;
import cn.oyzh.fx.gui.text.field.ChooseTextField;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.i18n.I18nHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2024/7/10
 */
public class DBFieldTextFiled extends ChooseTextField {

    {
        super.setAction(this::initPopup);
        this.setPromptText(I18nHelper.pleaseSelectField());
    }

    public DBFieldTextFiled() {
    }

    private List<MysqlColumn> columns;

    private List<String> selectedColumns;

    public DBFieldTextFiled(List<MysqlColumn> columns, List<String> selectedColumns) {
        this.columns = columns;
        this.setSelectedColumns(selectedColumns);
    }

    private PopupAdapter popup;

    protected void initPopup() {
        this.popup = PopupManager.parsePopup(DBColumnFieldPopupController.class);
        this.popup.setProp("columns", this.columns);
        this.popup.setProp("selectedColumns", this.selectedColumns);
        this.popup.setProp("onSubmit", (Runnable) () -> {
            DBColumnListView listView = this.listView();
            if (listView != null) {
                this.selectedColumns = listView.getSelectedColumnNames();
            }
            this.initText();
        });
        this.popup.showPopup(this);
    }

    public void setColumns(List<MysqlColumn> columns) {
        this.columns = columns;
        DBColumnListView listView = this.listView();
        if (listView != null) {
            listView.init(columns);
        }
        this.initText();
    }

    public void setSelectedColumns(List<String> selectedColumns) {
        this.selectedColumns = selectedColumns;
        DBColumnListView listView = this.listView();
        if (listView != null) {
            listView.select(selectedColumns);
        }
        this.initText();
    }

    public List<String> getSelectedColumns() {
        return Objects.requireNonNullElse(this.selectedColumns, Collections.emptyList());
    }

    protected void initText() {
        String text = "";
        if (CollUtil.isNotEmpty(this.selectedColumns)) {
            text = CollUtil.join(this.selectedColumns, ",");
        }
        this.setText(text);
        this.setTipText(text);
    }

    protected DBColumnListView listView() {
        if (this.popup != null && this.popup.content() != null) {
            return (DBColumnListView) this.popup.content().lookup("#listView");
        }
        return null;
    }
}

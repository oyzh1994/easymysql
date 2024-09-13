package cn.oyzh.easymysql.fx.table;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBIndex;
import cn.oyzh.easymysql.popups.DBIndexFieldPopupController;
import cn.oyzh.fx.plus.controls.textfield.ChooseTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import lombok.Getter;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/7/16
 */
public class DBIndexFieldTextFiled extends ChooseTextField {

    {
        super.setAction(this::initPopup);
        this.setPromptText(I18nHelper.pleaseSelectField());
    }

    public DBIndexFieldTextFiled() {
    }

    private DBIndex dbIndex;

    private List<DBColumn> columnList;

    @Getter
    private List<DBIndex.IndexColumn> columns;

    public DBIndexFieldTextFiled(DBIndex dbIndex, List<DBColumn> columnList, List<DBIndex.IndexColumn> columns) {
        this.dbIndex = dbIndex;
        this.columnList = columnList;
        this.setColumns(columns);
    }

    private PopupAdapter popup;

    protected void initPopup() {
        this.disable();
        this.popup = PopupManager.parsePopup(DBIndexFieldPopupController.class);
        this.popup.setProp("dbIndex", this.dbIndex);
        this.popup.setProp("columns", this.columns);
        this.popup.setProp("columnList", this.columnList);
        this.popup.setProp("onSubmit", (Runnable) () -> {
            this.enable();
            this.skin().resetButtonColor();
            DBIndexColumnListView listView = this.listView();
            if (listView != null) {
                this.columns = listView.getColumns();
            }
            this.initText();
        });
        this.popup.popup().setOnHiding(event -> {
            this.enable();
            this.skin().resetButtonColor();
        });
        this.popup.showPopup(this);
    }

    public void setColumns(List<DBIndex.IndexColumn> columns) {
        this.columns = columns;
        this.initText();
    }

    protected void initText() {
        String text;
        StringBuilder builder = new StringBuilder();
        if (CollUtil.isNotEmpty(this.columns)) {
            for (DBIndex.IndexColumn column : this.columns) {
                builder.append(",");
                builder.append(column.getColumnName());
                if (column.getSubPart() > 0) {
                    builder.append("(").append(column.getSubPart()).append(")");
                }
            }
            text = builder.substring(1);
        } else {
            text = "";
        }
        this.setText(text);
        this.setTipText(text);
    }

    protected DBIndexColumnListView listView() {
        if (this.popup != null && this.popup.content() != null) {
            return (DBIndexColumnListView) this.popup.content().lookup("#listView");
        }
        return null;
    }
}

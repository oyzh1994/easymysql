package cn.oyzh.easymysql.fx.table;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.index.MysqlIndex;
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

    private MysqlIndex dbIndex;

    private List<MysqlColumn> columnList;

    @Getter
    private List<MysqlIndex.IndexColumn> columns;

    public DBIndexFieldTextFiled(MysqlIndex dbIndex, List<MysqlColumn> columnList, List<MysqlIndex.IndexColumn> columns) {
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

    public void setColumns(List<MysqlIndex.IndexColumn> columns) {
        this.columns = columns;
        this.initText();
    }

    protected void initText() {
        String text;
        StringBuilder builder = new StringBuilder();
        if (CollUtil.isNotEmpty(this.columns)) {
            for (MysqlIndex.IndexColumn column : this.columns) {
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

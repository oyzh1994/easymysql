package cn.oyzh.easymysql.fx.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.easymysql.mysql.index.MysqlIndex;
import cn.oyzh.easymysql.popups.MysqlIndexFieldPopupController;
import cn.oyzh.fx.gui.text.field.ChooseTextField;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.i18n.I18nHelper;

import java.util.List;

/**
 * db索引字段文本框
 *
 * @author oyzh
 * @since 2024/7/16
 */
public class MysqlIndexFieldTextFiled extends ChooseTextField {

    {
        super.setAction(this::initPopup);
        this.setPromptText(I18nHelper.pleaseSelectField());
    }

    /**
     * 构造db索引字段文本框
     */
    public MysqlIndexFieldTextFiled() {
    }

    /**
     * 索引
     */
    private MysqlIndex dbIndex;

    /**
     * 字段列表
     */
    private List<MysqlColumn> columnList;

    /**
     * 索引字段列表
     */
    private List<MysqlIndex.IndexColumn> columns;

    /**
     * 构造db索引字段文本框
     *
     * @param dbIndex    索引
     * @param columnList 字段列表
     * @param columns    索引字段列表
     */
    public MysqlIndexFieldTextFiled(MysqlIndex dbIndex, List<MysqlColumn> columnList, List<MysqlIndex.IndexColumn> columns) {
        this.dbIndex = dbIndex;
        this.columnList = columnList;
        this.setColumns(columns);
    }

    /**
     * 弹窗组件
     */
    private PopupAdapter popup;

    /**
     * 初始化弹窗
     */
    protected void initPopup() {
        this.disable();
        this.popup = PopupManager.parsePopup(MysqlIndexFieldPopupController.class);
        this.popup.setProp("dbIndex", this.dbIndex);
        this.popup.setProp("columns", this.columns);
        this.popup.setProp("columnList", this.columnList);
        this.popup.setProp("onSubmit", (Runnable) () -> {
            this.enable();
            this.skin().resetButtonColor();
            MysqlIndexColumnListView listView = this.listView();
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

    /**
     * 设置索引字段列表
     *
     * @param columns 索引字段列表
     */
    public void setColumns(List<MysqlIndex.IndexColumn> columns) {
        this.columns = columns;
        this.initText();
    }

    /**
     * 初始化文本
     */
    protected void initText() {
        String text;
        StringBuilder builder = new StringBuilder();
        if (CollectionUtil.isNotEmpty(this.columns)) {
            for (MysqlIndex.IndexColumn column : this.columns) {
                builder.append(",");
                builder.append(column.getColumnName());
                if (column.getSubPart() != null && column.getSubPart() > 0) {
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

    /**
     * 获取列表视图
     *
     * @return 列表视图
     */
    protected MysqlIndexColumnListView listView() {
        if (this.popup != null && this.popup.content() != null) {
            return (MysqlIndexColumnListView) this.popup.content().lookup("#listView");
        }
        return null;
    }

    /**
     * 获取索引字段列表
     *
     * @return 索引字段列表
     */
    public List<MysqlIndex.IndexColumn> getColumns() {
        return columns;
    }
}

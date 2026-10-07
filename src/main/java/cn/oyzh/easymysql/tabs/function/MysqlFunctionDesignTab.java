package cn.oyzh.easymysql.tabs.function;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.mysql.function.MysqlFunction;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.FunctionSVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * db函数设计标签页
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class MysqlFunctionDesignTab extends MysqlTab {

    @Override
    protected String url() {
        return super.getBasePath() + "function/mysqlFunctionDesignTab.fxml";
    }

    @Override
    public void flushGraphic() {
        FunctionSVGGlyph graphic = (FunctionSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new FunctionSVGGlyph("12");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String name = this.functionName();
        if (StringUtil.isBlank(name)) {
            name = I18nHelper.unnamedFunction();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + name + "@" + this.dbName() + "(" + this.connectName() + ")");
        } else {
            this.setText(name + "@" + this.dbName() + "(" + this.connectName() + ")");
        }
    }

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String functionName() {
        return this.controller().getFunction().getName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.controller().getDbItem();
    }

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String dbName() {
        return this.dbItem().dbName();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
        return this.dbItem().connectName();
    }

    /**
     * 初始化
     *
     * @param function 函数对象
     * @param item     数据库树节点
     */
    public void init(MysqlFunction function, MysqlDatabaseTreeItem item) {
        this.controller().init(function, item);
        // 刷新tab
        this.flush();
    }

    @Override
    public MysqlFunctionDesignTabController controller() {
        return (MysqlFunctionDesignTabController) super.controller();
    }

    /**
     * 是否未保存
     *
     * @return 结果
     */
    public boolean isUnsaved() {
        return this.controller().isUnsaved();
    }

    @Override
    protected void onTabCloseRequest(Event event) {
        if (this.isUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            event.consume();
        } else {
            this.closeTab();
        }
    }
}

package cn.oyzh.easymysql.tabs.function;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.routine.MysqlFunction;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.controls.svg.FunctionSVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * db查询tab
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class MysqlFunctionDesignTab extends MysqlTab {

    {
        this.setClosable(true);
    }

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
        if (StrUtil.isBlank(name)) {
            name = I18nHelper.unnamedFunction();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + this.dbItem().dbName() + "-" + name);
        } else {
            this.setText(this.dbItem().dbName() + "-" + name);
        }
    }

    public String functionName() {
        return this.controller().function().getName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.controller().dbItem();
    }

    /**
     * 初始化
     *
     * @param function 查询对象
     * @param item     db库树节点
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

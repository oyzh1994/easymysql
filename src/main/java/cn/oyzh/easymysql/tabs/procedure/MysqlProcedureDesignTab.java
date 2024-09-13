package cn.oyzh.easymysql.tabs.procedure;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.routine.MysqlProcedure;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.controls.svg.ProcedureSVGGlyph;
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
public class MysqlProcedureDesignTab extends MysqlTab {

    {
        this.setClosable(true);
    }

    @Override
    protected String url() {
        return super.getBasePath() + "procedure/mysqlProcedureDesignTab.fxml";
    }

    @Override
    public void flushGraphic() {
        ProcedureSVGGlyph graphic = (ProcedureSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new ProcedureSVGGlyph("12");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String name = this.procedureName();
        if (StrUtil.isBlank(name)) {
            name = I18nHelper.unnamedProcedure();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + this.dbItem().dbName() + "-" + name);
        } else {
            this.setText(this.dbItem().dbName() + "-" + name);
        }
    }

    public MysqlProcedure procedure() {
        return this.controller().procedure();
    }

    public String procedureName() {
        return this.procedure().getName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.controller().dbItem();
    }

    /**
     * 初始化
     *
     * @param procedure 查询对象
     * @param item      db库树节点
     */
    public void init(MysqlProcedure procedure, MysqlDatabaseTreeItem item) {
        this.controller().init(procedure, item);
        // 刷新tab
        this.flush();
    }

    @Override
    public MysqlProcedureDesignTabController controller() {
        return (MysqlProcedureDesignTabController) super.controller();
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

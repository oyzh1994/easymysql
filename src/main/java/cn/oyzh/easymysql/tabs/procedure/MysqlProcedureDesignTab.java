package cn.oyzh.easymysql.tabs.procedure;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.mysql.procedure.MysqlProcedure;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.ProcedureSVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * db过程设计标签页
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class MysqlProcedureDesignTab extends MysqlTab {

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
        if (StringUtil.isBlank(name)) {
            name = I18nHelper.unnamedProcedure();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + name + "@" + this.dbName() + "(" + this.connectName() + ")");
        } else {
            this.setText(name + "@" + this.dbName() + "(" + this.connectName() + ")");
        }
    }

    /**
     * 获取过程对象
     *
     * @return 过程对象
     */
    public MysqlProcedure procedure() {
        return this.controller().getProcedure();
    }

    /**
     * 获取过程名称
     *
     * @return 过程名称
     */
    public String procedureName() {
        return this.procedure().getName();
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
     * @param procedure 过程对象
     * @param item      数据库树节点
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

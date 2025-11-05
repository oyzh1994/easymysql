package cn.oyzh.easymysql.tabs.query;

import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * db查询tab
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class MysqlQueryMainTab extends MysqlTab {

    {
        this.setClosable(true);
    }

    // /**
    //  * 内容已变化
    //  */
    // private boolean contentChanged;
    //
    // public void setContentChanged(boolean contentChanged) {
    //     this.contentChanged = contentChanged;
    //     this.flush();
    // }

    @Override
    protected String url() {
        return super.getBasePath() + "query/mysqlQueryMainTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new QuerySVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String queryName = this.query().getName();
        if (queryName == null) {
            queryName = I18nHelper.newQuery();
        }
        // 设置提示文本
        if (this.controller().isUnsaved()) {
            this.setText("* " + queryName + "@" + this.dbName() + "(" + this.connectName() + ")");
        } else {
            this.setText(queryName + "@" + this.dbName() + "(" + this.connectName() + ")");
        }
    }

    public MysqlQuery query() {
        return this.controller().getQuery();
    }

    public String queryId() {
        return this.query().getUid();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.controller().getDbItem();
    }

    public String dbName() {
        return this.dbItem().dbName();
    }

    public String connectName() {
        return this.dbItem().connectName();
    }


    /**
     * 初始化
     *
     * @param query 查询对象
     * @param item  db库树节点
     */
    public boolean init(MysqlQuery query, MysqlDatabaseTreeItem item) {
        this.controller().init(this, query, item);
        this.flush();
        return true;
    }

    @Override
    public MysqlQueryMainTabController controller() {
        return (MysqlQueryMainTabController) super.controller();
    }
}

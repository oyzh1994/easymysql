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

    /**
     * 获取查询对象
     *
     * @return 查询对象
     */
    public MysqlQuery query() {
        return this.controller().getQuery();
    }

    /**
     * 获取查询id
     *
     * @return 查询id
     */
    public String queryId() {
        return this.query().getUid();
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
     * @param query 查询对象
     * @param item  数据库树节点
     * @return 结果
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

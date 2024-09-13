package cn.oyzh.easymysql.tabs;

import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.Cursor;
import lombok.Getter;

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

    /**
     * 内容已变化
     */
    @Getter
    private boolean contentChanged;

    public void setContentChanged(boolean contentChanged) {
        this.contentChanged = contentChanged;
        this.flush();
    }

    @Override
    protected String url() {
        return super.getBasePath() + "mysqlQueryMainTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/query.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String queryName = this.query().getName();
        if (queryName == null) {
            queryName = "新建查询-无标题";
        }
        // 设置提示文本
        if (this.contentChanged) {
            this.setText("* " + this.dbItem().dbName() + "-" + queryName);
        } else {
            this.setText(this.dbItem().dbName() + "-" + queryName);
        }
    }

    public MysqlQuery query() {
        return this.controller().query();
    }

    public String queryId() {
        return this.query().getId();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.controller().dbItem();
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

package cn.oyzh.easymysql.tabs.view;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.record.MysqlRecordFilter;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.ViewSVGGlyph;
import javafx.scene.Cursor;

import java.util.List;

/**
 * db视图记录标签页
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class MysqlViewRecordTab extends MysqlTab {

    /**
     * 标签打开时间
     */
    private final long openedTime = System.currentTimeMillis();

    @Override
    protected String url() {
        return super.getBasePath() + "view/mysqlViewRecordTab.fxml";
    }

    @Override
    public void flushGraphic() {
        ViewSVGGlyph graphic = (ViewSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new ViewSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        this.setText(this.item().viewName() + "@" + this.item().dbName() + "(" + this.item().infoName() + ")");
    }

    /**
     * 初始化
     *
     * @param item 树键
     * @return 结果
     */
    public boolean init(MysqlViewTreeItem item) {
        this.controller().init(item);
        this.flush();
        return true;
    }

    @Override
    public MysqlViewRecordTabController controller() {
        return (MysqlViewRecordTabController) super.controller();
    }

    @Override
    public void reload() {
        this.controller().reload();
    }

    /**
     * 获取树键
     *
     * @return 树键
     */
    public MysqlViewTreeItem item() {
        return this.controller().getItem();
    }

    /**
     * 获取mysql客户端
     *
     * @return mysql客户端
     */
    public MysqlClient client() {
        return this.item().client();
    }

    /**
     * 获取视图名称
     *
     * @return 视图名称
     */
    public String viewName() {
        return this.item().viewName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.item().dbItem();
    }

    /**
     * 设置过滤条件
     *
     * @param filters 过滤条件列表
     */
    public void setFilters(List<MysqlRecordFilter> filters) {
        this.controller().setFilters(filters);
    }
}

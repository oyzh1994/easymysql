package cn.oyzh.easymysql.tabs.view;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.ViewSVGGlyph;
import javafx.scene.Cursor;
import lombok.Getter;

import java.util.List;

/**
 * db表tab
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class MysqlViewRecordTab extends MysqlTab {

    {
        this.setClosable(true);
    }

    /**
     * 标签打开时间
     */
    @Getter
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
        this.setText(this.item().infoName() + "-" + this.item().dbName() + "-" + this.item().viewName());
    }

    /**
     * 初始化
     *
     * @param item 树键
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

    public MysqlViewTreeItem item() {
        return this.controller().getItem();
    }

    public DBClient client() {
        return this.item().client();
    }

    public String viewName() {
        return this.item().viewName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.item().dbItem();
    }

    public void setFilters(List<MysqlRecordFilter> filters) {
        this.controller().setFilters(filters);
    }
}

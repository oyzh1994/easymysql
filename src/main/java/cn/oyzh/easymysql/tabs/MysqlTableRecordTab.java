package cn.oyzh.easymysql.tabs;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.MysqlTableTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.Cursor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * db表tab
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class MysqlTableRecordTab extends MysqlTab {

    {
        this.setClosable(true);
    }

    /**
     * 标签打开时间
     */
    @Getter
    private final long openedTime = System.currentTimeMillis();

    @Getter
    @Accessors(fluent = true, chain = true)
    private MysqlTableTreeItem item;

    @Override
    protected String url() {
        return super.getBasePath() + "mysqlTableRecordTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/table.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        // 设置提示文本
        this.setText(this.item.infoName() + "-" + this.item.dbName() + "-" + this.item.tableName());
    }

    /**
     * 初始化
     *
     * @param item 树键
     */
    public boolean init(MysqlTableTreeItem item) {
        this.item = item;
        this.controller().init(item);
        // 刷新tab
        this.flush();
        // 加载耗时处理
        return true;
    }

    @Override
    public MysqlTableRecordTabController controller() {
        return (MysqlTableRecordTabController) super.controller();
    }

    @Override
    public void reload() {
        this.controller().reload();
    }

    public DBClient client() {
        return this.item.client();
    }

    public void setFilters(List<MysqlRecordFilter> filters) {
        this.controller().setFilters(filters);
    }

    public String tableName() {
        return this.item.tableName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.item.dbItem();
    }

    public String dbName() {
        return this.item.dbName();
    }
}

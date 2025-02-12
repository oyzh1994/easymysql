package cn.oyzh.easymysql.tabs.view;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.svg.glyph.ViewSVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * db视图设计tab
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class MysqlViewDesignTab extends MysqlTab {

    {
        this.setClosable(true);
    }

    @Override
    protected String url() {
        return super.getBasePath() + "view/mysqlViewDesignTab.fxml";
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
        String name = this.viewName();
        if (StrUtil.isBlank(name)) {
            name = I18nHelper.unnamedView();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + this.dbName() + "-" + name);
        } else {
            this.setText(this.dbName() + "-" + name);
        }
    }

    public String dbName() {
        return this.controller().dbName();
    }

    public String viewName() {
        return this.controller().viewName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.controller().dbItem();
    }

    /**
     * 初始化
     *
     * @param item 树键
     */
    public void init(MysqlView view, MysqlDatabaseTreeItem item) {
        this.controller().init(view, item);
        // 刷新tab
        this.flush();
    }

    @Override
    public MysqlViewDesignTabController controller() {
        return (MysqlViewDesignTabController) super.controller();
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

package cn.oyzh.easymysql.tabs;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.event.DBEvent;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventDesignTab extends MysqlTab {

    {
        this.setClosable(true);
    }

    @Override
    protected String url() {
        return super.getBasePath() + "mysqlEventDesignTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/event.svg", "12");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String name = this.eventName();
        if (StrUtil.isBlank(name)) {
            name = I18nHelper.unnamedEvent();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + this.dbItem().dbName() + "-" + name);
        } else {
            this.setText(this.dbItem().dbName() + "-" + name);
        }
    }

    public DBEvent event() {
        return this.controller().event();
    }

    public String eventName() {
        return this.event().getName();
    }

    @Override
    public MysqlDatabaseTreeItem dbItem() {
        return this.controller().dbItem();
    }

    /**
     * 初始化
     *
     * @param event 事件对象
     * @param item  db库树节点
     */
    public void init(DBEvent event, MysqlDatabaseTreeItem item) {
        this.controller().init(event, item);
        // 刷新tab
        this.flush();
    }

    @Override
    public MysqlEventDesignTabController controller() {
        return (MysqlEventDesignTabController) super.controller();
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

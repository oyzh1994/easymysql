package cn.oyzh.easymysql.tabs.home;

import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controls.svg.HomeSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * redis主页tab
 *
 * @author oyzh
 * @since 2023/6/24
 */
public class DBHomeTab extends DynamicTab {

    public DBHomeTab() {
        super();
        super.flush();
    }

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "home/dbHomeTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new HomeSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    protected String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.home");
    }
}

package cn.oyzh.easymysql.trees.root;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;


/**
 * redis 根节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class DBRootTreeItemValue extends DBTreeItemValue {

    public DBRootTreeItemValue() {
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return I18nHelper.database();
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/database.svg", 11);
            this.graphic(glyph);
        }
    }
}

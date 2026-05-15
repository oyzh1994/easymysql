package cn.oyzh.easymysql.trees.event;

import cn.oyzh.fx.gui.svg.glyph.database.EventSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventTreeItemValue extends RichTreeItemValue {

    public MysqlEventTreeItemValue(MysqlEventTreeItem item) {
        super(item);
    }

    @Override
    public MysqlEventTreeItem item() {
        return (MysqlEventTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new EventSVGGlyph("12"));
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().eventName();
    }
}

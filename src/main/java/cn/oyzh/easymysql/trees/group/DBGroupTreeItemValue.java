package cn.oyzh.easymysql.trees.group;

import cn.oyzh.fx.gui.svg.glyph.GroupSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * db树 Group节点值
 *
 * @author oyzh
 * @since 2023/12/21
 */
public class DBGroupTreeItemValue extends RichTreeItemValue {

    public DBGroupTreeItemValue(DBGroupTreeItem item) {
        super(item);
    }

    @Override
    protected DBGroupTreeItem item() {
        return (DBGroupTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new GroupSVGGlyph("10");
            this.graphic.disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (!this.item.isChildEmpty()) {
           return Color.DEEPSKYBLUE;
        }
        return super.graphicColor();
    }
}

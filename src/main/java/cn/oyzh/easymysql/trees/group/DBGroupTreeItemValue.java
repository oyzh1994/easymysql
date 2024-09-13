package cn.oyzh.easymysql.trees.group;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.GroupSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.experimental.Accessors;


/**
 * db树 Group节点值
 *
 * @author oyzh
 * @since 2023/12/21
 */
@Accessors(chain = true, fluent = true)
public class DBGroupTreeItemValue extends DBTreeItemValue {

    private final DBGroupTreeItem item;

    public DBGroupTreeItemValue(DBGroupTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.value().getName());
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            GroupSVGGlyph glyph = new GroupSVGGlyph("10");
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isChildEmpty()) {
            super.flushGraphicColor();
        } else {
            glyph.setColor(Color.DEEPSKYBLUE);
        }
    }
}

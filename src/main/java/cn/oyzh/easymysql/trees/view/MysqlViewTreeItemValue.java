package cn.oyzh.easymysql.trees.view;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.gui.svg.glyph.ViewSVGGlyph;
import lombok.experimental.Accessors;

/**
 * db树表节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
@Accessors(chain = true, fluent = true)
public class MysqlViewTreeItemValue extends DBTreeItemValue {

    /**
     * db树表节点
     */
    private final MysqlViewTreeItem item;

    public MysqlViewTreeItemValue(MysqlViewTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.flushText();
    }

    @Override
    public void flushGraphic() {
        ViewSVGGlyph glyph = (ViewSVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new ViewSVGGlyph("12");
            this.graphic(glyph);
        }
    }

    @Override
    public String name() {
        return this.item.viewName();
    }
}

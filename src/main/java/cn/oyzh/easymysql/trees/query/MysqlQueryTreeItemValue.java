package cn.oyzh.easymysql.trees.query;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import lombok.experimental.Accessors;

/**
 * db树表节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
@Accessors(chain = true, fluent = true)
public class MysqlQueryTreeItemValue extends DBTreeItemValue {

    /**
     * db树表节点
     */
    private final MysqlQueryTreeItem item;

    public MysqlQueryTreeItemValue(MysqlQueryTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.flushText();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/query.svg", "12");
            this.graphic(glyph);
        }
    }

    @Override
    public String name() {
        return this.item.queryName();
    }
}

package cn.oyzh.easymysql.trees.query;

import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import lombok.experimental.Accessors;

/**
 * db树表节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
@Accessors(chain = true, fluent = true)
public class MysqlQueryTreeItemValue extends RichTreeItemValue {

//    /**
//     * db树表节点
//     */
//    private final MysqlQueryTreeItem item;

    public MysqlQueryTreeItemValue(MysqlQueryTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.flushGraphicColor();
//        this.flushText();
        super(item);
    }

    @Override
    protected MysqlQueryTreeItem item() {
        return (MysqlQueryTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic  = new QuerySVGGlyph("12");
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().queryName();
    }
}

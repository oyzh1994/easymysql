package cn.oyzh.easymysql.trees.function;

import cn.oyzh.easymysql.trees.event.MysqlEventTreeItem;
import cn.oyzh.fx.gui.svg.glyph.FunctionSVGGlyph;
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
public class MysqlFunctionTreeItemValue extends RichTreeItemValue {

//    /**
//     * db树表节点
//     */
//    private final MysqlFunctionTreeItem item;

    public MysqlFunctionTreeItemValue(MysqlFunctionTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.flushGraphicColor();
//        this.flushText();
        super(item);
    }

    @Override
    protected MysqlEventTreeItem item() {
        return (MysqlEventTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new FunctionSVGGlyph("12");
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().eventName();
    }
}

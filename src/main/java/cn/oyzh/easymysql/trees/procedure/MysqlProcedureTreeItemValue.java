package cn.oyzh.easymysql.trees.procedure;

import cn.oyzh.fx.gui.svg.glyph.ProcedureSVGGlyph;
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
public class MysqlProcedureTreeItemValue extends RichTreeItemValue {
//
//    /**
//     * db树表节点
//     */
//    private final MysqlProcedureTreeItem item;

    public MysqlProcedureTreeItemValue(MysqlProcedureTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.flushGraphicColor();
//        this.flushText();
        super(item);
    }

    @Override
    protected MysqlProcedureTreeItem item() {
        return (MysqlProcedureTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new ProcedureSVGGlyph("12");
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().procedureName();
    }
}

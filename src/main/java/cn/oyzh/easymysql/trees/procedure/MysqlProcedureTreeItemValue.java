package cn.oyzh.easymysql.trees.procedure;

import cn.oyzh.fx.gui.svg.glyph.database.ProcedureSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 过程树节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlProcedureTreeItemValue extends RichTreeItemValue {

    /**
     * 构造过程树节点值
     *
     * @param item 过程树节点
     */
    public MysqlProcedureTreeItemValue(MysqlProcedureTreeItem item) {
        super(item);
    }

    @Override
    public MysqlProcedureTreeItem item() {
        return (MysqlProcedureTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new ProcedureSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().procedureName();
    }
}

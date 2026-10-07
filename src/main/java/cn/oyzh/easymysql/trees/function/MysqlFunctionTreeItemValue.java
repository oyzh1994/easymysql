package cn.oyzh.easymysql.trees.function;

import cn.oyzh.fx.gui.svg.glyph.database.FunctionSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 函数树节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlFunctionTreeItemValue extends RichTreeItemValue {

    /**
     * 构造函数树节点值
     *
     * @param item 函数树节点
     */
    public MysqlFunctionTreeItemValue(MysqlFunctionTreeItem item) {
        super(item);
    }

    @Override
    public MysqlFunctionTreeItem item() {
        return (MysqlFunctionTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new FunctionSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().functionName();
    }
}

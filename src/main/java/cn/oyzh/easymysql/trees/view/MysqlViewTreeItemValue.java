package cn.oyzh.easymysql.trees.view;

import cn.oyzh.fx.gui.svg.glyph.database.ViewSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * db树视图节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlViewTreeItemValue extends RichTreeItemValue {

    /**
     * 构造视图树节点值
     *
     * @param item 视图树节点
     */
    public MysqlViewTreeItemValue(MysqlViewTreeItem item) {
        super(item);
    }

    @Override
    public MysqlViewTreeItem item() {
        return (MysqlViewTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new ViewSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().viewName();
    }
}

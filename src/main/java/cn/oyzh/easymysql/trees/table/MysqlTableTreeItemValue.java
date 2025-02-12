package cn.oyzh.easymysql.trees.table;

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
public class MysqlTableTreeItemValue extends RichTreeItemValue {

//    /**
//     * db树表节点
//     */
//    private final MysqlTableTreeItem item;

    public MysqlTableTreeItemValue(MysqlTableTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.flushGraphicColor();
//        this.flushText();
        super(item);
    }

    @Override
    protected MysqlTableTreeItem item() {
        return (MysqlTableTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/table2.svg", "12");
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().tableName();
    }
}

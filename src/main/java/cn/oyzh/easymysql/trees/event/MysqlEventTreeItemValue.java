package cn.oyzh.easymysql.trees.event;

import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tree.view.FXTreeItem;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/09/09
 */
@Accessors(chain = true, fluent = true)
public class MysqlEventTreeItemValue extends RichTreeItemValue {

//    /**
//     * db树表节点
//     */
//    private final MysqlEventTreeItem item;

    public MysqlEventTreeItemValue(MysqlEventTreeItem item) {
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
            this.graphic  = new SVGGlyph("/font/event.svg", "12");
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().eventName();
    }
}

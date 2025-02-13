package cn.oyzh.easymysql.trees.connect;

import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * db树连接值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DBConnectTreeItemValue extends RichTreeItemValue {

//    /**
//     * 节点
//     */
//    private final DBConnectTreeItem item;

    public DBConnectTreeItemValue(DBConnectTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.flushGraphicColor();
//        this.name(item.value().getName());
        super(item);
    }

    @Override
    protected DBConnectTreeItem item() {
        return (DBConnectTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/mysql.svg", "12");
            this.graphic.disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (!this.item().isChildEmpty()) {
          return Color.GREEN;
        }
        return super.graphicColor();
    }
}

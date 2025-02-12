package cn.oyzh.easymysql.trees.table;

import cn.oyzh.easymysql.trees.query.MysqlQueryTypeTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * db树表类型值
 *
 * @author oyzh
 * @since 2023/12/08
 */
public class MysqlTableTypeTreeItemValue extends RichTreeItemValue {

//    private final MysqlTableTypeTreeItem item;

    public MysqlTableTypeTreeItemValue(MysqlTableTypeTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.name(item.value());
        super(item);
    }

    @Override
    public MysqlTableTypeTreeItem item() {
        return (MysqlTableTypeTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/table2.svg", 12);
            this.graphic.disableTheme();

        }
        return this.graphic;
    }

    @Override
    public Color graphicColor() {
        SVGGlyph glyph = this.graphic();
        if (!this.item.isChildEmpty()) {
            return Color.GREEN;
        }
        return super.graphicColor();
    }

    @Override
    public String extra() {
        Integer size = this.item().tableSize();
        if (size != null) {
            return "(" + size + ")";
        }
        return super.extra();
    }

    @Override
    public Color extraColor() {
        return Color.valueOf("#228B22");
    }

//    /**
//     * 刷新节点数量
//     */
//    public void flushNum() {
//        try {
//            Integer size = this.item.tableSize();
//            // 寻找组件
//            FXText text = (FXText) this.lookup("#num");
//            if (size == null) {
//                this.removeChild(text);
//            } else {
//                if (text == null) {
//                    text = new FXText();
//                    this.addChild(text);
//                    text.setId("num");
//                    text.setFill(Color.valueOf("#228B22"));
//                    HBox.setMargin(text, new Insets(0, 0, 0, 3));
//                }
//                text.setTextExt("(" + size + ")");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
}

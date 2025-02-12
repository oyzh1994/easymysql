package cn.oyzh.easymysql.trees.view;

import cn.oyzh.fx.gui.svg.glyph.ViewSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.controls.tree.view.FXTreeItem;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * db树视图类型值
 *
 * @author oyzh
 * @since 2024/06/28
 */
public class MysqlViewTypeTreeItemValue extends RichTreeItemValue {

//    private final MysqlViewTypeTreeItem item;

    public MysqlViewTypeTreeItemValue(MysqlViewTypeTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.name(item.value());
        super(item);
    }

    @Override
    protected MysqlViewTypeTreeItem item() {
        return (MysqlViewTypeTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new ViewSVGGlyph("12");
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

    @Override
    public String extra() {
        Integer size = this.item().viewSize();
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
//            Integer size = this.item.viewSize();
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

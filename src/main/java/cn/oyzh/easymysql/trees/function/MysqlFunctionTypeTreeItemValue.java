package cn.oyzh.easymysql.trees.function;

import cn.oyzh.fx.gui.svg.glyph.FunctionSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;

/**
 * db树视图类型值
 *
 * @author oyzh
 * @since 2024/06/28
 */
public class MysqlFunctionTypeTreeItemValue extends RichTreeItemValue {

//    private final MysqlFunctionTypeTreeItem item;

    public MysqlFunctionTypeTreeItemValue(MysqlFunctionTypeTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.name(item.value());
        super(item);
    }

    @Override
    public MysqlFunctionTypeTreeItem item() {
        return (MysqlFunctionTypeTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new FunctionSVGGlyph("12");
            this.graphic.disableTheme();

        }
        return this.graphic;
    }

    @Override
    public Color graphicColor() {
        if (!this.item.isChildEmpty()) {
            return Color.GREEN;
        }
        return super.graphicColor();
    }

    @Override
    public String extra() {
        Integer size = this.item().functionSize();
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
//            Integer size = this.item.functionSize();
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

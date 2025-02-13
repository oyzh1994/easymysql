package cn.oyzh.easymysql.trees.event;

import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventTypeTreeItemValue extends RichTreeItemValue {

//    private final MysqlEventTypeTreeItem item;

    public MysqlEventTypeTreeItemValue(MysqlEventTypeTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
        super(item);
    }

    @Override
    protected MysqlEventTypeTreeItem item() {
        return (MysqlEventTypeTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/event.svg");
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
        Integer size = this.item().eventSize();
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
//            Integer size = this.item.eventSize();
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

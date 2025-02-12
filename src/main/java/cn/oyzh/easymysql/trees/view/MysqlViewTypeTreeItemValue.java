package cn.oyzh.easymysql.trees.view;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.gui.svg.glyph.ViewSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
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
public class MysqlViewTypeTreeItemValue extends DBTreeItemValue {

    private final MysqlViewTypeTreeItem item;

    public MysqlViewTypeTreeItemValue(MysqlViewTypeTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.name(item.value());
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            ViewSVGGlyph glyph = new ViewSVGGlyph("12");
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isChildEmpty()) {
            glyph.setColor((Paint) null);
        } else {
            glyph.setColor(Color.GREEN);
        }
    }

    /**
     * 刷新节点数量
     */
    public void flushNum() {
        try {
            Integer size = this.item.viewSize();
            // 寻找组件
            FXText text = (FXText) this.lookup("#num");
            if (size == null) {
                this.removeChild(text);
            } else {
                if (text == null) {
                    text = new FXText();
                    this.addChild(text);
                    text.setId("num");
                    text.setFill(Color.valueOf("#228B22"));
                    HBox.setMargin(text, new Insets(0, 0, 0, 3));
                }
                text.setTextExt("(" + size + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package cn.oyzh.easymysql.trees.table;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
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
public class MysqlTableTypeTreeItemValue extends DBTreeItemValue {

    private final MysqlTableTypeTreeItem item;

    public MysqlTableTypeTreeItemValue(MysqlTableTypeTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.name(item.value());
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/table2.svg", 12);
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (this.item.isChildEmpty()) {
            if (ThemeManager.isDarkMode()) {
                glyph.setColor(Color.WHITE);
            } else {
                glyph.setColor(Color.BLACK);
            }
        } else {
            glyph.setColor(Color.GREEN);
        }
    }

    /**
     * 刷新节点数量
     */
    public void flushNum() {
        try {
            Integer size = this.item.tableSize();
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

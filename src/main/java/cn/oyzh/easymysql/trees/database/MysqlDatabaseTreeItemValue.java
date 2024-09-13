package cn.oyzh.easymysql.trees.database;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * database值
 *
 * @author oyzh
 * @since 2023/12/20
 */
public class MysqlDatabaseTreeItemValue extends DBTreeItemValue {

    /**
     * db树database节点
     */
    private final MysqlDatabaseTreeItem item;

    public MysqlDatabaseTreeItemValue(MysqlDatabaseTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.dbName());
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/database2.svg", "12");
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
            Integer tableSize = this.item.tableSize();
            // 寻找组件
            FXText text = (FXText) this.lookup("#num");
            if (tableSize == null) {
                this.removeChild(text);
            } else {
                if (text == null) {
                    text = new FXText();
                    this.addChild(text);
                    text.setId("num");
                    text.setFill(Color.valueOf("#228B22"));
                    HBox.setMargin(text, new Insets(0, 0, 0, 3));
                }
                text.setText("(" + tableSize + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

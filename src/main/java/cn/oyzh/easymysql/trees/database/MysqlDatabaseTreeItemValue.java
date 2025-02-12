package cn.oyzh.easymysql.trees.database;

import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.controls.tree.view.FXTreeItem;
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
public class MysqlDatabaseTreeItemValue extends RichTreeItemValue {

//    /**
//     * db树database节点
//     */
//    private final MysqlDatabaseTreeItem item;

    public MysqlDatabaseTreeItemValue(MysqlDatabaseTreeItem item) {
//        this.item = item;
//        this.flushGraphic();
//        this.flushGraphicColor();
//        this.name(item.dbName());
        super(item);
    }

    @Override
    protected MysqlDatabaseTreeItem item() {
        return (MysqlDatabaseTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().dbName();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/database2.svg", "12");
            this.graphic.disableTheme();
        }
        return super.graphic();
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
        Integer tableSize = this.item().tableSize();
        if (tableSize != null) {
            return "(" + tableSize + ")";
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
//            Integer tableSize = this.item.tableSize();
//            // 寻找组件
//            FXText text = (FXText) this.lookup("#num");
//            if (tableSize == null) {
//                this.removeChild(text);
//            } else {
//                if (text == null) {
//                    text = new FXText();
//                    this.addChild(text);
//                    text.setId("num");
//                    text.setFill(Color.valueOf("#228B22"));
//                    HBox.setMargin(text, new Insets(0, 0, 0, 3));
//                }
//                text.setText("(" + tableSize + ")");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
}

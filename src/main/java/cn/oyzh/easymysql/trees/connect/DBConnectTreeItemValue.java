package cn.oyzh.easymysql.trees.connect;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.scene.paint.Color;


/**
 * db树连接值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DBConnectTreeItemValue extends DBTreeItemValue {

    /**
     * 节点
     */
    private final DBConnectTreeItem item;

    public DBConnectTreeItemValue(DBConnectTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.name(item.value().getName());
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            if (this.item.isMysqlType()) {
                glyph = new SVGGlyph("/font/mysql.svg", "12");
            } else if (this.item.isOracleType()) {
                glyph = new SVGGlyph("/font/oracle.svg", "12");
            } else if (this.item.isMariaDBType()) {
                glyph = new SVGGlyph("/font/mariadb.svg", "12");
            } else if (this.item.isMssqlType()) {
                glyph = new SVGGlyph("/font/sqlserver.svg", "12");
            }
            if (glyph != null) {
                glyph.disableTheme();
                this.graphic(glyph);
            }
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            return;
        }
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
}

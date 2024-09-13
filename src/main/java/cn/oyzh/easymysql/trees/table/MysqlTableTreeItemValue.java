package cn.oyzh.easymysql.trees.table;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import lombok.experimental.Accessors;

/**
 * db树表节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
@Accessors(chain = true, fluent = true)
public class MysqlTableTreeItemValue extends DBTreeItemValue {

    /**
     * db树表节点
     */
    private final MysqlTableTreeItem item;

    public MysqlTableTreeItemValue(MysqlTableTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.flushText();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/table2.svg", "12");
            this.graphic(glyph);
        }
    }

    @Override
    public String name() {
        return this.item.tableName();
    }
}

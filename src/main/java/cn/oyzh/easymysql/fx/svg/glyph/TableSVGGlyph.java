package cn.oyzh.easymysql.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2025-02-14
 */
public class TableSVGGlyph extends SVGGlyph {

    public TableSVGGlyph() {
        this.setUrl("/font/table2.svg");
    }

    public TableSVGGlyph(String size) {
        this();
        this.setSizeStr(size);
    }
}

package cn.oyzh.easymysql.fx.svg.glyph;// package cn.oyzh.easymysql.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 警告图标
 *
 * @author oyzh
 * @since 2025-02-14
 */
public class WarningSVGGlyph extends SVGGlyph {

    /**
     * 构造警告图标
     */
    public WarningSVGGlyph() {
        this.setUrl("/font/warning.svg");
    }

    /**
     * 构造警告图标
     *
     * @param size 尺寸
     */
    public WarningSVGGlyph(String size) {
        this();
        this.setSizeStr(size);
    }
}

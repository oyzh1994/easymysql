package cn.oyzh.easymysql.trees.function;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.FunctionSVGGlyph;
import lombok.experimental.Accessors;

/**
 * db树表节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
@Accessors(chain = true, fluent = true)
public class MysqlFunctionTreeItemValue extends DBTreeItemValue {

    /**
     * db树表节点
     */
    private final MysqlFunctionTreeItem item;

    public MysqlFunctionTreeItemValue(MysqlFunctionTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushGraphicColor();
        this.flushText();
    }

    @Override
    public void flushGraphic() {
        FunctionSVGGlyph glyph = (FunctionSVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new FunctionSVGGlyph("12");
            this.graphic(glyph);
        }
    }

    @Override
    public String name() {
        return this.item.functionName();
    }
}

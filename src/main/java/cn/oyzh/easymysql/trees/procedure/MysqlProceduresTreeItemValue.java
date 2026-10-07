package cn.oyzh.easymysql.trees.procedure;

import cn.oyzh.fx.gui.svg.glyph.database.ProcedureSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.paint.Color;

/**
 * 过程类型树节点值
 *
 * @author oyzh
 * @since 2024/06/28
 */
public class MysqlProceduresTreeItemValue extends RichTreeItemValue {

    /**
     * 构造过程类型树节点值
     *
     * @param item 过程类型树节点
     */
    public MysqlProceduresTreeItemValue(MysqlProceduresTreeItem item) {
        super(item);
    }

    @Override
    public MysqlProceduresTreeItem item() {
        return (MysqlProceduresTreeItem) super.item();
    }

    @Override
    public String name() {
        return I18nHelper.procedure();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new ProcedureSVGGlyph());
            super.graphic().disableTheme();
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
        Integer size = this.item().procedureSize();
        if (size != null) {
            return " (" + size + ")";
        }
        return super.extra();
    }

    @Override
    public Color extraColor() {
        return Color.valueOf("#228B22");
    }
}

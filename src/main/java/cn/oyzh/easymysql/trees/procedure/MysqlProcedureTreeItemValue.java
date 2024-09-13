package cn.oyzh.easymysql.trees.procedure;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.ProcedureSVGGlyph;
import lombok.experimental.Accessors;

/**
     * db树表节点值
     *
     * @author oyzh
     * @since 2023/12/22
     */
    @Accessors(chain = true, fluent = true)
    public  class MysqlProcedureTreeItemValue extends DBTreeItemValue {

        /**
         * db树表节点
         */
        private final MysqlProcedureTreeItem item;

        public MysqlProcedureTreeItemValue(MysqlProcedureTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.flushGraphicColor();
            this.flushText();
        }

        @Override
        public void flushGraphic() {
            ProcedureSVGGlyph glyph = (ProcedureSVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new ProcedureSVGGlyph("12");
                this.graphic(glyph);
            }
        }

        @Override
        public String name() {
            return this.item.procedureName();
        }
    }

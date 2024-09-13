package cn.oyzh.easymysql.fx.table;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * 行格式下拉框
 *
 * @author oyzh
 * @since 2024/07/17
 */
public class DBRowFormatComboBox extends FlexComboBox<String> {

    {
        this.addItem("COMPACT");
        this.addItem("COMPRESSED");
        this.addItem("DEFAULT");
        this.addItem("DYNAMIC");
        this.addItem("FIXED");
        this.addItem("REDUNDANT");
    }

    @Override
    public void select(String rowFormat) {
        if (rowFormat != null) {
            super.select(rowFormat.toUpperCase());
        }
    }
}

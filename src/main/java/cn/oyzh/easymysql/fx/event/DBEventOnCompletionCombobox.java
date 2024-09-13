package cn.oyzh.easymysql.fx.event;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024-09-09
 */
public class DBEventOnCompletionCombobox extends FlexComboBox<String> {

    {
        this.addItem("PRESERVE");
        this.addItem("NOT PRESERVE");
    }

    @Override
    public void select(String val) {
        if (val != null) {
            super.select(val.toUpperCase());
        }
    }
}

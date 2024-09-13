package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/8/27
 */
public class DataRecordLabelComboBox extends FlexComboBox<String> {

    {
        this.addItem("(Root)");
        this.addItem("RECORDS");
    }

    public boolean isRoot() {
        return this.getSelectedIndex() == 0;
    }
}

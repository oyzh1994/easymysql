package cn.oyzh.easymysql.fx.table;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/7/9
 */
public class DBTriggerPolicyComboBox extends FlexComboBox<String> {

    {
        this.addItem("BEFORE INSERT");
        this.addItem("BEFORE UPDATE");
        this.addItem("BEFORE DELETE");
        this.addItem("AFTER INSERT");
        this.addItem("AFTER UPDATE");
        this.addItem("AFTER DELETE");
    }
}

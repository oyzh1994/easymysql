package cn.oyzh.easymysql.fx.routine;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class DBParamModeComboBox extends FlexComboBox<String> {

    {
        this.addItem("IN");
        this.addItem("OUT");
        this.addItem("INOUT");
    }
}

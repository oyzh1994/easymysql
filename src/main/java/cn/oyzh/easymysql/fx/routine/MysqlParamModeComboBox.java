package cn.oyzh.easymysql.fx.routine;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * mysql参数模式下拉框
 *
 * @author oyzh
 * @since 2024/06/26
 */
public class MysqlParamModeComboBox extends FXComboBox<String> {

    {
        this.addItem("IN");
        this.addItem("OUT");
        this.addItem("INOUT");
    }
}

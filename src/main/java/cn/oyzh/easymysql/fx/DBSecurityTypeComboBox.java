package cn.oyzh.easymysql.fx;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * db安全类型下拉框
 *
 * @author oyzh
 * @since 2024/08/07
 */
public class DBSecurityTypeComboBox extends FlexComboBox<String> {

    {
        this.addItem("DEFINER");
        this.addItem("INVOKER");
    }

    @Override
    public void select(String obj) {
        if (obj != null) {
            super.select(obj.toUpperCase());
        }
    }
}

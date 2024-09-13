package cn.oyzh.easymysql.fx.table;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/1/26
 */
public class DBJoinSymbolComboBox extends FlexComboBox<String> {

    {
        this.addItem("AND");
        this.addItem("OR");
    }
}

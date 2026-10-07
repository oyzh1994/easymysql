package cn.oyzh.easymysql.fx.table;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * db连接符下拉框
 *
 * @author oyzh
 * @since 2024/1/26
 */
public class DBJoinSymbolComboBox extends FXComboBox<String> {

    {
        this.addItem("AND");
        this.addItem("OR");
    }
}

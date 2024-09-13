package cn.oyzh.easymysql.fx.table;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * db索引方法选择框
 *
 * @author oyzh
 * @since 2024/01/24
 */
public class DBIndexMethodComboBox extends FlexComboBox<String> {

    {
        this.addItem("");
        this.addItem("BTREE");
        this.addItem("HASH");
    }
}

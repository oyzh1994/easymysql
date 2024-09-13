package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/09/04
 */
public class DataTxtIdentifierComboBox extends FlexComboBox<String> {

    {
        this.addItem("\"");
        this.addItem("'");
    }
}

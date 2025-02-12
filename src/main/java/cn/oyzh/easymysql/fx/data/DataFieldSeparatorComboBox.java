package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2024/09/04
 */
public class DataFieldSeparatorComboBox extends FlexComboBox<String> {

    {
        this.addItem(I18nHelper.semicolon() + "(;)");
        this.addItem(I18nHelper.comma() + "(,)");
        this.addItem(I18nHelper.space() + "( )");
    }

    public String value() {
        int itemIndex = this.getSelectedIndex();
        if (itemIndex == 0) {
            return ";";
        }
        if (itemIndex == 1) {
            return ",";
        }
        return " ";
    }
}

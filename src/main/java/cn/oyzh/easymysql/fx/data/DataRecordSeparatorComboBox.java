package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.OSUtil;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/09/04
 */
public class DataRecordSeparatorComboBox extends FlexComboBox<String> {

    {
        this.addItem("CRLF");
        this.addItem("LF");
        this.addItem("CR");
        if (OSUtil.isWindows()) {
            this.select(0);
        } else if (OSUtil.isLinux()) {
            this.select(1);
        } else if (OSUtil.isMacOS()) {
            this.select(1);
        }
    }

    public String value() {
        int itemIndex = this.getSelectedIndex();
        if (itemIndex == 0) {
            return "\r\n";
        }
        if (itemIndex == 1) {
            return "\n";
        }
        return "\r";
    }
}

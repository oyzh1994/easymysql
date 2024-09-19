package cn.oyzh.easymysql.fx;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/01/26
 */
public class DBCollationComboBox extends FlexComboBox<String> {

    public void init(String charset, DBClient client) {
        String aCharset = this.getProp("charset");
        if (!StrUtil.equalsIgnoreCase(charset, aCharset)) {
            this.setProp("charset", charset);
            for (String collation : client.collation(charset)) {
                this.addItem(collation);
            }
        }
    }

    @Override
    public void select(String obj) {
        if (obj != null) {
            super.select(obj.toUpperCase());
        }
    }
}

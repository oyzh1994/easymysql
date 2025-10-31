package cn.oyzh.easymysql.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * @author oyzh
 * @since 2024/01/26
 */
public class DBCollationComboBox extends FXComboBox<String> {

    public void init(String charset, DBClient client) {
        if (charset == null) {
            return;
        }
        String aCharset = this.getProp("charset");
        if (!StringUtil.equalsIgnoreCase(charset, aCharset)) {
            this.setProp("charset", charset);
            this.clearItems();
            for (String collation : client.collation(charset)) {
                this.addItem(collation.toUpperCase());
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

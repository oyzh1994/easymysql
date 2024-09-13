package cn.oyzh.easymysql.fx;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/1/26
 */
public class DBCollationComboBox extends FlexComboBox<String> {

    private String charset;

    public void init(String charset, DBClient client) {
        if (!StrUtil.equalsIgnoreCase(charset, this.charset)) {
            this.charset = charset;
            this.setItem(client.collation(charset));
        }
    }
}

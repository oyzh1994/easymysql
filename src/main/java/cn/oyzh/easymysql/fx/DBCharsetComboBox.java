package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import lombok.Getter;

/**
 * @author oyzh
 * @since 2024/1/26
 */
public class DBCharsetComboBox extends FlexComboBox<String> {

    @Getter
    private DBClient client;

    public void setClient(DBClient client) {
        this.client = client;
        this.init(client);
    }

    public void init(DBClient client) {
        this.clearItems();
        // 空数据
        this.addItem("");
        // 正常数据
        for (String charset : client.charsets()) {
            this.addItem(charset.toUpperCase());
        }
    }

    @Override
    public void select(String obj) {
        if (obj != null) {
            super.select(obj.toUpperCase());
        }
    }
}

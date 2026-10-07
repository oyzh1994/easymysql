package cn.oyzh.easymysql.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * db排序规则下拉框
 *
 * @author oyzh
 * @since 2024/01/26
 */
public class DBCollationComboBox extends FXComboBox<String> {

    /**
     * 初始化排序规则
     *
     * @param charset 字符集
     * @param client  mysql客户端
     */
    public void init(String charset, MysqlClient client) {
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
        } else {
            super.clearSelection();
        }
    }
}

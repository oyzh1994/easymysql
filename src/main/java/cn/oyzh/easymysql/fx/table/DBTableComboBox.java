package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.table.MysqlTable;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class DBTableComboBox extends FXComboBox<String> {

    public void init(String dbName, MysqlClient client) {
        this.init(dbName, null, client);
    }

    public void init(String dbName, String tableName, MysqlClient client) {
        List<MysqlTable> list = client.selectTables(dbName);
        this.setItem(list.parallelStream().map(MysqlTable::getName).toList());
        if (tableName != null) {
            this.select(tableName);
        }
    }
}

package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class DBTableComboBox extends FlexComboBox<String> {

    public void init(String dbName, DBClient client) {
        this.init(dbName, null, client);
    }

    public void init(String dbName, String tableName, DBClient client) {
        List<DBTable> list = client.tables(dbName);
        this.setItem(list.parallelStream().map(DBTable::getName).toList());
        if (tableName != null) {
            this.select(tableName);
        }
    }
}

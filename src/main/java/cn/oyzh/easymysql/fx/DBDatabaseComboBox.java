package cn.oyzh.easymysql.fx;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class DBDatabaseComboBox extends FXComboBox<String> {

    public void init(DBClient client) {
        this.init(client, null);
    }

    public void init(DBClient client, String dbName) {
        this.clearItems();
        List<DBDatabase> databases = client.databases();
        if (CollUtil.isNotEmpty(databases)) {
            this.setItem(databases.stream().map(DBDatabase::getName).toList());
        }
        if (dbName != null) {
            this.select(dbName);
        }
    }
}

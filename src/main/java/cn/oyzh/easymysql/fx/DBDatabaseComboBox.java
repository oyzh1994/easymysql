package cn.oyzh.easymysql.fx;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
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

    /**
     * 初始化数据库列表
     *
     * @param client mysql客户端
     */
    public void init(MysqlClient client) {
        this.init(client, null);
    }

    /**
     * 初始化数据库列表并选中指定数据库
     *
     * @param client mysql客户端
     * @param dbName 数据库名称
     */
    public void init(MysqlClient client, String dbName) {
        this.clearItems();
        List<DBDatabase> databases = client.databases();
        if (CollectionUtil.isNotEmpty(databases)) {
            this.setItem(databases.stream().map(DBDatabase::getName).toList());
        }
        if (dbName != null) {
            this.select(dbName);
        }
    }
}

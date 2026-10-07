package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.table.MysqlTable;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db表选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class MysqlTableComboBox extends FXComboBox<String> {

    /**
     * 初始化表列表
     *
     * @param dbName 库名称
     * @param client mysql客户端
     */
    public void init(String dbName, MysqlClient client) {
        this.init(dbName, null, client);
    }

    /**
     * 初始化表列表并选中指定表
     *
     * @param dbName    库名称
     * @param tableName 表名称
     * @param client    mysql客户端
     */
    public void init(String dbName, String tableName, MysqlClient client) {
        List<MysqlTable> list = client.selectTables(dbName);
        this.setItem(list.parallelStream().map(MysqlTable::getName).toList());
        if (tableName != null) {
            this.select(tableName);
        }
    }
}

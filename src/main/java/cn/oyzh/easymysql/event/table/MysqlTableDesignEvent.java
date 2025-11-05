package cn.oyzh.easymysql.event.table;

import cn.oyzh.easymysql.mysql.table.MysqlTable;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/08/07
 */
public class MysqlTableDesignEvent extends Event<MysqlTable> {

    private MysqlDatabaseTreeItem dbItem;

    public String dbName() {
        return this.dbItem.dbName();
    }

    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    public String tableName() {
        return this.data().getName();
    }
}

package cn.oyzh.easymysql.event.function;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlFunctionAlertedEvent extends Event<String> {

    private MysqlDatabaseTreeItem dbItem;

    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}

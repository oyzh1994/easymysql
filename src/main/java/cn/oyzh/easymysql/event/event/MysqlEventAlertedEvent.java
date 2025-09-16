package cn.oyzh.easymysql.event.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventAlertedEvent extends Event<String> {

    private MysqlDatabaseTreeItem dbItem;

    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}

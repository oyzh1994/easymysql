package cn.oyzh.easymysql.event.event;

import cn.oyzh.easymysql.mysql.event.MysqlEvent;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventDesignEvent extends Event<MysqlEvent> {

    private MysqlDatabaseTreeItem dbItem;

    public String eventName() {
        return this.data().getName();
    }

    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}

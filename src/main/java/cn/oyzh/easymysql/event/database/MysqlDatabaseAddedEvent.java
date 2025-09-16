package cn.oyzh.easymysql.event.database;

import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class MysqlDatabaseAddedEvent extends Event<DBDatabase> implements EventFormatter {

    private DBConnectTreeItem connectItem;

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已新增", this.data().getName());
    }

    public DBConnectTreeItem getConnectItem() {
        return connectItem;
    }

    public void setConnectItem(DBConnectTreeItem connectItem) {
        this.connectItem = connectItem;
    }
}

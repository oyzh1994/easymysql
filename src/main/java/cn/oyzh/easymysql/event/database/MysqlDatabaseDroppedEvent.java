package cn.oyzh.easymysql.event.database;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 数据库已删除事件
 *
 * @author oyzh
 * @since 2024/01/30
 */
public class MysqlDatabaseDroppedEvent extends Event<MysqlDatabaseTreeItem> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已删除", this.data().dbName());
    }
}

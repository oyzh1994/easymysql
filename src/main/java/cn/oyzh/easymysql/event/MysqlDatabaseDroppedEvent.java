package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class MysqlDatabaseDroppedEvent extends Event<MysqlDatabaseTreeItem> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已删除", this.data().dbName());
    }
}

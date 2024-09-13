package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/26
 */
public class MysqlDatabaseClosedEvent extends Event<MysqlDatabaseTreeItem> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已关闭", this.data().value());
    }
}

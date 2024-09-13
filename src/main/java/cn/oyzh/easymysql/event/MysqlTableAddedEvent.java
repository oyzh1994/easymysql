package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/01/16
 */
@Getter
@Accessors(fluent = true)
public class MysqlTableAddedEvent extends Event<MysqlDatabaseTreeItem> {


}

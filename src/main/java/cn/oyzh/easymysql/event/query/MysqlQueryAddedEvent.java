package cn.oyzh.easymysql.event.query;

import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/22
 */
@Data
@Accessors(fluent = true)
public class MysqlQueryAddedEvent extends Event<MysqlQuery> {

    private MysqlDatabaseTreeItem item;
}

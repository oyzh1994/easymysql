package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/06/29
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public class MysqlViewAlertedEvent extends Event<String> {

    private MysqlDatabaseTreeItem dbItem;
}

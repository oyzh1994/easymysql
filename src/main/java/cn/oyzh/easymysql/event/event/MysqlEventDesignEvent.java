package cn.oyzh.easymysql.event.event;

import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/09/09
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class MysqlEventDesignEvent extends Event<MysqlEvent> {

    private MysqlDatabaseTreeItem dbItem;

    public String eventName() {
        return this.data().getName();
    }
}

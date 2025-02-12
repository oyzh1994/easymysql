package cn.oyzh.easymysql.event.table;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/08/07
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class MysqlTableDesignEvent extends Event<String> {

    private MysqlDatabaseTreeItem dbItem;

    public String dbName() {
        return this.dbItem.dbName();
    }
}

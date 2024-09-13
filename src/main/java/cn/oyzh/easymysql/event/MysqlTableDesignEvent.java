package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.Event;
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
public class MysqlTableDesignEvent extends Event<MysqlTable> {

    private MysqlDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().getName();
    }

    public MysqlTable table() {
        return this.data();
    }

}

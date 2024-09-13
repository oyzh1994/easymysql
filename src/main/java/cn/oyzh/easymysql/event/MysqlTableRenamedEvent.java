package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/01/23
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class MysqlTableRenamedEvent extends Event<MysqlTableTreeItem> {

    private MysqlDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().tableName();
    }

    public String dbName() {
        return this.dbItem.dbName();
    }
}

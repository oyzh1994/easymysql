package cn.oyzh.easymysql.event.table;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/22
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public class MysqlTableOpenEvent extends Event<MysqlTableTreeItem> {

    private MysqlDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().tableName();
    }

    public String dbName() {
        return this.dbItem.dbName();
    }

}

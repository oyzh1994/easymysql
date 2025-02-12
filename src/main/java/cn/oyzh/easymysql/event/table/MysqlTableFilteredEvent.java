package cn.oyzh.easymysql.event.table;

import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/06/26
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class MysqlTableFilteredEvent extends Event<MysqlTableTreeItem> {

    private List<MysqlRecordFilter> filters;

    private MysqlDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().tableName();
    }

}

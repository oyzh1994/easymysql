package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.record.DBRecordFilter;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.MysqlTableTreeItem;
import cn.oyzh.fx.plus.event.Event;
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

    private List<DBRecordFilter> filters;

    private MysqlDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().tableName();
    }

}

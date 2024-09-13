package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.MysqlViewTreeItem;
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
public class MysqlViewFilteredEvent extends Event<MysqlViewTreeItem> {

    private List<MysqlRecordFilter> filters;

    private MysqlDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }
}

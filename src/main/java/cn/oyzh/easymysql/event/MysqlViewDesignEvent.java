package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.view.DBView;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/22
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class MysqlViewDesignEvent extends Event<DBView> {

    private MysqlDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().getName();
    }

    public String dbName() {
        return this.dbItem().dbName();
    }
}

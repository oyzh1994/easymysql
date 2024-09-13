package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
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
public class MysqlViewOpenEvent extends Event<MysqlViewTreeItem> {

    private MysqlDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }
}

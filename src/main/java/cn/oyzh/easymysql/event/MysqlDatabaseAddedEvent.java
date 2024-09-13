package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/01/30
 */
@Data
@Accessors(fluent = true)
public class MysqlDatabaseAddedEvent extends Event<DBDatabase> implements EventFormatter {

    private DBConnectTreeItem connectItem;

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已新增", this.data().getName());
    }
}

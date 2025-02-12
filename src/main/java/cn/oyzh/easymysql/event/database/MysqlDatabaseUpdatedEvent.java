package cn.oyzh.easymysql.event.database;

import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/01/30
 */
@Data
@Accessors(fluent = true)
public class MysqlDatabaseUpdatedEvent extends Event<DBDatabase> implements EventFormatter {

    private DBConnectTreeItem connectItem;

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已修改", this.data().getName());
    }
}

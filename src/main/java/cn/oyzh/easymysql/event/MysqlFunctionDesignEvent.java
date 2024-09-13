package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.routine.MysqlFunction;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/06/29
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class MysqlFunctionDesignEvent extends Event<MysqlFunction> {

    private MysqlDatabaseTreeItem dbItem;

    public String functionName() {
        return this.data().getName();
    }
}

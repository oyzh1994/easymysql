package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.routine.DBProcedure;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
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
public class MysqlProcedureDesignEvent extends Event<DBProcedure> {

    private MysqlDatabaseTreeItem dbItem;

    public String procedureName() {
        return this.data().getName();
    }
}

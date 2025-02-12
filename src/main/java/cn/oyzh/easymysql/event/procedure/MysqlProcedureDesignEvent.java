package cn.oyzh.easymysql.event.procedure;

import cn.oyzh.easymysql.db.procedure.MysqlProcedure;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;
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
public class MysqlProcedureDesignEvent extends Event<MysqlProcedure> {

    private MysqlDatabaseTreeItem dbItem;

    public String procedureName() {
        return this.data().getName();
    }
}

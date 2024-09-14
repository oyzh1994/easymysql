package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.event.MysqlEvents;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-09-14
 */
@Data
@Accessors(fluent = true, chain = true)
public class MysqlTableAlertParam {

    private MysqlTable table;

    private MysqlChecks checks;

    private MysqlEvents events;

    private MysqlColumns columns;

    private MysqlIndexes indexes;

    private MysqlTriggers triggers;

    private MysqlForeignKeys foreignKeys;

}

package cn.oyzh.easymysql.db.table;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.check.MysqlChecks;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.db.index.MysqlIndexes;
import cn.oyzh.easymysql.db.trigger.MysqlTriggers;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-14
 */
@Data
@Accessors(fluent = true, chain = true)
public class MysqlTableSelectParam {

    private boolean full;

    private String dbName;

    private String tableName;

}

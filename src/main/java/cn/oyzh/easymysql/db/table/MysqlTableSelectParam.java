package cn.oyzh.easymysql.db.table;

import lombok.Data;
import lombok.experimental.Accessors;

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

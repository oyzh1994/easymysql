package cn.oyzh.easymysql.db.column;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-09-14
 */
@Data
@Accessors(fluent = true, chain = true)
public class MysqlSelectColumnParam {

    private String dbName;

    private String schema;

    private String tableName;

}

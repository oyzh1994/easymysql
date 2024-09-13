package cn.oyzh.easymysql.db.record;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-09-13
 */
@Data
@Accessors(fluent = true, chain = true)
public class MysqlDeleteRecordParam {

    private String dbName;

    private String schema;

    private String tableName;

    private MysqlRecordData record;

    private MysqlRecordPrimaryKey primaryKey;

}

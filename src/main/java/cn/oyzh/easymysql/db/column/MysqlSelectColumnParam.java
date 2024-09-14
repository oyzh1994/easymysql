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

    private boolean full;

    public MysqlSelectColumnParam() {
    }

    public MysqlSelectColumnParam(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public MysqlSelectColumnParam(String dbName, String schema, String tableName) {
        this.dbName = dbName;
        this.schema = schema;
        this.tableName = tableName;
    }

}

package cn.oyzh.easymysql.db.record;

import cn.oyzh.easymysql.db.column.MysqlColumn;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-13
 */
@Data
@Accessors(fluent = true, chain = true)
public class MysqlSelectRecordParam {

    private Long start;

    private Long limit;

    private String dbName;

    private String schema;

    private String tableName;

    private boolean readonly;

    private List<MysqlColumn> columns;

    private List<MysqlRecordFilter> filters;

    public boolean hasPageControl() {
        return this.start != null && this.limit != null;
    }

}

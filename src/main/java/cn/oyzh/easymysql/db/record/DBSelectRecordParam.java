package cn.oyzh.easymysql.db.record;

import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-13
 */
@Data
@Accessors(fluent = true, chain = true)
public class DBSelectRecordParam {

    private Long start;

    private Long limit;

    private String dbName;

    private String schema;

    private String tableName;

    private boolean readonly;

    private List<DBColumn> columns;

    private List<DBRecordFilter> filters;

    public boolean hasPageControl() {
        return this.start != null && this.limit != null;
    }

}

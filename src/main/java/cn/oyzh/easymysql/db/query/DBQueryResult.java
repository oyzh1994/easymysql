package cn.oyzh.easymysql.db.query;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.record.DBRecord;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/08/19
 */
@Getter
@Accessors(chain = true, fluent = true)
public abstract class DBQueryResult {

    /**
     * sql
     */
    @Setter
    protected String sql;

    /**
     * 耗时，微妙
     */
    @Setter
    protected long used;

    /**
     * 消息
     */
    @Setter
    protected String msg;

    /**
     * 变更总数
     */
    @Setter
    protected int updateCount;

    /**
     * 是否成功
     */
    @Setter
    protected boolean success;

    /**
     * 字段列表
     */
    protected DBColumns columns;

    /**
     * 行列表
     */
    protected List<DBRecord> records;

    public boolean hasResult() {
        if (CollUtil.isNotEmpty(this.records)) {
            return true;
        }
        return this.columns == null || this.columns.isEmpty();
    }

    public void parseResult(ResultSet resultSet, Connection connection) throws Exception {
        this.parseResult(resultSet, connection, true);
    }

    public abstract void parseResult(ResultSet resultSet, Connection connection, boolean readonly) throws Exception;

    public String dbName() {
        if (this.columns != null) {
            for (DBColumn column : this.columns) {
                return column.getDbName();
            }
        }
        return null;
    }

    public String tableName() {
        if (this.columns != null) {
            for (DBColumn column : this.columns) {
                return column.getTableName();
            }
        }
        return null;
    }

    public DBColumn getPrimaryKey() {
        if (this.columns != null) {
            for (DBColumn column : this.columns) {
                if (column.isAutoIncrement()) {
                    return column;
                }
            }
        }
        return null;
    }

    public boolean isUpdatable() {
        if (this.columns != null) {
            for (DBColumn column : this.columns) {
                if (column.isAutoIncrement()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getCount() {
        return this.records == null ? 0 : this.records.size();
    }

    public long getUsedMs() {
        return this.used / 1_000_000L;
    }

    public List<DBColumn> columnList() {
        if (this.columns == null) {
            return Collections.emptyList();
        }
        return this.columns;
    }
}

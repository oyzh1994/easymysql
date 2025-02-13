package cn.oyzh.easymysql.db.query;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.record.MysqlRecord;
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
public abstract class MysqlQueryResult {

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
    protected MysqlColumns columns;

    /**
     * 行列表
     */
    protected List<MysqlRecord> records;

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
            for (MysqlColumn column : this.columns) {
                return column.getDbName();
            }
        }
        return null;
    }

    public String tableName() {
        if (this.columns != null) {
            for (MysqlColumn column : this.columns) {
                return column.getTableName();
            }
        }
        return null;
    }

    public MysqlColumn getPrimaryKey() {
        if (this.columns != null) {
            for (MysqlColumn column : this.columns) {
                if (column.isAutoIncrement()) {
                    return column;
                }
            }
        }
        return null;
    }

    public boolean isUpdatable() {
        if (this.columns != null) {
            for (MysqlColumn column : this.columns) {
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

    public List<MysqlColumn> columnList() {
        if (this.columns == null) {
            return Collections.emptyList();
        }
        return this.columns;
    }
}

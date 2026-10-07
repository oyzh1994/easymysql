package cn.oyzh.easymysql.mysql.query;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.easymysql.mysql.column.MysqlColumns;
import cn.oyzh.easymysql.mysql.record.MysqlRecord;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

/**
 * MySQL查询结果
 *
 * @author oyzh
 * @since 2024/08/19
 */
public abstract class MysqlQueryResult {

    /**
     * sql
     */
    protected String sql;

    /**
     * 耗时，微妙
     */
    protected long used;

    /**
     * 消息
     */
    protected String msg;

    /**
     * 变更总数
     */
    protected int updateCount;

    /**
     * 是否成功
     */
    protected boolean success;

    /**
     * 字段列表
     */
    protected MysqlColumns columns;

    /**
     * 行列表
     */
    protected List<MysqlRecord> records;

    /**
     * 是否存在结果
     *
     * @return 是否存在结果
     */
    public boolean hasResult() {
        if (CollectionUtil.isNotEmpty(this.records)) {
            return true;
        }
        return this.columns == null || this.columns.isEmpty();
    }

    /**
     * 解析结果
     *
     * @param resultSet  结果集
     * @param connection 连接
     * @throws Exception 异常
     */
    public void parseResult(ResultSet resultSet, Connection connection) throws Exception {
        this.parseResult(resultSet, connection, true);
    }

    /**
     * 解析结果
     *
     * @param resultSet  结果集
     * @param connection 连接
     * @param readonly   是否只读
     * @throws Exception 异常
     */
    public abstract void parseResult(ResultSet resultSet, Connection connection, boolean readonly) throws Exception;

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String dbName() {
        if (this.columns != null) {
            for (MysqlColumn column : this.columns) {
                return column.getDbName();
            }
        }
        return null;
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        if (this.columns != null) {
            for (MysqlColumn column : this.columns) {
                return column.getTableName();
            }
        }
        return null;
    }

    /**
     * 获取主键字段
     *
     * @return 主键字段
     */
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

    /**
     * 是否可更新
     *
     * @return 是否可更新
     */
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

    /**
     * 获取记录数量
     *
     * @return 记录数量
     */
    public int getCount() {
        return this.records == null ? 0 : this.records.size();
    }

    /**
     * 获取耗时（毫秒）
     *
     * @return 耗时
     */
    public long getUsedMs() {
        return this.used / 1_000_000L;
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public List<MysqlColumn> columnList() {
        if (this.columns == null) {
            return Collections.emptyList();
        }
        return this.columns;
    }

    /**
     * 获取sql
     *
     * @return sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * 设置sql
     *
     * @param sql sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * 获取耗时
     *
     * @return 耗时
     */
    public long getUsed() {
        return used;
    }

    /**
     * 设置耗时
     *
     * @param used 耗时
     */
    public void setUsed(long used) {
        this.used = used;
    }

    /**
     * 获取消息
     *
     * @return 消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置消息
     *
     * @param msg 消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取变更总数
     *
     * @return 变更总数
     */
    public int getUpdateCount() {
        return updateCount;
    }

    /**
     * 设置变更总数
     *
     * @param updateCount 变更总数
     */
    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    /**
     * 是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置是否成功
     *
     * @param success 是否成功
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public MysqlColumns getColumns() {
        return columns;
    }

    /**
     * 设置字段列表
     *
     * @param columns 字段列表
     */
    public void setColumns(MysqlColumns columns) {
        this.columns = columns;
    }

    /**
     * 获取行列表
     *
     * @return 行列表
     */
    public List<MysqlRecord> getRecords() {
        return records;
    }

    /**
     * 设置行列表
     *
     * @param records 行列表
     */
    public void setRecords(List<MysqlRecord> records) {
        this.records = records;
    }
}

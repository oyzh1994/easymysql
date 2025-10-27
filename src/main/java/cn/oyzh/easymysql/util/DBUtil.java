package cn.oyzh.easymysql.util;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.record.MysqlRecordData;
import cn.oyzh.easymysql.exception.DBException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * db工具类
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class DBUtil {

    public static boolean ENABLE_PRINT_METADATA = true;

    /**
     * 是否内部库
     *
     * @param dbName 数据库名称
     * @return 结果
     */
    public static boolean isInternalDatabase(String dbName) {
        return StrUtil.equalsAnyIgnoreCase(dbName, "mysql", "information_schema", "performance_schema");
    }

    /**
     * 检查表类型
     *
     * @param resultSet 结果集
     * @param dbName    数据库名称
     * @return 结果
     * @throws SQLException 异常
     */
    public static boolean checkTableType(ResultSet resultSet, String dbName) throws SQLException {
        String tableCat = resultSet.getString("TABLE_CAT");
        String tableType = resultSet.getString("TABLE_TYPE");
        return StrUtil.equalsIgnoreCase(tableCat, dbName) && !StrUtil.equalsIgnoreCase("VIEW", tableType);
    }

    /**
     * 检查视图类型
     *
     * @param resultSet 结果集
     * @param dbName    数据库名称
     * @return 结果
     * @throws SQLException 异常
     */
    public static boolean checkViewType(ResultSet resultSet, String dbName) throws SQLException {
        String tableCat = resultSet.getString("TABLE_CAT");
        String tableType = resultSet.getString("TABLE_TYPE");
        return StrUtil.equalsIgnoreCase(tableCat, dbName) && StrUtil.equalsIgnoreCase("VIEW", tableType);
    }

    /**
     * 检查数据库是否相同
     *
     * @param resultSet 结果集
     * @param dbName    数据库名称
     * @return 结果
     * @throws SQLException 异常
     */
    public static boolean checkTableCat(ResultSet resultSet, String dbName, String tableName) throws SQLException {
        String tableCat = resultSet.getString("TABLE_CAT");
        String tableName1 = resultSet.getString("TABLE_NAME");
        return Objects.equals(tableCat, dbName) && Objects.equals(tableName1, tableName);
    }

    /**
     * 检查数据库是否相同
     *
     * @param resultSet 结果集
     * @param dbName    数据库名称
     * @return 结果
     * @throws SQLException 异常
     */
    public static boolean checkProcedureType(ResultSet resultSet, String dbName) throws SQLException {
        String tableCat = resultSet.getString("PROCEDURE_CAT");
        return StrUtil.equals(tableCat, dbName);
    }

    /**
     * 检查数据库是否相同
     *
     * @param resultSet 结果集
     * @param dbName    数据库名称
     * @return 结果
     * @throws SQLException 异常
     */
    public static boolean checkFunctionType(ResultSet resultSet, String dbName) throws SQLException {
        String functionCat = resultSet.getString("FUNCTION_CAT");
        return StrUtil.equalsIgnoreCase(functionCat, dbName);
    }

    /**
     * 检查数据库是否相同
     *
     * @param resultSet 结果集
     * @param dbName    数据库名称
     * @return 结果
     * @throws SQLException 异常
     */
    public static boolean checkFunctionType(ResultSet resultSet, String dbName, String schema) throws SQLException {
        String functionCat = resultSet.getString("FUNCTION_CAT");
        if (!StrUtil.equalsIgnoreCase(functionCat, dbName)) {
            return false;
        }
        if (schema == null) {
            return true;
        }
        String functionSchem = resultSet.getString("FUNCTION_SCHEM");
        if (functionSchem != null) {
            return StrUtil.equalsIgnoreCase(functionSchem, schema);
        }
        return true;
    }

    /**
     * 打印元数据
     *
     * @param resultSet 结果集
     * @throws SQLException 异常
     */
    public static void printMetaData(ResultSet resultSet) throws SQLException {
        if (ENABLE_PRINT_METADATA) {
            // 获取结果集元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            // 获取列数
            int columnCount = metaData.getColumnCount();
            // 遍历结果集并输出列名
            for (int i = 1; i <= columnCount; i++) {
                // 获取列名
                String columnName = metaData.getColumnName(i);
                JulLog.info("Column Name: {}", columnName);
            }
            JulLog.info("printMetaData======================>");
        }
    }

    /**
     * 打印sql
     *
     * @param sql sql语句
     */
    public static void printSql(String sql) {
        JulLog.info("\n" + sql);
        // JulLog.info("printSql======================>");
    }

    /**
     * 打印sql
     *
     * @param sqlList sql列表
     */
    public static void printSql(List<String> sqlList) {
        JulLog.info("\n" + Arrays.toString(sqlList.toArray()));
        // JulLog.info("printSql======================>");
    }

    /**
     * 打印数据
     *
     * @param data 数据
     */
    public static void printData(MysqlRecordData data) {
        if (data != null) {
            for (Map.Entry<MysqlColumn, Object> entry : data.entries()) {
                JulLog.info(entry.getKey().getName() + "=" + entry.getValue());
            }
            JulLog.info("printData======================>");
        }
    }

    /**
     * 打印信息
     *
     * @param sql  sql
     * @param data 数据
     */
    public static void printInfo(String sql, MysqlRecordData data) {
        printSql(sql);
        printData(data);
    }

    @Deprecated
    public static String wrap(String name) {
        if (name == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (!name.startsWith("`")) {
            builder.append("`");
        }
        builder.append(name);
        if (!name.endsWith("`")) {
            builder.append("`");
        }
        return builder.toString();
    }

    public static String wrap(String name, DBDialect dialect) {
        StringBuilder builder = new StringBuilder();
        if (dialect == DBDialect.MYSQL) {
            if (!name.startsWith("`")) {
                builder.append("`");
            }
            builder.append(name);
            if (!name.endsWith("`")) {
                builder.append("`");
            }
        }
        return builder.toString();
    }

    @Deprecated
    public static String wrap(String dbName, String tableName) {
        return wrap(dbName) + "." + wrap(tableName);
    }

    public static String wrap(String dbName, String tableName, DBDialect dialect) {
        if (dialect == DBDialect.MYSQL) {
            return wrap(dbName) + "." + wrap(tableName);
        }
        return null;
    }

    public static Object wrapData(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Number) {
            return val;
        }
        if (val instanceof CharSequence v) {
            String v1 = v.toString();
            if (v1.isEmpty()) {
                return "''";
            }
            if (!v1.startsWith("'") && !v1.startsWith("\"")) {
                v1 = "'" + v1;
            }
            if (!v1.endsWith("'") && !v1.endsWith("\"")) {
                v1 = v1 + "'";
            }
            return v1;
        }
        if (val instanceof LocalDateTime) {
            return "'" + val + "'";
        }
        return val;
    }

    public static Object unwrapData(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof CharSequence v) {
            String v1 = v.toString();
            if (v1.isEmpty()) {
                return null;
            }
            if (v1.startsWith("'") || v1.startsWith("\"")) {
                v1 = v1.substring(1);
            }
            if (v1.endsWith("'") || v1.endsWith("\"")) {
                v1 = v1.substring(0, v1.length() - 1);
            }
            return v1;
        }
        return val;
    }

    public static void setVal(PreparedStatement statement, Object val, int index) throws SQLException {
        if (val == null) {
            statement.setNull(index, JDBCType.NULL.ordinal());
        } else if (val instanceof Byte x) {
            statement.setByte(index, x);
        } else if (val instanceof Short x) {
            statement.setShort(index, x);
        } else if (val instanceof Integer x) {
            statement.setInt(index, x);
        } else if (val instanceof Long x) {
            statement.setLong(index, x);
        } else if (val instanceof Float x) {
            statement.setFloat(index, x);
        } else if (val instanceof Double x) {
            statement.setDouble(index, x);
        } else if (val instanceof CharSequence x) {
            statement.setString(index, x.toString());
        } else if (val instanceof Date x) {
            statement.setDate(index, x);
        } else if (val instanceof Timestamp x) {
            statement.setTimestamp(index, x);
        } else if (val instanceof java.util.Date x) {
            statement.setDate(index, new Date(x.getTime()));
        } else if (val instanceof LocalDate x) {
            statement.setDate(index, Date.valueOf(x));
        } else if (val instanceof LocalDateTime x) {
            statement.setTimestamp(index, Timestamp.valueOf(x));
        } else if (val instanceof Object x) {
            statement.setObject(index, x);
        }
    }

    public static boolean isSameVal(Object val, Object nVal) {
        if (val == nVal) {
            return true;
        }
        if (Objects.equals(val, nVal)) {
            return true;
        }
        if (val instanceof Number n1 && nVal instanceof Number n2) {
            if (n1.doubleValue() == n2.doubleValue()) {
                return true;
            }
        }
        if (val instanceof byte[] b1 && nVal instanceof byte[] b2) {
            if (StrUtil.equals(new String(b1), new String(b2))) {
                return true;
            }
        }
        return false;
    }

    public static void rollback(Connection connection) {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    public static int executeUpdate(PreparedStatement statement) throws SQLException {
        int result = statement.executeUpdate();
        statement.close();
        return result;
    }

    public static void close(AutoCloseable o) throws Exception {
        if (o instanceof ResultSet resultSet) {
            resultSet.close();
        } else if (o instanceof Statement statement) {
            statement.close();
        } else if (o instanceof Connection connection) {
            connection.close();
        } else if (o != null) {
            o.close();
        }
    }
}

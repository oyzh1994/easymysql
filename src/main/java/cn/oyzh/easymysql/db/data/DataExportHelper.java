package cn.oyzh.easymysql.db.data;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.oyzh.easymysql.db.record.DBRecord;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.easymysql.util.DBDataUtil;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.common.util.TextUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/02
 */
@UtilityClass
public class DataExportHelper {

    /**
     * 参数化，json
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForJson(DBColumn column, Object value, DataExportConfig config) {
        if (value == null) {
            return null;
        }
        if (column.supportGeometry()) {
            return "ST_GeomFromText('" + value + "')";
        }
        if (column.isDateType() || column.supportTimestamp()) {
            if (value instanceof LocalDateTime date) {
                return DateUtil.format(date, config.dateFormat());
            }
            if (value instanceof Date date) {
                return DateUtil.format(date, config.dateFormat());
            }
        }
        if (column.supportJson()) {
            return value.toString();
        }
        if (column.supportBinary()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "0x" + HexUtil.encodeHexStr(bytes, false);
        }
        if (column.supportBit()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "b'" + TextUtil.byteToBitStr(bytes) + "'";
        }
        if (column.supportEnum()) {
            return value.toString();
        }
        if (column.supportString()) {
            return DBDataUtil.escapeQuotes((String) value);
        }
        if (column.supportInteger() || column.supportDigits()) {
            return value;
        }
        return value.toString();
    }

    /**
     * 参数化，xml
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForXml(DBColumn column, Object value, DataExportConfig config) {
        if (value == null) {
            return null;
        }
        if (column.supportGeometry()) {
            return "ST_GeomFromText('" + value + "')";
        }
        if (column.isDateType() || column.supportTimestamp()) {
            if (value instanceof LocalDateTime date) {
                return DateUtil.format(date, config.dateFormat());
            }
            if (value instanceof Date date) {
                return DateUtil.format(date, config.dateFormat());
            }
        }
        if (column.supportJson()) {
            return value.toString();
        }
        if (column.supportBinary()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "0x" + HexUtil.encodeHexStr(bytes, false);
        }
        if (column.supportBit()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "b'" + TextUtil.byteToBitStr(bytes) + "'";
        }
        if (column.supportEnum()) {
            return value.toString();
        }
        if (column.supportString()) {
            return DBDataUtil.escapeQuotes((String) value);
        }
        if (column.supportInteger() || column.supportDigits()) {
            return value;
        }
        return value.toString();
    }

    /**
     * 参数化，csv
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForCsv(DBColumn column, Object value, DataExportConfig config) {
        if (value == null) {
            return "";
        }
        if (column.supportGeometry()) {
            return "\"ST_GeomFromText('" + value + "')\"";
        }
        if (column.isDateType() || column.supportTimestamp()) {
            if (value instanceof LocalDateTime date) {
                return "\"" + DateUtil.format(date, config.dateFormat()) + "\"";
            }
            if (value instanceof Date date) {
                return "\"" + DateUtil.format(date, config.dateFormat()) + "\"";
            }
        }
        if (column.supportBinary()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "0x" + HexUtil.encodeHexStr(bytes, false);
        }
        if (column.supportBit()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "\"b'" + TextUtil.byteToBitStr(bytes) + "'\"";
        }
        if (column.supportString()) {
            return "\"" + DBDataUtil.escapeQuotes((String) value) + "\"";
        }
        return "\"" + value + "\"";
    }

    /**
     * 参数化，sql
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForSql(DBColumn column, Object value, DataExportConfig config) {
        if (value == null) {
            return "NULL";
        }
        if (column.supportGeometry()) {
            return "ST_GeomFromText('" + value + "')";
        }
        if (column.isDateType() || column.supportTimestamp()) {
            if (value instanceof LocalDateTime date) {
                return "'" + DateUtil.format(date, config.dateFormat()) + "'";
            }
            if (value instanceof Date date) {
                return "'" + DateUtil.format(date, config.dateFormat()) + "'";
            }
        }
        if (column.supportJson()) {
            return "'" + value + "'";
        }
        if (column.supportBinary()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "NULL";
            }
            return "0x" + HexUtil.encodeHexStr(bytes, false);
        }
        if (column.supportBit()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "NULL";
            }
            return "b'" + TextUtil.byteToBitStr(bytes) + "'";
        }
        if (column.supportEnum()) {
            return "'" + value + "'";
        }
        if (column.supportString()) {
            String str = DBDataUtil.escapeQuotes((String) value);
            return "'" + str + "'";
        }
        return value;
    }

    /**
     * 参数化，html
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForHtml(DBColumn column, Object value, DataExportConfig config) {
        if (value == null) {
            return "";
        }
        if (column.supportGeometry()) {
            return "ST_GeomFromText('" + value + "')";
        }
        if (column.isDateType() || column.supportTimestamp()) {
            if (value instanceof LocalDateTime date) {
                return DateUtil.format(date, config.dateFormat());
            }
            if (value instanceof Date date) {
                return DateUtil.format(date, config.dateFormat());
            }
        }
        if (column.supportJson()) {
            return value.toString();
        }
        if (column.supportBinary()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "0x" + HexUtil.encodeHexStr(bytes, false);
        }
        if (column.supportBit()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "b'" + TextUtil.byteToBitStr(bytes) + "'";
        }
        if (column.supportEnum()) {
            return value.toString();
        }
        if (column.supportString()) {
            return DBDataUtil.escapeQuotes((String) value);
        }
        if (column.supportInteger() || column.supportDigits()) {
            return value;
        }
        return value.toString();
    }

    /**
     * 参数化，xls
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForXls(DBColumn column, Object value, DataExportConfig config) {
        if (value == null) {
            return null;
        }
        if (column.supportGeometry()) {
            return "ST_GeomFromText('" + value + "')";
        }
        if (column.isDateType() || column.supportTimestamp()) {
            if (value instanceof LocalDateTime date) {
                return DateUtil.format(date, config.dateFormat());
            }
            if (value instanceof Date date) {
                return DateUtil.format(date, config.dateFormat());
            }
        }
        if (column.supportJson()) {
            return value.toString();
        }
        if (column.supportBinary()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "0x" + HexUtil.encodeHexStr(bytes, false);
        }
        if (column.supportBit()) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "";
            }
            return "b'" + TextUtil.byteToBitStr(bytes) + "'";
        }
        if (column.supportEnum()) {
            return value.toString();
        }
        if (column.supportString()) {
            return DBDataUtil.escapeQuotes((String) value);
        }
        return value;
    }

    /**
     * 转换为导出sql
     *
     * @param columns 字段列表
     * @param records 记录
     * @param config  配置
     * @return 插入sql
     */
    public static List<String> toExportSql(DBColumns columns, List<DBRecord> records, DataExportConfig config) {
        List<String> list = new ArrayList<>();
        String tableName = columns.getTableName();
        List<DBColumn> columnList = columns.sortOfPosition();
        final String sqlBase = "INSERT INTO " + DBUtil.wrap(tableName);
        for (DBRecord record : records) {
            StringBuilder sql = new StringBuilder(sqlBase);
            if (config.includeFields()) {
                sql.append("(");
                for (DBColumn dbColumn : columnList) {
                    sql.append(DBUtil.wrap(dbColumn.getName())).append(", ");
                }
                if (sql.toString().endsWith(", ")) {
                    sql.delete(sql.length() - 2, sql.length());
                }
                sql.append(")");
            }
            sql.append(" VALUES (");
            for (DBColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForSql(dbColumn, value, config);
                sql.append(value).append(", ");
            }
            if (sql.toString().endsWith(", ")) {
                sql.delete(sql.length() - 2, sql.length());
            }
            sql.append(");");
            list.add(sql.toString());
        }
        return list;
    }

    /**
     * 转换为插入json
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入json
     */
    public static List<Map<String, Object>> toExportJson(DBColumns columns, List<DBRecord> records, DataExportConfig config) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<DBColumn> columnList = columns.sortOfPosition();
        for (DBRecord record : records) {
            Map<String, Object> object = new HashMap<>();
            for (DBColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForJson(dbColumn, value, config);
                object.put(dbColumn.getName(), value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入xml
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入xml
     */
    public static List<Map<String, Object>> toExportXml(DBColumns columns, List<DBRecord> records, DataExportConfig config) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<DBColumn> columnList = columns.sortOfPosition();
        for (DBRecord record : records) {
            Map<String, Object> object = new HashMap<>();
            for (DBColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForXml(dbColumn, value, config);
                object.put(dbColumn.getName(), value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入csv
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入csv
     */
    public static List<List<Object>> toExportCsv(DBColumns columns, List<DBRecord> records, DataExportConfig config) {
        List<List<Object>> list = new ArrayList<>();
        List<DBColumn> columnList = columns.sortOfPosition();
        for (DBRecord record : records) {
            List<Object> object = new ArrayList<>();
            for (DBColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForCsv(dbColumn, value, config);
                object.add(value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入html
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入html
     */
    public static List<List<Object>> toExportHtml(DBColumns columns, List<DBRecord> records, DataExportConfig config) {
        List<List<Object>> list = new ArrayList<>();
        List<DBColumn> columnList = columns.sortOfPosition();
        for (DBRecord record : records) {
            List<Object> object = new ArrayList<>();
            for (DBColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForHtml(dbColumn, value, config);
                object.add(value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入xls
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入xls
     */
    public static List<List<Object>> toExportXls(DBColumns columns, List<DBRecord> records, DataExportConfig config) {
        List<List<Object>> list = new ArrayList<>();
        List<DBColumn> columnList = columns.sortOfPosition();
        for (DBRecord record : records) {
            List<Object> object = new ArrayList<>();
            for (DBColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForXls(dbColumn, value, config);
                object.add(value);
            }
            list.add(object);
        }
        return list;
    }

}

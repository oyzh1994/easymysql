package cn.oyzh.easymysql.db.data;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.easymysql.util.DBDataUtil;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.common.file.LineFileWriter;
import cn.oyzh.fx.common.util.TextUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class MysqlSqlMysqlTypeFileWriter extends MysqlTypeFileWriter {

    /**
     * 字段列表
     */
    private DBColumns columns;

    /**
     * 导出配置
     */
    private MysqlDataExportConfig config;

    /**
     * 文件写入器
     */
    private final LineFileWriter writer;

    public MysqlSqlMysqlTypeFileWriter(String filePath, MysqlDataExportConfig config, DBColumns columns) {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeObject(Map<String, Object> object) throws Exception {
        String tableName = this.columns.getTableName();
        List<DBColumn> columnList = this.columns.sortOfPosition();
        final String sqlBase = "INSERT INTO " + DBUtil.wrap(tableName);
        StringBuilder sql = new StringBuilder(sqlBase);
        if (this.config.includeFields()) {
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
            Object val = object.get(dbColumn.getName());
            val = this.parameterized(dbColumn, val, this.config);
            sql.append(val).append(", ");
        }
        if (sql.toString().endsWith(", ")) {
            sql.delete(sql.length() - 2, sql.length());
        }
        sql.append(");");
        this.writer.writeLine(sql.toString());
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.config = null;
        this.columns = null;
    }

    @Override
    public Object parameterized(DBColumn column, Object value, MysqlDataExportConfig config) {
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
}

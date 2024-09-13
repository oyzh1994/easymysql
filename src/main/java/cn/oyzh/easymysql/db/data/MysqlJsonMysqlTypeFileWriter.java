package cn.oyzh.easymysql.db.data;

import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.fx.common.file.LineFileWriter;

import java.io.IOException;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class MysqlJsonMysqlTypeFileWriter extends MysqlTypeFileWriter {

    /**
     * 字段列表
     */
    private DBColumns columns;

    /**
     * 导出配置
     */
    private MysqlDataExportConfig config;

    /**
     * 文件读取器
     */
    private LineFileWriter writer;

    /**
     * 是否首次写入
     */
    private boolean firstWrite = true;

    public MysqlJsonMysqlTypeFileWriter(String filePath, MysqlDataExportConfig config, DBColumns columns) {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeHeader() throws Exception {
        if (this.config.earlyVersion()) {
            this.writer.writeLine("{");
            this.writer.writeLine(" \"RECORDS\": [");
        } else {
            this.writer.writeLine("[");
        }
    }

    @Override
    public void writeTrial() throws Exception {
        if (this.config.earlyVersion()) {
            this.writer.write("\n]}");
        } else {
            this.writer.write("\n]");
        }
    }

    @Override
    public void writeObject(Map<String, Object> object) throws Exception {
        if (!this.firstWrite) {
            this.writer.write(",\n");
        }
        int size = object.size();
        StringBuilder builder = new StringBuilder("  {\n");
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            // 名称
            builder.append("   \"").append(entry.getKey()).append("\" : ");
            // 值处理
            DBColumn column = this.columns.column(entry.getKey());
            Object val = this.parameterized(column, entry.getValue(), this.config);
            if (val != null) {
                // 数字
                if (val instanceof Number) {
                    builder.append(val);
                } else {// 其他类型
                    builder.append("\"").append(val).append("\"");
                }
            } else {
                builder.append("null");
            }
            if (--size != 0) {
                builder.append(",\n");
            } else {
                builder.append("\n");
            }
        }
        builder.append("  }");
        this.writer.write(builder.toString());
        this.firstWrite = false;
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.writer = null;
        this.config = null;
        this.columns = null;
    }

    @Override
    public Object parameterized(DBColumn column, Object value, MysqlDataExportConfig config) {
        if (value == null) {
            return null;
        }
        return super.parameterized(column, value, config);
    }
}

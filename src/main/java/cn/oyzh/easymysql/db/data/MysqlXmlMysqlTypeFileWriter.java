package cn.oyzh.easymysql.db.data;

import cn.hutool.core.io.IoUtil;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.fx.common.file.LineFileWriter;

import java.io.IOException;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class MysqlXmlMysqlTypeFileWriter extends MysqlTypeFileWriter {

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
    private LineFileWriter writer;

    public MysqlXmlMysqlTypeFileWriter(String filePath, MysqlDataExportConfig config, DBColumns columns) {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeHeader() throws Exception {
        this.writer.writeLine("<?xml version=\"1.0\" standalone=\"yes\"?>");
        this.writer.writeLine("<RECORDS>");
    }

    @Override
    public void writeTrial() throws Exception {
        this.writer.writeLine("</RECORDS>");
    }

    @Override
    public void writeObject(Map<String, Object> object) throws Exception {
        StringBuilder builder;
        if (this.config.fieldToAttr()) {
            builder = new StringBuilder("  <RECORD ");
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                // 值处理
                DBColumn column = this.columns.column(entry.getKey());
                Object val = this.parameterized(column, entry.getValue(), this.config);
                if (val != null) {
                    builder.append(entry.getKey())
                            .append("=\"")
                            .append(val)
                            .append("\" ");
                }
            }
            builder.append(" />");
        } else {
            builder = new StringBuilder("  <RECORD>\n");
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                // 名称
                builder.append("   <").append(entry.getKey());
                // 值处理
                DBColumn column = this.columns.column(entry.getKey());
                Object val = this.parameterized(column, entry.getValue(), this.config);
                if (val != null) {
                    builder.append(">");
                    builder.append(val);
                    builder.append("</").append(entry.getKey()).append(">");
                } else {
                    builder.append("/>");
                }
                builder.append("\n");
            }
            builder.append("  </RECORD>");
        }
        this.writer.writeLine(builder.toString());
    }

    @Override
    public void close() throws IOException {
        IoUtil.close(this.writer);
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

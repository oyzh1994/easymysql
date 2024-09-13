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
public class TxtTypeFileWriter extends TypeFileWriter {

    /**
     * 字段列表
     */
    private DBColumns columns;

    /**
     * 导出配置
     */
    private DataExportConfig config;

    /**
     * 文件写入器
     */
    private LineFileWriter writer;

    public TxtTypeFileWriter(String filePath, DataExportConfig config, DBColumns columns) {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeHeader() throws Exception {
        this.writer.write(this.formatLine(this.columns.columnNames(), this.config.fieldSeparator(), this.config.txtIdentifier(), this.config.recordSeparator()));
    }

    @Override
    public void writeObject(Map<String, Object> object) throws Exception {
        Object[] values = new Object[object.size()];
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            int index = this.columns.index(entry.getKey());
            DBColumn column = this.columns.column(entry.getKey());
            Object val = this.parameterized(column, entry.getValue(), this.config);
            values[index] = val;
        }
        this.writer.write(this.formatLine(values, this.config.fieldSeparator(), this.config.txtIdentifier(), this.config.recordSeparator()));
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.writer = null;
        this.config = null;
        this.columns = null;
    }
}

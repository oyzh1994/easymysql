package cn.oyzh.easymysql.db.query;

import cn.oyzh.easymysql.db.DBHelper;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author oyzh
 * @since 2024/02/19
 */
@Getter
@Accessors(chain = true, fluent = true)
public class MysqlExecuteResult extends MysqlQueryResult {

    /**
     * 是否全字段
     */
    @Setter
    private boolean fullColumn;

    @Override
    public boolean hasResult() {
        if (this.updateCount > 0) {
            return false;
        }
        return super.hasResult();
    }

    @Override
    public void parseResult(ResultSet resultSet, Connection connection, boolean readonly) throws Exception {
        // 获取列数
        this.records = new ArrayList<>();
        this.columns = DBHelper.parseColumns(resultSet);
        while (resultSet.next()) {
            MysqlRecord record = new MysqlRecord(readonly);
            int colIndex = 1;
            for (MysqlColumn dbColumn : this.columns) {
                Object data = resultSet.getObject(colIndex++);
                // 获取几何值
                if (dbColumn.supportGeometry()) {
                    data = DBHelper.getGeometryString(connection, data);
                }
                record.putValue(dbColumn, data);
            }
            this.records.add(record);
        }
    }
}

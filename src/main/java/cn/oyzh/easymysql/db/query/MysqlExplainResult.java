package cn.oyzh.easymysql.db.query;

import cn.oyzh.easymysql.db.DBHelper;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author oyzh
 * @since 2024/08/16
 */
@Getter
@Accessors(chain = true, fluent = true)
public class MysqlExplainResult extends MysqlQueryResult {

    @Override
    public void parseResult(ResultSet resultSet, Connection connection, boolean readonly) throws SQLException {
        this.columns = DBHelper.parseColumns(resultSet);
        this.records = new ArrayList<>();
        while (resultSet.next()) {
            int colIndex = 1;
            MysqlRecord record = new MysqlRecord(readonly);
            for (MysqlColumn dbColumn : this.columns) {
                Object data = resultSet.getObject(colIndex++);
                record.putValue(dbColumn, data);
            }
            this.records.add(record);
        }
    }
}

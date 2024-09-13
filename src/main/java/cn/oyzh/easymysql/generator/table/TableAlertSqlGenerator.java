package cn.oyzh.easymysql.generator.table;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.table.MysqlTable;
import lombok.Getter;

/**
 *
 * @author oyzh
 * @since 2024/01/25
 */
public abstract class TableAlertSqlGenerator {

    @Getter
    private DBDialect dialect;

    public TableAlertSqlGenerator(DBDialect dialect) {
        this.dialect = dialect;
    }

    public abstract String generate(MysqlTable table);

    public static String generate(DBDialect dialect, MysqlTable table) {
        return switch (dialect) {
            case MYSQL -> new MysqlTableAlertSqlGenerator().generate(table);
            default -> null;
        };
    }
}

package cn.oyzh.easymysql.generator;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.module.mariadb.generator.MariadbTableAlertSqlGenerator;
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

    public abstract String generate(DBTable table);

    public static String generate(DBDialect dialect, DBTable table) {
        return switch (dialect) {
            case MYSQL -> new MysqlTableAlertSqlGenerator().generate(table);
            case MARIADB -> new MariadbTableAlertSqlGenerator().generate(table);
            default -> null;
        };
    }
}

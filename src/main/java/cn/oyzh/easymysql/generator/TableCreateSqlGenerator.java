package cn.oyzh.easymysql.generator;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.generator.table.MysqlTableCreateSqlGenerator;
import cn.oyzh.easymysql.module.mariadb.generator.MariadbTableCreateSqlGenerator;
import lombok.Getter;

/**
 * @author oyzh
 * @since 2024/01/29
 */
public abstract class TableCreateSqlGenerator {

    @Getter
    private DBDialect dialect;

    public TableCreateSqlGenerator(DBDialect dialect) {
        this.dialect = dialect;
    }

    public abstract String generate(DBTable table);

    public static String generate(DBDialect dialect, DBTable table) {
        return switch (dialect) {
            case MYSQL -> new MysqlTableCreateSqlGenerator().generate(table);
            case MARIADB -> new MariadbTableCreateSqlGenerator().generate(table);
            default -> null;
        };
    }
}

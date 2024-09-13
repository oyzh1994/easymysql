package cn.oyzh.easymysql.generator.table;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.table.MysqlTable;
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

    public abstract String generate(MysqlTable table);

    public static String generate(DBDialect dialect, MysqlTable table) {
        return switch (dialect) {
            case MYSQL -> new MysqlTableCreateSqlGenerator().generate(table);
            default -> null;
        };
    }
}

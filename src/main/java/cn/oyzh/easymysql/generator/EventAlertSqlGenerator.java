package cn.oyzh.easymysql.generator;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.event.DBEvent;
import cn.oyzh.easymysql.module.mariadb.generator.MariadbEventAlertSqlGenerator;
import lombok.Getter;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public abstract class EventAlertSqlGenerator {

    @Getter
    private DBDialect dialect;

    public EventAlertSqlGenerator(DBDialect dialect) {
        this.dialect = dialect;
    }

    public abstract String generate(DBEvent event);

    public static String generate(DBDialect dialect, DBEvent event) {
        return switch (dialect) {
            case MYSQL -> new MysqlEventAlertSqlGenerator().generate(event);
            case MARIADB -> new MariadbEventAlertSqlGenerator().generate(event);
            default -> null;
        };
    }
}

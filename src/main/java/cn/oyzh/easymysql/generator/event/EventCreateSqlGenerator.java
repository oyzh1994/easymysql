package cn.oyzh.easymysql.generator.event;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.event.DBEvent;
import cn.oyzh.easymysql.module.mariadb.generator.MariadbEventCreateSqlGenerator;
import lombok.Getter;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public abstract class EventCreateSqlGenerator {

    @Getter
    private DBDialect dialect;

    public EventCreateSqlGenerator(DBDialect dialect) {
        this.dialect = dialect;
    }

    public abstract String generate(DBEvent event);

    public static String generate(DBDialect dialect, DBEvent event) {
        return switch (dialect) {
            case MYSQL -> new MysqlEventCreateSqlGenerator().generate(event);
            case MARIADB -> new MariadbEventCreateSqlGenerator().generate(event);
            default -> null;
        };
    }
}

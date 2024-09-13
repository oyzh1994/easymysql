package cn.oyzh.easymysql.generator.event;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.event.MysqlEvent;
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

    public abstract String generate(MysqlEvent event);

    public static String generate(DBDialect dialect, MysqlEvent event) {
        return switch (dialect) {
            case MYSQL -> new MysqlEventCreateSqlGenerator().generate(event);
            default -> null;
        };
    }
}

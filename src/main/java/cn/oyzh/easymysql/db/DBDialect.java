package cn.oyzh.easymysql.db;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库类型(方言)
 *
 * @author oyzh
 * @since 2024/2/20
 */
public enum DBDialect {
    MYSQL,
    ORACLE,
    MSSQL,
    DB2,
    MARIADB;

    public static DBDialect valueFrom(String dialect) {
        if (dialect != null) {
            for (DBDialect dbDialect : DBDialect.values()) {
                if (dbDialect.name().equals(dialect.toUpperCase())) {
                    return dbDialect;
                }
            }
        }
        return DBDialect.MYSQL;
    }

    public static List<DBDialect> valueList() {
        List<DBDialect> list = new ArrayList<>();
        Collections.addAll(list, values());
        return list;
    }

}

package cn.oyzh.easymysql.db;

import cn.oyzh.easymysql.module.oracle.OracleDBClient;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.module.mariadb.MariaDBClient;
import cn.oyzh.easymysql.module.mssql.MssqlDBClient;
import lombok.experimental.UtilityClass;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2020/6/8
 */
@UtilityClass
public class DBClientUtil {

    public static DBClient newClient(DBInfo info) {
        if (DBDialect.valueOf(info.getType()) == DBDialect.MYSQL) {
            return new MysqlDBClient(info);
        }
        if (DBDialect.valueOf(info.getType()) == DBDialect.ORACLE) {
            return new OracleDBClient(info);
        }
        if (DBDialect.valueOf(info.getType()) == DBDialect.MARIADB) {
            return new MariaDBClient(info);
        }
        if (DBDialect.valueOf(info.getType()) == DBDialect.MSSQL) {
            return new MssqlDBClient(info);
        }
        return null;
    }

}

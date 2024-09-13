package cn.oyzh.easymysql.db;

import cn.oyzh.easymysql.domain.DBInfo;
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
        return null;
    }

}

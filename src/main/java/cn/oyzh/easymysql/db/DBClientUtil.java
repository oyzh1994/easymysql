package cn.oyzh.easymysql.db;

import cn.oyzh.easymysql.domain.MysqlConnect;
import lombok.experimental.UtilityClass;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2020/6/8
 */
@UtilityClass
public class DBClientUtil {

    public static DBClient newClient(MysqlConnect info) {
        if (DBDialect.valueOf(info.getType()) == DBDialect.MYSQL) {
            return new DBClient(info);
        }
        return null;
    }

}

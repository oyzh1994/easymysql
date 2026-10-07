package cn.oyzh.easymysql.db;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.mysql.MysqlClient;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2020/6/8
 */
public class DBClientUtil {

    /**
     * 新建数据库客户端
     *
     * @param info 连接信息
     * @return 数据库客户端
     */
    public static MysqlClient newClient(MysqlConnect info) {
        if (StringUtil.isBlank(info.getType()) || DBDialect.valueOf(info.getType()) == DBDialect.MYSQL) {
            return new MysqlClient(info);
        }
        return null;
    }

}

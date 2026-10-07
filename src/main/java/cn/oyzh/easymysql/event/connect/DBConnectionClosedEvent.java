package cn.oyzh.easymysql.event.connect;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 数据库连接关闭事件
 *
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionClosedEvent extends Event<MysqlClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().connectName());
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public MysqlConnect dbConnect() {
        return this.data().getDbConnect();
    }

    /**
     * 是否mysql类型
     *
     * @return 是否为mysql类型
     */
    public boolean isMysqlType() {
        return this.data().dialect() == DBDialect.MYSQL;
    }
}

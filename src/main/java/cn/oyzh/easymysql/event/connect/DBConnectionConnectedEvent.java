package cn.oyzh.easymysql.event.connect;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 数据库连接成功事件
 *
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionConnectedEvent extends Event<MysqlClient> implements  EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已连接", this.data().connectName());
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public MysqlConnect dbConnect() {
        return this.data().getDbConnect();
    }
}

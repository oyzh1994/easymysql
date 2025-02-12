package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionConnectedEvent extends Event<DBClient> implements  EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已连接", this.data().infoName());
    }

    public MysqlConnect info() {
        return this.data().dbInfo();
    }
}

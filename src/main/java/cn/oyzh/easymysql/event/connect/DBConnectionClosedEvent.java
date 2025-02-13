package cn.oyzh.easymysql.event.connect;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionClosedEvent extends Event<DBClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().connectName());
    }

    public MysqlConnect dbConnect() {
        return this.data().dbConnect();
    }

    public boolean isMysqlType() {
        return this.data().dialect() == DBDialect.MYSQL;
    }
}

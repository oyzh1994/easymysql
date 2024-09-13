package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionClosedEvent extends Event<DBClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().infoName());
    }

    public DBInfo info() {
        return this.data().dbInfo();
    }

    public boolean isMysqlType() {
        return this.data().dialect() == DBDialect.MYSQL;
    }

    public boolean isMariadbType() {
        return this.data().dialect() == DBDialect.MARIADB;
    }
}

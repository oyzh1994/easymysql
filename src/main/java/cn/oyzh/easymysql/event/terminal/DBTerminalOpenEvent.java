package cn.oyzh.easymysql.event.terminal;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/11/20
 */
public class DBTerminalOpenEvent extends Event<MysqlClient> {

    private String dbName;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}

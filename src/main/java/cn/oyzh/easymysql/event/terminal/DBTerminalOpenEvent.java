package cn.oyzh.easymysql.event.terminal;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.event.Event;

/**
 * 终端打开事件
 *
 * @author oyzh
 * @since 2023/11/20
 */
public class DBTerminalOpenEvent extends Event<MysqlClient> {

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置数据库名称
     *
     * @param dbName 数据库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}

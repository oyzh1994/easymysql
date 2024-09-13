package cn.oyzh.easymysql.handler.dump;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.handler.dump.MysqlDataDumpHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author oyzh
 * @since 2024/09/10
 */
@Slf4j
public class MariaDataDumpHandler extends MysqlDataDumpHandler {

    public MariaDataDumpHandler(DBClient dbClient, String dbName) {
        super(dbClient, dbName);
    }
}


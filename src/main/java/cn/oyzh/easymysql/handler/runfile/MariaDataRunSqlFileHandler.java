package cn.oyzh.easymysql.handler.runfile;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.handler.runfile.MysqlDataRunSqlFileHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author oyzh
 * @since 2024/09/10
 */
@Slf4j
public class MariaDataRunSqlFileHandler extends MysqlDataRunSqlFileHandler {

    public MariaDataRunSqlFileHandler(DBClient dbClient, String dbName) {
        super(dbClient, dbName);
    }
}


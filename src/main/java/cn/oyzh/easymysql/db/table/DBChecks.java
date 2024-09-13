package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * db外键列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class DBChecks extends DBObjectList<DBCheck> {

    public DBChecks() {

    }

    public DBChecks(List<DBCheck> list) {
        super.addAll(list);
    }
}

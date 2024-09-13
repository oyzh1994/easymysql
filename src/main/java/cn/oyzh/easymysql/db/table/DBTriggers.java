package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * db表触发器列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class DBTriggers extends DBObjectList<DBTrigger> {

    public DBTriggers() {

    }

    public DBTriggers(List<DBTrigger> list) {
        super.addAll(list);
    }
}

package cn.oyzh.easymysql.db.event;

import cn.oyzh.easymysql.db.DBObjectList;
import cn.oyzh.easymysql.db.table.MysqlCheck;

import java.util.List;

/**
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlEvents extends DBObjectList<MysqlEvent> {

    public MysqlEvents() {

    }

    public MysqlEvents(List<MysqlEvent> list) {
        super.addAll(list);
    }
}

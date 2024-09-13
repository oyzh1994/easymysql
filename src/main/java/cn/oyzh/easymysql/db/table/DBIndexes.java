package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * db表索引
 *
 * @author oyzh
 * @since 2024/01/24
 */
public class DBIndexes extends DBObjectList<DBIndex> {

    public DBIndexes() {

    }

    public DBIndexes(List<DBIndex> list) {
        super.addAll(list);
    }
}

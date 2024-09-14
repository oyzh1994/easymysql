package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * db表索引
 *
 * @author oyzh
 * @since 2024/01/24
 */
public class MysqlIndexes extends DBObjectList<MysqlIndex> {

    public MysqlIndexes() {

    }

    public MysqlIndexes(Collection<MysqlIndex> list) {
        super.addAll(list);
    }
}

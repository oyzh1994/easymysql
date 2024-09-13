package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * db外键列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class DBForeignKeys extends DBObjectList<DBForeignKey> {

    public DBForeignKeys() {

    }

    public DBForeignKeys(List<DBForeignKey> list) {
        super.addAll(list);
    }
}




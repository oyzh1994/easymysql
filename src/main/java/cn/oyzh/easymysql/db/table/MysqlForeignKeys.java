package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * db外键列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlForeignKeys extends DBObjectList<MysqlForeignKey> {

    public MysqlForeignKeys() {

    }

    public MysqlForeignKeys(Collection<MysqlForeignKey> list) {
        super.addAll(list);
    }
}




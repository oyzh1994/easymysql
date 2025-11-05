package cn.oyzh.easymysql.mysql.check;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * db外键列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlChecks extends DBObjectList<MysqlCheck> {

    public MysqlChecks() {

    }

    public MysqlChecks(List<MysqlCheck> list) {
        super.addAll(list);
    }
}

package cn.oyzh.easymysql.mysql.check;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * MySQL检查约束列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlChecks extends DBObjectList<MysqlCheck> {

    /**
     * 构造检查约束列表
     */
    public MysqlChecks() {

    }

    /**
     * 构造检查约束列表
     *
     * @param list 检查约束列表
     */
    public MysqlChecks(List<MysqlCheck> list) {
        super.addAll(list);
    }
}

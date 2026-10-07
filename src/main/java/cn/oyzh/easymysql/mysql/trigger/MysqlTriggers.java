package cn.oyzh.easymysql.mysql.trigger;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * db表触发器列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlTriggers extends DBObjectList<MysqlTrigger> {

    /**
     * 构建触发器列表
     */
    public MysqlTriggers() {

    }

    /**
     * 构建触发器列表
     *
     * @param list 触发器列表
     */
    public MysqlTriggers(List<MysqlTrigger> list) {
        super.addAll(list);
    }
}

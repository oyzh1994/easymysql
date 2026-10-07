package cn.oyzh.easymysql.mysql.event;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.List;

/**
 * MySQL事件列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlEvents extends DBObjectList<MysqlEvent> {

    /**
     * 构建事件列表
     */
    public MysqlEvents() {

    }

    /**
     * 根据事件列表构建事件列表
     *
     * @param list 事件列表
     */
    public MysqlEvents(List<MysqlEvent> list) {
        super.addAll(list);
    }
}

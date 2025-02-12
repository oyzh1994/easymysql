package cn.oyzh.easymysql.event.connect;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class DBInfoUpdatedEvent extends Event<MysqlConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已修改", this.data().getName());
    }
}

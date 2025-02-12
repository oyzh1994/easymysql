package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class DBInfoAddedEvent extends Event<MysqlConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已新增", this.data().getName());
    }
}

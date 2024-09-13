package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class DBInfoAddedEvent extends Event<MysqlInfo> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已新增", this.data().getName());
    }
}

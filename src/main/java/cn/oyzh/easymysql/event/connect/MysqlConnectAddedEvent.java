package cn.oyzh.easymysql.event.connect;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 连接已新增事件
 *
 * @author oyzh
 * @since 2024/01/30
 */
public class MysqlConnectAddedEvent extends Event<MysqlConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已新增", this.data().getName());
    }
}

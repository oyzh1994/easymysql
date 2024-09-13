package cn.oyzh.easymysql.event;

import cn.oyzh.easymysql.trees.query.MysqlQueryTreeItem;
import cn.oyzh.fx.plus.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlQueryDeletedEvent extends Event<MysqlQueryTreeItem> {

    public String queryId() {
        return this.data().value().getId();
    }
}

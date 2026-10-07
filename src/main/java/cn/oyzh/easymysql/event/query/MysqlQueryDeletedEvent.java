package cn.oyzh.easymysql.event.query;

import cn.oyzh.easymysql.trees.query.MysqlQueryTreeItem;
import cn.oyzh.event.Event;

/**
 * 查询已删除事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlQueryDeletedEvent extends Event<MysqlQueryTreeItem> {

    /**
     * 获取查询标识
     *
     * @return 查询标识
     */
    public String queryId() {
        return this.data().value().getUid();
    }
}

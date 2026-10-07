package cn.oyzh.easymysql.event.event;

import cn.oyzh.easymysql.mysql.event.MysqlEvent;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * MySQL事件设计事件
 *
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventDesignEvent extends Event<MysqlEvent> {

    /**
     * 数据库树节点
     */
    private MysqlDatabaseTreeItem dbItem;

    /**
     * 获取事件名称
     *
     * @return 事件名称
     */
    public String eventName() {
        return this.data().getName();
    }

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    /**
     * 设置数据库树节点
     *
     * @param dbItem 数据库树节点
     */
    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}

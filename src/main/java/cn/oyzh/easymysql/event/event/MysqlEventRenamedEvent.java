package cn.oyzh.easymysql.event.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.event.MysqlEventTreeItem;
import cn.oyzh.event.Event;

/**
 * MySQL事件已更名事件
 *
 * @author oyzh
 * @since 2024/01/23
 */
public class MysqlEventRenamedEvent extends Event<MysqlEventTreeItem> {

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
        return this.data().eventName();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.dbItem.dbName();
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

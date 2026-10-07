package cn.oyzh.easymysql.event.event;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * MySQL事件已提示事件
 *
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventAlertedEvent extends Event<String> {

    /**
     * 数据库树节点
     */
    private MysqlDatabaseTreeItem dbItem;

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

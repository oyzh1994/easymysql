package cn.oyzh.easymysql.event.query;

import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * 查询已更名事件
 *
 * @author oyzh
 * @since 2024/01/23
 */
public class MysqlQueryRenamedEvent extends Event<MysqlQuery> {

    /**
     * 数据库树节点
     */
    private MysqlDatabaseTreeItem dbItem;

    /**
     * 获取查询名称
     *
     * @return 查询名称
     */
    public String queryName() {
        return this.data().getName();
    }

    /**
     * 获取查询标识
     *
     * @return 查询标识
     */
    public String queryId() {
        return this.data().getUid();
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

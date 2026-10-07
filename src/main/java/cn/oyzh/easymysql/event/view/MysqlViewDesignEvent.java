package cn.oyzh.easymysql.event.view;

import cn.oyzh.easymysql.mysql.view.MysqlView;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * 视图设计事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlViewDesignEvent extends Event<MysqlView> {

    /**
     * 数据库树节点
     */
    private MysqlDatabaseTreeItem dbItem;

    /**
     * 获取视图名称
     *
     * @return 视图名称
     */
    public String viewName() {
        return this.data().getName();
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

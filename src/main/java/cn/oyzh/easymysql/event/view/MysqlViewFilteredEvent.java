package cn.oyzh.easymysql.event.view;

import cn.oyzh.easymysql.mysql.record.MysqlRecordFilter;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.event.Event;

import java.util.List;

/**
 * 视图已过滤事件
 *
 * @author oyzh
 * @since 2024/06/26
 */
public class MysqlViewFilteredEvent extends Event<MysqlViewTreeItem> {

    /**
     * 过滤条件列表
     */
    private List<MysqlRecordFilter> filters;

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
        return this.data().viewName();
    }

    /**
     * 获取过滤条件列表
     *
     * @return 过滤条件列表
     */
    public List<MysqlRecordFilter> getFilters() {
        return filters;
    }

    /**
     * 设置过滤条件列表
     *
     * @param filters 过滤条件列表
     */
    public void setFilters(List<MysqlRecordFilter> filters) {
        this.filters = filters;
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

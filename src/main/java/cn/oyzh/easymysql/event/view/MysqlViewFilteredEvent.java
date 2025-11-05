package cn.oyzh.easymysql.event.view;

import cn.oyzh.easymysql.mysql.record.MysqlRecordFilter;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.event.Event;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class MysqlViewFilteredEvent extends Event<MysqlViewTreeItem> {

    private List<MysqlRecordFilter> filters;

    private MysqlDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }

    public List<MysqlRecordFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<MysqlRecordFilter> filters) {
        this.filters = filters;
    }

    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}

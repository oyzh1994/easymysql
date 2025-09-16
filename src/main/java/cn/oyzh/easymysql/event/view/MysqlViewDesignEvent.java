package cn.oyzh.easymysql.event.view;

import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlViewDesignEvent extends Event<MysqlView> {

    private MysqlDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().getName();
    }

    public String dbName() {
        return this.dbItem.dbName();
    }
    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

}

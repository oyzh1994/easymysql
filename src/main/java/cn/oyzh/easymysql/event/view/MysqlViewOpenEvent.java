package cn.oyzh.easymysql.event.view;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.view.MysqlViewTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class MysqlViewOpenEvent extends Event<MysqlViewTreeItem> {

    private MysqlDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }

    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}

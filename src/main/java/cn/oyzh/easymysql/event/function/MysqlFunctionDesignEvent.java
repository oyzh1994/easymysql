package cn.oyzh.easymysql.event.function;

import cn.oyzh.easymysql.db.function.MysqlFunction;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlFunctionDesignEvent extends Event<MysqlFunction> {

    private MysqlDatabaseTreeItem dbItem;

    public String functionName() {
        return this.data().getName();
    }

    public MysqlDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MysqlDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

}

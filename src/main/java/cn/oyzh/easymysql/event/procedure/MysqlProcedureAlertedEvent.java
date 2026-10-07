package cn.oyzh.easymysql.event.procedure;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * 存储过程已提示事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlProcedureAlertedEvent extends Event<String> {

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

package cn.oyzh.easymysql.event.function;

import cn.oyzh.easymysql.mysql.function.MysqlFunction;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * 函数设计事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlFunctionDesignEvent extends Event<MysqlFunction> {

    /**
     * 数据库树节点
     */
    private MysqlDatabaseTreeItem dbItem;

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String functionName() {
        return this.data().getName();
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

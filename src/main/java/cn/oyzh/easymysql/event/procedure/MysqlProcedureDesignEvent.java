package cn.oyzh.easymysql.event.procedure;

import cn.oyzh.easymysql.mysql.procedure.MysqlProcedure;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * 存储过程设计事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlProcedureDesignEvent extends Event<MysqlProcedure> {

    /**
     * 数据库树节点
     */
    private MysqlDatabaseTreeItem dbItem;

    /**
     * 获取存储过程名称
     *
     * @return 存储过程名称
     */
    public String procedureName() {
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

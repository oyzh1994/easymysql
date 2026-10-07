package cn.oyzh.easymysql.trees.terminal;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * 终端树节点
 *
 * @author oyzh
 * @since 2023/1/30
 */
public class MysqlTerminalTreeItem extends RichTreeItem<MysqlTerminalTreeItemValue> {

    /**
     * 构造终端树节点
     *
     * @param treeView 树视图
     */
    public MysqlTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new MysqlTerminalTreeItemValue());
    }

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public MysqlDatabaseTreeItem parent() {
        return (MysqlDatabaseTreeItem) super.parent();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public MysqlConnect shellConnect() {
        return this.parent().info();
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    public MysqlClient client() {
        return this.parent().client();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.terminalOpen(this.client(), this.parent().dbName());
    }

}

package cn.oyzh.easymysql.trees.terminal;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class MysqlTerminalTreeItem extends RichTreeItem<MysqlTerminalTreeItemValue> {

    public MysqlTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new MysqlTerminalTreeItemValue());
    }

    public MysqlDatabaseTreeItem parent() {
        return (MysqlDatabaseTreeItem) super.parent();
    }

    public MysqlConnect shellConnect() {
        return this.parent().info();
    }

    public MysqlClient client() {
        return this.parent().client();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.terminalOpen(this.client(), this.parent().dbName());
    }

}

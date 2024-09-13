package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.trees.DBTreeItemValue;
import cn.oyzh.easymysql.trees.DBTreeView;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeView;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/06/27
 */
public abstract class DBTreeItem<V extends DBTreeItemValue> extends RichTreeItem<V> {

    public DBTreeItem(RichTreeView treeView) {
        super(treeView);
    }

    @Override
    public DBTreeView getTreeView() {
        return (DBTreeView) super.getTreeView();
    }
}

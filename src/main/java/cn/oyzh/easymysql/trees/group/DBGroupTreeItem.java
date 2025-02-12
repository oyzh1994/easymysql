package cn.oyzh.easymysql.trees.group;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.controller.info.MysqlInfoAddController;
import cn.oyzh.easymysql.db.DBConnectManager;
import cn.oyzh.easymysql.domain.MysqlGroup;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.easymysql.store.DBGroupStore;
import cn.oyzh.easymysql.store.DBInfoStore;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.DBTreeView;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.easymysql.trees.root.DBRootTreeItem;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DB分组树节点
 *
 * @author oyzh
 * @since 2023/05/12
 */
public class DBGroupTreeItem extends DBTreeItem<DBGroupTreeItemValue> implements DBConnectManager {

    /**
     * 分组对象
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final MysqlGroup value;

    /**
     * DB信息储存
     */
    private final DBInfoStore infoStore = DBInfoStore.INSTANCE;

    /**
     * DB分组储存
     */
    private final DBGroupStore groupStore = DBGroupStore.INSTANCE;

    public DBGroupTreeItem(@NonNull MysqlGroup group, @NonNull DBTreeView treeView) {
        super(treeView);
        this.value = group;
        this.setValue(new DBGroupTreeItemValue(this));
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
        // 监听变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            DBEventUtil.treeChildChanged();
            this.flushLocal();
        });
        // 监听收缩变化
        super.addEventHandler(branchCollapsedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(false);
            this.groupStore.update(this.value);
        });
        // 监听展开变化
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(true);
            this.groupStore.update(this.value);
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect("12", this::addConnect);
        FXMenuItem renameGroup = MenuItemHelper.renameGroup("12", this::rename);
        FXMenuItem delGroup = MenuItemHelper.deleteGroup("12", this::delete);
        items.add(addConnect);
        items.add(renameGroup);
        items.add(delGroup);
        return items;
    }

    @Override
    public void rename() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (groupName == null || Objects.equals(groupName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StrUtil.isBlank(groupName)) {
            return;
        }
        // 检查是否存在
        String name = this.value.getName();
        this.value.setName(groupName);
        if (this.groupStore.exist(this.value)) {
            this.value.setName(name);
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        // 修改名称
        if (this.groupStore.update(this.value)) {
            this.getValue().flushText();
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip1())) {
            return;
        }
        if (!this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip2())) {
            return;
        }
        // 删除失败
        if (!this.groupStore.delete(this.value)) {
            MessageBox.warn(I18nHelper.operationFail());
            return;
        }
        // 处理连接
        if (!this.isChildEmpty()) {
            // 清除分组id
            List<DBConnectTreeItem> childes = this.getConnectItems();
            childes.forEach(c -> c.value().setGroupId(null));
            // 连接转移到父节点
            this.parent().addConnectItems(childes);
        }
        // 移除节点
        this.remove();
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageAdapter fxView = StageManager.parseStage(MysqlInfoAddController.class, this.window());
        fxView.setProp("group", this.value);
        fxView.display();
    }

    /**
     * 父节点
     *
     * @return 根节点
     */
    public DBRootTreeItem parent() {
        TreeItem<?> treeItem = this.getParent();
        return (DBRootTreeItem) treeItem;
    }

    @Override
    public void addConnect(@NonNull MysqlConnect DBInfo) {
        this.addConnectItem(new DBConnectTreeItem(DBInfo, this.getTreeView()));
    }

    @Override
    public void addConnectItem(@NonNull DBConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
                item.value().setGroupId(this.value.getGid());
                this.infoStore.update(item.value());
            }
            super.addChild(item);
        }
    }

    @Override
    public void addConnectItems(@NonNull List<DBConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.addChild((List) items);
        }
    }

    @Override
    public boolean delConnectItem(@NonNull DBConnectTreeItem item) {
        // 删除连接
        if (this.infoStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<DBConnectTreeItem> getConnectItems() {
        List<DBConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.getRealChildren()) {
            if (item instanceof DBConnectTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    @Override
    public boolean allowDrop() {
        return true;
    }

    @Override
    public boolean allowDropNode(DragNodeItem item) {
        if (item instanceof DBConnectTreeItem connectTreeItem) {
            return !Objects.equals(connectTreeItem.value().getGroupId(), this.value.getGid());
        }
        return false;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof DBConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }
}

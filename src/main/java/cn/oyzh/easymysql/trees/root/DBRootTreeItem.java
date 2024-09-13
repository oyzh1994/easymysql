package cn.oyzh.easymysql.trees.root;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.controller.info.DBInfoAddController;
import cn.oyzh.easymysql.db.DBConnectManager;
import cn.oyzh.easymysql.domain.DBGroup;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.dto.DBInfoExport;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.easymysql.store.DBGroupStore;
import cn.oyzh.easymysql.store.DBInfoStore;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.DBTreeView;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.easymysql.trees.group.DBGroupTreeItem;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.file.FileExtensionFilter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * DB树根节点
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class DBRootTreeItem extends DBTreeItem<DBRootTreeItemValue> implements DBConnectManager {

    /**
     * DB信息储存
     */
    private final DBInfoStore infoStore = DBInfoStore.INSTANCE;

    /**
     * DB分组储存
     */
    private final DBGroupStore groupStore = DBGroupStore.INSTANCE;

    public DBRootTreeItem(@NonNull DBTreeView treeView) {
        super(treeView);
        this.setValue(new DBRootTreeItemValue());
        // 初始化子节点
        this.initChildes();
        // 监听变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            DBEventUtil.treeChildChanged();
            this.flushLocal();
        });
    }

    /**
     * 初始化子节点
     */
    private void initChildes() {
        // 初始化分组
        List<DBGroup> groups = this.groupStore.load();
        if (CollUtil.isNotEmpty(groups)) {
            List<TreeItem<?>> list = new ArrayList<>();
            for (DBGroup group : groups) {
                list.add(new DBGroupTreeItem(group, this.getTreeView()));
            }
            this.addChild(list);
        }
        // 初始化连接
        List<DBInfo> infos = this.infoStore.load();
        if (CollUtil.isNotEmpty(infos)) {
            this.addConnects(infos);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect("12", this::addConnect);
        FXMenuItem exportConnect = MenuItemHelper.exportConnect("12", this::exportConnect);
        FXMenuItem importConnect = MenuItemHelper.importConnect("12", this::importConnect);
        FXMenuItem addGroup = MenuItemHelper.addGroup("12", this::addGroup);

        exportConnect.setDisable(this.isChildEmpty());

        items.add(addConnect);
        items.add(exportConnect);
        items.add(importConnect);
        items.add(addGroup);
        return items;
    }

    /**
     * 导出连接
     */
    private void exportConnect() {
        List<DBInfo> infos = this.infoStore.load();
        if (infos.isEmpty()) {
            MessageBox.warn(I18nHelper.connectionIsEmpty());
            return;
        }
        DBInfoExport export = DBInfoExport.fromConnects(infos);
        FileExtensionFilter extensionFilter = FileChooserHelper.jsonExtensionFilter();
        File file = FileChooserHelper.save(I18nHelper.saveConnection(), I18nResourceBundle.i18nString("base.database", "base.connect", "base._json"), extensionFilter);
        if (file != null) {
            try {
                FileUtil.writeUtf8String(export.toJSONString(), file);
                MessageBox.okToast(I18nHelper.operationSuccess());
            } catch (Exception ex) {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    /**
     * 拖拽文件
     *
     * @param files 文件
     */
    public void dragFile(List<File> files) {
        if (CollUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn(I18nHelper.onlySupportSingleFile());
            return;
        }
        File file = CollUtil.getFirst(files);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    private void importConnect() {
        FileExtensionFilter filter1 = FileChooserHelper.jsonExtensionFilter();
        File file = FileChooserHelper.choose(I18nHelper.chooseFile(), filter1);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 解析连接文件
     *
     * @param file 文件
     */
    private void parseConnect(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            MessageBox.warn(I18nHelper.fileNotExists());
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn(I18nHelper.notSupportFolder());
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "json")) {
            MessageBox.warn(I18nHelper.invalidFormat());
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
        try {
            String text = FileUtil.readUtf8String(file);
            DBInfoExport export = DBInfoExport.fromJSON(text);
            List<DBInfo> infos = export.getConnects();
            if (CollUtil.isNotEmpty(infos)) {
                for (DBInfo info : infos) {
                    if (this.infoStore.add(info)) {
                        this.addConnect(info);
                    } else {
                        MessageBox.warn(I18nHelper.connect() + "[" + info.getName() + "]" + I18nHelper.importFail());
                    }
                }
                MessageBox.okToast(I18nHelper.operationSuccess());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.operationException());
        }
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageManager.showStage(DBInfoAddController.class, this.window());
    }

    /**
     * 添加分组
     */
    public void addGroup() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1());

        // 名称为null，则忽略
        if (groupName == null) {
            return;
        }

        // 不能为空
        if (StrUtil.isBlank(groupName)) {
            MessageBox.warn(I18nHelper.nameCanNotEmpty());
            return;
        }

        DBGroup group = new DBGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        group = this.groupStore.add(groupName);
        if (group != null) {
            this.addChild(new DBGroupTreeItem(group, this.getTreeView()));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 获取分组树节点组件
     *
     * @param groupId 分组id
     */
    private DBGroupTreeItem getGroupItem(String groupId) {
        if (StrUtil.isNotBlank(groupId)) {
            List<DBGroupTreeItem> items = this.getGroupItems();
            Optional<DBGroupTreeItem> groupTreeItem = items.parallelStream().filter(g -> Objects.equals(g.value().getGid(), groupId)).findAny();
            return groupTreeItem.orElse(null);
        }
        return null;
    }

    /**
     * 获取分组树节点组件
     *
     * @return 分组树节点组件
     */
    private List<DBGroupTreeItem> getGroupItems() {
        List<DBGroupTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.getRealChildren()) {
            if (item instanceof DBGroupTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    /**
     * 连接新增事件
     *
     * @param info 连接
     */
    public void infoAdd(DBInfo info) {
        this.addConnect(info);
    }

    /**
     * 连接变更事件
     *
     * @param info 连接
     */
    public void infoUpdate(DBInfo info) {
        f1:
        for (TreeItem<?> item : this.getRealChildren()) {
            if (item instanceof DBConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == info) {
                    connectTreeItem.value(info);
                    break;
                }
            } else if (item instanceof DBGroupTreeItem groupTreeItem) {
                for (DBConnectTreeItem connectTreeItem : groupTreeItem.getConnectedItems()) {
                    if (connectTreeItem.value() == info) {
                        connectTreeItem.value(info);
                        break f1;
                    }
                }
            }
        }
    }

    @Override
    public void addConnect(@NonNull DBInfo info) {
        DBGroupTreeItem groupItem = this.getGroupItem(info.getGroupId());
        if (groupItem == null) {
            super.addChild(new DBConnectTreeItem(info, this.getTreeView()));
        } else {
            groupItem.addConnect(info);
        }
    }

    @Override
    public void addConnectItem(@NonNull DBConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (item.value().getGroupId() != null) {
                item.value().setGroupId(null);
                this.infoStore.update(item.value());
            }
            super.addChild(item);
            this.extend();
        }
    }

    @Override
    public void addConnectItems(@NonNull List<DBConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.addChild((List) items);
            this.extend();
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
        for (TreeItem<?> child : this.getRealChildren()) {
            if (child instanceof DBConnectTreeItem connectTreeItem) {
                items.add(connectTreeItem);
            } else if (child instanceof DBGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectItems());
            }
        }
        return items;
    }

    @Override
    public List<DBConnectTreeItem> getConnectedItems() {
        List<DBConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (Object item : this.getRealChildren()) {
            if (item instanceof DBConnectTreeItem connectTreeItem) {
                if (connectTreeItem.isConnected()) {
                    items.add(connectTreeItem);
                }
            } else if (item instanceof DBGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectedItems());
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
        return item instanceof DBConnectTreeItem;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof DBConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }
}

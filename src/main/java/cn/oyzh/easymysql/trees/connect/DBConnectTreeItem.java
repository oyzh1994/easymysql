package cn.oyzh.easymysql.trees.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.controller.info.MysqlInfoUpdateController;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBClientUtil;
import cn.oyzh.easymysql.db.DBConnectManager;
import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.easymysql.controller.database.MysqlDatabaseAddController;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.store.DBInfoStore;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.DBTreeView;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.CancelConnectMenuItem;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * db树连接节点
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DBConnectTreeItem extends DBTreeItem<DBConnectTreeItemValue> {

    /**
     * db信息
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private MysqlInfo value;

    /**
     * db客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private DBClient client;

    /**
     * 已取消操作标志位
     */
    private boolean canceled;

    /**
     * redis信息储存
     */
    private final DBInfoStore infoStore = DBInfoStore.INSTANCE;

    public DBConnectTreeItem(@NonNull MysqlInfo value, @NonNull DBTreeView treeView) {
        super(treeView);
        this.value(value);
        // 监听键变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            DBEventUtil.treeChildChanged();
            this.flushLocal();
        });
    }

    @Override
    public void reloadChild() {
        List<DBDatabase> databases = this.client.databases();
        List<TreeItem<?>> list = new ArrayList<>();
        for (DBDatabase database : databases) {
            list.add(new MysqlDatabaseTreeItem(database, this));
        }
        this.setChild(list);
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isConnecting()) {
            CancelConnectMenuItem cancelConnect = new CancelConnectMenuItem("12", this::cancelConnect);
            items.add(cancelConnect);
        } else if (this.isConnected()) {
            FXMenuItem closeConnect = MenuItemHelper.closeConnect("10", this::closeConnect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("11", this::editConnect);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect("12", this::repeatConnect);
            FXMenuItem addDatabase = MenuItemHelper.addDatabase("12", this::addDatabase);
            FXMenuItem reload = MenuItemHelper.reload("12", this::reloadChild);

            items.add(closeConnect);
            items.add(editConnect);
            items.add(repeatConnect);
            items.add(addDatabase);
            items.add(reload);
        } else {
            FXMenuItem connect = MenuItemHelper.startConnect("12", this::connect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem renameConnect = MenuItemHelper.renameConnect("12", this::rename);
            FXMenuItem deleteConnect = MenuItemHelper.deleteConnect("12", this::delete);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect("12", this::repeatConnect);
            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(repeatConnect);
            items.add(deleteConnect);
        }

        return items;
    }

    /**
     * 新增数据库
     */
    @FXML
    private void addDatabase() {
        StageAdapter fxView = StageManager.parseStage(MysqlDatabaseAddController.class, this.window());
        fxView.setProp("connectItem", this);
        fxView.display();
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        DBEventUtil.terminalOpen(this.value);
    }

    /**
     * 取消连接
     */
    public void cancelConnect() {
        this.canceled = true;
        ThreadUtil.startVirtual(() -> {
            this.client.close();
            this.stopWaiting();
        });
    }

    /**
     * 连接
     */
    public void connect() {
        if (!this.isConnected() && !this.isConnecting()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        this.client.start();
                        if (!this.isConnected()) {
                            if (!this.canceled) {
                                MessageBox.warn("[" + this.value.getName() + "] " + I18nHelper.connectFail());
                            }
                            this.canceled = false;
                            this.closeConnect(false);
                        } else {
                            this.reloadChild();
                            this.extend();
                        }
                        this.flushGraphic();
                    })
                    .onFinish(this::stopWaiting)
                    .onSuccess(this::flushLocal)
                    .onError(ex -> {
                        this.closeConnect();
                        MessageBox.exception(ex);
                    })
                    .build();
            // 执行连接
            this.startWaiting(task);
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (this.isConnected()) {
            this.closeConnect(true);
        }
    }

    /**
     * 关闭连接
     *
     * @param waiting 是否开启等待动画
     */
    public void closeConnect(boolean waiting) {
        Runnable func = () -> {
            this.client.close();
            this.clearChild();
            this.flushGraphic();
        };
        if (waiting) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(func)
                    .onFinish(this::stopWaiting)
                    .onSuccess(this::flushLocal)
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
        } else {
            func.run();
        }
    }

    @Override
    public void free() {
        if (!this.isConnected()) {
            this.connect();
        } else {
            super.free();
        }
    }

    /**
     * 编辑连接
     */
    private void editConnect() {
        if (this.isConnected()) {
            if (!MessageBox.confirm(I18nHelper.closeAndContinue())) {
                return;
            }
            this.closeConnect();
        }
        StageAdapter fxView = StageManager.parseStage(MysqlInfoUpdateController.class, this.window());
        fxView.setProp("info", this.value());
        fxView.display();
    }

    /**
     * 复制连接
     */
    private void repeatConnect() {
        MysqlInfo dbInfo = new MysqlInfo();
        dbInfo.copy(this.value);
        dbInfo.setName(this.value.getName() + "-" + I18nHelper.repeat());
        dbInfo.setCollects(Collections.emptyList());
        if (this.infoStore.add(dbInfo)) {
            this.parent().addConnect(dbInfo);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " [" + this.value().getName() + "]")) {
            this.closeConnect(false);
            if (this.parent().delConnectItem(this)) {
                DBEventUtil.infoDeleted(this.value);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    @Override
    public void rename() {
        String connectName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (connectName == null || Objects.equals(connectName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StrUtil.isBlank(connectName)) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
        this.value.setName(connectName);
        // 修改名称
        if (this.infoStore.update(this.value)) {
            this.setValue(new DBConnectTreeItemValue(this));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 设置值
     *
     * @param value redis信息
     */
    public void value(@NonNull MysqlInfo value) {
        this.value = value;
        this.client = DBClientUtil.newClient(value);
        this.setValue(new DBConnectTreeItemValue(this));
    }

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    /**
     * 是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.client != null && this.client.isConnecting();
    }

    /**
     * 获取当前父节点
     *
     * @return 父节点
     */
    public DBConnectManager parent() {
        Object object = this.getParent();
        if (object instanceof DBConnectManager connectManager) {
            return connectManager;
        }
        return null;
    }

    @Override
    public boolean allowDrag() {
        return true;
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.connect();
    }

    public boolean existDatabase(String dbName) {
        return this.client.existDatabase(dbName);
    }

    public boolean createDatabase(DBDatabase database) {
        return this.client.createDatabase(database);
    }

    public boolean alterDatabase(DBDatabase database) {
        return this.client.alterDatabase(database);
    }

    public String databaseCollation(String dbName) {
        return this.client.databaseCollation(dbName);
    }

    public boolean dropDatabase(String dbName) {
        return this.client.dropDatabase(dbName);
    }

    public String type() {
        return this.value.getType();
    }

}

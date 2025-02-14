package cn.oyzh.easymysql.trees.event;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * db树事件节点
 *
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventTreeItem extends DBTreeItem<MysqlEventTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final MysqlEvent value;

    public MysqlEventTreeItem(MysqlEvent event, RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.value = event;
        this.setValue(new MysqlEventTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) e -> this.flushLocal());
    }

    @Override
    public MysqlEventTypeTreeItem parent(){
        return (MysqlEventTypeTreeItem) super.parent();
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public DBClient client() {
        return this.parent().client();
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public MysqlConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem design = MenuItemHelper.designEvent("12", this::onPrimaryDoubleClick);
        FXMenuItem delete = MenuItemHelper.deleteEvent("12", this::delete);
        FXMenuItem info = MenuItemHelper.eventInfo("12", this::eventInfo);
        items.add(design);
        items.add(delete);
        items.add(info);
        return items;
    }

    private void eventInfo() {
    }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteEvent() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            this.dbItem().dropEvent(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public MysqlDatabaseTreeItem dbItem() {
        return this.parent().parent();
    }

    public String dbName() {
        return parent().dbName();
    }

    public String infoName() {
        return parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.designEvent(this.value, this.dbItem());
    }

    public String eventName() {
        return this.value.getName();
    }

//    @Override
//    public boolean supportFilter() {
//        return true;
//    }

}

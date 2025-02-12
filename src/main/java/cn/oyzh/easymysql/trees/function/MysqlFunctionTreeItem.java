package cn.oyzh.easymysql.trees.function;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.function.MysqlFunction;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
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
 * db树函数节点
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlFunctionTreeItem extends DBTreeItem<MysqlFunctionTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final MysqlFunction value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlFunctionTypeTreeItem parent;

    public MysqlFunctionTreeItem(MysqlFunction function, MysqlFunctionTypeTreeItem parent) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.value = function;
        this.setValue(new MysqlFunctionTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> this.flushLocal());
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public DBClient client() {
        return this.parent.client();
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public MysqlConnect info() {
        return this.parent.info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem design = MenuItemHelper.designFunction("12", this::onPrimaryDoubleClick);
        FXMenuItem delete = MenuItemHelper.deleteFunction("12", this::delete);
        FXMenuItem info = MenuItemHelper.functionInfo("12", this::functionInfo);
        items.add(design);
        items.add(delete);
        items.add(info);
        return items;
    }

    private void functionInfo() {
    }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteFunction() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            this.dbItem().dropFunction(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public MysqlDatabaseTreeItem dbItem() {
        return this.parent.dbItem();
    }

    public String dbName() {
        return parent.dbName();
    }

    public String infoName() {
        return parent.infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.designFunction(this.value, this.dbItem());
    }

    public String functionName() {
        return this.value.getName();
    }

//    @Override
//    public boolean supportFilter() {
//        return true;
//    }

    @Override
    public void reloadChild() {
        try {
            MysqlFunction function = this.client().selectFunction(this.dbName(), this.functionName());
            if (function != null) {
                this.value.copy(function);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

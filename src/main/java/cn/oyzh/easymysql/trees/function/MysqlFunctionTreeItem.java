package cn.oyzh.easymysql.trees.function;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.function.MysqlFunction;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;

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
    private final MysqlFunction value;

    /**
     * 构造函数树节点
     *
     * @param function 函数
     * @param treeView 树视图
     */
    public MysqlFunctionTreeItem(MysqlFunction function, RichTreeView treeView) {
        super(treeView);
        this.value = function;
        super.setFilterable(true);
        this.setValue(new MysqlFunctionTreeItemValue(this));
    }

    @Override
    public MysqlFunctionsTreeItem parent() {
        return (MysqlFunctionsTreeItem) super.parent();
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public MysqlClient client() {
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
        // FXMenuItem open = MenuItemHelper.openFunction( this::onPrimaryDoubleClick);
        // items.add(open);
        FXMenuItem design = MenuItemHelper.designFunction( this::onPrimaryDoubleClick);
        items.add(design);
        FXMenuItem delete = MenuItemHelper.deleteFunction( this::delete);
        items.add(delete);
        // FXMenuItem info = MenuItemHelper.functionInfo( this::functionInfo);
        // items.add(info);
        return items;
    }

    // private void functionInfo() {
    // }

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

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public MysqlDatabaseTreeItem dbItem() {
        return this.parent().parent();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.parent().dbName();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return this.parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.designFunction(this.value, this.dbItem());
    }

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String functionName() {
        return this.value.getName();
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    @Override
    public void loadChild() {
        try {
            this.setLoaded(true);
            this.setLoading(true);
            MysqlFunction function = this.client().selectFunction(this.dbName(), this.functionName());
            if (function != null) {
                this.value.copy(function);
            }
        } catch (Exception ex) {
            this.setLoaded(false);
            ex.printStackTrace();
            MessageBox.exception(ex);
        } finally {
            this.setLoading(false);
        }
    }

    @Override
    public void onPrimarySingleClick() {
        if (!this.isLoaded()) {
            super.onPrimarySingleClick();
        } else {
            super.onPrimarySingleClick();
        }
    }

    /**
     * 获取当前值
     *
     * @return 当前值
     */
    public MysqlFunction value() {
        return value;
    }
}

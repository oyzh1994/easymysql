package cn.oyzh.easymysql.trees.procedure;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.procedure.MysqlProcedure;
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
 * 过程树节点
 *
 * @author oyzh
 * @since 2024/12/27
 */
public class MysqlProcedureTreeItem extends DBTreeItem<MysqlProcedureTreeItemValue> {

    /**
     * 当前值
     */
    private final MysqlProcedure value;

    /**
     * 获取当前值
     *
     * @return 当前值
     */
    public MysqlProcedure value() {
        return value;
    }

    /**
     * 构造过程树节点
     *
     * @param procedure 过程
     * @param treeView  树视图
     */
    public MysqlProcedureTreeItem(MysqlProcedure procedure, RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.value = procedure;
        this.setValue(new MysqlProcedureTreeItemValue(this));
    }

    @Override
    public MysqlProceduresTreeItem parent(){
        return (MysqlProceduresTreeItem) super.parent();
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
        // FXMenuItem open = MenuItemHelper.openProcedure( this::onPrimaryDoubleClick);
        // items.add(open);
        FXMenuItem design = MenuItemHelper.designProcedure( this::onPrimaryDoubleClick);
        items.add(design);
        FXMenuItem delete = MenuItemHelper.deleteProcedure( this::delete);
        items.add(delete);
        // FXMenuItem info = MenuItemHelper.procedureInfo( this::procedureInfo);
        // items.add(info);
        return items;
    }

    // private void procedureInfo() {
    // }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteProcedure() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            this.dbItem().dropProcedure(this.value);
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
        MysqlEventUtil.designProcedure(this.value, this.dbItem());
    }

    /**
     * 获取过程名称
     *
     * @return 过程名称
     */
    public String procedureName() {
        return this.value.getName();
    }
}

package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.routine.DBProcedure;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.fx.plus.controls.svg.ProcedureSVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * db树视图节点
 *
 * @author oyzh
 * @since 2024/12/27
 */
public class MysqlProcedureTreeItem extends DBTreeItem<MysqlProcedureTreeItem.MysqlProcedureTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final DBProcedure value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlProcedureTypeTreeItem parent;

    public MysqlProcedureTreeItem(DBProcedure procedure, MysqlProcedureTypeTreeItem parent) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.value = procedure;
        this.setValue(new MysqlProcedureTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.flushLocal();
        });
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
    public DBInfo info() {
        return this.parent.info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem design = MenuItemHelper.designProcedure("12", this::onPrimaryDoubleClick);
        FXMenuItem delete = MenuItemHelper.deleteProcedure("12", this::delete);
        FXMenuItem info = MenuItemHelper.procedureInfo("12", this::procedureInfo);
        items.add(design);
        items.add(delete);
        items.add(info);
        return items;
    }

    private void procedureInfo() {
    }

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
        MysqlEventUtil.designProcedure(this.value, this.dbItem());
    }

    public String procedureName() {
        return this.value.getName();
    }

    @Override
    public boolean supportFilter() {
        return true;
    }

    /**
     * db树表节点值
     *
     * @author oyzh
     * @since 2023/12/22
     */
    @Accessors(chain = true, fluent = true)
    public static class MysqlProcedureTreeItemValue extends DBTreeItemValue {

        /**
         * db树表节点
         */
        private final MysqlProcedureTreeItem item;

        public MysqlProcedureTreeItemValue(MysqlProcedureTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.flushGraphicColor();
            this.flushText();
        }

        @Override
        public void flushGraphic() {
            ProcedureSVGGlyph glyph = (ProcedureSVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new ProcedureSVGGlyph("12");
                this.graphic(glyph);
            }
        }

        @Override
        public String name() {
            return this.item.procedureName();
        }
    }
}

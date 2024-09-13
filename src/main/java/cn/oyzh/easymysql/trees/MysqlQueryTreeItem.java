package cn.oyzh.easymysql.trees;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.domain.DBQuery;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.store.DBQueryStore;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
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
import java.util.Objects;

/**
 * db树查询节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MysqlQueryTreeItem extends DBTreeItem<MysqlQueryTreeItem.MysqlQueryTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final DBQuery value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlQueryTypeTreeItem parent;

    public MysqlQueryTreeItem(DBQuery query, MysqlQueryTypeTreeItem parent) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.value = query;
        this.setValue(new MysqlQueryTreeItemValue(this));
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
        FXMenuItem openQuery = MenuItemHelper.openQuery("12", this::onPrimaryDoubleClick);
        FXMenuItem renameQuery = MenuItemHelper.renameQuery("12", this::rename);
        FXMenuItem deleteQuery = MenuItemHelper.deleteTable("12", this::delete);
        items.add(openQuery);
        items.add(renameQuery);
        items.add(deleteQuery);
        return items;
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " " + this.queryName() + "?")) {
            if (DBQueryStore.INSTANCE.delete(this.value)) {
                this.remove();
                MysqlEventUtil.queryDeleted(this);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    @Override
    public void rename() {
        String name = MessageBox.prompt(I18nHelper.pleaseInputName(), this.queryName());
        // 名称为null或者跟当前名称相同，则忽略
        if (name == null || Objects.equals(name, this.queryName())) {
            return;
        }
        // 检查名称
        if (StrUtil.isBlank(name)) {
            MessageBox.warn(I18nHelper.pleaseInputName());
            return;
        }
        String oldName = this.value.getName();
        this.value.setName(name);
        // 修改名称
        if (DBQueryStore.INSTANCE.update(this.value)) {
            this.getValue().flushText();
        } else {
            this.value.setName(oldName);
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    public MysqlDatabaseTreeItem dbItem() {
        return this.parent.dbItem();
    }

    public String dbName() {
        return parent.dbName();
    }

    public String queryName() {
        return this.value.getName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.queryOpen(this.value, this.dbItem());
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
    public static class MysqlQueryTreeItemValue extends DBTreeItemValue {

        /**
         * db树表节点
         */
        private final MysqlQueryTreeItem item;

        public MysqlQueryTreeItemValue(MysqlQueryTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.flushGraphicColor();
            this.flushText();
        }

        @Override
        public void flushGraphic() {
            SVGGlyph glyph = (SVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new SVGGlyph("/font/query.svg", "12");
                this.graphic(glyph);
            }
        }

        @Override
        public String name() {
            return this.item.queryName();
        }
    }
}

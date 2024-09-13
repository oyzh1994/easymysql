package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.event.DBEvent;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.event.MysqlEventUtil;
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

/**
 * db树事件节点
 *
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEventTreeItem extends DBTreeItem<MysqlEventTreeItem.MysqlEventTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final DBEvent value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlEventTypeTreeItem parent;

    public MysqlEventTreeItem(DBEvent event, MysqlEventTypeTreeItem parent) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.value = event;
        this.setValue(new MysqlEventTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) e -> this.flushLocal());
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
        MysqlEventUtil.designEvent(this.value, this.dbItem());
    }

    public String eventName() {
        return this.value.getName();
    }

    @Override
    public boolean supportFilter() {
        return true;
    }

    /**
     * @author oyzh
     * @since 2024/09/09
     */
    @Accessors(chain = true, fluent = true)
    public static class MysqlEventTreeItemValue extends DBTreeItemValue {

        /**
         * db树表节点
         */
        private final MysqlEventTreeItem item;

        public MysqlEventTreeItemValue(MysqlEventTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.flushGraphicColor();
            this.flushText();
        }

        @Override
        public void flushGraphic() {
            SVGGlyph glyph = (SVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new SVGGlyph("/font/event.svg", "12");
                this.graphic(glyph);
            }
        }

        @Override
        public String name() {
            return this.item.eventName();
        }
    }
}

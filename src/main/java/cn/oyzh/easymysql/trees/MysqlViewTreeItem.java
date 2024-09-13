package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.record.MysqlRecordData;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.db.record.MysqlRecordPrimaryKey;
import cn.oyzh.easymysql.db.table.MysqlColumn;
import cn.oyzh.easymysql.db.table.MysqlColumns;
import cn.oyzh.easymysql.db.view.DBView;
import cn.oyzh.easymysql.domain.DBInfo;
import cn.oyzh.easymysql.controller.view.MysqlViewInfoController;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controls.svg.ViewSVGGlyph;
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
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * db树视图节点
 *
 * @author oyzh
 * @since 2024/12/27
 */
public class MysqlViewTreeItem extends DBTreeItem<MysqlViewTreeItem.MysqlViewTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final DBView value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlViewTypeTreeItem parent;

    public MysqlViewTreeItem(DBView view, MysqlViewTypeTreeItem parent) {
        super(parent.getTreeView());
        super.setFilterable(true);
        this.parent = parent;
        this.value = view;
        this.setValue(new MysqlViewTreeItemValue(this));
        // 监听展开
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.flushLocal();
        });
    }

    public DBClient client() {
        return this.parent.client();
    }

    public String dbName() {
        return this.parent.dbName();
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public DBInfo info() {
        return this.parent.info();
    }

    public MysqlColumns viewColumns() {
        this.value.setColumns(new MysqlColumns(this.columns()));
        return this.value.getColumns();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem open = MenuItemHelper.openView("12", this::onPrimaryDoubleClick);
        FXMenuItem info = MenuItemHelper.viewInfo("12", this::viewInfo);
        FXMenuItem design = MenuItemHelper.designView("12", this::designView);
        FXMenuItem delete = MenuItemHelper.deleteView("12", this::delete);
        items.add(open);
        items.add(design);
        items.add(delete);
        items.add(info);
        return items;
    }

    private void viewInfo() {
        StageAdapter fxView = StageManager.parseStage(MysqlViewInfoController.class, this.window());
        fxView.setProp("item", this);
        fxView.display();
    }

    private void designView() {
        MysqlEventUtil.designView(this.value, this.dbItem());
    }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteView() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            this.dbItem().dropView(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public MysqlDatabaseTreeItem dbItem() {
        return this.parent.dbItem();
    }

    public Paging<MysqlRecord> recordPage(long pageNo, long limit, List<MysqlRecordFilter> filters) {
        List<MysqlRecord> records = this.client().viewRecords(this.dbName(), this.viewName(), pageNo * limit, limit, filters);
        long count = this.client().tableCount(dbName(), this.viewName(), filters);
        Paging<MysqlRecord> paging = new Paging<>(records, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    public String infoName() {
        return parent.infoName();
    }

    public List<MysqlColumn> columns() {
        return this.client().viewColumns(this.dbName(), this.viewName());
    }

    @Override
    public void onPrimaryDoubleClick() {
        MysqlEventUtil.viewOpen(this);
    }

    /**
     * 获取主键列，优先返回自动递增列
     *
     * @return 主键列
     */
    public MysqlColumn getPrimaryKey() {
        if (this.value.getColumns() == null) {
            this.viewColumns();
        }
        MysqlColumn dbColumn = null;
        if (this.value.columns() != null) {
            for (MysqlColumn column : this.value.columns()) {
                if (column.isAutoIncrement()) {
                    dbColumn = column;
                    break;
                }
            }
        }
        return dbColumn;
    }

    @Override
    public boolean supportFilter() {
        return true;
    }

    public boolean isUpdatable() {
        return this.value.isUpdatable();
    }

    public String viewName() {
        return this.value.getName();
    }

    public int insertRecord(MysqlRecordData recordData) {
        return this.insertRecord(recordData, null);
    }

    public int insertRecord(MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey) {
        return this.client().insertRecord(this.dbName(), this.viewName(), recordData, primaryKey);
    }

    public int deleteRecord(MysqlRecordData recordData) {
        return this.client().deleteRecord(this.dbName(), this.viewName(), recordData);
    }

    public int deleteRecord(MysqlRecordPrimaryKey primaryKey) {
        return this.client().deleteRecord(this.dbName(), this.viewName(), primaryKey);
    }

    public MysqlRecord selectRecord(MysqlRecordPrimaryKey primaryKey) {
        return this.client().selectRecord(this.dbName(), this.viewName(), primaryKey);
    }

    public int updateRecord(MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey) {
        return this.client().updateRecord(this.dbName(), this.viewName(), recordData, primaryKey);
    }

    public int updateRecord(MysqlRecordData recordData, MysqlRecordData originalRecordData) {
        return this.client().updateRecord(this.dbName(), this.viewName(), recordData, originalRecordData);
    }

    /**
     * db树表节点值
     *
     * @author oyzh
     * @since 2023/12/22
     */
    @Accessors(chain = true, fluent = true)
    public static class MysqlViewTreeItemValue extends DBTreeItemValue {

        /**
         * db树表节点
         */
        private final MysqlViewTreeItem item;

        public MysqlViewTreeItemValue(MysqlViewTreeItem item) {
            this.item = item;
            this.flushGraphic();
            this.flushGraphicColor();
            this.flushText();
        }

        @Override
        public void flushGraphic() {
            ViewSVGGlyph glyph = (ViewSVGGlyph) this.graphic();
            if (glyph == null) {
                glyph = new ViewSVGGlyph("12");
                this.graphic(glyph);
            }
        }

        @Override
        public String name() {
            return this.item.viewName();
        }
    }
}

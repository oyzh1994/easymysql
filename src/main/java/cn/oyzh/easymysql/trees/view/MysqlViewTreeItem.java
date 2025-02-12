package cn.oyzh.easymysql.trees.view;

import cn.oyzh.easymysql.controller.view.MysqlViewInfoController;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.record.MysqlDeleteRecordParam;
import cn.oyzh.easymysql.db.record.MysqlInsertRecordParam;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.record.MysqlRecordData;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.db.record.MysqlRecordPrimaryKey;
import cn.oyzh.easymysql.db.record.MysqlSelectRecordParam;
import cn.oyzh.easymysql.db.record.MysqlUpdateRecordParam;
import cn.oyzh.easymysql.db.view.MysqlView;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.DBTreeItem;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.common.dto.Paging;
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
public class MysqlViewTreeItem extends DBTreeItem<MysqlViewTreeItemValue> {

    /**
     * 当前值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final MysqlView value;

    /**
     * 连接树节点
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected MysqlViewTypeTreeItem parent;

    public MysqlViewTreeItem(MysqlView view, MysqlViewTypeTreeItem parent) {
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
    public MysqlConnect info() {
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

    public Paging<MysqlRecord> recordPage(long pageNo, long limit, List<MysqlRecordFilter> filters, List<MysqlColumn> columns) {
        MysqlSelectRecordParam param = new MysqlSelectRecordParam();
        param.limit(limit);
        param.filters(filters);
        param.columns(columns);
        param.start(pageNo * limit);
        param.dbName(this.dbName());
        param.tableName(this.viewName());
        List<MysqlRecord> records = this.client().viewRecords(this.dbName(), this.viewName(), pageNo * limit, limit, filters);
        long count = this.client().selectRecordCount(param);
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
        MysqlInsertRecordParam param = new MysqlInsertRecordParam();
        param.record(recordData);
        param.dbName(this.dbName());
        param.primaryKey(primaryKey);
        param.tableName(this.viewName());
        return this.client().insertRecord(param);
    }

    public int deleteRecord(MysqlRecordData recordData) {
        MysqlDeleteRecordParam param = new MysqlDeleteRecordParam();
        param.record(recordData);
        param.dbName(this.dbName());
        param.tableName(this.viewName());
        return this.client().deleteRecord(param);
    }

    public int deleteRecord(MysqlRecordPrimaryKey primaryKey) {
        MysqlDeleteRecordParam param = new MysqlDeleteRecordParam();
        param.dbName(this.dbName());
        param.tableName(this.viewName());
        param.primaryKey(primaryKey);
        return this.client().deleteRecord(param);
    }

    public MysqlRecord selectRecord(MysqlRecordPrimaryKey primaryKey) {
        MysqlSelectRecordParam param = new MysqlSelectRecordParam();
        param.dbName(this.dbName());
        param.tableName(this.viewName());
        param.primaryKey(primaryKey);
        return this.client().selectRecord(param);
    }

    public int updateRecord(MysqlRecordData recordData, MysqlRecordPrimaryKey primaryKey) {
        MysqlUpdateRecordParam param = new MysqlUpdateRecordParam();
        param.dbName(this.dbName());
        param.tableName(this.viewName());
        param.primaryKey(primaryKey);
        param.updateRecord(recordData);
        return this.client().updateRecord(param);
    }

    public int updateRecord(MysqlRecordData recordData, MysqlRecordData originalRecordData) {
        MysqlUpdateRecordParam param = new MysqlUpdateRecordParam();
        param.dbName(this.dbName());
        param.tableName(this.viewName());
        param.updateRecord(recordData);
        param.record(originalRecordData);
        return this.client().updateRecord(param);
    }
}

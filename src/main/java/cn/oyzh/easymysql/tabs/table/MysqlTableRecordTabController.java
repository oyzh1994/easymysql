package cn.oyzh.easymysql.tabs.table;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.common.dto.Paging;
import cn.oyzh.easymysql.db.DBObjectList;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.record.MysqlRecordData;
import cn.oyzh.easymysql.db.record.MysqlRecordFilter;
import cn.oyzh.easymysql.db.record.MysqlRecordPrimaryKey;
import cn.oyzh.easymysql.domain.MysqlSetting;
import cn.oyzh.easymysql.event.record.RecordDeleteEvent;
import cn.oyzh.easymysql.fx.DBStatusColumn;
import cn.oyzh.easymysql.fx.record.DBRecordColumn;
import cn.oyzh.easymysql.fx.record.DBRecordTableView;
import cn.oyzh.easymysql.listener.DBStatusListener;
import cn.oyzh.easymysql.listener.DBStatusListenerManager;
import cn.oyzh.easymysql.popups.MysqlPageSettingPopupController;
import cn.oyzh.easymysql.popups.MysqlTableRecordFilterPopupController;
import cn.oyzh.easymysql.store.MysqlSettingStore;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.easymysql.util.DBRecordUtil;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.page.PageBox;
import cn.oyzh.fx.gui.page.PageEvent;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.box.FXVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableColumn;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.node.NodeUtil;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * db表tab内容组件
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class MysqlTableRecordTabController extends DynamicTabController {

    /**
     * 根节点
     */
    @FXML
    private FXVBox root;

    /**
     * db树表节点
     */
    private MysqlTableTreeItem item;

    /**
     * 分页数据
     */
    private Paging<MysqlRecord> pageData;

    /**
     * 记录过滤按钮
     */
    @FXML
    private SVGGlyph filter;

    /**
     * 缺少主键警告
     */
    @FXML
    private SVGGlyph missPrimaryKey;

    /**
     * 数据分页组件
     */
    @FXML
    private PageBox<MysqlRecord> pageBox;

    /**
     * 数据表单组件
     */
    @FXML
    private DBRecordTableView recordTable;

    /**
     * 过滤列表
     */
    @Setter
    private List<MysqlRecordFilter> filters;

    /**
     * 应用
     */
    @FXML
    private SVGGlyph apply;

    /**
     * 抛弃
     */
    @FXML
    private SVGGlyph discard;

    /**
     * 记录变更监听器
     */
    private DBStatusListener changeListener;

    /**
     * 字段列表
     */
    private MysqlColumns columns;

    /**
     * 设置
     */
    private final MysqlSetting setting = MysqlSettingStore.SETTING;

    /**
     * 执行初始化
     *
     * @param item db树表节点
     */
    public void init(MysqlTableTreeItem item) {
        this.item = item;
        this.reload();
        if (this.changeListener == null) {
            this.changeListener = new DBStatusListener(this.item.dbName() + ":" + this.item.tableName()) {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    apply.enable();
                }
            };
        }
    }

    /**
     * 初始化数据列表
     *
     * @param pageNo 数据页码
     */
    private void initDataList(long pageNo) {
        try {
            this.pageData = this.item.recordPage(pageNo, this.setting.getRecordPageLimit(), this.enabledFilters(), this.columns);
            this.pageBox.setPaging(this.pageData);
            this.initRecords(this.pageData.dataList());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 获取已启用的表过滤条件
     *
     * @return 已启用的表过滤条件
     */
    private List<MysqlRecordFilter> enabledFilters() {
        if (CollUtil.isNotEmpty(this.filters)) {
            return this.filters.stream().filter(MysqlRecordFilter::isEnabled).toList();
        }
        return null;
    }

    /**
     * 初始化列
     *
     * @param columns 列数据
     */
    private void initColumns(MysqlColumns columns) {
        // 设置字段列表
        this.columns = columns;
        // 数据列集合
        List<FXTableColumn<MysqlRecord, Object>> columnList = new ArrayList<>();
        DBStatusColumn<MysqlRecord> statusColumn = new DBStatusColumn<>();
        columnList.add(statusColumn);
        for (MysqlColumn column : columns) {
            DBRecordColumn tableColumn = new DBRecordColumn(column);
            tableColumn.setRealWidth(DBRecordUtil.suitableColumnWidth(column.getType()));
            columnList.add(tableColumn);
        }
        this.recordTable.getColumns().setAll(columnList);
    }

    /**
     * 初始化记录
     *
     * @param records 数据
     */
    private void initRecords(List<MysqlRecord> records) {
        this.recordTable.setItem(records);
    }

    /**
     * 添加记录
     */
    @FXML
    private void addRecord() {
        MysqlRecord record = new MysqlRecord();
        record.setCreated(true);
        for (MysqlColumn column : this.columns) {
            Object val = null;
            if (column.supportDefaultValue()) {
                val = column.getDefaultValue();
            }
            record.putValue(column, val);
        }
        this.recordTable.addItem(record);
        this.recordTable.selectLast();
    }

    /**
     * 插入记录
     *
     * @param record 记录
     */
    private void insertRecord(MysqlRecord record) {
        MysqlRecordData recordData = record.getRecordData();
        MysqlRecordPrimaryKey primaryKey = this.initPrimaryKey(record);
        if (primaryKey != null) {
            this.item.insertRecord(recordData, primaryKey);
            // 处理回显
            record.copy(this.item.selectRecord(primaryKey));
        } else {
            this.item.insertRecord(recordData);
        }
    }

    /**
     * 更改记录
     *
     * @param record 记录
     */
    private void updateRecord(MysqlRecord record) {
        // 获取主键
        MysqlRecordPrimaryKey primaryKey = this.initPrimaryKey(record);
        // 主键存在，则根据主键更新
        if (primaryKey != null) {
            // 记录数据
            MysqlRecordData recordData = record.getChangedRecordData();
            // 如果主键未变更，则移除主键数据
            if (!record.isColumnChanged(primaryKey.getColumnName())) {
                recordData.remove(primaryKey.getColumnName());
            }
            // 更新行
            this.item.updateRecord(recordData, primaryKey);
            // 处理回显
            record.copy(this.item.selectRecord(primaryKey));
        } else {// 主键不存在，则根据所有字段更新
            // 变更数据
            MysqlRecordData changedRecordData = record.getChangedRecordData();
            // 原始数据
            MysqlRecordData originalRecordData = record.getOriginalRecordData();
            // 更新行
            this.item.updateRecord(changedRecordData, originalRecordData);
        }
    }

    /**
     * 初始化主键
     *
     * @param record 记录
     * @return 主键
     */
    private MysqlRecordPrimaryKey initPrimaryKey(MysqlRecord record) {
        MysqlColumn primaryKeyColumn = this.item.getPrimaryKey();
        if (primaryKeyColumn != null) {
            MysqlRecordPrimaryKey primaryKey = new MysqlRecordPrimaryKey();
            primaryKey.init(primaryKeyColumn, record);
            return primaryKey;
        }
        return null;
    }



    /**
     * 应用变更
     */
    @FXML
    private void apply() {
        if (this.apply.isEnable()) {
            try {
                List<MysqlRecord> records = this.recordTable.getItems();
                for (MysqlRecord record : records) {
                    if (DBObjectList.isCreated(record)) {
                        this.insertRecord(record);
                        record.clearStatus();
                    } else if (DBObjectList.isChanged(record)) {
                        this.updateRecord(record);
                        record.clearStatus();
                    }
                }
                this.apply.disable();
            } catch (Exception ex) {
                MessageBox.exception(ex);
            }
        }
    }

    /**
     * 丢弃变更
     */
    @FXML
    private void discard() {
        try {
            MysqlRecord discardRecord = null;
            for (MysqlRecord record : this.recordTable.getItems()) {
                if (record.isCreated()) {
                    discardRecord = record;
                } else if (record.isChanged()) {
                    record.discard();
                }
            }
            this.recordTable.removeItem(discardRecord);
            this.apply.disable();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 刷新记录
     */
    @FXML
    public void reload() {
        try {
            // 检查是否有未保存的数据
            if (this.apply.isEnable() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
                return;
            }
            // 初始化字段
            this.initColumns(this.item.columns());
            // 初始化数据
            this.initDataList(0);
            // 判断是否缺少主键列
            this.missPrimaryKey.setVisible(!this.columns.hasPrimaryKey());
            // 设置过滤激活
            this.filter.setActive(CollUtil.isNotEmpty(this.enabledFilters()));
            // 禁用组件
            this.apply.disable();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 过滤记录
     */
    @FXML
    private void filter() {
        try {
            PopupAdapter popup = PopupManager.parsePopup(MysqlTableRecordFilterPopupController.class);
            popup.setProp("item", this.item);
            popup.setProp("filters", this.filters);
            popup.showPopup(this.filter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 下一页
     */
    @FXML
    private void nextPage() {
        this.initDataList(this.pageData.nextPage());
    }

    /**
     * 上一页
     */
    @FXML
    private void prevPage() {
        this.initDataList(this.pageData.prevPage());
    }

    /**
     * 尾页
     */
    @FXML
    private void lastPage() {
        this.initDataList(this.pageData.lastPage());
    }

    /**
     * 首页
     */
    @FXML
    private void firstPage() {
        this.initDataList(0);
    }

    /**
     * 跳页
     */
    @FXML
    private void pageJump(PageEvent.PageJumpEvent event) {
        this.initDataList(event.getPage());
    }

    /**
     * 页码设置
     */
    @FXML
    private void pageSetting() {
        PopupAdapter popup = PopupManager.parsePopup(MysqlPageSettingPopupController.class);
        popup.showPopup(this.pageBox.getSettingBtn());
        int limit = this.setting.getRecordPageLimit();
        popup.setSubmitHandler(o -> {
            if (o instanceof Integer l && l != limit) {
                this.firstPage();
            }
        });
    }

    /**
     * 删除记录
     */
    @EventSubscribe
    private void deleteRecord(RecordDeleteEvent event) {
        this.deleteRecord();
    }

    /**
     * 删除记录
     */
    @FXML
    private void deleteRecord() {
        try {
            MysqlRecord record = this.recordTable.getSelectedItem();
            if (record == null) {
                return;
            }
            if (!MessageBox.confirm(I18nHelper.deleteRecord() + "?")) {
                return;
            }
            // 如果是新增的数据，直接删除
            boolean success;
            if (record.isCreated()) {
                success = true;
            } else {
                // 获取主键
                MysqlRecordPrimaryKey primaryKey = this.initPrimaryKey(record);
                // 主键存在，则根据主键删除
                if (primaryKey != null) {
                    success = this.item.deleteRecord(primaryKey) == 1;
                } else {// 主键不存在，则根据所有字段更新
                    // 所有字段数据
                    MysqlRecordData recordData = record.getOriginalRecordData();
                    // 删除行
                    success = this.item.deleteRecord(recordData) == 1;
                }
            }
            // 操作成功
            if (success) {
                this.recordTable.removeItem(record);
            } else {// 操作失败
                MessageBox.warnToast(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        super.onTabClose(tab, event);
        DBStatusListenerManager.removeListener(this.changeListener);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            super.initialize(url, resourceBundle);
            this.missPrimaryKey.managedBindVisible();
            this.missPrimaryKey.disableTheme();
            this.discard.disableProperty().bind(this.apply.disableProperty());
            this.apply.disabledProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    NodeGroupUtil.enable(this.root, "action2");
                } else {
                    NodeGroupUtil.disable(this.root, "action2");
                }
            });
            this.recordTable.getItems().addListener((ListChangeListener<MysqlRecord>) c -> {
                if (c.next() && c.wasAdded()) {
                    List<? extends MysqlRecord> rows = c.getAddedSubList();
                    for (MysqlRecord row : rows) {
                        if (DBObjectList.isCreated(row)) {
                            this.apply.enable();
                            break;
                        }
                    }
                }
            });
            this.recordTable.setCtrlSAction(this::apply);
            NodeUtil.nodeOnCtrlS(this.root, this::apply);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

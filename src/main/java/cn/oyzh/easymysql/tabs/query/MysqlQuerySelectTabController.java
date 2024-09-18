package cn.oyzh.easymysql.tabs.query;

import cn.oyzh.easymysql.db.DBObjectList;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.query.MysqlExecuteResult;
import cn.oyzh.easymysql.db.record.MysqlDeleteRecordParam;
import cn.oyzh.easymysql.db.record.MysqlInsertRecordParam;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.record.MysqlRecordData;
import cn.oyzh.easymysql.db.record.MysqlRecordPrimaryKey;
import cn.oyzh.easymysql.db.record.MysqlSelectRecordParam;
import cn.oyzh.easymysql.db.record.MysqlUpdateRecordParam;
import cn.oyzh.easymysql.fx.DBStatusColumn;
import cn.oyzh.easymysql.fx.record.DBRecordColumn;
import cn.oyzh.easymysql.fx.record.DBRecordTableView;
import cn.oyzh.easymysql.listener.DBStatusListener;
import cn.oyzh.easymysql.listener.DBStatusListenerManager;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.util.DBRecordUtil;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.util.NodeUtil;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author oyzh
 * @since 2024/08/12
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class MysqlQuerySelectTabController extends DynamicTabController {

    /**
     * 根节点
     */
    @FXML
    private FlexVBox root;

    /**
     * sql组件
     */
    @FXML
    private FlexText sql;

    /**
     * 耗时组件
     */
    @FXML
    private FlexText used;

    /**
     * 计数组件
     */
    @FXML
    private FlexText count;

    /**
     * 数据表单组件
     */
    @FXML
    private DBRecordTableView recordTable;

    /**
     * 数据库树节点
     */
    private MysqlDatabaseTreeItem dbItem;

    /**
     * 执行结果
     */
    private MysqlExecuteResult result;

    /**
     * 新增
     */
    @FXML
    private SVGGlyph add;

    /**
     * 删除
     */
    @FXML
    private SVGGlyph delete;

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
    private List<MysqlColumn> columns;

    /**
     * 执行初始化
     *
     * @param result 执行结果
     * @param dbItem db树表节点
     */
    public void init(MysqlExecuteResult result, MysqlDatabaseTreeItem dbItem) {
        this.result = result;
        this.dbItem = dbItem;
        this.initDataList();
        if (result.isUpdatable()) {
            if (this.changeListener == null) {
                this.changeListener = new DBStatusListener(this.result.dbName() + ":" + this.result.tableName()) {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        apply.enable();
                    }
                };
            }
            // 部分按钮显示处理
            if (result.fullColumn()) {
                this.add.display();
            }
            this.apply.display();
            this.delete.display();
            this.discard.display();
        }
    }

    /**
     * 初始化数据列表
     */
    private void initDataList() {
        try {
            // 初始化字段
            this.initColumns(this.result.columnList());
            // 初始化数据
            this.initRecords(this.result.records());
            // 初始化sql信息
            this.sql.setText(this.result.sql());
            this.used.setText(I18nHelper.time() + ": " + this.result.getUsedMs() + "ms");
            this.count.setText(I18nHelper.totalData() + ": " + this.result.getCount());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 初始化列
     *
     * @param columns 列数据
     */
    private void initColumns(List<MysqlColumn> columns) {
        // 设置字段列表
        this.columns = columns;
        // 数据列集合
        List<FlexTableColumn<MysqlRecord, Object>> columnList = new ArrayList<>();
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
        MysqlInsertRecordParam param = new MysqlInsertRecordParam();
        param.record(recordData);
        param.primaryKey(primaryKey);
        param.dbName(this.result.dbName());
        param.tableName(this.result.tableName());
        this.dbItem.client().insertRecord(param);
        if (primaryKey != null) {
            MysqlSelectRecordParam selectRecordParam = new MysqlSelectRecordParam();
            selectRecordParam.primaryKey(primaryKey);
            selectRecordParam.dbName(this.result.dbName());
            selectRecordParam.tableName(this.result.tableName());
            // 处理回显
            record.copy(this.dbItem.client().selectRecord(selectRecordParam));
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
        MysqlUpdateRecordParam param = new MysqlUpdateRecordParam();
        param.dbName(this.result.dbName());
        param.tableName(this.result.tableName());
        // 主键存在，则根据主键更新
        if (primaryKey != null) {
            // 记录数据
            MysqlRecordData recordData = record.getChangedRecordData();
            // 如果主键未变更，则移除主键数据
            if (!record.isColumnChanged(primaryKey.getColumnName())) {
                recordData.remove(primaryKey.getColumnName());
            }
            param.primaryKey(primaryKey);
            // 更新行
            this.dbItem.client().updateRecord(param);
            MysqlSelectRecordParam selectRecordParam = new MysqlSelectRecordParam();
            selectRecordParam.primaryKey(primaryKey);
            selectRecordParam.dbName(this.result.dbName());
            selectRecordParam.tableName(this.result.tableName());
            // 处理回显
            record.copy(this.dbItem.selectRecord(selectRecordParam));
        } else {// 主键不存在，则根据所有字段更新
            // 变更数据
            MysqlRecordData changedRecordData = record.getChangedRecordData();
            // 原始数据
            MysqlRecordData originalRecordData = record.getOriginalRecordData();
            param.record(originalRecordData);
            param.updateRecord(changedRecordData);
            // 更新行
            this.dbItem.client().updateRecord(param);
        }
    }

    /**
     * 初始化主键
     *
     * @param record 记录
     * @return 主键
     */
    private MysqlRecordPrimaryKey initPrimaryKey(MysqlRecord record) {
        MysqlColumn primaryKeyColumn = this.result.getPrimaryKey();
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
            // 执行查询
            this.result = this.dbItem.executeSingleSql(this.result.sql());
            // 初始化数据
            this.initDataList();
            // 禁用组件
            this.apply.disable();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
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
                MysqlDeleteRecordParam param = new MysqlDeleteRecordParam();
                param.dbName(this.result.dbName());
                param.tableName(this.result.tableName());
                param.primaryKey(primaryKey);
                param.record(record.getOriginalRecordData());
                success = this.dbItem.deleteRecord(param) == 1;
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
            this.add.managedBindVisible();
            this.delete.managedBindVisible();
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

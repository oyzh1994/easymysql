package cn.oyzh.easymysql.tabs.table;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.db.event.MysqlEvents;
import cn.oyzh.easymysql.db.table.MysqlCheck;
import cn.oyzh.easymysql.db.table.MysqlChecks;
import cn.oyzh.easymysql.db.table.MysqlForeignKey;
import cn.oyzh.easymysql.db.table.MysqlForeignKeys;
import cn.oyzh.easymysql.db.table.MysqlIndex;
import cn.oyzh.easymysql.db.table.MysqlIndexes;
import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.db.table.MysqlTrigger;
import cn.oyzh.easymysql.db.table.MysqlTriggers;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.fx.DBCharsetComboBox;
import cn.oyzh.easymysql.fx.DBCollationComboBox;
import cn.oyzh.easymysql.fx.DBStatusColumn;
import cn.oyzh.easymysql.fx.DBStatusTableView;
import cn.oyzh.easymysql.fx.table.DBEngineComboBox;
import cn.oyzh.easymysql.fx.table.DBRowFormatComboBox;
import cn.oyzh.easymysql.listener.DBListener;
import cn.oyzh.easymysql.listener.DBListenerManager;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.util.NodeUtil;
import cn.oyzh.fx.plus.util.TableViewUtil;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * db表设计业务
 *
 * @author oyzh
 * @since 2024/08/07
 */
public class MysqlTableDesignTabController extends DynamicTabController {

    /**
     * 新增按钮
     */
    @FXML
    private SVGGlyph add;

    /**
     * 删除按钮
     */
    @FXML
    private SVGGlyph delete;

    /**
     * 上移按钮
     */
    @FXML
    private SVGGlyph moveUp;

    /**
     * 下移按钮
     */
    @FXML
    private SVGGlyph moveDown;

    /**
     * 切换面板
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * 引擎
     */
    @FXML
    private DBEngineComboBox tableEngine;

    /**
     * 字符集
     */
    @FXML
    private DBCharsetComboBox tableCharset;

    /**
     * 排序方式
     */
    @FXML
    private DBCollationComboBox tableCollation;

    /**
     * 行格式组件
     */
    @FXML
    private FlexHBox tableRowFormatBox;

    /**
     * 行格式
     */
    @FXML
    private DBRowFormatComboBox tableRowFormat;

    /**
     * 自动递增组件
     */
    @FXML
    private FlexHBox tableAutoIncrementBox;

    /**
     * 自动递增
     */
    @FXML
    private NumberTextField tableAutoIncrement;

    /**
     * 注释
     */
    @FXML
    private FlexTextArea tableComment;

    /**
     * 表字段组件
     */
    @FXML
    private DBStatusTableView<MysqlColumn> columnTable;

    /**
     * 字段状态列
     */
    @FXML
    private DBStatusColumn<MysqlColumn> colStatus;

    /**
     * 字段名称列
     */
    @FXML
    private FlexTableColumn<MysqlColumn, String> colName;

    /**
     * 字段类型列
     */
    @FXML
    private FlexTableColumn<MysqlColumn, String> colType;

    /**
     * 字段长度
     */
    @FXML
    private FlexTableColumn<MysqlColumn, Integer> colSize;

    /**
     * 字段小数点列
     */
    @FXML
    private FlexTableColumn<MysqlColumn, Integer> colDigits;

    /**
     * 字段是否主键列
     */
    @FXML
    private FlexTableColumn<MysqlColumn, Boolean> colPrimaryKey;

    /**
     * 字段可为null列
     */
    @FXML
    private FlexTableColumn<MysqlColumn, Boolean> colNullable;

    /**
     * 字段注释列
     */
    @FXML
    private FlexTableColumn<MysqlColumn, String> colComment;

    /**
     * 字段配置
     */
    @FXML
    private FlexTableColumn<MysqlColumn, String> colConfig;

    /**
     * 表索引组件
     */
    @FXML
    private DBStatusTableView<MysqlIndex> indexTable;

    /**
     * 索引状态列
     */
    @FXML
    private DBStatusColumn<MysqlIndex> indexStatus;

    /**
     * 索引名称列
     */
    @FXML
    private TableColumn<MysqlIndex, String> indexName;

    /**
     * 索引字段列
     */
    @FXML
    private TableColumn<MysqlIndex, String> indexColumn;

    /**
     * 索引类型列
     */
    @FXML
    private TableColumn<MysqlIndex, String> indexType;

    /**
     * 索引方法列
     */
    @FXML
    private TableColumn<MysqlIndex, String> indexMethod;

    /**
     * 索引注释列
     */
    @FXML
    private FlexTableColumn<MysqlIndex, String> indexComment;

    /**
     * 表外键组件
     */
    @FXML
    private DBStatusTableView<MysqlForeignKey> foreignKeyTable;

    /**
     * 外键状态列
     */
    @FXML
    private DBStatusColumn<MysqlForeignKey> foreignKeyStatus;

    /**
     * 外键名称列
     */
    @FXML
    private TableColumn<MysqlForeignKey, String> foreignKeyName;

    /**
     * 外键字段列
     */
    @FXML
    private TableColumn<MysqlForeignKey, String> foreignKeyColumn;

    /**
     * 外键引用库
     */
    @FXML
    private TableColumn<MysqlForeignKey, String> foreignKeyPKDatabase;

    /**
     * 外键引用表
     */
    @FXML
    private TableColumn<MysqlForeignKey, String> foreignKeyPKTable;

    /**
     * 外键引用字段
     */
    @FXML
    private FlexTableColumn<MysqlForeignKey, String> foreignKeyPKColumn;

    /**
     * 外键删除策略
     */
    @FXML
    private FlexTableColumn<MysqlForeignKey, String> foreignKeyDeletePolicy;

    /**
     * 外键更新策略
     */
    @FXML
    private FlexTableColumn<MysqlForeignKey, String> foreignKeyUpdatePolicy;

    /**
     * db表
     */
    private MysqlTable mysqlTable;

    /**
     * 触发器组件
     */
    @FXML
    private DBStatusTableView<MysqlTrigger> triggerTable;

    /**
     * 触发器状态
     */
    @FXML
    private DBStatusColumn<MysqlTrigger> triggerStatus;

    /**
     * 触发器名称
     */
    @FXML
    private FlexTableColumn<MysqlTrigger, String> triggerName;

    /**
     * 触发器策略
     */
    @FXML
    private FlexTableColumn<MysqlTrigger, String> triggerPolicy;

    /**
     * 触发器定义
     */
    @FXML
    private FlexTableColumn<MysqlTrigger, String> triggerDefinition;

    /**
     * 检查器组件
     */
    @FXML
    private DBStatusTableView<MysqlCheck> checkTable;

    /**
     * 检查器状态
     */
    @FXML
    private DBStatusColumn<MysqlTrigger> checkStatus;

    /**
     * 检查器名称
     */
    @FXML
    private FlexTableColumn<MysqlTrigger, String> checkName;

    /**
     * 检查器子语句
     */
    @FXML
    private FlexTableColumn<MysqlTrigger, String> checkClause;

    /**
     * db库节点
     */
    @Getter
    @Accessors(fluent = true)
    private MysqlDatabaseTreeItem dbItem;

    /**
     * db库节点
     */
    @Getter
    @Accessors(fluent = true)
    private MysqlTableTreeItem tableItem;

    /**
     * 数据监听器
     */
    private DBListener listener;

    /**
     * 未保存标志位
     */
    @Getter
    private boolean unsaved;

    /**
     * 新数据标志位
     */
    private boolean newData;

    /**
     * 初始化中标志位
     */
    private boolean initiating;

    /**
     * 保存db表
     */
    @FXML
    private void save() {
        try {
            MysqlTable tempTable = new MysqlTable();

            String tableName;
            // 表名称
            if (this.newData) {
                tableName = MessageBox.prompt(I18nHelper.pleaseInputTableName());
                if (tableName == null) {
                    return;
                }
                tempTable.setName(tableName);
            } else {
                tableName = this.mysqlTable.getName();
                tempTable.setName(tableName);
            }

            // 数据库
            tempTable.setDbName(this.mysqlTable.getDbName());

            // 注释
            String comment = this.tableComment.getText();
            if (!StrUtil.equals(comment, this.mysqlTable.getComment())) {
                tempTable.setComment(comment);
            }

            // 引擎
            String engine = this.tableEngine.getSelectedItem();
            if (!StrUtil.equalsIgnoreCase(engine, this.mysqlTable.getEngine())) {
                tempTable.setEngine(engine);
            }

            // 字符集
            String charset = this.tableCharset.getSelectedItem();
            if (!StrUtil.equalsIgnoreCase(charset, this.mysqlTable.getCharset())) {
                tempTable.setCharset(charset);
            }

            // 排序
            String collation = this.tableCollation.getSelectedItem();
            if (!StrUtil.equalsIgnoreCase(collation, this.mysqlTable.getCollation())) {
                tempTable.setCollation(collation);
            }

            // 行格式
            if (this.tableRowFormatBox.isVisible()) {
                String rowFormat = this.tableRowFormat.getValue();
                if (!StrUtil.equalsIgnoreCase(rowFormat, this.mysqlTable.getRowFormat())) {
                    tempTable.setRowFormat(rowFormat);
                }
            }

            // 自动递增
            if (this.tableAutoIncrementBox.isVisible()) {
                Long autoIncrement = this.tableAutoIncrement.getValue();
                if (!Objects.equals(autoIncrement, this.mysqlTable.getAutoIncrement())) {
                    tempTable.setAutoIncrement(autoIncrement);
                }
            }

            // 索引处理
            MysqlIndexes indexes = new MysqlIndexes(this.indexTable.getItems());
            // if (indexes != null) {
            for (MysqlIndex index : indexes) {
                if (StrUtil.isBlank(index.getName())) {
                    MessageBox.warn(I18nHelper.invalidData());
                    this.tabPane.select(2);
                    return;
                }
            }
            //     tempTable.setIndexes(indexes);
            // }

            // 字段处理
            MysqlColumns columns = new MysqlColumns(this.columnTable.getItems());
            // MysqlColumns columns = this.mysqlTable.getColumns();
            // if (columns != null) {
            for (MysqlColumn column : columns) {
                if (StrUtil.isBlank(column.getName())) {
                    MessageBox.warn(I18nHelper.invalidData());
                    this.tabPane.select(1);
                    return;
                }
            }
            // tempTable.setColumns(columns);
            // }

            // 外键处理
            // MysqlForeignKeys foreignKeys = this.mysqlTable.getForeignKeys();
            MysqlForeignKeys foreignKeys = new MysqlForeignKeys(this.foreignKeyTable.getItems());
            // if (foreignKeys != null) {
            for (MysqlForeignKey foreignKey : foreignKeys) {
                if (StrUtil.isBlank(foreignKey.getName())) {
                    MessageBox.warn(I18nHelper.invalidData());
                    this.tabPane.select(3);
                    return;
                }
            }
            // tempTable.setForeignKeys(foreignKeys);
            // }

            // 触发器处理
            // MysqlTriggers triggers = this.mysqlTable.getTriggers();
            MysqlTriggers triggers = new MysqlTriggers(this.triggerTable.getItems());
            // if (triggers != null) {
                for (MysqlTrigger trigger : triggers) {
                    if (StrUtil.isBlank(trigger.getName())) {
                        MessageBox.warn(I18nHelper.invalidData());
                        this.tabPane.select(4);
                        return;
                    }
                }
                // tempTable.setTriggers(triggers);
            // }

            // 检查处理
            MysqlChecks checks =new MysqlChecks(this.checkTable.getItems());
            // MysqlChecks checks = this.mysqlTable.getChecks();
            // if (checks != null) {
                for (MysqlCheck check : checks) {
                    if (StrUtil.isBlank(check.getName()) || StrUtil.isBlank(check.getClause())) {
                        MessageBox.warn(I18nHelper.invalidData());
                        this.tabPane.select(5);
                        return;
                    }
                }
                // tempTable.setChecks(checks);
            // }
            // 检查处理

            this.disableTab();

            // 创建表
            if (this.newData) {
                this.dbItem.createTable(tempTable);
                MysqlEventUtil.tableAdded(this.dbItem);
                this.initDBListener();
            } else {// 修改表
                this.dbItem.alterTable(tempTable,columns,indexes,foreignKeys,triggers,checks);
                MysqlEventUtil.tableAlerted(tableName, this.dbItem);
            }
            // 判断结果
            this.unsaved = false;
            // 重载表数据
            this.mysqlTable = this.dbItem.selectTable(tableName);
            // 初始化信息
            this.initInfo();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        } finally {
            this.enableTab();
            this.flushTab();
        }
    }

    /**
     * 数据列表监听器
     */
    private final ListChangeListener<DBObjectStatus> listChangeListener = c -> {
        if (c.next()) {
            if (c.wasRemoved() || c.wasAdded() || c.wasReplaced()) {
                this.initChangedFlag();
            }
            if (c.wasReplaced() || c.wasAdded()) {
                List<DBObjectStatus> list = null;
                if (c.wasReplaced()) {
                    list = (List<DBObjectStatus>) c.getList();
                } else if (c.wasAdded()) {
                    list = (List<DBObjectStatus>) c.getAddedSubList();
                }
                if (list != null) {
                    for (DBObjectStatus status : list) {
                        DBListenerManager.bindListener(status.statusProperty(), this.listener);
                    }
                }
            }
        }
    };

    /**
     * 初始化数据监听器
     */
    private void initDBListener() {
        // 初始化监听器
        this.listener = new DBListener(this.mysqlTable.getDbName() + ":" + this.mysqlTable.getName()) {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                initChangedFlag();
            }
        };
    }

    /**
     * 初始化变更标志
     */
    private void initChangedFlag() {
        if (!this.initiating) {
            this.unsaved = true;
            this.flushTab();
        }
    }

    /**
     * 初始化信息
     */
    protected void initInfo() {
        // 更新初始化标志位
        this.initiating = true;

        // 更新新表标志位
        this.newData = this.mysqlTable.isNew();

        // 新数据
        if (newData) {
            this.tableEngine.select("innoDB");
        } else {// 已有数据
            this.moveUp.disappear();
        }

        // 基本信息
        this.tableEngine.select(this.mysqlTable.getEngine());
        this.tableComment.setText(this.mysqlTable.getComment());
        // 字符集
        if (this.tableCharset.isItemEmpty()) {
            this.tableCharset.init(this.dbItem.client());
        }
        this.tableCharset.select(this.mysqlTable.getCharset());
        // 排序规则
        this.tableCollation.init(this.mysqlTable.getCharset(), this.dbItem.client());
        this.tableCollation.select(this.mysqlTable.getCollation());

        // 检查器
        if (this.dbItem.isSupportCheckFeature()) {
            this.checkTable.setItem(this.tableItem.checks());
        }
        // 索引
        this.indexTable.setItem(this.tableItem.indexes());
        // 字段
        this.columnTable.setItem(this.tableItem.columns());
        // 触发器
        this.triggerTable.setItem(this.tableItem.triggers());
        // 外键
        this.foreignKeyTable.setItem(this.tableItem.foreignKeys());

        // 行格式
        if (this.mysqlTable.isInnoDB()) {
            this.tableRowFormatBox.display();
            this.tableRowFormat.select(this.mysqlTable.getRowFormat());
        }

        // 表自动递增
        if (this.mysqlTable.hasAutoIncrement()) {
            this.tableAutoIncrementBox.display();
            this.tableAutoIncrement.setValue(this.mysqlTable.getAutoIncrement());
        }

        // 标记为结束
        FXUtil.runPulse(() -> this.initiating = false);
    }

    /**
     * 新增字段
     */
    private void addColumn() {
        MysqlColumn column = new MysqlColumn();
        column.setCreated(true);
        column.setNullable(true);
        this.columnTable.addItem(column);
        this.columnTable.selectLast();
        // this.mysqlTable.columns().add(column);
    }

    /**
     * 删除字段
     */
    private void deleteColumn() {
        try {
            MysqlColumn column = this.columnTable.getSelectedItem();
            if (column != null) {
                // 非新增的数据进行提示
                if (!column.isCreated() && !MessageBox.confirm(I18nHelper.deleteField() + " " + column.getName())) {
                    return;
                }
                // 从表格移除数据
                this.columnTable.removeItem(column);
                // 从table移除数据
                if (column.isCreated()) {
                    // this.mysqlTable.removeColumn(column);
                } else {// 标记为删除
                    column.setDeleted(true);
                }
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 上移字段
     */
    private void moveColumnUp() {
        try {
            TableViewUtil.moveUp(this.columnTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 下移字段
     */
    private void moveColumnDown() {
        try {
            TableViewUtil.moveDown(this.columnTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 新增索引
     */
    private void addIndex() {
        MysqlIndex index = new MysqlIndex();
        index.setCreated(true);
        this.indexTable.addItem(index);
        this.indexTable.selectLast();
        // this.mysqlTable.indexes().add(index);
    }

    /**
     * 删除索引
     */
    private void deleteIndex() {
        try {
            MysqlIndex index = this.indexTable.getSelectedItem();
            if (index != null) {
                // 非新增的数据进行提示
                if (!index.isCreated() && !MessageBox.confirm(I18nHelper.deleteIndex() + " " + index.getName())) {
                    return;
                }
                this.indexTable.getItems().remove(index);
                // 从table移除数据
                if (index.isCreated()) {
                    // this.mysqlTable.removeIndex(index);
                } else {// 标记为删除
                    index.setDeleted(true);
                }
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 上移索引
     */
    private void moveIndexUp() {
        try {
            TableViewUtil.moveUp(this.indexTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 下移索引
     */
    private void moveIndexDown() {
        try {
            TableViewUtil.moveDown(this.indexTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 新增外键
     */
    private void addForeignKey() {
        MysqlForeignKey foreignKey = new MysqlForeignKey();
        foreignKey.setCreated(true);
        this.foreignKeyTable.addItem(foreignKey);
        this.foreignKeyTable.selectLast();
        // this.mysqlTable.foreignKeys().add(foreignKey);
    }

    /**
     * 删除外键
     */
    private void deleteForeignKey() {
        try {
            MysqlForeignKey foreignKey = this.foreignKeyTable.getSelectedItem();
            if (foreignKey != null) {
                // 非新增的数据进行提示
                if (!foreignKey.isCreated() && !MessageBox.confirm(I18nHelper.deleteForeignKey() + " " + foreignKey.getName())) {
                    return;
                }
                this.foreignKeyTable.getItems().remove(foreignKey);
                // 从table移除数据
                if (foreignKey.isCreated()) {
                    // this.mysqlTable.removeForeignKey(foreignKey);
                } else {// 标记为删除
                    foreignKey.setDeleted(true);
                }
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 上移外键
     */
    private void moveForeignKeyUp() {
        try {
            TableViewUtil.moveUp(this.foreignKeyTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 下移外键
     */
    private void moveForeignKeyDown() {
        try {
            TableViewUtil.moveDown(this.foreignKeyTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 新增触发器
     */
    private void addTrigger() {
        MysqlTrigger trigger = new MysqlTrigger();
        trigger.setCreated(true);
        this.triggerTable.addItem(trigger);
        this.triggerTable.selectLast();
        // this.mysqlTable.triggers().add(trigger);
    }

    /**
     * 删除触发器
     */
    private void deleteTrigger() {
        try {
            MysqlTrigger trigger = this.triggerTable.getSelectedItem();
            if (trigger != null) {
                // 非新增的数据进行提示
                if (!trigger.isCreated() && !MessageBox.confirm(I18nHelper.deleteTrigger() + " " + trigger.getName())) {
                    return;
                }
                this.triggerTable.removeItem(trigger);
                // 从table移除数据
                if (trigger.isCreated()) {
                    // this.mysqlTable.removeTrigger(trigger);
                } else {// 标记为删除
                    trigger.setDeleted(true);
                }
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 上移触发器
     */
    private void moveTriggerUp() {
        try {
            TableViewUtil.moveUp(this.triggerTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 下移触发器
     */
    private void moveTriggerDown() {
        try {
            TableViewUtil.moveDown(this.triggerTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 新增检查
     */
    private void addCheck() {
        MysqlCheck check = new MysqlCheck();
        check.setCreated(true);
        this.checkTable.addItem(check);
        this.checkTable.selectLast();
        // this.mysqlTable.checks().add(check);
    }

    /**
     * 删除检查
     */
    private void deleteCheck() {
        try {
            MysqlCheck check = this.checkTable.getSelectedItem();
            if (check != null) {
                // 非新增的数据进行提示
                if (!check.isCreated() && !MessageBox.confirm(I18nHelper.deleteCheck() + " " + check.getName())) {
                    return;
                }
                this.checkTable.removeItem(check);
                // 从table移除数据
                if (check.isCreated()) {
                    // this.mysqlTable.removeCheck(check);
                } else {// 标记为删除
                    check.setDeleted(true);
                }
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 上移检查
     */
    private void moveCheckUp() {
        try {
            TableViewUtil.moveUp(this.checkTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 下移检查
     */
    private void moveCheckDown() {
        try {
            TableViewUtil.moveDown(this.checkTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 初始化列表控件
     */
    private void initTable() {
        // 字段
        this.colName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        this.colType.setCellValueFactory(new PropertyValueFactory<>("typeControl"));
        this.colSize.setCellValueFactory(new PropertyValueFactory<>("sizeControl"));
        this.colDigits.setCellValueFactory(new PropertyValueFactory<>("digitsControl"));
        this.colComment.setCellValueFactory(new PropertyValueFactory<>("commentControl"));
        this.colNullable.setCellValueFactory(new PropertyValueFactory<>("nullableControl"));
        this.colPrimaryKey.setCellValueFactory(new PropertyValueFactory<>("primaryKeyControl"));
        this.colConfig.setCellValueFactory(new PropertyValueFactory<>("configControl"));

        // 索引
        this.indexName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        this.indexColumn.setCellValueFactory(new PropertyValueFactory<>("columnControl"));
        this.indexType.setCellValueFactory(new PropertyValueFactory<>("typeControl"));
        this.indexMethod.setCellValueFactory(new PropertyValueFactory<>("methodControl"));
        this.indexComment.setCellValueFactory(new PropertyValueFactory<>("commentControl"));

        // 外键
        this.foreignKeyName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        this.foreignKeyColumn.setCellValueFactory(new PropertyValueFactory<>("columnControl"));
        this.foreignKeyPKTable.setCellValueFactory(new PropertyValueFactory<>("primaryKeyTableControl"));
        this.foreignKeyDeletePolicy.setCellValueFactory(new PropertyValueFactory<>("deletePolicyControl"));
        this.foreignKeyUpdatePolicy.setCellValueFactory(new PropertyValueFactory<>("updatePolicyControl"));
        this.foreignKeyPKColumn.setCellValueFactory(new PropertyValueFactory<>("primaryKeyColumnControl"));
        this.foreignKeyPKDatabase.setCellValueFactory(new PropertyValueFactory<>("primaryKeyDatabaseControl"));

        // 触发器
        this.triggerName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        this.triggerPolicy.setCellValueFactory(new PropertyValueFactory<>("policyControl"));
        this.triggerDefinition.setCellValueFactory(new PropertyValueFactory<>("definitionControl"));

        // 触发器
        this.checkName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        this.checkClause.setCellValueFactory(new PropertyValueFactory<>("clauseControl"));

        // 表单保存事件
        this.indexTable.setCtrlSAction(this::save);
        this.checkTable.setCtrlSAction(this::save);
        this.columnTable.setCtrlSAction(this::save);
        this.triggerTable.setCtrlSAction(this::save);
        this.foreignKeyTable.setCtrlSAction(this::save);

        // 监听事件
        NodeUtil.nodeOnCtrlS(this.getTab(), this::save);
        NodeUtil.nodeOnCtrlS(this.tableComment, this::save);
        NodeUtil.nodeOnCtrlS(this.tableAutoIncrement, this::save);

        // 更新字段列表
        this.columnTable.itemsProperty().get().addListener((ListChangeListener<MysqlColumn>) c -> CacheHelper.set("columnList", this.columnTable.getItems()));

        // 监听列表变化
        this.checkTable.itemList().addListener(this.listChangeListener);
        this.indexTable.itemList().addListener(this.listChangeListener);
        this.columnTable.itemList().addListener(this.listChangeListener);
        this.triggerTable.itemList().addListener(this.listChangeListener);
        this.foreignKeyTable.itemList().addListener(this.listChangeListener);
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        super.onTabClose(tab, event);
        CacheHelper.clear();
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // 字符集选中事件
        this.tableCharset.selectedItemChanged((observable, oldValue, newValue) -> {
            this.tableCollation.init(newValue, this.dbItem.client());
            this.tableCollation.select(0);
        });
        // 引擎选中事件
        this.tableEngine.selectedItemChanged((observable, oldValue, newValue) -> {
            if (this.tableEngine.isInnoDB()) {
                this.tableRowFormatBox.display();
                if (StrUtil.isBlank(this.mysqlTable.getRowFormat())) {
                    this.tableRowFormat.select(this.mysqlTable.getRowFormat());
                } else {
                    this.tableRowFormat.select(3);
                }
            } else {
                this.tableRowFormatBox.disappear();
            }
        });
        // 表格下标监听
        this.tabPane.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                this.add.disappear();
                if (this.newData) {
                    this.moveUp.disappear();
                }
            } else {
                this.add.display();
                if (this.newData) {
                    this.moveUp.display();
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        // 初始化表单
        this.initTable();
        // 组件管理
        this.delete.visibleProperty().bind(this.add.visibleProperty());
        this.delete.managedProperty().bind(this.add.managedProperty());
        this.moveDown.visibleProperty().bind(this.moveUp.visibleProperty());
        this.moveDown.managedProperty().bind(this.moveUp.managedProperty());
        this.tableRowFormat.managedBindVisible();
        this.tableAutoIncrementBox.managedBindVisible();
    }

    /**
     * 执行初始化
     *
     * @param table  表信息
     * @param dbItem db库树节点
     */
    public void init(MysqlTable table, MysqlDatabaseTreeItem dbItem) {
        // 获取对象
        this.dbItem = dbItem;
        this.mysqlTable = table;
        // 初始化监听器
        this.initDBListener();
        // 初始化引擎
        this.tableEngine.init(this.dbItem.client());

        // 设置缓存
        CacheHelper.set("dbName", this.dbItem.dbName());
        CacheHelper.set("dbClient", this.dbItem.client());

        // 初始化信息
        this.initInfo();

        // 监听组件
        DBListenerManager.bindListener(this.tableEngine, this.listener);
        DBListenerManager.bindListener(this.tableCharset, this.listener);
        DBListenerManager.bindListener(this.tableComment, this.listener);
        DBListenerManager.bindListener(this.tableRowFormat, this.listener);
        DBListenerManager.bindListener(this.tableCollation, this.listener);
        DBListenerManager.bindListener(this.tableAutoIncrement, this.listener);

        // 移除tab
        if (!this.dbItem.isSupportCheckFeature()) {
            this.tabPane.removeTab(5);
        }
    }

    /**
     * 执行添加
     */
    @FXML
    private void doAdd() {
        if (this.tabPane.getSelectedIndex() == 1) {
            this.addColumn();
        } else if (this.tabPane.getSelectedIndex() == 2) {
            this.addIndex();
        } else if (this.tabPane.getSelectedIndex() == 3) {
            this.addForeignKey();
        } else if (this.tabPane.getSelectedIndex() == 4) {
            this.addTrigger();
        } else if (this.tabPane.getSelectedIndex() == 5) {
            this.addCheck();
        }
    }

    /**
     * 执行删除
     */
    @FXML
    private void doDelete() {
        if (this.tabPane.getSelectedIndex() == 1) {
            this.deleteColumn();
        } else if (this.tabPane.getSelectedIndex() == 2) {
            this.deleteIndex();
        } else if (this.tabPane.getSelectedIndex() == 3) {
            this.deleteForeignKey();
        } else if (this.tabPane.getSelectedIndex() == 4) {
            this.deleteTrigger();
        } else if (this.tabPane.getSelectedIndex() == 5) {
            this.deleteCheck();
        }
    }

    /**
     * 执行上移
     */
    @FXML
    private void doMoveUp() {
        if (this.tabPane.getSelectedIndex() == 1) {
            this.moveColumnUp();
        } else if (this.tabPane.getSelectedIndex() == 2) {
            this.moveIndexUp();
        } else if (this.tabPane.getSelectedIndex() == 3) {
            this.moveForeignKeyUp();
        } else if (this.tabPane.getSelectedIndex() == 4) {
            this.moveTriggerUp();
        } else if (this.tabPane.getSelectedIndex() == 5) {
            this.moveCheckUp();
        }
    }

    /**
     * 执行下移
     */
    @FXML
    private void doMoveDown() {
        if (this.tabPane.getSelectedIndex() == 1) {
            this.moveColumnDown();
        } else if (this.tabPane.getSelectedIndex() == 2) {
            this.moveIndexDown();
        } else if (this.tabPane.getSelectedIndex() == 3) {
            this.moveForeignKeyDown();
        } else if (this.tabPane.getSelectedIndex() == 4) {
            this.moveTriggerDown();
        } else if (this.tabPane.getSelectedIndex() == 5) {
            this.moveCheckDown();
        }
    }

    public String tableName() {
        return this.mysqlTable.getName();
    }

    public String dbName() {
        return this.mysqlTable.getDbName();
    }
}

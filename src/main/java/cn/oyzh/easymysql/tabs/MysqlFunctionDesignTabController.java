package cn.oyzh.easymysql.tabs;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.db.routine.DBFunction;
import cn.oyzh.easymysql.db.routine.DBRoutineParam;
import cn.oyzh.easymysql.fx.DBCharsetComboBox;
import cn.oyzh.easymysql.fx.DBSecurityTypeComboBox;
import cn.oyzh.easymysql.fx.DBSqlTextArea;
import cn.oyzh.easymysql.fx.routine.DBCharacteristicCombobox;
import cn.oyzh.easymysql.fx.table.DBEnumTextFiled;
import cn.oyzh.easymysql.fx.table.DBFiledTypeComboBox;
import cn.oyzh.easymysql.generator.routine.DBFunctionSqlGenerator;
import cn.oyzh.easymysql.listener.DBListener;
import cn.oyzh.easymysql.listener.DBListenerManager;
import cn.oyzh.easymysql.module.mysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.textfield.FlexTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.util.NodeUtil;
import cn.oyzh.fx.plus.util.TableViewUtil;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * db函数内容组件
 *
 * @author oyzh
 * @since 2024/07/08
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class MysqlFunctionDesignTabController extends DynamicTabController {

    /**
     * 函数
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private DBFunction function;

    /**
     * db数据库树节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private MysqlDatabaseTreeItem dbItem;

    /**
     * 定义
     */
    @FXML
    private DBSqlTextArea definition;

    /**
     * 预览
     */
    @FXML
    private DBSqlTextArea preview;

    /**
     * 切换面板
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * 注释
     */
    @FXML
    private FlexTextArea comment;

    /**
     * 定义者
     */
    @FXML
    private FlexTextField definer;

    /**
     * 安全性
     */
    @FXML
    private DBSecurityTypeComboBox securityType;

    /**
     * 特征
     */
    @FXML
    private DBCharacteristicCombobox characteristic;

    /**
     * 参数表单
     */
    @FXML
    private FlexTableView<DBRoutineParam> paramTable;

    /**
     * 参数类型
     */
    @FXML
    private FlexTableColumn<DBRoutineParam, String> paramType;

    /**
     * 参数长度
     */
    @FXML
    private FlexTableColumn<DBRoutineParam, String> paramSize;

    /**
     * 参数值
     */
    @FXML
    private FlexTableColumn<DBRoutineParam, String> paramValue;

    /**
     * 参数小数
     */
    @FXML
    private FlexTableColumn<DBRoutineParam, String> paramDigits;

    /**
     * 参数字符集
     */
    @FXML
    private FlexTableColumn<DBRoutineParam, String> paramCharset;

    /**
     * 参数排序
     */
    @FXML
    private FlexTableColumn<DBRoutineParam, String> paramCollation;

    /**
     * 参数名称
     */
    @FXML
    private FlexTableColumn<DBRoutineParam, String> paramName;

    /**
     * 返回值类型
     */
    @FXML
    private DBFiledTypeComboBox returnType;

    /**
     * 返回值列表
     */
    @FXML
    private DBEnumTextFiled returnValues;

    /**
     * 返回值小数
     */
    @FXML
    private NumberTextField returnDigits;

    /**
     * 返回值长度
     */
    @FXML
    private NumberTextField returnSize;

    /**
     * 返回值字符集
     */
    @FXML
    private DBCharsetComboBox returnCharset;

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
     * 执行初始化
     *
     * @param function 查询对象
     * @param dbItem   db库树节点
     */
    public void init(DBFunction function, MysqlDatabaseTreeItem dbItem) {
        this.function = function;
        this.dbItem = dbItem;

        // 初始化字符集列表
        this.returnCharset.init(this.dbItem.client());

        // 初始化监听器
        this.initDBListener();

        // 初始化信息
        this.initInfo();

        // 监听组件
        CacheHelper.set("dbClient", this.dbItem.client());
        DBListenerManager.bindListener(this.definer, this.listener);
        DBListenerManager.bindListener(this.comment, this.listener);
        DBListenerManager.bindListener(this.definition, this.listener);
        DBListenerManager.bindListener(this.returnType, this.listener);
        DBListenerManager.bindListener(this.returnSize, this.listener);
        DBListenerManager.bindListener(this.returnDigits, this.listener);
        DBListenerManager.bindListener(this.securityType, this.listener);
        DBListenerManager.bindListener(this.returnValues, this.listener);
        DBListenerManager.bindListener(this.returnCharset, this.listener);
        DBListenerManager.bindListener(this.characteristic, this.listener);
        this.paramTable.itemList().addListener(this.listChangeListener);
    }

    /**
     * 初始化数据监听器
     */
    private void initDBListener() {
        // 初始化监听器
        this.listener = new DBListener(this.function.getDbName() + ":" + this.function.getName()) {
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
        this.newData = this.function.isNew();

        // 初始化数据
        this.definer.setText(this.function.getDefiner());
        this.comment.setText(this.function.getComment());
        this.definition.setText(this.function.getDefinition());
        this.definition.forgetHistory();
        this.definition.setDialect(this.dbItem.dialect());
        this.paramTable.setItem(this.function.getParams());
        this.securityType.select(this.function.getSecurityType());
        this.characteristic.select(this.function.getCharacteristic());

        // 返回值处理
        DBRoutineParam returnParam = this.function.getReturnParam();
        if (returnParam != null) {
            this.returnType.select(returnParam.getType());
            if (returnParam.getSize() != null) {
                this.returnSize.setValue(returnParam.getSize());
            }
            if (returnParam.getDigits() != null) {
                this.returnDigits.setValue(returnParam.getDigits());
            }
            if (StrUtil.isNotBlank(returnParam.getCharset())) {
                this.returnCharset.setValue(returnParam.getCharset());
            }
            if (StrUtil.isNotBlank(returnParam.getValue())) {
                this.returnValues.setValues(returnParam.getValueList());
            }
        }

        // 如果是新数据，则默认触发变更
        if (this.newData) {
            this.unsaved = true;
            this.definer.setText("`root`@`%`");
            String defDefinition = """
                    BEGIN
                        #Routine body goes here...
                    
                         RETURN 0;
                    END
                    """;
            this.definition.setText(defDefinition);
        }

        // 标记为结束
        FXUtil.runPulse(() -> this.initiating = false);
    }

    /**
     * 保存
     */
    @FXML
    private void save() {
        try {
            // 创建临时对象
            DBFunction tempFunction = this.tempData();

            // 函数名称
            String functionName;
            if (this.newData) {
                functionName = MessageBox.prompt(I18nHelper.pleaseInputFunctionName());
                if (functionName == null) {
                    return;
                }
                tempFunction.setName(functionName);
            } else {
                functionName = tempFunction.getName();
            }

            this.disableTab();

            // 创建函数
            if (this.newData) {
                this.dbItem.createFunction(tempFunction);
                MysqlEventUtil.functionAdded(this.dbItem);
                this.initDBListener();
            } else {// 修改过程
                this.dbItem.alertFunction(tempFunction);
                MysqlEventUtil.functionAlerted(functionName, this.dbItem);
            }
            // 更新保存标志位
            this.unsaved = false;
            // 重载表数据
            this.function = this.dbItem.selectFunction(functionName);
            // 刷新tab
            this.initInfo();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        } finally {
            this.enableTab();
            this.flushTab();
        }
    }

    /**
     * 获取临时数据
     *
     * @return 临时数据
     */
    private DBFunction tempData() {
        // 创建临时对象
        DBFunction tempFunction = new DBFunction();
        tempFunction.setName(this.function.getName());

        // 基本信息处理
        tempFunction.setDbName(this.function.getDbName());
        tempFunction.setParams(this.paramTable.getItems());
        tempFunction.setDefiner(this.definer.getTextTrim());
        tempFunction.setComment(this.comment.getTextTrim());
        tempFunction.setDefinition(this.definition.getTextTrim());
        tempFunction.setSecurityType(this.securityType.getSelectedItem());
        tempFunction.setCharacteristic(this.characteristic.getSelectedItem());

        // 返回值处理
        DBRoutineParam returnParam = new DBRoutineParam();
        returnParam.setType(this.returnType.getValue());
        if (this.returnSize.isEnable()) {
            returnParam.setSize(this.returnSize.getIntValue());
        }
        if (this.returnValues.isEnable()) {
            returnParam.setValue(this.returnValues.getTextTrim());
        }
        if (this.returnDigits.isEnable()) {
            returnParam.setDigits(this.returnDigits.getIntValue());
        }
        if (this.returnCharset.isEnable()) {
            returnParam.setCharset(this.returnCharset.getSelectedItem());
        }
        tempFunction.setReturnParam(returnParam);

        return tempFunction;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        // 监听事件
        NodeUtil.nodeOnCtrlS(this.getTab(), this::save);
        NodeUtil.nodeOnCtrlS(this.definer, this::save);
        NodeUtil.nodeOnCtrlS(this.comment, this::save);
        NodeUtil.nodeOnCtrlS(this.definition, this::save);
        NodeUtil.nodeOnCtrlS(this.returnSize, this::save);
        NodeUtil.nodeOnCtrlS(this.returnType, this::save);
        NodeUtil.nodeOnCtrlS(this.returnValues, this::save);
        NodeUtil.nodeOnCtrlS(this.returnDigits, this::save);
        NodeUtil.nodeOnCtrlS(this.securityType, this::save);
        NodeUtil.nodeOnCtrlS(this.returnCharset, this::save);
        NodeUtil.nodeOnCtrlS(this.characteristic, this::save);
        // 绑定属性
        this.paramName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        this.paramType.setCellValueFactory(new PropertyValueFactory<>("typeControl"));
        this.paramSize.setCellValueFactory(new PropertyValueFactory<>("sizeControl"));
        this.paramValue.setCellValueFactory(new PropertyValueFactory<>("valueControl"));
        this.paramDigits.setCellValueFactory(new PropertyValueFactory<>("digitsControl"));
        this.paramCharset.setCellValueFactory(new PropertyValueFactory<>("charsetControl"));
        this.paramCollation.setCellValueFactory(new PropertyValueFactory<>("collationControl"));

        // 返回值监听
        this.returnType.selectedItemChanged((observable, oldValue, newValue) -> {
            if (this.returnType.supportCharset()) {
                this.returnCharset.enable();
            } else {
                this.returnCharset.disable();
            }
            if (this.returnType.supportSize()) {
                this.returnSize.enable();
            } else {
                this.returnSize.disable();
            }
            if (this.returnType.supportDigits()) {
                this.returnDigits.enable();
            } else {
                this.returnDigits.disable();
            }
            if (this.returnType.supportValue()) {
                this.returnValues.enable();
            } else {
                this.returnValues.disable();
            }
        });

        // 切换面板监听
        this.tabPane.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 1) {
                NodeGroupUtil.display(this.getTab(), "param");
            } else {
                NodeGroupUtil.disappear(this.getTab(), "param");
            }
            if (newValue.intValue() == 4) {
                DBFunction temp = this.tempData();
                if (StrUtil.isBlank(temp.getName())) {
                    temp.setName("Unnamed_Function");
                }
                String sql = DBFunctionSqlGenerator.INSTANCE.generate(temp);
                this.preview.setText(sql);
            }
        });
    }

    /**
     * 添加参数
     */
    @FXML
    private void addParam() {
        DBRoutineParam param = new DBRoutineParam();
        param.setCreated(true);
        this.paramTable.addItem(param);
        this.paramTable.selectLast();
    }

    /**
     * 删除参数
     */
    @FXML
    private void deleteParam() {
        try {
            DBRoutineParam param = this.paramTable.getSelectedItem();
            if (param != null) {
                // 非新增的数据进行提示
                if (!param.isCreated() && !MessageBox.confirm(I18nHelper.delete() + " " + param.getName())) {
                    return;
                }
                this.paramTable.getItems().remove(param);
                // 从table移除数据
                if (param.isCreated()) {
                    this.paramTable.removeItem(param);
                } else {// 标记为删除
                    param.setDeleted(true);
                }
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 上移参数
     */
    @FXML
    private void moveParamUp() {
        try {
            TableViewUtil.moveUp(this.paramTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 下移参数
     */
    @FXML
    private void moveParamDown() {
        try {
            TableViewUtil.moveDown(this.paramTable);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }
}

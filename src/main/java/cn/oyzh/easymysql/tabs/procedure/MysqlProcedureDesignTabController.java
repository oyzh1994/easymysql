package cn.oyzh.easymysql.tabs.procedure;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.db.routine.MysqlProcedure;
import cn.oyzh.easymysql.db.routine.MysqlRoutineParam;
import cn.oyzh.easymysql.fx.DBSecurityTypeComboBox;
import cn.oyzh.easymysql.fx.DBSqlTextArea;
import cn.oyzh.easymysql.fx.routine.DBCharacteristicCombobox;
import cn.oyzh.easymysql.generator.routine.DBProcedureSqlGenerator;
import cn.oyzh.easymysql.listener.DBListener;
import cn.oyzh.easymysql.listener.DBListenerManager;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.textfield.FlexTextField;
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
 * db存储过程内容组件
 *
 * @author oyzh
 * @since 2024/07/08
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class MysqlProcedureDesignTabController extends DynamicTabController {

    /**
     * 过程
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private MysqlProcedure procedure;

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
    private FlexTableView<MysqlRoutineParam> paramTable;

    /**
     * 参数类型
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramType;

    /**
     * 参数长度
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramSize;

    /**
     * 参数值
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramValue;

    /**
     * 参数小数
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramDigits;

    /**
     * 参数字符集
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramCharset;

    /**
     * 参数排序
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramCollation;

    /**
     * 参数名称
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramName;

    /**
     * 参数模式
     */
    @FXML
    private FlexTableColumn<MysqlRoutineParam, String> paramMode;

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
     * @param procedure 查询对象
     * @param dbItem    db库树节点
     */
    public void init(MysqlProcedure procedure, MysqlDatabaseTreeItem dbItem) {
        this.procedure = procedure;
        this.dbItem = dbItem;

        // 初始化监听器
        this.initDBListener();

        // 初始化信息
        this.initInfo();

        // 监听组件
        CacheHelper.set("dbClient", this.dbItem.client());
        DBListenerManager.bindListener(this.definer, this.listener);
        DBListenerManager.bindListener(this.comment, this.listener);
        DBListenerManager.bindListener(this.definition, this.listener);
        DBListenerManager.bindListener(this.securityType, this.listener);
        DBListenerManager.bindListener(this.characteristic, this.listener);
        this.paramTable.itemList().addListener(this.listChangeListener);
    }

    /**
     * 初始化数据监听器
     */
    private void initDBListener() {
        // 初始化监听器
        this.listener = new DBListener(this.procedure.getDbName() + ":" + this.procedure.getName()) {
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
        this.newData = this.procedure.isNew();

        // 初始化数据
        this.definer.setText(this.procedure.getDefiner());
        this.comment.setText(this.procedure.getComment());
        this.definition.setText(this.procedure.getDefinition());
        this.definition.forgetHistory();
        this.paramTable.setItem(this.procedure.getParams());
        this.securityType.select(this.procedure.getSecurityType());
        this.characteristic.select(this.procedure.getCharacteristic());

        // 如果是新数据，则默认触发变更
        if (this.newData) {
            this.unsaved = true;
            this.definer.setText("`root`@`%`");
            String defDefinition = """
                    BEGIN
                        #Routine body goes here...
                    
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
            MysqlProcedure tempProcedure = this.tempData();

            // 过程名称
            String procedureName;
            if (this.newData) {
                procedureName = MessageBox.prompt(I18nHelper.pleaseInputProcedureName());
                if (procedureName == null) {
                    return;
                }
                tempProcedure.setName(procedureName);
            } else {
                procedureName = tempProcedure.getName();
            }

            this.disableTab();

            // 创建过程
            if (this.newData) {
                this.dbItem.createProcedure(tempProcedure);
                MysqlEventUtil.procedureAdded(this.dbItem);
                this.initDBListener();
            } else {// 修改过程
                this.dbItem.alertProcedure(tempProcedure);
                MysqlEventUtil.procedureAlerted(procedureName, this.dbItem);
            }
            // 更新保存标志位
            this.unsaved = false;
            // 重载表数据
            this.procedure = this.dbItem.selectProcedure(procedureName);
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
    private MysqlProcedure tempData() {
        // 创建临时对象
        MysqlProcedure tempFunction = new MysqlProcedure();
        tempFunction.setName(this.procedure.getName());

        // 基本信息处理
        tempFunction.setDbName(this.procedure.getDbName());
        tempFunction.setParams(this.paramTable.getItems());
        tempFunction.setDefiner(this.definer.getTextTrim());
        tempFunction.setComment(this.comment.getTextTrim());
        tempFunction.setDefinition(this.definition.getTextTrim());
        tempFunction.setSecurityType(this.securityType.getSelectedItem());
        tempFunction.setCharacteristic(this.characteristic.getSelectedItem());

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
        // 绑定属性
        this.paramName.setCellValueFactory(new PropertyValueFactory<>("nameControl"));
        this.paramMode.setCellValueFactory(new PropertyValueFactory<>("modeControl"));
        this.paramType.setCellValueFactory(new PropertyValueFactory<>("typeControl"));
        this.paramSize.setCellValueFactory(new PropertyValueFactory<>("sizeControl"));
        this.paramValue.setCellValueFactory(new PropertyValueFactory<>("valueControl"));
        this.paramDigits.setCellValueFactory(new PropertyValueFactory<>("digitsControl"));
        this.paramCharset.setCellValueFactory(new PropertyValueFactory<>("charsetControl"));
        this.paramCollation.setCellValueFactory(new PropertyValueFactory<>("collationControl"));

        // 切换面板监听
        this.tabPane.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 1) {
                NodeGroupUtil.display(this.getTab(), "param");
            } else {
                NodeGroupUtil.disappear(this.getTab(), "param");
            }
            if (newValue.intValue() == 3) {
                MysqlProcedure temp = this.tempData();
                if (StrUtil.isBlank(temp.getName())) {
                    temp.setName("Unnamed_Procedure");
                }
                String sql = DBProcedureSqlGenerator.INSTANCE.generate(temp);
                this.preview.setText(sql);
            }
        });
    }

    /**
     * 添加参数
     */
    @FXML
    private void addParam() {
        MysqlRoutineParam param = new MysqlRoutineParam();
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
            MysqlRoutineParam param = this.paramTable.getSelectedItem();
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

package cn.oyzh.easymysql.mysql.trigger;

import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.easymysql.db.DBObjectStatus;

/**
 * db表触发器
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlTrigger extends DBObjectStatus implements ObjectCopier<MysqlTrigger> {

    /**
     * 名称
     */
    private String name;

    /**
     * 策略
     */
    private String policy;

    /**
     * 定义
     */
    private String definition;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 获取原始名称
     *
     * @return 原始名称
     */
    public String originalName() {
        return (String) this.getOriginalData("name");
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    // public ClearableTextField getNameControl() {
    //     ClearableTextField textField = new ClearableTextField();
    //     textField.setPromptText(I18nHelper.pleaseInputName());
    //     textField.addTextChangeListener((observable, oldValue, newValue) -> {
    //         this.setName(newValue);
    //     });
    //     if (this.name != null) {
    //         textField.setText(this.name);
    //     }
    //     TableViewUtil.rowOnCtrlS(textField);
    //     TableViewUtil.selectRowOnMouseClicked(textField);
    //     return textField;
    // }

    /**
     * 设置策略
     *
     * @param policy 策略
     */
    public void setPolicy(String policy) {
        this.policy = policy;
        super.putOriginalData("policy", policy);
    }
    //
    // public MysqlTriggerPolicyComboBox getPolicyControl() {
    //     MysqlTriggerPolicyComboBox comboBox = new MysqlTriggerPolicyComboBox();
    //     comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
    //         this.setPolicy(newValue);
    //     });
    //     comboBox.selectFirstIfNull(this.policy);
    //     TableViewUtil.rowOnCtrlS(comboBox);
    //     TableViewUtil.selectRowOnMouseClicked(comboBox);
    //     return comboBox;
    // }

    /**
     * 设置定义
     *
     * @param definition 定义
     */
    public void setDefinition(String definition) {
        this.definition = definition;
        super.putOriginalData("definition", definition);
    }

    // public EnlargeTextFiled getDefinitionControl() {
    //     EnlargeTextFiled textField = new EnlargeTextFiled();
    //     textField.setPromptText(I18nHelper.pleaseInputContent());
    //     textField.addTextChangeListener((observable, oldValue, newValue) -> {
    //         // if (!StrUtil.equalsIgnoreCase(newValue, this.definition)) {
    //         //     this.definition = newValue;
    //         //     this.setChanged(true);
    //         // }
    //         this.setDefinition(newValue);
    //     });
    //     if (this.definition != null) {
    //         textField.setText(this.definition);
    //     }
    //     TableViewUtil.rowOnCtrlS(textField);
    //     TableViewUtil.selectRowOnMouseClicked(textField);
    //     return textField;
    // }

    /**
     * 根据时机与操作设置策略
     *
     * @param timing      时机
     * @param manipulation 操作
     */
    public void setPolicy(String timing, String manipulation) {
        this.setPolicy(timing.toUpperCase() + " " + manipulation.toUpperCase());
    }

    /**
     * 设置表名
     *
     * @param tableName 表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
        super.putOriginalData("tableName", tableName);
    }

    @Override
    public void copy(MysqlTrigger t1) {
        if (t1 != null) {
            this.name = t1.name;
            this.policy = t1.policy;
            this.definition = t1.definition;
        }
    }

    /**
     * 是否无效
     *
     * @return 是否无效
     */
    public boolean isInvalid() {
        return false;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取策略
     *
     * @return 策略
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * 获取定义
     *
     * @return 定义
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getTableName() {
        return tableName;
    }
}

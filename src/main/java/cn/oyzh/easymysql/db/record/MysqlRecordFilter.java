package cn.oyzh.easymysql.db.record;

import cn.oyzh.easymysql.condition.MysqlCondition;
import cn.oyzh.easymysql.condition.MysqlConditionUtil;
import cn.oyzh.easymysql.db.table.MysqlColumn;
import cn.oyzh.easymysql.fx.table.DBColumnComboBox;
import cn.oyzh.easymysql.fx.table.DBConditionComboBox;
import cn.oyzh.easymysql.fx.table.DBJoinSymbolComboBox;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.flex.FlexUtil;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.util.TableViewUtil;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 记录过滤条件
 *
 * @author oyzh
 * @since 2024/06/26
 */
public class MysqlRecordFilter {

    /**
     * 值
     */
    @Getter
    @Setter
    private Object value;

    /**
     * 是否已启用
     */
    @Getter
    @Setter
    private boolean enabled = true;

    /**
     * 连接符号
     */
    @Getter
    @Setter
    private String joinSymbol;

    /**
     * 条件
     */
    @Getter
    @Setter
    private MysqlCondition condition;

    /**
     * 字段
     */
    @Getter
    @Setter
    private MysqlColumn column;

    /**
     * 字段列表
     */
    @Setter
    private List<MysqlColumn> columns;

    /**
     * 值组件
     */
    private FlexHBox valueBox;

    /**
     * 获取值
     *
     * @return 值
     */
    public Object value() throws Exception {
        if (this.valueBox == null || this.valueBox.isChildEmpty()) {
            return this.value;
        }
        return this.value = MysqlConditionUtil.getNodeVal(this.valueBox.getChildren());
    }

    /**
     * 获取值组件
     *
     * @return 值组件
     */
    public Node getValueControl() {
        this.updateValueControl();
        return this.valueBox;
    }

    /**
     * 更新值组件
     */
    private void updateValueControl() {
        if (this.valueBox == null) {
            this.valueBox = new FlexHBox();
            FlexUtil.flexWidth(this.valueBox, "100%");
        }
        List<Node> nodes = MysqlConditionUtil.generateNode(this.column, this.condition);
        MysqlConditionUtil.setNodeVal(nodes, this.value);
        if (nodes.size() == 1) {
            FlexUtil.flexWidth(nodes.getFirst(), "100% - 10");
            FlexUtil.flexHeight(nodes.getFirst(), "100%");
        } else if (nodes.size() == 2) {
            FlexUtil.flexWidth(nodes.get(0), "50% - 10");
            FlexUtil.flexHeight(nodes.get(0), "100%");
            FlexUtil.flexWidth(nodes.get(1), "50% - 10");
            FlexUtil.flexHeight(nodes.get(1), "100%");
        }
        for (Node node : nodes) {
            if (node instanceof TextField textField) {
                textField.setPromptText(I18nHelper.pleaseInputContent());
            }
            TableViewUtil.selectRowOnMouseClicked(node);
        }
        this.valueBox.setChild(nodes);
    }

    /**
     * 获取字段组件
     *
     * @return 字段组件
     */
    public DBColumnComboBox getColumnControl() {
        DBColumnComboBox comboBox = new DBColumnComboBox(this.columns);
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
            this.column = newValue;
            this.updateValueControl();
        });
        comboBox.selectFirstIfNull(this.column);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取条件组件
     *
     * @return 条件组件
     */
    public DBConditionComboBox getConditionControl() {
        DBConditionComboBox comboBox = new DBConditionComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
            this.condition = newValue;
            this.updateValueControl();
        });
        comboBox.selectFirstIfNull(this.condition);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取启用组件
     *
     * @return 启用组件
     */
    public FlexCheckBox getEnabledControl() {
        FlexCheckBox checkBox = new FlexCheckBox();
        checkBox.setSelected(this.enabled);
        checkBox.selectedChanged((observable, oldValue, newValue) -> this.enabled = newValue);
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    /**
     * 获取连接符组件
     *
     * @return 连接符组件
     */
    public DBJoinSymbolComboBox getJoinSymbolControl() {
        DBJoinSymbolComboBox comboBox = new DBJoinSymbolComboBox();
        comboBox.selectFirstIfNull(this.joinSymbol);
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.joinSymbol = newValue);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取字段名
     *
     * @return 字段名
     */
    public String column() {
        return this.column.getName();
    }

    /**
     * 获取条件
     *
     * @return 条件
     */
    public String condition() throws Exception {
        return this.condition.wrapCondition(this.value());
    }

    /**
     * 是否需要条件
     *
     * @return 结果
     */
    public boolean isRequireCondition() {
        return this.condition.isRequireCondition();
    }
}

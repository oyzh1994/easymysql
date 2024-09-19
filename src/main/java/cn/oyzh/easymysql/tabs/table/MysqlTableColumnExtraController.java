package cn.oyzh.easymysql.tabs.table;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.fx.DBCharsetComboBox;
import cn.oyzh.easymysql.fx.DBCollationComboBox;
import cn.oyzh.easymysql.fx.table.MysqlDefaultValueTextFiled;
import cn.oyzh.easymysql.fx.table.DBEnumTextFiled;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.SubTabController;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 字段配置弹窗
 *
 * @author oyzh
 * @since 2024/07/12
 */
public class MysqlTableColumnExtraController extends SubTabController {

    /**
     * 默认值组件
     */
    @FXML
    private FlexHBox defaultValueBox;

    /**
     * 默认值
     */
    @FXML
    private MysqlDefaultValueTextFiled defaultValue;

    /**
     * 字段值组件
     */
    @FXML
    private FlexHBox valueBox;

    /**
     * 字段值
     */
    @FXML
    private DBEnumTextFiled value;

    /**
     * 主键长度组件
     */
    @FXML
    private FlexHBox primaryKeySizeBox;

    /**
     * 主键长度
     */
    @FXML
    private NumberTextField primaryKeySize;

    /**
     * 填充零组件
     */
    @FXML
    private FlexHBox zeroFillBox;

    /**
     * 填充零
     */
    @FXML
    private FlexCheckBox zeroFill;

    /**
     * 自动递增组件
     */
    @FXML
    private FlexHBox autoIncrementBox;

    /**
     * 自动递增
     */
    @FXML
    private FlexCheckBox autoIncrement;

    /**
     * 无符号组件
     */
    @FXML
    private FlexHBox unsignedBox;

    /**
     * 无符号
     */
    @FXML
    private FlexCheckBox unsigned;

    /**
     * 根据当前时间戳更新组件
     */
    @FXML
    private FlexHBox currentTimestampBox;

    /**
     * 根据当前时间戳更新
     */
    @FXML
    private FlexCheckBox currentTimestamp;

    /**
     * 字符集组件
     */
    @FXML
    private FlexHBox charsetBox;

    /**
     * 字符集
     */
    @FXML
    private DBCharsetComboBox charset;

    /**
     * 排序方式组件
     */
    @FXML
    private FlexHBox collationBox;

    /**
     * 排序方式
     */
    @FXML
    private DBCollationComboBox collation;

    /**
     * db字段
     */
    private MysqlColumn column;

    /**
     * db客户端
     */
    private DBClient dbClient;

    /**
     * 应用
     */
    private void apply() {
        try {
            if (this.ignoreChanged) {
                return;
            }
            // 值处理
            if (this.valueBox.isVisible()) {
                this.column.setValue(this.value.getTextTrim());
            }
            // 字符集、排序处理
            if (this.charsetBox.isVisible()) {
                this.column.setCharset(this.charset.getValue());
                this.column.setCollation(this.collation.getValue());
            }
            // 填充零处理
            if (this.zeroFillBox.isVisible()) {
                this.column.setZeroFill(this.zeroFill.isSelected());
            }
            // 无符号处理
            if (this.unsignedBox.isVisible()) {
                this.column.setUnsigned(this.unsigned.isSelected());
            }
            // 默认值处理
            if (this.defaultValueBox.isVisible()) {
                this.column.setDefaultValue(this.defaultValue.getValue());
            }
            // 自动递增处理
            if (this.autoIncrementBox.isVisible()) {
                this.column.setAutoIncrement(this.autoIncrement.isSelected());
            }
            // 主键长度处理
            if (this.primaryKeySizeBox.isVisible()) {
                this.column.setPrimaryKeySize(this.primaryKeySize.getIntValue());
            }
            // 根据时间戳更新处理
            if (this.currentTimestampBox.isVisible()) {
                this.column.setUpdateOnCurrentTimestamp(this.currentTimestamp.isSelected());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    private boolean ignoreChanged = false;

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // 字符集选中事件
        this.charset.selectedItemChanged((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.collation.init(newValue, this.dbClient);
                this.collation.selectFirst();
            }
            this.apply();
        });
        // 填充零变化事件
        this.zeroFill.selectedChanged((observable, oldValue, newValue) -> this.apply());
        // 无符号变化事件
        this.unsigned.selectedChanged((observable, oldValue, newValue) -> this.apply());
        // 值变化事件
        this.value.addTextChangeListener((observable, oldValue, newValue) -> this.apply());
        // 排序选中事件
        this.collation.selectedItemChanged((observable, oldValue, newValue) -> this.apply());
        // 自动递增变化事件
        this.autoIncrement.selectedChanged((observable, oldValue, newValue) -> this.apply());
        // 当前时间戳变化事件
        this.currentTimestamp.selectedChanged((observable, oldValue, newValue) -> this.apply());
        // 默认值变化事件
        this.defaultValue.addTextChangeListener((observable, oldValue, newValue) -> this.apply());
        // 键长度变化事件
        this.primaryKeySize.addTextChangeListener((observable, oldValue, newValue) -> this.apply());
    }

    /**
     * 执行初始化
     *
     * @param column   字段
     * @param dbClient 客户端
     */
    public void init(MysqlColumn column, DBClient dbClient) {
        if (column == null) {
            return;
        }
        this.ignoreChanged = true;
        this.column = column;
        this.dbClient = dbClient;
        // 值
        if (this.column.supportValue()) {
            this.valueBox.display();
            this.value.setValues(this.column.getValueList());
        } else {
            this.valueBox.disappear();
        }

        // 默认值
        if (this.column.supportDefaultValue()) {
            this.defaultValueBox.display();
            this.defaultValue.init(this.column, this.column.getDefaultValueString());
        } else {
            this.defaultValueBox.disappear();
        }

        // 自动递增
        if (this.column.supportAutoIncrement()) {
            this.autoIncrementBox.display();
            this.autoIncrement.setSelected(this.column.isAutoIncrement());
        } else {
            this.autoIncrementBox.disappear();
        }

        // 填充零
        if (this.column.supportZeroFill()) {
            this.zeroFillBox.display();
            this.zeroFill.setSelected(this.column.isZeroFill());
        } else {
            this.zeroFillBox.disappear();
        }

        // 无符号
        if (this.column.supportUnsigned()) {
            this.unsignedBox.display();
            this.unsigned.setSelected(this.column.isUnsigned());
        } else {
            this.unsignedBox.disappear();
        }

        // 字符集及排序
        if (this.column.supportCharset()) {
            this.charsetBox.display();
            this.collationBox.display();
            if (this.charset.isItemEmpty()) {
                this.charset.init(this.dbClient);
            }
            this.charset.select(this.column.getCharset());
            this.collation.select(this.column.getCollation());
        } else {
            this.charsetBox.disappear();
            this.collationBox.disappear();
        }

        // 主键长度
        if (this.column.isPrimaryKey() && this.column.supportKeySize()) {
            this.primaryKeySizeBox.display();
            if (this.column.getPrimaryKeySize() != null) {
                this.primaryKeySize.setValue(this.column.getPrimaryKeySize());
            }
        } else {
            this.primaryKeySizeBox.disappear();
        }

        // 根据时间戳更新
        if (this.column.supportTimestamp()) {
            this.currentTimestampBox.display();
            this.currentTimestamp.setSelected(this.column.isUpdateOnCurrentTimestamp());
        } else {
            this.currentTimestampBox.disappear();
        }
        this.ignoreChanged = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        this.valueBox.managedBindVisible();
        this.charsetBox.managedBindVisible();
        this.unsignedBox.managedBindVisible();
        this.zeroFillBox.managedBindVisible();
        this.collationBox.managedBindVisible();
        this.defaultValueBox.managedBindVisible();
        this.autoIncrementBox.managedBindVisible();
        this.primaryKeySizeBox.managedBindVisible();
        this.currentTimestampBox.managedBindVisible();
    }
}

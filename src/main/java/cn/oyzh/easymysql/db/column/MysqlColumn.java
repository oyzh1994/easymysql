package cn.oyzh.easymysql.db.column;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.fx.table.DBFiledTypeComboBox;
import cn.oyzh.easymysql.popups.DBColumnConfigPopupController;
import cn.oyzh.easymysql.util.DBColumnUtil;
import cn.oyzh.fx.common.util.CacheHelper;
import cn.oyzh.fx.common.util.ObjectCopier;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.ConfigurationSVGGlyph;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.util.TableViewUtil;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * db字段
 *
 * @author oyzh
 * @since 2023/12/20
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlColumn extends DBObjectStatus implements ObjectCopier<MysqlColumn> {

    /**
     * 库名称
     */
    @Getter
    @Setter
    private String dbName;

    /**
     * 模式名称
     */
    @Getter
    @Setter
    private String schema;

    /**
     * 表名称
     */
    @Getter
    @Setter
    private String tableName;

    /**
     * 字段大小
     */
    @Getter
    private Integer size;

    /**
     * 字段类型
     */
    @Getter
    private String type;

    /**
     * 字段值
     */
    @Getter
    private String value;

    /**
     * 注释
     */
    @Getter
    private String comment;

    /**
     * 可为null
     */
    @Getter
    private Boolean nullable;

    /**
     * 无符号
     */
    @Getter
    private Boolean unsigned;

    /**
     * 填充零
     */
    @Getter
    private Boolean zeroFill;

    /**
     * 根据当前时间戳更新
     */
    @Getter
    private Boolean updateOnCurrentTimestamp;

    /**
     * 字段位置
     */
    @Getter
    @Setter
    private Integer position;

    /**
     * 主键属性
     */
    private SimpleBooleanProperty primaryKeyProperty;

    /**
     * 键长度
     */
    @Getter
    private Integer primaryKeySize;

    /**
     * 默认值
     */
    @Getter
    private Object defaultValue;

    /**
     * 小数位
     */
    @Getter
    private Integer digits;

    /**
     * 自动递增
     */
    @Getter
    private Boolean autoIncrement;

    /**
     * 名称
     */
    @Getter
    private String name;

    /**
     * 字段字符集
     */
    @Getter
    private String charset;

    /**
     * 字段排序规则
     */
    @Getter
    private String collation;

    public MysqlColumn() {

    }

    public MysqlColumn(String name) {
        this.name = name;
    }

    public boolean isNameChanged() {
        return super.checkOriginalData("name", this.name);
    }

    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    public void setType(String type) {
        if (StrUtil.containsIgnoreCase(type, " UNSIGNED")) {
            this.setUnsigned(true);
            type = type.substring(0, type.length() - 9);
        }
        this.type = type;
        super.putOriginalData("type", type);
    }

    public List<String> getValueList() {
        List<String> valueList = new ArrayList<>();
        if (this.getValue() != null) {
            List<String> list = StrUtil.split(this.getValue(), ",");
            for (String s : list) {
                if (s.startsWith("'") && s.endsWith("'")) {
                    valueList.add(s.substring(1, s.length() - 1));
                } else {
                    valueList.add(s);
                }
            }
        }
        return valueList;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        super.putOriginalData("defaultValue", defaultValue);
    }

    // public void defaultValue(Object defaultValue) {
    //     if (!Objects.equals(defaultValue, this.defaultValue)) {
    //         this.defaultValue = defaultValue;
    //         this.setChanged(true);
    //     }
    // }

    public String getDefaultValueString() {
        Object defaultValue = this.defaultValue;
        return defaultValue == null ? null : defaultValue.toString();
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        super.putOriginalData("autoIncrement", autoIncrement);
        // 如果是自动递增，则清除默认值
        if (BooleanUtil.isTrue(autoIncrement)) {
            this.setDefaultValue(null);
        }
    }

    // public void autoIncrement(Boolean autoIncrement) {
    //     if (autoIncrement != this.autoIncrement) {
    //         this.autoIncrement = autoIncrement;
    //         this.setChanged(true);
    //     }
    // }

    public boolean isAutoIncrement() {
        return BooleanUtil.isTrue(this.autoIncrement);
    }

    public boolean hasComment() {
        return this.getComment() != null;
    }

    public void setCharset(String charset) {
        // if (!StrUtil.equalsIgnoreCase(charset, this.charset)) {
        //     this.charset = charset;
        //     this.setChanged(true);
        // }
        this.charset = charset;
        super.putOriginalData("charset", charset);
    }

    public void setCollation(String collation) {
        // if (!StrUtil.equalsIgnoreCase(collation, this.collation)) {
        //     this.collation = collation;
        //     this.setChanged(true);
        // }
        this.collation = collation;
        super.putOriginalData("collation", collation);
    }

    public void setValue(String value) {
        // if (!StrUtil.equalsIgnoreCase(value, this.value)) {
        //     this.value = value;
        //     this.setChanged(true);
        // }
        this.value = value;
        super.putOriginalData("value", value);
    }

    public void setUnsigned(Boolean unsigned) {
        // if (unsigned != this.unsigned) {
        //     this.unsigned = unsigned;
        //     this.setChanged(true);
        // }
        this.unsigned = unsigned;
        super.putOriginalData("unsigned", unsigned);
    }

    /**
     * 是否无符号模式
     *
     * @return 无符号模式
     */
    public boolean isUnsigned() {
        return BooleanUtil.isTrue(this.unsigned);
    }

    public void setUpdateOnCurrentTimestamp(Boolean updateOnCurrentTimestamp) {
        this.updateOnCurrentTimestamp = updateOnCurrentTimestamp;
        super.putOriginalData("updateOnCurrentTimestamp", updateOnCurrentTimestamp);
    }

    // public void updateOnCurrentTimestamp(Boolean updateOnCurrentTimestamp) {
    //     if (updateOnCurrentTimestamp != this.updateOnCurrentTimestamp) {
    //         super.putOriginalData("updateOnCurrentTimestamp", updateOnCurrentTimestamp);
    //         this.updateOnCurrentTimestamp = updateOnCurrentTimestamp;
    //         // this.setChanged(true);
    //     }
    // }

    public boolean isUpdateOnCurrentTimestamp() {
        return BooleanUtil.isTrue(this.updateOnCurrentTimestamp);
    }

    /**
     * 是否支持长度
     *
     * @return 结果
     */
    public boolean supportSize() {
        return DBColumnUtil.supportSize(this.getType());
    }

    /**
     * 获取推荐长度
     *
     * @return 推荐长度
     */
    public Integer suggestSize() {
        return DBColumnUtil.suggestSize(this.getType());
    }

    /**
     * 是否支持长度
     *
     * @return 结果
     */
    public boolean supportGeometry() {
        return DBColumnUtil.supportGeometry(this.getType());
    }

    /**
     * 是否支持字符集及排序
     *
     * @return 结果
     */
    public boolean supportCharset() {
        return DBColumnUtil.supportCharset(this.getType());
    }

    /**
     * 是否支持无符号
     *
     * @return 结果
     */
    public boolean supportUnsigned() {
        return DBColumnUtil.supportUnsigned(this.getType());
    }

    /**
     * 是否支持小数
     *
     * @return 结果
     */
    public boolean supportDigits() {
        return DBColumnUtil.supportDigits(this.getType());
    }

    /**
     * 是否支持整数
     *
     * @return 结果
     */
    public boolean supportInteger() {
        return DBColumnUtil.supportInteger(this.getType());
    }

    /**
     * 是否支持自动递增
     *
     * @return 结果
     */
    public boolean supportAutoIncrement() {
        return DBColumnUtil.supportAutoIncrement(this.getType());
    }

    /**
     * 是否支持默认值
     *
     * @return 结果
     */
    public boolean supportDefaultValue() {
        return DBColumnUtil.supportDefaultValue(this.getType());
    }

    /**
     * 是否支持当前时间戳
     *
     * @return 结果
     */
    public boolean supportTimestamp() {
        return DBColumnUtil.supportTimestamp(this.getType());
    }

    /**
     * 是否支持主键
     *
     * @return 结果
     */
    public boolean supportValue() {
        return DBColumnUtil.supportValue(this.getType());
    }

    /**
     * 是否支持填充零
     *
     * @return 结果
     */
    public boolean supportZeroFill() {
        return DBColumnUtil.supportZeroFill(this.getType());
    }

    /**
     * 是否支持填充零
     *
     * @return 结果
     */
    public boolean supportBit() {
        return DBColumnUtil.supportBit(this.getType());
    }

    /**
     * 是否支持填充零
     *
     * @return 结果
     */
    public boolean supportJson() {
        return DBColumnUtil.supportJson(this.getType());
    }

    /**
     * 是否支持键长度
     *
     * @return 结果
     */
    public boolean supportKeySize() {
        return DBColumnUtil.supportKeySize(this.getType());
    }

    public boolean supportString() {
        return DBColumnUtil.supportString(this.getType());
    }

    public Long minValue() {
        return DBColumnUtil.minValue(this.getType());
    }

    public Long maxValue() {
        return DBColumnUtil.maxValue(this.getType());
    }

    public Object exampleValue() {
        return DBColumnUtil.exampleValue(this.getType());
    }

    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        if (this.name != null) {
            textField.setText(this.name);
        }
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public void setComment(String comment) {
        this.comment = comment;
        super.putOriginalData("comment", comment);
    }

    public ClearableTextField getCommentControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputComment());
        if (this.comment != null) {
            textField.setText(this.comment);
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setComment(newValue));
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public void setSize(Integer size) {
        this.size = size;
        super.putOriginalData("size", size);
    }

    public NumberTextField getSizeControl() {
        NumberTextField textField = new NumberTextField();
        if (this.size != null) {
            textField.setValue(this.size);
        } else if (this.supportSize() && this.isCreated() && this.suggestSize() != null) {
            this.size = this.suggestSize();
            textField.setValue(this.size);
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setSize(textField.getIntValue()));
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public void setDigits(Integer digits) {
        this.digits = digits;
        super.putOriginalData("digits", digits);
    }

    public NumberTextField getDigitsControl() {
        NumberTextField textField = new NumberTextField();
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setDigits(textField.getIntValue()));
        if (this.digits != null) {
            textField.setValue(this.digits);
        }
        textField.setFlexWidth("100% - 12");
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public DBFiledTypeComboBox getTypeControl() {
        DBFiledTypeComboBox comboBox = new DBFiledTypeComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        comboBox.selectFirstIfNull(this.type);
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
        super.putOriginalData("nullable", nullable);
    }

    public FlexCheckBox getNullableControl() {
        FlexCheckBox checkBox = new FlexCheckBox();
        checkBox.selectedChanged((observable, oldValue, newValue) -> this.setNullable(newValue));
        checkBox.setSelected(this.isNullable());
        // 监听主键值变化
        this.primaryKeyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                checkBox.setSelected(false);
            }
        });
        TableViewUtil.rowOnCtrlS(checkBox);
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    public SimpleBooleanProperty primaryKeyProperty() {
        if (this.primaryKeyProperty == null) {
            this.primaryKeyProperty = new SimpleBooleanProperty();
        }
        return this.primaryKeyProperty;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKeyProperty().set(primaryKey);
        super.putOriginalData("primaryKey", primaryKey);
    }

    public FlexCheckBox getPrimaryKeyControl() {
        FlexCheckBox checkBox = new FlexCheckBox();
        checkBox.selectedChanged((observable, oldValue, newValue) -> {
            // if (!Objects.equals(newValue, this.primaryKey)) {
            //     this.primaryKey = newValue;
            //     this.primaryKeyChanged = true;
            // }
            this.setPrimaryKey(newValue);
        });
        checkBox.setSelected(this.isPrimaryKey());
        TableViewUtil.rowOnCtrlS(checkBox);
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    public ConfigurationSVGGlyph getConfigControl() {
        ConfigurationSVGGlyph glyph = new ConfigurationSVGGlyph();
        glyph.setOnMousePrimaryClicked(event -> {
            PopupAdapter popup = PopupManager.parsePopup(DBColumnConfigPopupController.class);
            popup.setProp("dbColumn", this);
            popup.setProp("dbClient", CacheHelper.get("dbClient"));
            popup.showPopup(glyph);
        });
        TableViewUtil.selectRowOnMouseClicked(glyph);
        return glyph;
    }

    public void setZeroFill(Boolean zeroFill) {
        // if (zeroFill != this.zeroFill) {
        //     this.zeroFill = zeroFill;
        //     this.setChanged(true);
        // }
        this.zeroFill = zeroFill;
        super.putOriginalData("zeroFill", zeroFill);
    }

    public boolean isZeroFill() {
        return BooleanUtil.isTrue(this.zeroFill);
    }

    public void setPrimaryKeySize(Integer primaryKeySize) {
        // if (!Objects.equals(keySize, this.primaryKeySize)) {
        //     this.primaryKeySize = keySize;
        //     this.primaryKeyChanged = true;
        // }
        this.primaryKeySize = primaryKeySize;
        super.putOriginalData("primaryKeySize", primaryKeySize);
    }

    public boolean isPrimaryKey() {
        return BooleanUtil.isTrue(this.primaryKeyProperty().get());
    }

    public boolean isNullable() {
        return BooleanUtil.isTrue(this.nullable);
    }

    public boolean isPrimaryKeyChanged() {
        // return BooleanUtil.isTrue(this.primaryKeyChanged);
        return super.checkOriginalData("primaryKey", this.primaryKeyProperty().get());
    }

    @Override
    public void setDeleted(boolean deleted) {
        super.setDeleted(deleted);
        if (deleted && this.isPrimaryKey()) {
            // this.primaryKeyChanged = true;
            this.setPrimaryKey(false);
        }
    }

    public boolean isYearType() {
        return DBColumnUtil.isYearType(this.getType());
    }

    public boolean isDateType() {
        return DBColumnUtil.isDateType(this.getType());
    }

    public boolean isTimeType() {
        return DBColumnUtil.isTimeType(this.getType());
    }

    public boolean supportBinary() {
        return DBColumnUtil.supportBinary(this.getType());
    }

    public boolean supportEnum() {
        return DBColumnUtil.supportEnum(this.getType());
    }

    @Override
    public void initStatus() {
        if (this.size == null) {
            this.setSize(null);
        }
        if (this.value == null) {
            this.setValue(null);
        }
        if (this.digits == null) {
            this.setDigits(null);
        }
        if (this.unsigned == null) {
            this.setUnsigned(null);
        }
        if (this.zeroFill == null) {
            this.setZeroFill(null);
        }
        if (this.autoIncrement == null) {
            this.setAutoIncrement(null);
        }
        if (this.updateOnCurrentTimestamp == null) {
            this.setUpdateOnCurrentTimestamp(null);
        }
    }

    public void initColumn(String columnType, String columnExtra) {
        if (!columnType.contains("(") && !columnType.contains(" ")) {
            this.setType(columnType.toUpperCase());
        } else if (!columnType.contains("(")) {
            this.setType(columnType.toUpperCase());
        } else {
            String type = columnType.substring(0, columnType.indexOf("("));
            this.setType(type.toUpperCase());
            String sub1 = columnType.substring(columnType.indexOf("(") + 1, columnType.lastIndexOf(")"));
            if (this.supportEnum()) {
                this.setValue(sub1);
            } else if (this.supportDigits() && sub1.contains(",")) {
                String[] arr = sub1.split(",");
                this.setSize(Integer.parseInt(arr[0]));
                this.setDigits(Integer.parseInt(arr[1]));
            } else {
                this.setSize(Integer.parseInt(sub1));
            }
            if (StrUtil.containsIgnoreCase(columnType, "unsigned")) {
                this.setUnsigned(true);
            }
            if (StrUtil.containsIgnoreCase(columnType, "zerofill")) {
                this.setZeroFill(true);
            }
        }
        if (StrUtil.containsIgnoreCase(columnExtra, "auto_increment")) {
            this.setAutoIncrement(true);
        }
        if (StrUtil.containsIgnoreCase(columnExtra, "on update CURRENT_TIMESTAMP")) {
            this.setUpdateOnCurrentTimestamp(true);
        }
    }

    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }

    @Override
    public void copy(MysqlColumn column) {
        if (column != null) {
            this.size = column.size;
            this.name = column.name;
            this.type = column.type;
            this.value = column.value;
            this.dbName = column.dbName;
            this.digits = column.digits;
            this.comment = column.comment;
            this.charset = column.charset;
            this.position = column.position;
            this.nullable = column.nullable;
            this.unsigned = column.unsigned;
            this.zeroFill = column.zeroFill;
            this.collation = column.collation;
            this.tableName = column.tableName;
            this.defaultValue = column.defaultValue;
            this.setPrimaryKey(column.isPrimaryKey());
            this.autoIncrement = column.autoIncrement;
            this.primaryKeySize = column.primaryKeySize;
            this.updateOnCurrentTimestamp = column.updateOnCurrentTimestamp;
        }
    }
}

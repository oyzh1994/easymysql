package cn.oyzh.easymysql.db.routine;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.cache.CacheHelper;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.fx.DBCharsetComboBox;
import cn.oyzh.easymysql.fx.DBCollationComboBox;
import cn.oyzh.easymysql.fx.routine.DBParamModeComboBox;
import cn.oyzh.easymysql.fx.table.DBEnumTextFiled;
import cn.oyzh.easymysql.fx.table.DBFiledTypeComboBox;
import cn.oyzh.easymysql.util.DBColumnUtil;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/7/1
 */
public class MysqlRoutineParam extends DBObjectStatus {

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private final StringProperty typeProperty = new SimpleStringProperty();

    /**
     * 模式
     */
    private String mode;

    /**
     * 长度
     */
    private Integer size;

    /**
     * 小数位
     */
    private Integer digits;

    /**
     * 值
     */
    private String value;

    /**
     * 字符集
     */
    private final StringProperty charsetProperty = new SimpleStringProperty();

    /**
     * 排序
     */
    private String collation;

    public String getType() {
        return this.typeProperty.get();
    }

    public void setType(String type) {
        this.typeProperty.set(type);
    }

    public String getCharset() {
        return this.charsetProperty.get();
    }

    public void setCharset(String charset) {
        this.charsetProperty.set(charset);
    }

    /**
     * 获取名称组件
     *
     * @return 名称组件
     */
    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setFlexWidth("100% - 10");
        textField.setPromptText(I18nHelper.pleaseInputContent());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        textField.setText(this.name);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取类型组件
     *
     * @return 类型组件
     */
    public DBFiledTypeComboBox getTypeControl() {
        DBFiledTypeComboBox comboBox = new DBFiledTypeComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        comboBox.selectFirstIfNull(this.getType());
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取字符集组件
     *
     * @return 字符集组件
     */
    public DBCharsetComboBox getCharsetControl() {
        DBClient dbClient = CacheHelper.get("dbClient");
        DBCharsetComboBox comboBox = new DBCharsetComboBox();
        comboBox.init(dbClient);
        comboBox.select(this.getCharset());
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setCharset(newValue));
        Runnable func = () -> {
            if (DBColumnUtil.supportCharset(this.getType())) {
                comboBox.enable();
            } else {
                comboBox.disable();
                comboBox.clearSelection();
            }
        };
        this.typeProperty.addListener((observable, oldValue, newValue) -> func.run());
        func.run();
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取小数位组件
     *
     * @return 小数位组件
     */
    public NumberTextField getDigitsControl() {
        NumberTextField textField = new NumberTextField();
        textField.setFlexWidth("100% - 12");
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setDigits(textField.getIntValue()));
        textField.setValue(this.digits);
        Runnable func = () -> {
            if (DBColumnUtil.supportDigits(this.getType())) {
                textField.enable();
            } else {
                textField.disable();
                textField.clear();
            }
        };
        this.typeProperty.addListener((observable, oldValue, newValue) -> func.run());
        func.run();
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取字段长度组件
     *
     * @return 字段长度组件
     */
    public NumberTextField getSizeControl() {
        NumberTextField textField = new NumberTextField();
        textField.setFlexWidth("100% - 12");
        textField.setValue(this.size);
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setSize(textField.getIntValue()));
        Runnable func = () -> {
            if (DBColumnUtil.supportSize(this.getType())) {
                textField.enable();
            } else {
                textField.disable();
                textField.clear();
            }
        };
        this.typeProperty.addListener((observable, oldValue, newValue) -> func.run());
        func.run();
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
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

    /**
     * 获取值组件
     *
     * @return 值组件
     */
    public DBEnumTextFiled getValueControl() {
        DBEnumTextFiled textField = new DBEnumTextFiled();
        textField.setFlexWidth("100% - 12");
        textField.setValues(this.getValueList());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setValue(textField.getTextTrim()));
        Runnable func = () -> {
            if (DBColumnUtil.supportValue(this.getType())) {
                textField.enable();
            } else {
                textField.disable();
                textField.clear();
            }
        };
        this.typeProperty.addListener((observable, oldValue, newValue) -> func.run());
        func.run();
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取排序组件
     *
     * @return 排序组件
     */
    public DBCollationComboBox getCollationControl() {
        DBClient dbClient = CacheHelper.get("dbClient");
        DBCollationComboBox comboBox = new DBCollationComboBox();
        comboBox.init(this.getCharset(), dbClient);
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setCollation(newValue));
        comboBox.select(this.collation);
        this.charsetProperty.addListener((observable, oldValue, newValue) -> {
            comboBox.init(newValue, dbClient);
            comboBox.selectFirst();
        });
        Runnable func = () -> {
            if (DBColumnUtil.supportCharset(this.getType())) {
                comboBox.enable();
            } else {
                comboBox.disable();
                comboBox.clearSelection();
            }
        };
        this.typeProperty.addListener((observable, oldValue, newValue) -> func.run());
        func.run();
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取模式组件
     *
     * @return 模式组件
     */
    public DBParamModeComboBox getModeControl() {
        DBParamModeComboBox comboBox = new DBParamModeComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.mode = newValue);
        comboBox.selectFirstIfNull(this.mode);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    public boolean isReturnParam() {
        return StrUtil.isBlank(this.name) && StrUtil.isBlank(this.mode);
    }

    /**
     * 获取字段定义
     *
     * @return 字段定义
     */
    public String getDefinition() {
        String definition = "";
        if (StrUtil.isNotBlank(this.getName())) {
            definition += DBUtil.wrap(this.getName());
        }
        definition += " " + this.getType();
        definition += " (";
        if (DBColumnUtil.supportSize(this.getType()) && this.getSize() != null) {
            definition += this.getSize();
            if (DBColumnUtil.supportDigits(this.getType()) && this.getDigits() != null) {
                definition += "," + this.getDigits();
            }
        }
        if (DBColumnUtil.supportValue(this.getType()) && this.getValue() != null) {
            definition += this.getValue();
        }
        definition += ")";
        definition = definition.replaceFirst("\\(\\)", "");
        // 字符集、排序
        if (DBColumnUtil.supportCharset(this.getType())) {
            if (StrUtil.isNotBlank(this.getCharset())) {
                definition += " CHARSET " + this.getCharset();
            }
            if (StrUtil.isNotBlank(this.getCollation())) {
                definition += " COLLATE " + this.getCollation();
            }
        }
        return definition;
    }

    public void setDtdIdentifier(String dtdIdentifier) {
        if (!dtdIdentifier.contains("(") && !dtdIdentifier.contains(" ")) {
            this.setType(dtdIdentifier.toUpperCase());
        } else if (!dtdIdentifier.contains("(")) {
            this.setType(dtdIdentifier.toUpperCase());
        } else {
            String type = dtdIdentifier.substring(0, dtdIdentifier.indexOf("("));
            this.setType(type.toUpperCase());
            String sub1 = dtdIdentifier.substring(dtdIdentifier.indexOf("(") + 1, dtdIdentifier.lastIndexOf(")"));
            if (this.supportEnum()) {
                this.setValue(sub1);
            } else if (this.supportDigits() && sub1.contains(",")) {
                String[] arr = sub1.split(",");
                this.setSize(Integer.parseInt(arr[0]));
                this.setDigits(Integer.parseInt(arr[1]));
            } else {
                this.setSize(Integer.parseInt(sub1));
            }
        }
    }

    public boolean supportDigits() {
        return DBColumnUtil.supportDigits(this.getType());
    }

    public boolean supportEnum() {
        return DBColumnUtil.supportEnum(this.getType());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeProperty() {
        return typeProperty.get();
    }

    public StringProperty typePropertyProperty() {
        return typeProperty;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getDigits() {
        return digits;
    }

    public void setDigits(Integer digits) {
        this.digits = digits;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCharsetProperty() {
        return charsetProperty.get();
    }

    public StringProperty charsetPropertyProperty() {
        return charsetProperty;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }
}

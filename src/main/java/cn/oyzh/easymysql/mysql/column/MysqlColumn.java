package cn.oyzh.easymysql.mysql.column;

import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.BooleanUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.util.DBColumnUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * db字段
 *
 * @author oyzh
 * @since 2023/12/20
 */
public class MysqlColumn extends DBObjectStatus implements ObjectCopier<MysqlColumn> {

    /**
     * 库名称
     */
    private String dbName;

    /**
     * 模式名称
     */
    private String schema;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 字段大小
     */
    private Integer size;

    /**
     * 字段类型
     */
    private final StringProperty typeProperty = new SimpleStringProperty();

    /**
     * 字段值
     */
    private String value;

    /**
     * 注释
     */
    private String comment;

    /**
     * 可为null
     */
    private Boolean nullable;

    /**
     * 无符号
     */
    private Boolean unsigned;

    /**
     * 填充零
     */
    private Boolean zeroFill;

    /**
     * 根据当前时间戳更新
     */
    private Boolean updateOnCurrentTimestamp;

    /**
     * 字段位置
     */
    private Integer position;

    /**
     * 主键属性
     */
    private SimpleBooleanProperty primaryKeyProperty;

    /**
     * 键长度
     */
    private Integer primaryKeySize;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 小数位
     */
    private Integer digits;

    /**
     * 自动递增
     */
    private Boolean autoIncrement;

    /**
     * 名称
     */
    private String name;

    /**
     * 字段字符集
     */
    private String charset;

    /**
     * 字段排序规则
     */
    private String collation;

    /**
     * 构建字段
     */
    public MysqlColumn() {

    }

    /**
     * 构建字段
     *
     * @param name 字段名称
     */
    public MysqlColumn(String name) {
        this.name = name;
    }

    /**
     * 判断名称是否变更
     *
     * @return 是否变更
     */
    public boolean isNameChanged() {
        return super.checkOriginalData("name", this.name);
    }

    /**
     * 获取原始名称
     *
     * @return 原始名称
     */
    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    /**
     * 设置字段类型
     *
     * @param type 字段类型
     */
    public void setType(String type) {
        if (type != null) {
            type = type.toUpperCase();
        }
        this.typeProperty.set(type);
        super.putOriginalData("type", type);
    }

    /**
     * 获取值列表
     *
     * @return 值列表
     */
    public List<String> getValueList() {
        List<String> valueList = new ArrayList<>();
        if (this.getValue() != null) {
            List<String> list = StringUtil.split(this.getValue(), ",");
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
     * 设置默认值
     *
     * @param defaultValue 默认值
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        super.putOriginalData("defaultValue", defaultValue);
    }

    /**
     * 获取默认值字符串
     *
     * @return 默认值字符串
     */
    public String getDefaultValueString() {
        Object defaultValue = this.defaultValue;
        return defaultValue == null ? null : defaultValue.toString();
    }

    /**
     * 设置是否自动递增
     *
     * @param autoIncrement 是否自动递增
     */
    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        super.putOriginalData("autoIncrement", autoIncrement);
        // // 如果是自动递增，则清除默认值
        // if (BooleanUtil.isTrue(autoIncrement)) {
        //     this.setDefaultValue(null);
        // }
    }

    /**
     * 是否自动递增
     *
     * @return 是否自动递增
     */
    public boolean isAutoIncrement() {
        return BooleanUtil.isTrue(this.autoIncrement);
    }

    /**
     * 是否存在注释
     *
     * @return 是否存在注释
     */
    public boolean hasComment() {
        return this.getComment() != null;
    }

    /**
     * 设置字段字符集
     *
     * @param charset 字段字符集
     */
    public void setCharset(String charset) {
        this.charset = charset;
        super.putOriginalData("charset", charset);
    }

    /**
     * 设置字段排序规则
     *
     * @param collation 字段排序规则
     */
    public void setCollation(String collation) {
        this.collation = collation;
        super.putOriginalData("collation", collation);
    }

    /**
     * 设置字段值
     *
     * @param value 字段值
     */
    public void setValue(String value) {
        this.value = value;
        super.putOriginalData("value", value);
    }

    /**
     * 设置是否无符号
     *
     * @param unsigned 是否无符号
     */
    public void setUnsigned(Boolean unsigned) {
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

    /**
     * 设置是否根据当前时间戳更新
     *
     * @param updateOnCurrentTimestamp 是否根据当前时间戳更新
     */
    public void setUpdateOnCurrentTimestamp(Boolean updateOnCurrentTimestamp) {
        this.updateOnCurrentTimestamp = updateOnCurrentTimestamp;
        super.putOriginalData("updateOnCurrentTimestamp", updateOnCurrentTimestamp);
    }

    /**
     * 是否根据当前时间戳更新
     *
     * @return 是否根据当前时间戳更新
     */
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
     * 是否支持几何类型
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
     * 是否支持值（枚举、集合类型）
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
     * 是否支持位类型
     *
     * @return 结果
     */
    public boolean supportBit() {
        return DBColumnUtil.supportBit(this.getType());
    }

    /**
     * 是否支持JSON类型
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

    /**
     * 是否支持字符串类型
     *
     * @return 是否支持
     */
    public boolean supportString() {
        return DBColumnUtil.supportString(this.getType());
    }

    /**
     * 获取最小值
     *
     * @return 最小值
     */
    public Long minValue() {
        return DBColumnUtil.minValue(this.getType());
    }

    /**
     * 获取最大值
     *
     * @return 最大值
     */
    public Long maxValue() {
        return DBColumnUtil.maxValue(this.getType());
    }

    /**
     * 获取示例值
     *
     * @return 示例值
     */
    public Object exampleValue() {
        return DBColumnUtil.exampleValue(this.getType());
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

    /**
     * 设置注释
     *
     * @param comment 注释
     */
    public void setComment(String comment) {
        this.comment = comment;
        super.putOriginalData("comment", comment);
    }

    /**
     * 设置字段大小
     *
     * @param size 字段大小
     */
    public void setSize(Integer size) {
        this.size = size;
        super.putOriginalData("size", size);
    }

    /**
     * 设置小数位
     *
     * @param digits 小数位
     */
    public void setDigits(Integer digits) {
        this.digits = digits;
        super.putOriginalData("digits", digits);
    }

    /**
     * 设置是否可为null
     *
     * @param nullable 是否可为null
     */
    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
        super.putOriginalData("nullable", nullable);
    }

    /**
     * 获取主键属性
     *
     * @return 主键属性
     */
    public SimpleBooleanProperty primaryKeyProperty() {
        if (this.primaryKeyProperty == null) {
            this.primaryKeyProperty = new SimpleBooleanProperty();
        }
        return this.primaryKeyProperty;
    }

    /**
     * 是否主键
     *
     * @return 是否主键
     */
    public boolean isPrimaryKey() {
        return this.primaryKeyProperty != null && this.primaryKeyProperty.get();
    }

    /**
     * 设置是否主键
     *
     * @param primaryKey 是否主键
     */
    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKeyProperty().set(primaryKey);
        super.putOriginalData("primaryKey", primaryKey);
    }

    /**
     * 判断字段是否变更
     *
     * @return 是否变更
     */
    public boolean isColumnChanged() {
        for (Map.Entry<String, Object> entry : super.originalData().entrySet()) {
            if (!StringUtil.equalsAny(entry.getKey(), "primaryKey", "primaryKeySize")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断主键是否变更
     *
     * @return 是否变更
     */
    public boolean isPrimaryKeyChanged() {
        boolean checked1 = super.checkOriginalData("primaryKey", this.isPrimaryKey());
        if (checked1) {
            return true;
        }
        boolean checked2 = super.checkOriginalData("primaryKeySize", this.getPrimaryKeySize());
        if (checked2) {
            return true;
        }
        if (this.isCreated() && this.isPrimaryKey()) {
            return true;
        }
        if (this.isNameChanged() && this.isPrimaryKey()) {
            return true;
        }
        if (this.isDeleted() && this.isPrimaryKey()) {
            return true;
        }
        return this.isDeleted();
    }

    /**
     * 设置是否填充零
     *
     * @param zeroFill 是否填充零
     */
    public void setZeroFill(Boolean zeroFill) {
        this.zeroFill = zeroFill;
        super.putOriginalData("zeroFill", zeroFill);
    }

    /**
     * 是否填充零
     *
     * @return 是否填充零
     */
    public boolean isZeroFill() {
        return BooleanUtil.isTrue(this.zeroFill);
    }

    /**
     * 设置键长度
     *
     * @param primaryKeySize 键长度
     */
    public void setPrimaryKeySize(Integer primaryKeySize) {
        this.primaryKeySize = primaryKeySize;
        super.putOriginalData("primaryKeySize", primaryKeySize);
        // if (this.primaryKey != null) {
        //     this.primaryKey.setPrimaryKeySize(primaryKeySize);
        // }
    }

    /**
     * 是否可为null
     *
     * @return 是否可为null
     */
    public boolean isNullable() {
        return BooleanUtil.isTrue(this.nullable);
    }

    // @Override
    // public void setDeleted(boolean deleted) {
    //     super.setDeleted(deleted);
    //     // if (deleted && this.isPrimaryKey()) {
    //     //     this.setPrimaryKey(false);
    //     // }
    // }

    /**
     * 是否年份类型
     *
     * @return 是否年份类型
     */
    public boolean isYearType() {
        return DBColumnUtil.isYearType(this.getType());
    }

    /**
     * 是否日期类型
     *
     * @return 是否日期类型
     */
    public boolean isDateType() {
        return DBColumnUtil.isDateType(this.getType());
    }

    /**
     * 是否几何类型
     *
     * @return 是否几何类型
     */
    public boolean isGeometryType() {
        return DBColumnUtil.isGeometryType(this.getType());
    }

    /**
     * 是否时间类型
     *
     * @return 是否时间类型
     */
    public boolean isTimeType() {
        return DBColumnUtil.isTimeType(this.getType());
    }

    /**
     * 是否支持二进制
     *
     * @return 是否支持
     */
    public boolean supportBinary() {
        return DBColumnUtil.supportBinary(this.getType());
    }

    /**
     * 是否支持枚举
     *
     * @return 是否支持
     */
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

    /**
     * 解析键信息
     *
     * @param key 键信息
     */
    public void parseKey(String key) {
        if (StringUtil.isEmpty(key)) {
            return;
        }
        if ("pri".equalsIgnoreCase(key)) {
            // this.primaryKey = new MysqlPrimaryKey();
            // this.primaryKey.setPrimaryKey(true);
            this.setPrimaryKey(true);
        } else {
            this.setPrimaryKey(false);
        }
    }

    /**
     * 解析字段类型
     *
     * @param type 字段类型
     */
    public void parseType(String type) {
        if (!type.contains("(") && !type.contains(" ")) {
            this.setType(type);
            return;
        }
        type = type.toLowerCase();
        if (type.contains("unsigned")) {
            this.setUnsigned(true);
            type = type.replace("unsigned", "").trim();
        }
        if (type.contains("zerofill")) {
            this.setZeroFill(true);
            type = type.replace("zerofill", "").trim();
        }
        if (!type.contains("(")) {
            this.setType(type);
            return;
        }

        String _type = type.substring(0, type.indexOf("("));
        this.setType(_type);
        String sub1 = type.substring(type.indexOf("(") + 1, type.lastIndexOf(")"));
        // 枚举
        if (this.supportEnum()) {
            this.setValue(sub1);
        } else if (this.supportDigits() && sub1.contains(",")) {// 小数
            String[] arr = sub1.split(",");
            this.setSize(Integer.parseInt(arr[0]));
            this.setDigits(Integer.parseInt(arr[1]));
        } else {// 整数
            this.setSize(Integer.parseInt(sub1));
        }
    }

    /**
     * 解析额外信息
     *
     * @param extra 额外信息
     */
    public void parseExtra(String extra) {
        if (StringUtil.isEmpty(extra)) {
            return;
        }
        if (StringUtil.containsIgnoreCase(extra, "auto_increment")) {
            this.setAutoIncrement(true);
        }
        if (StringUtil.containsIgnoreCase(extra, "on update CURRENT_TIMESTAMP")) {
            this.setUpdateOnCurrentTimestamp(true);
        }
    }

    /**
     * 解析排序规则
     *
     * @param collation 排序规则
     */
    public void parseCollation(String collation) {
        if (StringUtil.isEmpty(collation)) {
            return;
        }
        this.setCollation(collation);
        this.setCharset(collation.substring(0, collation.indexOf("_")));
    }

    /**
     * 初始化字段信息
     *
     * @param columnType  字段类型
     * @param columnExtra 额外信息
     */
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
            if (StringUtil.containsIgnoreCase(columnType, "unsigned")) {
                this.setUnsigned(true);
            }
            if (StringUtil.containsIgnoreCase(columnType, "zerofill")) {
                this.setZeroFill(true);
            }
        }
        if (StringUtil.containsIgnoreCase(columnExtra, "auto_increment")) {
            this.setAutoIncrement(true);
        }
        if (StringUtil.containsIgnoreCase(columnExtra, "on update CURRENT_TIMESTAMP")) {
            this.setUpdateOnCurrentTimestamp(true);
        }
    }

    /**
     * 是否存在默认值
     *
     * @return 是否存在默认值
     */
    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }

    @Override
    public void copy(MysqlColumn column) {
        if (column != null) {
            this.setSize(column.size);
            this.setName(column.name);
            this.setType(column.getType());
            this.setValue(column.value);
            this.setDbName(column.dbName);
            this.setDigits(column.digits);
            this.setComment(column.comment);
            this.setCharset(column.charset);
            this.setNullable(column.nullable);
            this.setUnsigned(column.unsigned);
            this.setZeroFill(column.zeroFill);
            this.setTableName(column.tableName);
            this.setCollation(column.collation);
            this.setDefaultValue(column.defaultValue);
            this.setPrimaryKey(column.isPrimaryKey());
            this.setAutoIncrement(column.autoIncrement);
            this.setPrimaryKeySize(column.primaryKeySize);
            this.setUpdateOnCurrentTimestamp(column.updateOnCurrentTimestamp);
        }
    }

    /**
     * 是否无效字段（名称或类型为空）
     *
     * @return 是否无效
     */
    public boolean isInvalid() {
        return StringUtil.isBlank(this.getName()) || StringUtil.isBlank(this.getType());
    }

    // public Integer getPrimaryKeySize() {
    //     return this.primaryKey == null ? null : this.primaryKey.getPrimaryKeySize();
    // }


    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置库名称
     *
     * @param dbName 库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取模式名称
     *
     * @return 模式名称
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置模式名称
     *
     * @param schema 模式名称
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名称
     *
     * @param tableName 表名称
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取字段大小
     *
     * @return 字段大小
     */
    public Integer getSize() {
        return size;
    }

    /**
     * 获取字段类型
     *
     * @return 字段类型
     */
    public String getType() {
        return typeProperty.get();
    }

    /**
     * 获取字段类型属性
     *
     * @return 字段类型属性
     */
    public StringProperty typeProperty() {
        return typeProperty;
    }

    /**
     * 获取字段值
     *
     * @return 字段值
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取注释
     *
     * @return 注释
     */
    public String getComment() {
        return comment;
    }

    /**
     * 获取是否可为null
     *
     * @return 是否可为null
     */
    public Boolean getNullable() {
        return nullable;
    }

    /**
     * 获取是否无符号
     *
     * @return 是否无符号
     */
    public Boolean getUnsigned() {
        return unsigned;
    }

    /**
     * 获取是否填充零
     *
     * @return 是否填充零
     */
    public Boolean getZeroFill() {
        return zeroFill;
    }

    /**
     * 获取是否根据当前时间戳更新
     *
     * @return 是否根据当前时间戳更新
     */
    public Boolean getUpdateOnCurrentTimestamp() {
        return updateOnCurrentTimestamp;
    }

    /**
     * 获取字段位置
     *
     * @return 字段位置
     */
    public Integer getPosition() {
        return position == null ? 0 : position;
    }

    /**
     * 设置字段位置
     *
     * @param position 字段位置
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * 是否主键属性
     *
     * @return 是否主键属性
     */
    public boolean isPrimaryKeyProperty() {
        return primaryKeyProperty.get();
    }

    /**
     * 获取主键属性
     *
     * @return 主键属性
     */
    public SimpleBooleanProperty primaryKeyPropertyProperty() {
        return primaryKeyProperty;
    }

    /**
     * 设置主键属性
     *
     * @param primaryKeyProperty 主键属性
     */
    public void setPrimaryKeyProperty(boolean primaryKeyProperty) {
        this.primaryKeyProperty.set(primaryKeyProperty);
    }

    /**
     * 获取键长度
     *
     * @return 键长度
     */
    public Integer getPrimaryKeySize() {
        return primaryKeySize;
    }

    /**
     * 获取默认值
     *
     * @return 默认值
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * 获取小数位
     *
     * @return 小数位
     */
    public Integer getDigits() {
        return digits;
    }

    /**
     * 获取是否自动递增
     *
     * @return 是否自动递增
     */
    public Boolean getAutoIncrement() {
        return autoIncrement;
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
     * 获取字段字符集
     *
     * @return 字段字符集
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 获取字段排序规则
     *
     * @return 字段排序规则
     */
    public String getCollation() {
        return collation;
    }
}

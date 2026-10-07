package cn.oyzh.easymysql.util;


import cn.oyzh.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库字段工具类
 *
 * @author oyzh
 * @since 2024/1/29
 */
public class DBColumnUtil {

    /**
     * 字段定义集合
     */
    private static final List<DBColumnField> COLUMN_FIELD = new ArrayList<>();

    static {

        DBColumnField charFiled = new DBColumnField("CHAR");
        charFiled.suggestSize = 255;
        charFiled.supportSize = true;
        charFiled.supportString = true;
        charFiled.supportKeySize = true;
        charFiled.supportCharset = true;
        charFiled.supportDefaultValue = true;

        DBColumnField varcharField = new DBColumnField("VARCHAR");
        varcharField.suggestSize = 255;
        varcharField.supportSize = true;
        varcharField.supportString = true;
        varcharField.supportCharset = true;
        varcharField.supportKeySize = true;
        varcharField.supportDefaultValue = true;

        DBColumnField intField = new DBColumnField("INT");
        intField.suggestSize = 11;
        intField.supportSize = true;
        intField.supportInteger = true;
        intField.supportUnsigned = true;
        intField.supportZeroFill = true;
        intField.supportDefaultValue = true;
        intField.supportAutoIncrement = true;

        DBColumnField bigintFiled = new DBColumnField("BIGINT");
        bigintFiled.suggestSize = 20;
        bigintFiled.supportSize = true;
        bigintFiled.supportInteger = true;
        bigintFiled.supportUnsigned = true;
        bigintFiled.supportZeroFill = true;
        bigintFiled.supportDefaultValue = true;
        bigintFiled.supportAutoIncrement = true;

        DBColumnField mediumintField = new DBColumnField("MEDIUMINT");
        mediumintField.suggestSize = 10;
        mediumintField.supportSize = true;
        mediumintField.supportInteger = true;
        mediumintField.supportUnsigned = true;
        mediumintField.supportZeroFill = true;
        mediumintField.supportDefaultValue = true;
        mediumintField.supportAutoIncrement = true;

        DBColumnField tinyintField = new DBColumnField("TINYINT");
        tinyintField.suggestSize = 4;
        tinyintField.supportSize = true;
        tinyintField.supportInteger = true;
        tinyintField.supportUnsigned = true;
        tinyintField.supportZeroFill = true;
        tinyintField.supportDefaultValue = true;
        tinyintField.supportAutoIncrement = true;

        DBColumnField smallintFiled = new DBColumnField("SMALLINT");
        smallintFiled.suggestSize = 6;
        smallintFiled.supportSize = true;
        smallintFiled.supportInteger = true;
        smallintFiled.supportUnsigned = true;
        smallintFiled.supportZeroFill = true;
        smallintFiled.supportDefaultValue = true;
        smallintFiled.supportAutoIncrement = true;

        DBColumnField integerField = new DBColumnField("INTEGER");
        integerField.suggestSize = 11;
        integerField.supportSize = true;
        integerField.supportInteger = true;
        integerField.supportZeroFill = true;
        integerField.supportDefaultValue = true;
        integerField.supportAutoIncrement = true;

        DBColumnField datetimeField = new DBColumnField("DATETIME");
        datetimeField.suggestSize = 6;
        datetimeField.supportTimestamp = true;
        datetimeField.supportDefaultValue = true;

        DBColumnField timestampField = new DBColumnField("TIMESTAMP");
        timestampField.suggestSize = 6;
        timestampField.supportTimestamp = true;
        timestampField.supportDefaultValue = true;

        DBColumnField dateField = new DBColumnField("DATE");
        dateField.supportDefaultValue = true;

        DBColumnField yearField = new DBColumnField("YEAR");
        yearField.suggestSize = 4;
        yearField.supportDefaultValue = true;

        DBColumnField timeField = new DBColumnField("TIME");
        timeField.suggestSize = 6;
        timeField.supportDefaultValue = true;

        DBColumnField textField = new DBColumnField("TEXT");
        textField.supportCharset = true;
        textField.supportString = true;
        textField.supportKeySize = true;

        DBColumnField mediumtextField = new DBColumnField("MEDIUMTEXT");
        mediumtextField.supportString = true;
        mediumtextField.supportKeySize = true;
        mediumtextField.supportCharset = true;

        DBColumnField longtextField = new DBColumnField("LONGTEXT");
        longtextField.supportString = true;
        longtextField.supportKeySize = true;
        longtextField.supportCharset = true;

        DBColumnField tinytextFiled = new DBColumnField("TINYTEXT");
        tinytextFiled.supportString = true;
        tinytextFiled.supportKeySize = true;
        tinytextFiled.supportCharset = true;

        DBColumnField floatField = new DBColumnField("FLOAT");
        floatField.suggestSize = 11;
        floatField.supportSize = true;
        floatField.supportDigits = true;
        floatField.supportUnsigned = true;
        floatField.supportZeroFill = true;
        floatField.supportDefaultValue = true;
        floatField.supportAutoIncrement = true;

        DBColumnField doubleField = new DBColumnField("DOUBLE");
        doubleField.supportSize = true;
        doubleField.suggestSize = 20;
        doubleField.supportDigits = true;
        doubleField.supportUnsigned = true;
        doubleField.supportZeroFill = true;
        doubleField.supportDefaultValue = true;
        doubleField.supportAutoIncrement = true;

        DBColumnField decimalField = new DBColumnField("DECIMAL");
        decimalField.suggestSize = 20;
        decimalField.supportSize = true;
        decimalField.supportDigits = true;
        decimalField.supportZeroFill = true;
        decimalField.supportUnsigned = true;
        decimalField.supportDefaultValue = true;
        decimalField.supportAutoIncrement = true;

        DBColumnField bitFiled = new DBColumnField("BIT");
        bitFiled.minValue = 0L;
        bitFiled.maxValue = 1L;
        bitFiled.suggestSize = 1;
        bitFiled.supportBit = true;
        bitFiled.supportSize = true;

        DBColumnField jsonField = new DBColumnField("JSON");
        jsonField.supportSize = true;
        jsonField.supportJson = true;

        DBColumnField enumField = new DBColumnField("ENUM");
        enumField.supportEnum = true;
        enumField.supportValue = true;
        enumField.supportDefaultValue = true;

        DBColumnField setField = new DBColumnField("SET");
        setField.supportEnum = true;
        setField.supportValue = true;
        setField.supportDefaultValue = true;

        DBColumnField binaryField = new DBColumnField("BINARY");
        binaryField.suggestSize = 255;
        binaryField.supportBinary = true;
        binaryField.supportDefaultValue = true;

        DBColumnField varbinaryField = new DBColumnField("VARBINARY");
        varbinaryField.suggestSize = 65535;
        varbinaryField.supportBinary = true;
        varbinaryField.supportDefaultValue = true;

        DBColumnField blobField = new DBColumnField("BLOB");
        blobField.supportBinary = true;
        blobField.supportDefaultValue = true;

        DBColumnField longblobField = new DBColumnField("LONGBLOB");
        longblobField.supportBinary = true;
        longblobField.supportDefaultValue = true;

        DBColumnField tinyblobField = new DBColumnField("TINYBLOB");
        tinyblobField.supportBinary = true;
        tinyblobField.supportDefaultValue = true;

        DBColumnField mediumblobField = new DBColumnField("MEDIUMBLOB");
        mediumblobField.supportBinary = true;
        mediumblobField.supportDefaultValue = true;

        DBColumnField geometryField = new DBColumnField("GEOMETRY");
        geometryField.exampleValue = "POINT(0 0)";
        geometryField.supportGeometry = true;

        DBColumnField pointField = new DBColumnField("POINT");
        pointField.exampleValue = "POINT(0 0)";
        pointField.supportGeometry = true;

        DBColumnField multipointField = new DBColumnField("MULTIPOINT");
        multipointField.exampleValue = "MULTIPOINT((0 0), (1 1))";
        multipointField.supportGeometry = true;

        DBColumnField polygonField = new DBColumnField("POLYGON");
        polygonField.exampleValue = "POLYGON((0 0,5 0,5 5,0 5,0 0))";
        polygonField.supportGeometry = true;

        DBColumnField multipolygonField = new DBColumnField("MULTIPOLYGON");
        multipolygonField.exampleValue = "MULTIPOLYGON(((0 0,5 0,5 5,0 5,0 0)), ((0 0,10 0,10 10,0 10,0 0)))";
        multipolygonField.supportGeometry = true;

        DBColumnField linestringField = new DBColumnField("LINESTRING");
        linestringField.exampleValue = "LINESTRING(0 0,1 1,2 2)";
        linestringField.supportGeometry = true;

        DBColumnField multilinestringField = new DBColumnField("MULTILINESTRING");
        multilinestringField.exampleValue = "MULTILINESTRING((0 0,1 1,2 2), (3 3,4 4,5 5))";
        multilinestringField.supportGeometry = true;

        DBColumnField geometrycollectionField = new DBColumnField("GEOMETRYCOLLECTION");
        geometrycollectionField.exampleValue = "GEOMETRYCOLLECTION(POINT(0 0),LINESTRING(0 0,1 1,2 2),POLYGON((5 5, 6 5, 6 6, 5 6, 5 5)))";
        geometrycollectionField.supportGeometry = true;

        putFiled(charFiled);
        putFiled(varcharField);

        putFiled(intField);
        putFiled(bigintFiled);
        putFiled(tinyintField);
        putFiled(smallintFiled);
        putFiled(mediumintField);
        putFiled(integerField);

        putFiled(floatField);
        putFiled(doubleField);
        putFiled(decimalField);

        putFiled(datetimeField);
        putFiled(timestampField);
        putFiled(dateField);
        putFiled(yearField);
        putFiled(timeField);

        putFiled(textField);
        putFiled(longtextField);
        putFiled(tinytextFiled);
        putFiled(mediumtextField);

        putFiled(bitFiled);
        putFiled(jsonField);

        putFiled(enumField);
        putFiled(setField);

        putFiled(binaryField);
        putFiled(varbinaryField);
        putFiled(blobField);
        putFiled(longblobField);
        putFiled(mediumblobField);
        putFiled(tinyblobField);

        putFiled(geometryField);
        putFiled(pointField);
        putFiled(multipointField);
        putFiled(polygonField);
        putFiled(multipolygonField);
        putFiled(linestringField);
        putFiled(multilinestringField);
        putFiled(geometrycollectionField);
    }

    /**
     * 添加字段定义
     *
     * @param columnField 字段定义
     */
    private static void putFiled(DBColumnField columnField) {
        COLUMN_FIELD.add(columnField);
    }

    /**
     * 获取字段名称列表
     *
     * @return 字段名称列表
     */
    public static List<String> fields() {
        return COLUMN_FIELD.parallelStream().map(DBColumnField::getName).collect(Collectors.toList());
    }

    /**
     * 字段定义
     */
    private static class DBColumnField {

        /**
         * 名称
         */
        private String name;

        /**
         * 最大值
         */
        private Long maxValue;

        /**
         * 最小值
         */
        private Long minValue;

        /**
         * 推荐字段长
         */
        private Integer suggestSize;

        /**
         * 是否支持bit
         */
        private boolean supportBit;

        /**
         * 示例值
         */
        private String exampleValue;

        /**
         * 是否支持长度
         */
        private boolean supportSize;

        /**
         * 是否支持json
         */
        private boolean supportJson;

        /**
         * 是否支持枚举
         */
        private boolean supportEnum;

        /**
         * 是否支持值列表
         */
        private boolean supportValue;

        /**
         * 是否支持二进制
         */
        private boolean supportBinary;

        /**
         * 是否支持小数位
         */
        private boolean supportDigits;

        /**
         * 是否支持字符串
         */
        private boolean supportString;

        /**
         * 是否支持键长
         */
        private boolean supportKeySize;

        /**
         * 是否支持整数
         */
        private boolean supportInteger;

        /**
         * 是否支持字符集
         */
        private boolean supportCharset;

        /**
         * 是否支持无符号
         */
        private boolean supportUnsigned;

        /**
         * 是否支持补零
         */
        private boolean supportZeroFill;

        /**
         * 是否支持几何类型
         */
        private boolean supportGeometry;

        /**
         * 是否支持时间戳
         */
        private boolean supportTimestamp;

        /**
         * 是否支持默认值
         */
        private boolean supportDefaultValue;

        /**
         * 是否支持自增
         */
        private boolean supportAutoIncrement;

        /**
         * 构造字段定义
         *
         * @param name 名称
         */
        public DBColumnField(String name) {
            this.name = name;
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
         * 设置名称
         *
         * @param name 名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取最大值
         *
         * @return 最大值
         */
        public Long getMaxValue() {
            return maxValue;
        }

        /**
         * 设置最大值
         *
         * @param maxValue 最大值
         */
        public void setMaxValue(Long maxValue) {
            this.maxValue = maxValue;
        }

        /**
         * 获取最小值
         *
         * @return 最小值
         */
        public Long getMinValue() {
            return minValue;
        }

        /**
         * 设置最小值
         *
         * @param minValue 最小值
         */
        public void setMinValue(Long minValue) {
            this.minValue = minValue;
        }

        /**
         * 获取推荐字段长
         *
         * @return 推荐字段长
         */
        public Integer getSuggestSize() {
            return suggestSize;
        }

        /**
         * 设置推荐字段长
         *
         * @param suggestSize 推荐字段长
         */
        public void setSuggestSize(Integer suggestSize) {
            this.suggestSize = suggestSize;
        }

        /**
         * 是否支持bit
         *
         * @return 结果
         */
        public boolean isSupportBit() {
            return supportBit;
        }

        /**
         * 设置是否支持bit
         *
         * @param supportBit 是否支持bit
         */
        public void setSupportBit(boolean supportBit) {
            this.supportBit = supportBit;
        }

        /**
         * 获取示例值
         *
         * @return 示例值
         */
        public String getExampleValue() {
            return exampleValue;
        }

        /**
         * 设置示例值
         *
         * @param exampleValue 示例值
         */
        public void setExampleValue(String exampleValue) {
            this.exampleValue = exampleValue;
        }

        /**
         * 是否支持长度
         *
         * @return 结果
         */
        public boolean isSupportSize() {
            return supportSize;
        }

        /**
         * 设置是否支持长度
         *
         * @param supportSize 是否支持长度
         */
        public void setSupportSize(boolean supportSize) {
            this.supportSize = supportSize;
        }

        /**
         * 是否支持json
         *
         * @return 结果
         */
        public boolean isSupportJson() {
            return supportJson;
        }

        /**
         * 设置是否支持json
         *
         * @param supportJson 是否支持json
         */
        public void setSupportJson(boolean supportJson) {
            this.supportJson = supportJson;
        }

        /**
         * 是否支持枚举
         *
         * @return 结果
         */
        public boolean isSupportEnum() {
            return supportEnum;
        }

        /**
         * 设置是否支持枚举
         *
         * @param supportEnum 是否支持枚举
         */
        public void setSupportEnum(boolean supportEnum) {
            this.supportEnum = supportEnum;
        }

        /**
         * 是否支持值列表
         *
         * @return 结果
         */
        public boolean isSupportValue() {
            return supportValue;
        }

        /**
         * 设置是否支持值列表
         *
         * @param supportValue 是否支持值列表
         */
        public void setSupportValue(boolean supportValue) {
            this.supportValue = supportValue;
        }

        /**
         * 是否支持二进制
         *
         * @return 结果
         */
        public boolean isSupportBinary() {
            return supportBinary;
        }

        /**
         * 设置是否支持二进制
         *
         * @param supportBinary 是否支持二进制
         */
        public void setSupportBinary(boolean supportBinary) {
            this.supportBinary = supportBinary;
        }

        /**
         * 是否支持小数位
         *
         * @return 结果
         */
        public boolean isSupportDigits() {
            return supportDigits;
        }

        /**
         * 设置是否支持小数位
         *
         * @param supportDigits 是否支持小数位
         */
        public void setSupportDigits(boolean supportDigits) {
            this.supportDigits = supportDigits;
        }

        /**
         * 是否支持字符串
         *
         * @return 结果
         */
        public boolean isSupportString() {
            return supportString;
        }

        /**
         * 设置是否支持字符串
         *
         * @param supportString 是否支持字符串
         */
        public void setSupportString(boolean supportString) {
            this.supportString = supportString;
        }

        /**
         * 是否支持键长
         *
         * @return 结果
         */
        public boolean isSupportKeySize() {
            return supportKeySize;
        }

        /**
         * 设置是否支持键长
         *
         * @param supportKeySize 是否支持键长
         */
        public void setSupportKeySize(boolean supportKeySize) {
            this.supportKeySize = supportKeySize;
        }

        /**
         * 是否支持整数
         *
         * @return 结果
         */
        public boolean isSupportInteger() {
            return supportInteger;
        }

        /**
         * 设置是否支持整数
         *
         * @param supportInteger 是否支持整数
         */
        public void setSupportInteger(boolean supportInteger) {
            this.supportInteger = supportInteger;
        }

        /**
         * 是否支持字符集
         *
         * @return 结果
         */
        public boolean isSupportCharset() {
            return supportCharset;
        }

        /**
         * 设置是否支持字符集
         *
         * @param supportCharset 是否支持字符集
         */
        public void setSupportCharset(boolean supportCharset) {
            this.supportCharset = supportCharset;
        }

        /**
         * 是否支持无符号
         *
         * @return 结果
         */
        public boolean isSupportUnsigned() {
            return supportUnsigned;
        }

        /**
         * 设置是否支持无符号
         *
         * @param supportUnsigned 是否支持无符号
         */
        public void setSupportUnsigned(boolean supportUnsigned) {
            this.supportUnsigned = supportUnsigned;
        }

        /**
         * 是否支持补零
         *
         * @return 结果
         */
        public boolean isSupportZeroFill() {
            return supportZeroFill;
        }

        /**
         * 设置是否支持补零
         *
         * @param supportZeroFill 是否支持补零
         */
        public void setSupportZeroFill(boolean supportZeroFill) {
            this.supportZeroFill = supportZeroFill;
        }

        /**
         * 是否支持几何类型
         *
         * @return 结果
         */
        public boolean isSupportGeometry() {
            return supportGeometry;
        }

        /**
         * 设置是否支持几何类型
         *
         * @param supportGeometry 是否支持几何类型
         */
        public void setSupportGeometry(boolean supportGeometry) {
            this.supportGeometry = supportGeometry;
        }

        /**
         * 是否支持时间戳
         *
         * @return 结果
         */
        public boolean isSupportTimestamp() {
            return supportTimestamp;
        }

        /**
         * 设置是否支持时间戳
         *
         * @param supportTimestamp 是否支持时间戳
         */
        public void setSupportTimestamp(boolean supportTimestamp) {
            this.supportTimestamp = supportTimestamp;
        }

        /**
         * 是否支持默认值
         *
         * @return 结果
         */
        public boolean isSupportDefaultValue() {
            return supportDefaultValue;
        }

        /**
         * 设置是否支持默认值
         *
         * @param supportDefaultValue 是否支持默认值
         */
        public void setSupportDefaultValue(boolean supportDefaultValue) {
            this.supportDefaultValue = supportDefaultValue;
        }

        /**
         * 是否支持自增
         *
         * @return 结果
         */
        public boolean isSupportAutoIncrement() {
            return supportAutoIncrement;
        }

        /**
         * 设置是否支持自增
         *
         * @param supportAutoIncrement 是否支持自增
         */
        public void setSupportAutoIncrement(boolean supportAutoIncrement) {
            this.supportAutoIncrement = supportAutoIncrement;
        }
    }

    /**
     * 是否支持长度
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportSize(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportSize;
            }
        }
        return false;
    }

    /**
     * 获取推荐字段长
     *
     * @param type 字段类型
     * @return 推荐字段长
     */
    public static Integer suggestSize(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.suggestSize;
            }
        }
        return null;
    }

    /**
     * 是否支持无符号
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportUnsigned(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportUnsigned;
            }
        }
        return false;
    }

    /**
     * 是否支持json
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportJson(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportJson;
            }
        }
        return false;
    }

    /**
     * 是否支持键长
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportKeySize(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportKeySize;
            }
        }
        return false;
    }

    /**
     * 是否支持字符串
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportString(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportString;
            }
        }
        return false;
    }

    /**
     * 是否支持值列表
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportValue(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportValue;
            }
        }
        return false;
    }

    /**
     * 是否支持补零
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportZeroFill(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportZeroFill;
            }
        }
        return false;
    }

    /**
     * 是否支持bit
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportBit(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportBit;
            }
        }
        return false;
    }

    /**
     * 是否支持二进制
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportBinary(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportBinary;
            }
        }
        return false;
    }

    /**
     * 是否支持小数位
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportDigits(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportDigits;
            }
        }
        return false;
    }

    /**
     * 是否支持默认值
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportDefaultValue(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportDefaultValue;
            }
        }
        return false;
    }

    /**
     * 是否支持几何类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportGeometry(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportGeometry;
            }
        }
        return false;
    }

    /**
     * 是否支持枚举
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportEnum(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportEnum;
            }
        }
        return false;
    }

    /**
     * 是否支持字符集
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportCharset(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportCharset;
            }
        }
        return false;
    }

    /**
     * 是否支持时间戳
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportTimestamp(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportTimestamp;
            }
        }
        return false;
    }

    /**
     * 是否支持整数
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportInteger(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportInteger;
            }
        }
        return false;
    }

    /**
     * 是否支持自增
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean supportAutoIncrement(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.supportAutoIncrement;
            }
        }
        return false;
    }

    /**
     * 获取示例值
     *
     * @param type 字段类型
     * @return 示例值
     */
    public static Object exampleValue(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.exampleValue;
            }
        }
        return false;
    }

    /**
     * 获取最小值
     *
     * @param type 字段类型
     * @return 最小值
     */
    public static Long minValue(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.minValue;
            }
        }
        return null;
    }

    /**
     * 获取最大值
     *
     * @param type 字段类型
     * @return 最大值
     */
    public static Long maxValue(String type) {
        for (DBColumnField value : COLUMN_FIELD) {
            if (StringUtil.equalsIgnoreCase(value.name, type)) {
                return value.maxValue;
            }
        }
        return null;
    }

    /**
     * 是否年份类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isYearType(String type) {
        return "YEAR".equalsIgnoreCase(type);
    }

    /**
     * 是否日期类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isDateType(String type) {
        return "DATE".equalsIgnoreCase(type);
    }

    /**
     * 是否时间类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isTimeType(String type) {
        return "TIME".equalsIgnoreCase(type);
    }

    /**
     * 是否多边形类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isPolygonType(String type) {
        return "POLYGON".equalsIgnoreCase(type);
    }

    /**
     * 是否多多边形类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isMultiPolygonType(String type) {
        return "MULTIPOLYGON".equalsIgnoreCase(type);
    }

    /**
     * 是否点类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isPointType(String type) {
        return "Point".equalsIgnoreCase(type);
    }

    /**
     * 是否多点类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isMultiPointType(String type) {
        return "MultiPoint".equalsIgnoreCase(type);
    }

    /**
     * 是否线类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isLineStringType(String type) {
        return "LineString".equalsIgnoreCase(type);
    }

    /**
     * 是否多线类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isMultiLineStringType(String type) {
        return "MultiLineString".equalsIgnoreCase(type);
    }

    /**
     * 是否几何集合类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isGeomCollectionType(String type) {
        return "GeomCollection".equalsIgnoreCase(type);
    }

    /**
     * 是否几何类型
     *
     * @param type 字段类型
     * @return 结果
     */
    public static boolean isGeometryType(String type) {
        return "Geometry".equalsIgnoreCase(type);
    }

    /**
     * 获取默认值
     *
     * @param type 字段类型
     * @return 默认值
     */
    public static Object defaultValue(String type) {
        if (supportDefaultValue(type)) {
            if (supportDigits(type)) {
                return 0.0;
            }
            if (supportInteger(type)) {
                return 0;
            }
            if (supportString(type)) {
                return "";
            }
            if (supportJson(type)) {
                return "{'a':1}";
            }
            if (supportBinary(type)) {
                return new byte[]{};
            }
        }
        return null;
    }
}

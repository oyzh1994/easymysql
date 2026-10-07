package cn.oyzh.easymysql.mysql.record;

import cn.oyzh.easymysql.mysql.column.MysqlColumn;

import java.util.Objects;

/**
 * MySQL记录主键
 *
 * @author oyzh
 * @since 2023/12/29
 */
public class MysqlRecordPrimaryKey {

    /**
     * 当前数据
     */
    private Object data;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段
     */
    private MysqlColumn column;

    /**
     * 自动递增的返回值
     */
    private Object returnData;

    /**
     * 编辑前的原始数据
     */
    private Object originalData;

    /**
     * 是否自动递增
     */
    private boolean autoIncrement;

    /**
     * 初始化主键
     *
     * @param column 主键字段
     * @param record 记录
     */
    public void init(MysqlColumn column, MysqlRecord record) {
        this.column = column;
        this.columnName = column.getName();
        this.autoIncrement = column.isAutoIncrement();
        this.data = record.getValue(this.columnName);
        this.originalData = record.getOriginal(this.columnName);
    }

    /**
     * 获取主键数据
     *
     * @return 主键数据
     */
    public Object data() {
        if (this.data != null) {
            return this.data;
        }
        return this.returnData;
    }

    /**
     * 获取原始主键数据
     *
     * @return 原始主键数据
     */
    public Object originalData() {
        if (this.originalData != null) {
            return this.originalData;
        }
        return this.data;
    }

    /**
     * 判断是否需要回填自动递增数据
     *
     * @return 是否需要回填
     */
    public boolean shouldReturnData() {
        return this.data == null && this.autoIncrement;
    }

    /**
     * 判断主键数据是否变更
     *
     * @return 是否变更
     */
    public boolean isChanged() {
        if (this.originalData == null) {
            return false;
        }
        return !Objects.equals(this.originalData, this.data);
    }

    /**
     * 获取当前数据
     *
     * @return 当前数据
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置当前数据
     *
     * @param data 当前数据
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 获取字段名称
     *
     * @return 字段名称
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 设置字段名称
     *
     * @param columnName 字段名称
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 获取自动递增的返回值
     *
     * @return 自动递增的返回值
     */
    public Object getReturnData() {
        return returnData;
    }

    /**
     * 设置自动递增的返回值
     *
     * @param returnData 自动递增的返回值
     */
    public void setReturnData(Object returnData) {
        this.returnData = returnData;
    }

    /**
     * 获取编辑前的原始数据
     *
     * @return 编辑前的原始数据
     */
    public Object getOriginalData() {
        return originalData;
    }

    /**
     * 设置编辑前的原始数据
     *
     * @param originalData 编辑前的原始数据
     */
    public void setOriginalData(Object originalData) {
        this.originalData = originalData;
    }

    /**
     * 是否自动递增
     *
     * @return 是否自动递增
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * 设置是否自动递增
     *
     * @param autoIncrement 是否自动递增
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * 获取主键字段
     *
     * @return 主键字段
     */
    public MysqlColumn getColumn() {
        return column;
    }
}

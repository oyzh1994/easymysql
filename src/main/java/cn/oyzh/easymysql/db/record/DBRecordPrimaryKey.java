package cn.oyzh.easymysql.db.record;

import cn.oyzh.easymysql.db.table.DBColumn;
import lombok.Data;

import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/12/29
 */
@Data
public class DBRecordPrimaryKey {

    /**
     * 当前数据
     */
    private Object data;

    /**
     * 字段名称
     */
    private String columnName;

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

    public void init(DBColumn column, DBRecord record) {
        this.columnName = column.getName();
        this.autoIncrement = column.isAutoIncrement();
        this.data = record.getValue(this.columnName);
        this.originalData = record.getOriginal(this.columnName);
    }

    public Object data() {
        if (this.data != null) {
            return this.data;
        }
        return this.returnData;
    }

    public Object originalData() {
        if (this.originalData != null) {
            return this.originalData;
        }
        return this.data;
    }

    public boolean shouldReturnData() {
        return this.data == null && this.autoIncrement;
    }

    public boolean isChanged() {
        if (this.originalData == null) {
            return false;
        }
        return !Objects.equals(this.originalData, this.data);
    }
}

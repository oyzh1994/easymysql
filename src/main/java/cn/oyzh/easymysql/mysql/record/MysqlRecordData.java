package cn.oyzh.easymysql.mysql.record;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MySQL记录数据
 *
 * @author oyzh
 * @since 2024/7/5
 */
public class MysqlRecordData {

    /**
     * 数据集合
     */
    private Map<MysqlColumn, Object> dataList;

    /**
     * 获取字段名称集合
     *
     * @return 字段名称集合
     */
    public Set<String> columns() {
        if (this.dataList == null) {
            return Collections.emptySet();
        }
        return this.dataList.keySet().stream().map(MysqlColumn::getName).collect(Collectors.toSet());
    }

    /**
     * 获取值非空的字段名称集合
     *
     * @return 值非空的字段名称集合
     */
    public Set<String> notNullColumns() {
        Set<String> columns = this.columns();
        return columns.parallelStream().filter(this::hasValue).collect(Collectors.toSet());
    }

    /**
     * 获取指定名称的字段
     *
     * @param column 字段名称
     * @return 字段
     */
    public MysqlColumn column(String column) {
        if (this.dataList != null) {
            for (MysqlColumn dbColumn : dataList.keySet()) {
                if (StringUtil.equalsAnyIgnoreCase(column, dbColumn.getName())) {
                    return dbColumn;
                }
            }
        }
        return null;
    }

    /**
     * 判断指定字段是否存在值
     *
     * @param column 字段名称
     * @return 是否存在值
     */
    public boolean hasValue(String column) {
        return this.value(column) != null;
    }

    /**
     * 获取指定字段的值
     *
     * @param column 字段名称
     * @return 字段值
     */
    public Object value(String column) {
        if (this.dataList != null) {
            for (Map.Entry<MysqlColumn, Object> entry : dataList.entrySet()) {
                if (StringUtil.equalsAnyIgnoreCase(column, entry.getKey().getName())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 添加数据
     *
     * @param column 字段
     * @param value  值
     */
    public void put(MysqlColumn column, Object value) {
        if (this.dataList == null) {
            this.dataList = new HashMap<>();
        }
        this.dataList.put(column, value);
    }

    /**
     * 判断数据是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(this.dataList);
    }

    /**
     * 判断指定字段是否为几何类型
     *
     * @param column 字段名称
     * @return 是否为几何类型
     */
    public boolean isTypeGeometry(String column) {
        if (CollectionUtil.isEmpty(this.dataList)) {
            return false;
        }
        MysqlColumn dbColumn = this.column(column);
        if (dbColumn == null) {
            return false;
        }
        return dbColumn.supportGeometry();
    }

    /**
     * 移除指定字段的数据
     *
     * @param column 字段名称
     */
    public void remove(String column) {
        if (this.dataList != null) {
            MysqlColumn dbColumn = this.column(column);
            if (dbColumn != null) {
                this.dataList.remove(dbColumn);
            }
        }
    }

    /**
     * 获取数据条目集合
     *
     * @return 数据条目集合
     */
    public Set<Map.Entry<MysqlColumn, Object>> entries() {
        if (this.dataList == null) {
            return Collections.emptySet();
        }
        return this.dataList.entrySet();
    }

    /**
     * 获取全部值集合
     *
     * @return 值集合
     */
    public Collection<Object> values() {
        if (this.dataList == null) {
            return Collections.emptyList();
        }
        return this.dataList.values();
    }

    /**
     * 获取字段数量
     *
     * @return 字段数量
     */
    public int columnSize() {
        if (this.dataList == null) {
            return 0;
        }
        return this.dataList.size();
    }

    /**
     * 判断指定字段是否非空
     *
     * @param column 字段名称
     * @return 是否非空
     */
    public boolean notNull(String column) {
        return this.value(column) != null;
    }
}

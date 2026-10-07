package cn.oyzh.easymysql.mysql.column;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.db.DBObjectList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * db字段列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlColumns extends DBObjectList<MysqlColumn> {

    /**
     * 构建字段列表
     */
    public MysqlColumns() {

    }

    /**
     * 构建字段列表
     *
     * @param list 字段列表
     */
    public MysqlColumns(List<MysqlColumn> list) {
        super.addAll(list);
    }

    /**
     * 获取主键字段列表
     *
     * @return 主键字段列表
     */
    public List<MysqlColumn> primaryKeys() {
        List<MysqlColumn> list1 = new ArrayList<>();
        for (MysqlColumn column : this) {
            if (column.isPrimaryKey() && !DBObjectList.isDeleted(column)) {
                list1.add(column);
            }
        }
        return list1.parallelStream().filter(MysqlColumn::isPrimaryKey).sorted((o1, o2) -> {
            if (o1.isAutoIncrement() && !o2.isAutoIncrement()) {
                return -1;
            }
            if (o1.isAutoIncrement() && o2.isAutoIncrement()) {
                return 0;
            }
            return 1;
        }).collect(Collectors.toList());
    }

    /**
     * 判断主键是否变更
     *
     * @return 是否变更
     */
    public boolean primaryKeyChanged() {
        return false;
    }

    /**
     * 获取指定名称的字段
     *
     * @param name 字段名称
     * @return 字段
     */
    public MysqlColumn column(String name) {
        if (!this.isEmpty()) {
            for (MysqlColumn dbColumn : this) {
                if (StringUtil.equalsAnyIgnoreCase(dbColumn.getName(), name)) {
                    return dbColumn;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定名称字段的索引
     *
     * @param name 字段名称
     * @return 索引
     */
    public int index(String name) {
        int index = 0;
        for (MysqlColumn dbColumn : this) {
            if (dbColumn.getName().equals(name)) {
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * 按位置排序
     *
     * @return 排序后的字段列表
     */
    public List<MysqlColumn> sortOfPosition() {
        return this.parallelStream()
                .sorted(Comparator.comparing(MysqlColumn::getPosition))
                .collect(Collectors.toList());
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        for (MysqlColumn dbColumn : this) {
            return dbColumn.getTableName();
        }
        return null;
    }

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String dbName() {
        for (MysqlColumn dbColumn : this) {
            return dbColumn.getDbName();
        }
        return null;
    }

    /**
     * 获取字段名称列表
     *
     * @return 字段名称列表
     */
    public List<String> columnNames() {
        List<String> list = new ArrayList<>();
        for (MysqlColumn dbColumn : this) {
            list.add(dbColumn.getName());
        }
        return list;
    }

    /**
     * 是否存在主键
     *
     * @return 是否存在主键
     */
    public boolean hasPrimaryKey() {
        for (MysqlColumn column : this) {
            if (column.isPrimaryKey() || column.isAutoIncrement()) {
                return true;
            }
        }
        return false;
    }
}

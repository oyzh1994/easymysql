package cn.oyzh.easymysql.db.table;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * db外键列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class DBColumns extends DBObjectList<DBColumn> {

    public DBColumns() {

    }

    public DBColumns(List<DBColumn> list) {
        super.addAll(list);
    }

    public List<DBColumn> primaryKeys() {
        List<DBColumn> list1 = new ArrayList<>();
        for (DBColumn column : this) {
            if (column.isPrimaryKey() && !DBObjectList.isDeleted(column)) {
                list1.add(column);
            }
        }
        return list1.parallelStream().filter(DBColumn::isPrimaryKey).sorted((o1, o2) -> {
            if (o1.isAutoIncrement() && !o2.isAutoIncrement()) {
                return -1;
            }
            if (o1.isAutoIncrement() && o2.isAutoIncrement()) {
                return 0;
            }
            return 1;
        }).collect(Collectors.toList());
    }

    public boolean primaryKeyChanged() {
        for (DBColumn column : this) {
            if (column.isPrimaryKeyChanged()) {
                return true;
            }
        }
        return false;
    }

    public DBColumn column(String name) {
        if (!this.isEmpty()) {
            for (DBColumn dbColumn : this) {
                if (StrUtil.equalsAnyIgnoreCase(dbColumn.getName(), name)) {
                    return dbColumn;
                }
            }
        }
        return null;
    }

    public int index(String name) {
        int index = 0;
        for (DBColumn dbColumn : this) {
            if (dbColumn.getName().equals(name)) {
                break;
            }
            index++;
        }
        return index;
    }

    public List<DBColumn> sortOfPosition() {
        return this.parallelStream()
                .sorted(Comparator.comparing(DBColumn::getPosition))
                .collect(Collectors.toList());
    }

    public String getTableName() {
        for (DBColumn dbColumn : this) {
            return dbColumn.getTableName();
        }
        return null;
    }

    public List<String> columnNames() {
        List<String> list = new ArrayList<>();
        for (DBColumn dbColumn : this) {
            list.add(dbColumn.getName());
        }
        return list;
    }
}

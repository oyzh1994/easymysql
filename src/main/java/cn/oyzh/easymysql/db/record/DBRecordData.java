package cn.oyzh.easymysql.db.record;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.table.DBColumn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2024/7/5
 */
public class DBRecordData {

    private Map<DBColumn, Object> dataList;

    public Set<String> columns() {
        if (this.dataList == null) {
            return Collections.emptySet();
        }
        return this.dataList.keySet().stream().map(DBColumn::getName).collect(Collectors.toSet());
    }

    public DBColumn column(String column) {
        if (this.dataList != null) {
            for (DBColumn dbColumn : dataList.keySet()) {
                if (StrUtil.equalsAnyIgnoreCase(column, dbColumn.getName())) {
                    return dbColumn;
                }
            }
        }
        return null;
    }

    public Object value(String column) {
        if (this.dataList != null) {
            for (Map.Entry<DBColumn, Object> entry : dataList.entrySet()) {
                if (StrUtil.equalsAnyIgnoreCase(column, entry.getKey().getName())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public void put(DBColumn column, Object value) {
        if (this.dataList == null) {
            this.dataList = new HashMap<>();
        }
        this.dataList.put(column, value);
    }

    public boolean isEmpty() {
        return CollUtil.isEmpty(this.dataList);
    }

    public boolean isTypeGeometry(String column) {
        if (CollUtil.isEmpty(this.dataList)) {
            return false;
        }
        DBColumn dbColumn = this.column(column);
        if (dbColumn == null) {
            return false;
        }
        return dbColumn.supportGeometry();
    }

    public void remove(String column) {
        if (this.dataList != null) {
            DBColumn dbColumn = this.column(column);
            if (dbColumn != null) {
                this.dataList.remove(dbColumn);
            }
        }
    }

    public Set<Map.Entry<DBColumn, Object>> entries() {
        if (this.dataList == null) {
            return Collections.emptySet();
        }
        return this.dataList.entrySet();
    }

    public Collection<Object> values() {
        if (this.dataList == null) {
            return Collections.emptyList();
        }
        return this.dataList.values();
    }

    public int columnSize() {
        if (this.dataList == null) {
            return 0;
        }
        return this.dataList.size();
    }

    public boolean notNull(String column) {
        return this.value(column) != null;
    }
}

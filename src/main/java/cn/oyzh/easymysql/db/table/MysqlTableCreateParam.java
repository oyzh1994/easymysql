package cn.oyzh.easymysql.db.table;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.check.MysqlChecks;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.column.MysqlColumns;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.db.index.MysqlIndexes;
import cn.oyzh.easymysql.db.trigger.MysqlTriggers;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-14
 */
@Data
@Accessors(fluent = true, chain = true)
public class MysqlTableCreateParam {

    private MysqlTable table;

    private MysqlChecks checks;

    private MysqlColumns columns;

    private MysqlIndexes indexes;

    private MysqlTriggers triggers;

    private MysqlForeignKeys foreignKeys;

    public String dbName() {
        return this.table.getDbName();
    }

    public boolean hasColumns() {
        return CollUtil.isNotEmpty(this.columns);
    }

    public List<MysqlColumn> primaryKeys() {
        return this.columns.primaryKeys();
    }

    public boolean hasIndex() {
        return CollUtil.isNotEmpty(this.indexes);
    }

    public boolean hasForeignKey() {
        return CollUtil.isNotEmpty(this.foreignKeys);
    }

    public boolean hasCheck() {
        return CollUtil.isNotEmpty(this.checks);
    }

    public boolean hasTrigger() {
        return CollUtil.isNotEmpty(this.triggers);
    }

    public String tableName() {
        return this.table.getName();
    }
}

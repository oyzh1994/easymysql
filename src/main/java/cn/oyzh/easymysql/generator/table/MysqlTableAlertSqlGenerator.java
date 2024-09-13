package cn.oyzh.easymysql.generator.table;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.DBObjectList;
import cn.oyzh.easymysql.db.table.DBCheck;
import cn.oyzh.easymysql.db.table.DBChecks;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.easymysql.db.table.DBForeignKey;
import cn.oyzh.easymysql.db.table.DBForeignKeys;
import cn.oyzh.easymysql.db.table.DBIndex;
import cn.oyzh.easymysql.db.table.DBIndexes;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.db.table.DBTrigger;
import cn.oyzh.easymysql.db.table.DBTriggers;
import cn.oyzh.easymysql.util.DBUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class MysqlTableAlertSqlGenerator extends TableAlertSqlGenerator {

    public MysqlTableAlertSqlGenerator() {
        super(DBDialect.MSSQL);
    }

    protected MysqlTableAlertSqlGenerator(DBDialect dialect) {
        super(dialect);
    }

    @Override
    public String generate(DBTable table) {
        String dbName = table.getDbName();
        StringBuilder builder = new StringBuilder();
        if (table.hasForeignKey()) {
            this.foreignKeyHandle2(builder, table);
        }
        builder.append("ALTER TABLE ").append(DBUtil.wrap(dbName, table.getName())).append(" ");
        // 字段
        if (table.hasColumns()) {
            this.columnHandle(builder, table);
        }
        // 主键
        if (table.primaryKeyChanged()) {
            this.primaryKeyHandle(builder, table);
        }
        // 索引
        if (table.hasIndex()) {
            this.indexHandle(builder, table);
        }
        // 外键
        if (table.hasForeignKey()) {
            this.foreignKeyHandle1(builder, table);
        }
        // 检查
        if (table.hasCheck()) {
            this.checkHandle(builder, table);
        }
        // 表字符集
        if (table.hasCharset()) {
            builder.append(" CHARACTER SET = ").append(table.getCharset()).append(",");
        }
        // 表排序
        if (table.hasCollation()) {
            builder.append(" COLLATE = ").append(table.getCollation()).append(",");
        }
        // 表引擎
        if (table.hasEngine()) {
            builder.append(" ENGINE = ").append(table.getEngine()).append(",");
        }
        // 表注释
        if (table.hasComment()) {
            builder.append(" COMMENT = ").append(DBUtil.wrapData(table.getComment())).append(",");
        }
        // 行格式
        if (table.hasRowFormat()) {
            builder.append(" ROW_FORMAT = ").append(table.getRowFormat()).append(",");
        }
        // 表自动递增
        if (table.hasAutoIncrement()) {
            builder.append(" AUTO_INCREMENT = ").append(table.getAutoIncrement()).append(",");
        }
        builder.append(";");
        // 表触发器
        if (table.hasTrigger()) {
            this.triggerHandle(builder, table);
        }
        String sql = builder.toString();
        sql = sql.replaceAll(",\\)", ")");
        sql = sql.replaceAll(",;", ";");
        return sql;
    }

    protected void triggerHandle(StringBuilder builder, DBTable table) {
        DBTriggers triggers = table.getTriggers();
        for (DBTrigger trigger : triggers) {
            if (DBTriggers.isDeleted(trigger) || DBTriggers.isChanged(trigger)) {
                builder.append("DROP TRIGGER ").append(DBUtil.wrap(trigger.originalName())).append(";");
            }
            if (DBTriggers.isChanged(trigger) || DBTriggers.isCreated(trigger)) {
                builder.append("CREATE TRIGGER ")
                        .append(DBUtil.wrap(trigger.getName()))
                        .append(" ")
                        .append(trigger.getPolicy())
                        .append(" ON ")
                        .append(DBUtil.wrap(table.getName()))
                        .append(" FOR EACH ROW ")
                        .append(trigger.getDefinition())
                        .append(";");
            }
        }
    }

    protected void columnHandle(StringBuilder builder, DBTable table) {
        for (DBColumn column : table.columns()) {
            if (DBColumns.isChanged(column) || DBColumns.isCreated(column)) {
                if (column.isCreated()) {
                    builder.append(" ADD COLUMN ").append(DBUtil.wrap(column.getName()));
                } else if (column.isNameChanged()) {
                    builder.append(" CHANGE COLUMN ").append(DBUtil.wrap(column.originalName())).append(" ").append(DBUtil.wrap(column.getName()));
                } else {
                    builder.append(" MODIFY COLUMN ").append(DBUtil.wrap(column.getName()));
                }
                // 字段类型
                builder.append(" ").append(column.getType());

                // 字段长度
                if (column.supportSize() && column.getSize() != null) {
                    builder.append("(").append(column.getSize());
                    // 小数位
                    if (column.supportDigits() && column.getDigits() != null) {
                        builder.append(",").append(column.getDigits());
                    }
                    builder.append(")");
                } else if (column.supportValue() && column.getValue() != null) {// 值
                    builder.append("(").append(column.getValue()).append(")");
                }

                // 无符号
                if (column.supportUnsigned() && column.isUnsigned()) {
                    builder.append(" UNSIGNED ");
                }

                // 填充零
                if (column.supportZeroFill() && column.isZeroFill()) {
                    builder.append(" ZEROFILL ");
                }

                // 字符集及排序
                if (column.supportCharset()) {
                    if (column.getCharset() != null) {
                        builder.append(" CHARACTER SET ").append(column.getCharset());
                    }
                    if (column.getCollation() != null) {
                        builder.append(" COLLATE ").append(column.getCollation());
                    }
                }

                // 默认值
                if (column.supportDefaultValue() && column.getDefaultValue() != null) {
                    builder.append(" DEFAULT ").append(DBUtil.wrapData(column.getDefaultValueString()));
                }

                // 可为null
                if (column.isNullable()) {
                    builder.append(" NULL");
                } else {
                    builder.append(" NOT NULL");
                }

                // 根据时间戳更新
                if (column.supportTimestamp() && column.isUpdateOnCurrentTimestamp()) {
                    builder.append(" ON UPDATE CURRENT_TIMESTAMP(0)");
                }

                // 自动递增
                if (column.supportAutoIncrement() && column.isAutoIncrement()) {
                    builder.append(" AUTO_INCREMENT ");
                }

                // 注释
                if (column.hasComment()) {
                    builder.append(" COMMENT ").append("'").append(column.getComment()).append("'");
                }
                builder.append(",");
            } else if (DBColumns.isDeleted(column)) {
                builder.append(" DROP COLUMN ").append(DBUtil.wrap(column.getName())).append(",");
            }
        }
    }

    protected void primaryKeyHandle(StringBuilder builder, DBTable table) {
        if (table.isHasPrimaryKey()) {
            builder.append(" DROP PRIMARY KEY,");
        }
        List<DBColumn> keyList = table.primaryKeys();
        if (!keyList.isEmpty()) {
            builder.append(" ADD PRIMARY KEY (");
            for (DBColumn column : keyList) {
                builder.append(DBUtil.wrap(column.getName()));
                if (column.supportKeySize()) {
                    if (column.getPrimaryKeySize() != null) {
                        builder.append("(").append(column.getPrimaryKeySize()).append(")");
                    } else if (column.getSize() != null) {
                        builder.append("(").append(Math.min(column.getSize(), 100)).append(")");
                    } else {
                        builder.append("(").append(100).append(")");
                    }
                }
                builder.append(",");
            }
            builder.append(") USING BTREE,");
        }
    }

    protected void indexHandle(StringBuilder builder, DBTable table) {
        DBIndexes indexes = table.indexes();
        for (DBIndex index : indexes) {
            // 索引删除、变更
            if (DBIndexes.isDeleted(index) || DBIndexes.isChanged(index)) {
                builder.append("DROP INDEX ").append(DBUtil.wrap(index.originalName())).append(",");
            }
            // 索引新增、变更
            if (DBIndexes.isCreated(index) || DBIndexes.isChanged(index)) {
                // 新增索引
                builder.append(" ADD");
                // 类型名称
                if (index.typeName() != null) {
                    builder.append(" ").append(index.typeName());
                }
                builder.append(" INDEX ").append(DBUtil.wrap(index.getName()));
                builder.append(" (");
                for (DBIndex.IndexColumn column : index.getColumns()) {
                    builder.append(DBUtil.wrap(column.getColumnName()));
                    if (column.getSubPart() > 0) {
                        builder.append("(").append(column.getSubPart()).append(")");
                    }
                    builder.append(",");
                }
                builder.append(") ");
                // 方法名称
                if (index.methodName() != null) {
                    builder.append(" USING ").append(index.methodName());
                }
                if (index.getComment() != null) {
                    builder.append(" COMMENT ").append(DBUtil.wrapData(index.getName()));
                }
                // 拼接,
                builder.append(",");
            }
        }
    }

    protected void foreignKeyHandle1(StringBuilder builder, DBTable table) {
        DBForeignKeys foreignKeys = table.foreignKeys();
        if (!foreignKeys.hasCreated() && !foreignKeys.hasChanged()) {
            return;
        }
        for (DBForeignKey foreignKey : foreignKeys.filterList(DBObjectList.TYPE_CHANGED, DBObjectList.TYPE_CREATED)) {
            // 新增外键
            builder.append(" ADD CONSTRAINT ").append(DBUtil.wrap(foreignKey.getName()));
            builder.append(" FOREIGN KEY (");
            for (String column : foreignKey.getColumns()) {
                builder.append(DBUtil.wrap(column)).append(",");
            }
            builder.append(")");
            builder.append(" REFERENCES ").append(DBUtil.wrap(foreignKey.getPrimaryKeyDatabase(), foreignKey.getPrimaryKeyTable()));
            builder.append(" (");
            for (String column : foreignKey.getPrimaryKeyColumns()) {
                builder.append(DBUtil.wrap(column)).append(",");
            }
            builder.append(")");
            builder.append(" ON DELETE ").append(foreignKey.getDeletePolicy());
            builder.append(" ON UPDATE ").append(foreignKey.getUpdatePolicy());
            // 拼接,
            builder.append(",");
        }
    }

    protected void foreignKeyHandle2(StringBuilder builder, DBTable table) {
        DBForeignKeys foreignKeys = table.getForeignKeys();
        if (foreignKeys == null || foreignKeys.isEmpty()) {
            return;
        }
        if (!foreignKeys.hasChanged() && !foreignKeys.hasDeleted()) {
            return;
        }
        builder.append("ALTER TABLE ").append(DBUtil.wrap(table.getDbName(), table.getName())).append(" ");
        for (DBForeignKey foreignKey : foreignKeys.filterList(DBObjectList.TYPE_DELETED, DBObjectList.TYPE_CHANGED)) {
            String fkName = foreignKey.originalName();
            // 名称为null是临时数据
            if (StrUtil.isNotBlank(fkName)) {
                builder.append(" DROP FOREIGN KEY ").append(DBUtil.wrap(foreignKey.originalName())).append(",");
            }
        }
        builder.append(";");
    }

    protected void checkHandle(StringBuilder builder, DBTable table) {
        DBChecks checks = table.checks();
        for (DBCheck check : checks) {
            // 检查删除、变更
            if (DBChecks.isDeleted(check) || DBChecks.isChanged(check)) {
                builder.append("DROP CONSTRAINT ").append(DBUtil.wrap(check.originalName())).append(",");
            }
            // 检查新增、变更
            if (DBChecks.isCreated(check) || DBChecks.isChanged(check)) {
                builder.append(" ADD CONSTRAINT ")
                        .append(DBUtil.wrap(check.getName()))
                        .append(" CHECK (")
                        .append(check.getClause())
                        .append(")");
                // 拼接,
                builder.append(",");
            }
        }
    }
}

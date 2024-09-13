package cn.oyzh.easymysql.generator.table;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.table.DBCheck;
import cn.oyzh.easymysql.db.table.DBChecks;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBForeignKey;
import cn.oyzh.easymysql.db.table.DBForeignKeys;
import cn.oyzh.easymysql.db.table.DBIndex;
import cn.oyzh.easymysql.db.table.DBIndexes;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.db.table.DBTrigger;
import cn.oyzh.easymysql.util.DBUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class MysqlTableCreateSqlGenerator extends TableCreateSqlGenerator {

    public MysqlTableCreateSqlGenerator() {
        super(DBDialect.MSSQL);
    }

    protected MysqlTableCreateSqlGenerator(DBDialect dialect) {
        super(dialect);
    }

    @Override
    public String generate(DBTable table) {
        String dbName = table.getDbName();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ").append(DBUtil.wrap(dbName, table.getName())).append(" ( ");
        // 字段
        if (table.hasColumns()) {
            this.columnHandle(builder, table);
        }
        // 主键
        this.primaryKeyHandle(builder, table);
        // 索引
        if (table.hasIndex()) {
            this.indexHandle(builder, table);
        }
        // 外键
        if (table.hasForeignKey()) {
            this.foreignKeyHandle(builder, table);
        }
        // 检查
        if (table.hasCheck()) {
            this.checkHandle(builder, table);
        }
        builder.append(" )");
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
        sql = sql.replaceAll(", \\)", ")");
        sql = sql.replaceAll(",;", ";");
        return sql;
    }

    protected void triggerHandle(StringBuilder builder, DBTable table) {
        for (DBTrigger trigger : table.getTriggers()) {
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

    protected void columnHandle(StringBuilder builder, DBTable table) {
        for (DBColumn column : table.columns()) {
            builder.append(DBUtil.wrap(column.getName()));
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
                builder.append(" COMMENT ").append(DBUtil.wrapData(column.getComment()));
            }
            builder.append(",");
        }
    }

    protected void primaryKeyHandle(StringBuilder builder, DBTable table) {
        List<DBColumn> keyList = table.primaryKeys();
        if (!keyList.isEmpty()) {
            builder.append(" PRIMARY KEY (");
            for (DBColumn column : keyList) {
                builder.append(DBUtil.wrap(column.getName())).append(",");
            }
            builder.append("),");
        }
    }

    protected void indexHandle(StringBuilder builder, DBTable table) {
        DBIndexes indexes = table.indexes();
        for (DBIndex index : indexes) {
            // 新增索引
            builder.append(" ADD");
            if (index.isUnique()) {
                builder.append(" UNIQUE");
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
            builder.append(" USING ").append(index.getType());
            if (index.getComment() != null) {
                builder.append(" COMMENT ").append(DBUtil.wrapData(index.getName()));
            }
            // 拼接,
            builder.append(",");
        }
    }

    protected void foreignKeyHandle(StringBuilder builder, DBTable table) {
        DBForeignKeys foreignKeys = table.foreignKeys();
        for (DBForeignKey foreignKey : foreignKeys) {
            // 新增外键
            builder.append(" ADD CONSTRAINT ").append(DBUtil.wrap(foreignKey.originalName()));
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

    protected void checkHandle(StringBuilder builder, DBTable table) {
        DBChecks checks = table.checks();
        for (DBCheck check : checks) {
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

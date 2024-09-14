package cn.oyzh.easymysql.generator.table;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.db.check.MysqlCheck;
import cn.oyzh.easymysql.db.check.MysqlChecks;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKey;
import cn.oyzh.easymysql.db.foreignKey.MysqlForeignKeys;
import cn.oyzh.easymysql.db.index.MysqlIndex;
import cn.oyzh.easymysql.db.index.MysqlIndexes;
import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.db.table.MysqlTableCreateParam;
import cn.oyzh.easymysql.db.trigger.MysqlTrigger;
import cn.oyzh.easymysql.util.DBUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class MysqlTableCreateSqlGenerator {


    public String generate(MysqlTableCreateParam param) {
        String dbName = param.dbName();
        MysqlTable table = param.table();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ")
                .append(DBUtil.wrap(dbName, table.getName(), DBDialect.MYSQL))
                .append(" ( ");
        // 字段
        if (param.hasColumns()) {
            this.columnHandle(builder, param);
        }
        // 主键
        this.primaryKeyHandle(builder, param);
        // 索引
        if (param.hasIndex()) {
            this.indexHandle(builder, param);
        }
        // 外键
        if (param.hasForeignKey()) {
            this.foreignKeyHandle(builder, param);
        }
        // 检查
        if (param.hasCheck()) {
            this.checkHandle(builder, param);
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
        if (param.hasTrigger()) {
            this.triggerHandle(builder, param);
        }
        String sql = builder.toString();
        sql = sql.replaceAll(",\\)", ")");
        sql = sql.replaceAll(", \\)", ")");
        sql = sql.replaceAll(",;", ";");
        return sql;
    }

    protected void triggerHandle(StringBuilder builder, MysqlTableCreateParam param) {
        for (MysqlTrigger trigger : param.triggers()) {
            builder.append("CREATE TRIGGER ")
                    .append(DBUtil.wrap(trigger.getName(), DBDialect.MYSQL))
                    .append(" ")
                    .append(trigger.getPolicy())
                    .append(" ON ")
                    .append(DBUtil.wrap(param.tableName(), DBDialect.MYSQL))
                    .append(" FOR EACH ROW ")
                    .append(trigger.getDefinition())
                    .append(";");
        }
    }

    protected void columnHandle(StringBuilder builder, MysqlTableCreateParam param) {
        for (MysqlColumn column : param.columns()) {
            builder.append(DBUtil.wrap(column.getName(), DBDialect.MYSQL));
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

    protected void primaryKeyHandle(StringBuilder builder, MysqlTableCreateParam param) {
        List<MysqlColumn> keyList = param.primaryKeys();
        if (!keyList.isEmpty()) {
            builder.append(" PRIMARY KEY (");
            for (MysqlColumn column : keyList) {
                builder.append(DBUtil.wrap(column.getName(), DBDialect.MYSQL))
                        .append(",");
            }
            builder.append("),");
        }
    }

    protected void indexHandle(StringBuilder builder, MysqlTableCreateParam param) {
        MysqlIndexes indexes = param.indexes();
        for (MysqlIndex index : indexes) {
            // 新增索引
            builder.append(" ADD");
            if (index.isUnique()) {
                builder.append(" UNIQUE");
            }
            builder.append(" INDEX ")
                    .append(DBUtil.wrap(index.getName(), DBDialect.MYSQL))
                    .append(" (");
            for (MysqlIndex.IndexColumn column : index.getColumns()) {
                builder.append(DBUtil.wrap(column.getColumnName(), DBDialect.MYSQL));
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

    protected void foreignKeyHandle(StringBuilder builder, MysqlTableCreateParam param) {
        MysqlForeignKeys foreignKeys = param.foreignKeys();
        for (MysqlForeignKey foreignKey : foreignKeys) {
            // 新增外键
            builder.append(" ADD CONSTRAINT ")
                    .append(DBUtil.wrap(foreignKey.originalName(), DBDialect.MYSQL))
                    .append(" FOREIGN KEY (");
            for (String column : foreignKey.getColumns()) {
                builder.append(DBUtil.wrap(column, DBDialect.MYSQL)).append(",");
            }
            builder.append(")");
            builder.append(" REFERENCES ")
                    .append(DBUtil.wrap(foreignKey.getPrimaryKeyDatabase(), foreignKey.getPrimaryKeyTable(), DBDialect.MYSQL))
                    .append(" (");
            for (String column : foreignKey.getPrimaryKeyColumns()) {
                builder.append(DBUtil.wrap(column, DBDialect.MYSQL)).append(",");
            }
            builder.append(")")
                    .append(" ON DELETE ")
                    .append(foreignKey.getDeletePolicy())
                    .append(" ON UPDATE ")
                    .append(foreignKey.getUpdatePolicy());
            // 拼接,
            builder.append(",");
        }
    }

    protected void checkHandle(StringBuilder builder, MysqlTableCreateParam table) {
        MysqlChecks checks = table.checks();
        for (MysqlCheck check : checks) {
            builder.append(" ADD CONSTRAINT ")
                    .append(DBUtil.wrap(check.getName(), DBDialect.MYSQL))
                    .append(" CHECK (")
                    .append(check.getClause())
                    .append(")");
            // 拼接,
            builder.append(",");
        }
    }

    public static String generateSql(MysqlTableCreateParam param) {
        return new MysqlTableCreateSqlGenerator().generate(param);
    }
}

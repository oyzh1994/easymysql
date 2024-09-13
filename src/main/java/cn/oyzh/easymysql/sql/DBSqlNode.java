package cn.oyzh.easymysql.sql;// package cn.oyzh.easymysql.sql;
//
// import cn.hutool.core.util.StrUtil;
// import lombok.Getter;
// // import org.apache.calcite.sql.SqlDialect;
// // import org.apache.calcite.sql.SqlNode;
// // import org.apache.calcite.sql.SqlWriterConfig;
// // import org.apache.calcite.sql.util.SqlString;
// //
// // import java.util.function.UnaryOperator;
//
// /**
//  * sql元信息
//  *
//  * @author oyzh
//  * @since 2024/2/19
//  */
// public class DBSqlNode {
//
//     @Getter
//     private String sql;
//
//     @Getter
//     private DBSqlKind kind;
//
//     // @Getter
//     // private SqlNode node;
//
//     public DBSqlNode(String sql) {
//         this.sql = sql.trim();
//         if (this.sql.startsWith("\n")) {
//             this.sql = this.sql.substring(1);
//         }
//         if (this.sql.endsWith("\n")) {
//             this.sql = this.sql.substring(0, this.sql.length() - 2);
//         }
//         if (StrUtil.startWithIgnoreCase(this.sql, "SELECT")) {
//             this.kind = DBSqlKind.SELECT;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "DELETE")) {
//             this.kind = DBSqlKind.DELETE;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "INSERT")) {
//             this.kind = DBSqlKind.INSERT;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "UPDATE")) {
//             this.kind = DBSqlKind.UPDATE;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "SHOW")) {
//             this.kind = DBSqlKind.SHOW;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "ALTER TABLE")) {
//             this.kind = DBSqlKind.ALTER_TABLE;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "DROP TABLE")) {
//             this.kind = DBSqlKind.DROP_TABLE;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "CHANGE TABLE")) {
//             this.kind = DBSqlKind.CHANGE_TABLE;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "CREATE TABLE")) {
//             this.kind = DBSqlKind.CREATE_TABLE;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "TRUNCATE TABLE")) {
//             this.kind = DBSqlKind.TRUNCATE_TABLE;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "CREATE TABLE")) {
//             this.kind = DBSqlKind.CREATE_VIEW;
//         } else if (StrUtil.startWithIgnoreCase(this.sql, "EXPLAIN")) {
//             this.kind = DBSqlKind.EXPLAIN;
//         }
//     }
//
//     // public DBSqlNode(SqlNode node) {
//     //     this.node = node;
//     //     this.kind = DBSqlKind.valueOf(node.getKind());
//     // }
//
//     // public String getSql(DBDialect dialect) {
//     //     return this.getSql(dialect.toSqlDialect());
//     // }
//     //
//     // public String getSql(SqlDialect dialect) {
//     //     return this.getSqlSting(dialect).getSql();
//     // }
//
//     // public SqlString getSqlSting(DBDialect dialect) {
//     //     return this.getSqlSting(dialect.toSqlDialect());
//     // }
//
//     // public SqlString getSqlSting(SqlDialect dialect) {
//     //     UnaryOperator<SqlWriterConfig> operator = c -> c.withDialect(dialect)
//     //             .withIndentation(0)
//     //             .withWindowNewline(false)
//     //             .withClauseEndsLine(false)
//     //             .withClauseStartsLine(false)
//     //             .withValuesListNewline(false)
//     //             .withUpdateSetListNewline(false)
//     //             .withCaseClausesOnNewLines(false)
//     //             .withWindowDeclListNewline(false)
//     //             .withCaseClausesOnNewLines(false)
//     //             .withSelectListItemsOnSeparateLines(false);
//     //     return this.node.toSqlString(operator);
//     // }
//
//     public boolean isQueryMode() {
//         return this.kind == DBSqlKind.SELECT ||
//                 this.kind == DBSqlKind.SHOW ||
//                 this.kind == DBSqlKind.EXPLAIN;
//     }
//
//     public boolean isUpdateMode() {
//         return this.kind == DBSqlKind.UPDATE || this.kind == DBSqlKind.DELETE;
//     }
//
//     public boolean isDDLMode() {
//         return this.kind == DBSqlKind.ALTER_TABLE || this.kind == DBSqlKind.CHANGE_TABLE || this.kind == DBSqlKind.DROP_TABLE;
//     }
// }

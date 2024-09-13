package cn.oyzh.easymysql.sql;// package cn.oyzh.easymysql.sql;
//
// import cn.oyzh.easymysql.db.DBDialect;
// // import org.apache.calcite.config.Lex;
// // import org.apache.calcite.sql.SqlNode;
// // import org.apache.calcite.sql.SqlNodeList;
// // import org.apache.calcite.sql.parser.SqlParseException;
// // import org.apache.calcite.sql.parser.SqlParser;
// // import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
// // import org.apache.calcite.sql.validate.SqlConformanceEnum;
//
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
//
// /**
//  * @author oyzh
//  * @since 2024/2/26
//  */
// public class CalciteSqlParser extends DBSqlParser {
//
//     public CalciteSqlParser(String sqlContent, DBDialect dialect) {
//         super(sqlContent, dialect);
//     }
//
//     @Override
//     public DBSqlNodes parseNode()   {
//         // SqlParser parser = SqlParser.create(this.sqlContent, this.sqlConfig());
//         // SqlNodeList nodeList = parser.parseStmtList();
//         // DBSqlNodes nodes = new DBSqlNodes();
//         // for (SqlNode sqlNode : nodeList) {
//         //     nodes.add(sqlNode);
//         // }
//         // return nodes;
//
//         return new DBSqlNodes();
//     }
//
//     @Override
//     public List<String> parseSql()   {
//         DBSqlNodes nodes = this.parseNode();
//         List<String> list = new ArrayList<>();
//         for (DBSqlNode dbSqlNode : nodes.nodes()) {
//             list.add(dbSqlNode.getSql());
//             // list.add(dbSqlNode.getSql(this.dialect));
//         }
//         return list;
//     }
//
//     @Override
//     public String prettySql() throws Exception {
//         DBSqlNodes nodes = this.parseNode();
//         StringBuilder builder = new StringBuilder();
//         for (DBSqlNode dbSqlNode : nodes.nodes()) {
//             builder.append("\n").append(dbSqlNode.getSql()).append(";");
//             // builder.append("\n").append(dbSqlNode.getSql(this.dialect)).append(";");
//         }
//         return builder.toString().replaceFirst("\n", "");
//     }
//
//     // private SqlParser.Config sqlConfig() {
//     //     SqlParser.Config config = SqlParser.config();
//     //     if (this.dialect == DBDialect.MYSQL) {
//     //         config = config.withParserFactory(SqlDdlParserImpl.FACTORY);
//     //         config = config.withConformance(SqlConformanceEnum.MYSQL_5);
//     //         config = config.withLex(Lex.MYSQL);
//     //     }
//     //     return config;
//     // }
// }

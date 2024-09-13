package cn.oyzh.easymysql.sql;// package cn.oyzh.easymysql.sql;
//
// import cn.oyzh.easymysql.db.DBDialect;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * @author oyzh
//  * @since 2024/2/26
//  */
// public class SystemSqlParser extends DBSqlParser{
//
//     public SystemSqlParser(String sqlContent, DBDialect dialect) {
//         super(sqlContent, dialect);
//     }
//
//     @Override
//     public String removeComment() {
//         return "";
//     }
//
//     // @Override
//     // public DBSqlNodes parseNode() {
//     //     DBSqlNodes node = new DBSqlNodes();
//     //     List<String> sqlList = this.parseSql();
//     //     for (String sql : sqlList) {
//     //         node.add(sql);
//     //     }
//     //     return node;
//     // }
//
//     @Override
//     public List<String> parseSql() {
//         List<String> sqlList = new ArrayList<>();
//         char[] chars = this.sqlContent.toCharArray();
//         int index = 0;
//         int num1 = 0;
//         int num2 = 0;
//         int num3 = 0;
//         int startIndex = 0;
//         for (char aChar : chars) {
//             String ch1 = aChar + "";
//             switch (ch1) {
//                 case "'" -> num1++;
//                 case "\"" -> num2++;
//                 case "`" -> num3++;
//             }
//             // 判断符号是否成对出现
//             if (num1 % 2 == 0 && num2 % 2 == 0 && num3 % 2 == 0 && ch1.equals(";")) {
//                 String sql = this.sqlContent.substring(startIndex, index);
//                 sqlList.add(sql);
//                 startIndex = index + 1;
//             }
//             index++;
//         }
//         if(sqlList.isEmpty()){
//             sqlList.add(this.sqlContent);
//         }
//         return sqlList;
//     }
//
//     @Override
//     public String prettySql() throws Exception {
//         return this.sqlContent;
//     }
// }

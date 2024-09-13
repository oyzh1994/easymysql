package cn.oyzh.easymysql.sql;// package cn.oyzh.easymysql.sql;
//
// import cn.hutool.core.collection.CollUtil;
// // import org.apache.calcite.sql.SqlNode;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * sql元信息
//  *
//  * @author oyzh
//  * @since 2024/2/19
//  */
// public class DBSqlNodes {
//
//     private List<DBSqlNode> nodes;
//
//     public boolean isEmpty() {
//         return CollUtil.isEmpty(this.nodes);
//     }
//
//     public int size() {
//         return CollUtil.size(this.nodes);
//     }
//
//     public void add(String sqlContent) {
//         this.nodes().add(new DBSqlNode(sqlContent));
//     }
//
//     // public void add(SqlNode sqlNode) {
//     //     this.nodes().add(new DBSqlNode(sqlNode));
//     // }
//
//     public List<DBSqlNode> nodes(){
//         if (this.nodes == null) {
//             this.nodes = new ArrayList<>();
//         }
//         return this.nodes;
//     }
// }

// package cn.oyzh.easymysql.db.primaryKey;
//
// import cn.oyzh.easymysql.db.DBObjectStatus;
// import cn.oyzh.fx.common.util.ObjectCopier;
// import lombok.EqualsAndHashCode;
// import lombok.Getter;
//
// /**
//  * db表外键
//  *
//  * @author oyzh
//  * @since 2024/01/25
//  */
// @EqualsAndHashCode(callSuper = true)
// public class MysqlPrimaryKey extends DBObjectStatus implements ObjectCopier<MysqlPrimaryKey> {
//
//     /**
//      * 主键顺序
//      */
//     @Getter
//     private int position;
//
//     /**
//      * 是否主键
//      */
//     @Getter
//     private boolean primaryKey;
//
//     /**
//      * 键长度
//      */
//     @Getter
//     private Integer primaryKeySize;
//
//     public void setPosition(int position) {
//         this.position = position;
//         super.putOriginalData("position", position);
//     }
//
//     public void setPrimaryKey(Boolean primaryKey) {
//         this.primaryKey = primaryKey;
//         super.putOriginalData("primaryKey", primaryKey);
//     }
//
//     public void setPrimaryKeySize(Integer primaryKeySize) {
//         this.primaryKeySize = primaryKeySize;
//         super.putOriginalData("primaryKeySize", primaryKeySize);
//     }
//
//     @Override
//     public void copy(MysqlPrimaryKey t1) {
//         if (t1 != null) {
//             this.setPosition(t1.position);
//             this.setPrimaryKey(t1.primaryKey);
//             this.setPrimaryKeySize(t1.primaryKeySize);
//         }
//     }
//
//     public boolean isPrimaryKeyChanged() {
//         return super.checkOriginalData("primaryKey", this.isPrimaryKey()) || super.checkOriginalData("primarySize", this.getPrimaryKeySize());
//     }
// }

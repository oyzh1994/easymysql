package cn.oyzh.easymysql.db.table;// package cn.oyzh.easymysql.db.table;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.core.util.StrUtil;
// import cn.oyzh.easymysql.db.DBObjectStatus;
// import cn.oyzh.fx.common.util.ObjectComparator;
// import javafx.beans.property.SimpleStringProperty;
// import lombok.EqualsAndHashCode;
// import lombok.Getter;
// import lombok.Setter;
//
// import java.util.Collections;
// import java.util.List;
//
// /**
//  * db表
//  *
//  * @author oyzh
//  * @since 2024/01/16
//  */
// @EqualsAndHashCode(callSuper = true)
// public class DBTableSchema extends DBObjectStatus implements ObjectComparator<DBTableSchema> {
//
//     /**
//      * 库名称
//      */
//     @Setter
//     @Getter
//     private String dbName;
//
//     /**
//      * 表字段
//      */
//     @Setter
//     @Getter
//     protected DBColumns columns;
//
//     /**
//      * 表名称
//      */
//     private SimpleStringProperty nameProperty;
//
//     /**
//      * 表注释
//      */
//     private SimpleStringProperty commentProperty;
//
//     public SimpleStringProperty nameProperty() {
//         if (this.nameProperty == null) {
//             this.nameProperty = new SimpleStringProperty();
//         }
//         return this.nameProperty;
//     }
//
//     public void setName(String name) {
//         this.nameProperty().setValue(name);
//     }
//
//     public String getName() {
//         return this.nameProperty == null ? null : this.nameProperty.get();
//     }
//
//     public SimpleStringProperty commentProperty() {
//         if (this.commentProperty == null) {
//             this.commentProperty = new SimpleStringProperty();
//         }
//         return this.commentProperty;
//     }
//
//     public void setComment(String comment) {
//         this.commentProperty().setValue(comment);
//     }
//
//     public String getComment() {
//         return this.commentProperty == null ? null : this.commentProperty.get();
//     }
//
//     public boolean primaryKeyChanged() {
//         if (this.hasColumns()) {
//             boolean b1 = this.columns.primaryKeyChanged();
//             if (b1) {
//                 return true;
//             }
//             for (DBColumn column : this.columns.createdList()) {
//                 if (column.isPrimaryKey()) {
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }
//
//     public List<DBColumn> primaryKeys() {
//         if (this.hasColumns()) {
//             return this.columns.primaryKeys();
//         }
//         return Collections.emptyList();
//     }
//
//     public boolean hasPrimaryKey() {
//         return CollUtil.isNotEmpty(this.primaryKeys());
//     }
//
//     public boolean hasColumns() {
//         return this.columns != null && !this.columns.isEmpty();
//     }
//
//     public boolean hasComment() {
//         return this.getComment() != null;
//     }
//
//     public DBColumns columns() {
//         if (this.columns == null) {
//             this.columns = new DBColumns();
//         }
//         return this.columns;
//     }
//
//     @Override
//     public boolean compare(DBTableSchema tableSchema) {
//         if (tableSchema == null) {
//             return false;
//         }
//         if (tableSchema == this) {
//             return true;
//         }
//         if (!StrUtil.equals(this.getName(), tableSchema.getName())) {
//             return false;
//         }
//         return StrUtil.equals(this.getDbName(), tableSchema.getDbName());
//     }
//
//     public void removeColumn(DBColumn column) {
//         if (column != null && this.columns != null) {
//             this.columns().remove(column);
//         }
//     }
//
//     /**
//      * 是否新数据
//      *
//      * @return 结果
//      */
//
//     public boolean isNew() {
//         return StrUtil.isBlank(this.getName());
//     }
// }
//

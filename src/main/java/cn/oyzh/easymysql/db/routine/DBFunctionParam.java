package cn.oyzh.easymysql.db.routine;// package cn.oyzh.easymysql.db.routine;
//
// import cn.oyzh.easymysql.db.DBObjectStatus;
// import cn.oyzh.easymysql.fx.table.DBFiledTypeComboBox;
// import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
// import cn.oyzh.fx.plus.i18n.I18nHelper;
// import cn.oyzh.fx.plus.util.TableViewUtil;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
//
// /**
//  * @author oyzh
//  * @since 2024/07/01
//  */
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class DBFunctionParam extends DBObjectStatus {
//
//     /**
//      * 名称
//      */
//     private String name;
//
//     /**
//      * 类型
//      */
//     private String type;
//
//     /**
//      * 获取字段组件
//      *
//      * @return 字段组件
//      */
//     public ClearableTextField getNameControl() {
//         ClearableTextField textField = new ClearableTextField();
//         textField.setFlexWidth("100%");
//         textField.setPromptText(I18nHelper.pleaseInputContent());
//         textField.addTextChangeListener((observable, oldValue, newValue) -> this.name = newValue);
//         if (this.name != null) {
//             textField.setText(this.name);
//         }
//         TableViewUtil.selectRowOnMouseClicked(textField);
//         return textField;
//     }
//
//     /**
//      * 获取类型组件
//      *
//      * @return 类型组件
//      */
//     public DBFiledTypeComboBox getTypeControl() {
//         DBFiledTypeComboBox comboBox = new DBFiledTypeComboBox();
//         comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.type = newValue);
//         comboBox.selectFirstIfNull(this.type);
//         TableViewUtil.selectRowOnMouseClicked(comboBox);
//         return comboBox;
//     }
// }

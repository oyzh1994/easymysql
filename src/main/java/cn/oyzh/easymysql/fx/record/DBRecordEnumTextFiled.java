package cn.oyzh.easymysql.fx.record;//package cn.oyzh.easymysql.fx.record;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.oyzh.easymysql.controller.popup.DBRecordEnumController;
//import cn.oyzh.fx.plus.controls.textfield.ChooseTextField;
//import cn.oyzh.fx.plus.controls.view.FlexListView;
//import cn.oyzh.fx.plus.window.PopupManager;
//import cn.oyzh.fx.plus.window.PopupWrapper;
//import javafx.scene.control.CheckBox;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author oyzh
// * @since 2024/7/10
// */
//public class DBRecordEnumTextFiled extends ChooseTextField {
//
//    {
//        super.setChooseAction(this::initPopup);
//    }
//
//    private List<String> values;
//
//    private final List<String> allValues;
//
//    public DBRecordEnumTextFiled(List<String> allValues, Object object) {
//        this.allValues = allValues;
//        if (object instanceof List list) {
//            this.values = list;
//        } else if (object instanceof String string) {
//            this.values = StrUtil.split(string, ",");
//        } else {
//            this.values = new ArrayList<>();
//        }
//    }
//
//    private PopupWrapper popup;
//
//    protected void initPopup() {
//        this.popup = PopupManager.parsePopup(DBRecordEnumController.class);
//        this.popup.setProp("values", this.values);
//        this.popup.setProp("allValues", this.allValues);
//        this.popup.setProp("onSubmit", (Runnable) () -> {
//            FlexListView<CheckBox> listView = this.listView();
//            if (listView != null) {
//                this.values = new ArrayList<>();
//                for (CheckBox item : listView.getItems()) {
//                    if (item.isSelected()) {
//                        this.values.add(item.getText());
//                    }
//                }
//            }
//            this.initText();
//        });
//        this.popup.showPopup(this);
//    }
//
//    public void initText() {
//        if (CollUtil.isEmpty(this.values)) {
//            this.setText("");
//        } else {
//            StringBuilder builder = new StringBuilder();
//            for (String value : this.values) {
//                builder.append(",").append(value);
//            }
//            this.setText(builder.substring(1));
//        }
//    }
//
//    protected FlexListView<CheckBox> listView() {
//        if (this.popup != null) {
//            return (FlexListView<CheckBox>) this.popup.content().lookup("#listView");
//        }
//        return null;
//    }
//}

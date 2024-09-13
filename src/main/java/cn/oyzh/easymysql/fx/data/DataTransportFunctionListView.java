package cn.oyzh.easymysql.fx.data;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.routine.DBFunction;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.view.FlexListView;
import cn.oyzh.fx.plus.util.ListViewUtil;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportFunctionListView extends FlexListView<FXCheckBox> {

    @Setter
    private Runnable selectedChanged;

    public void of(List<DBFunction> functions) {
        List<DataTransportFunction> list = CollUtil.newArrayList();
        for (DBFunction function : functions) {
            DataTransportFunction obj = new DataTransportFunction();
            obj.setName(function.getName());
            list.add(obj);
        }
        this.init(list);
    }

    public void init(List<DataTransportFunction> functions) {
        this.clearItems();
        if (CollUtil.isNotEmpty(functions)) {
            for (DataTransportFunction function : functions) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(function.getName());
                checkBox.setSelected(function.isSelected());
                checkBox.setProp("data", function);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    function.setSelected(newValue);
                    if (this.selectedChanged != null) {
                        this.selectedChanged.run();
                    }
                });
                ListViewUtil.selectRowOnMouseClicked(checkBox);
                this.addItem(checkBox);
            }
        }
        if (this.selectedChanged != null) {
            this.selectedChanged.run();
        }
    }

    public List<DataTransportFunction> getSelectedFunctions() {
        List<DataTransportFunction> list = new ArrayList<>();
        for (FXCheckBox item : this.getItems()) {
            if (item.isSelected()) {
                list.add(item.getProp("data"));
            }
        }
        return list;
    }


    public int getSelectedSize() {
        int size = 0;
        for (FXCheckBox item : this.getItems()) {
            if (item.isSelected()) {
                size++;
            }
        }
        return size;
    }
}

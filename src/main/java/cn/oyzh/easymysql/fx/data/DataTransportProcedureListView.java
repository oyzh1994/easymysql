package cn.oyzh.easymysql.fx.data;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.routine.MysqlProcedure;
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
public class DataTransportProcedureListView extends FlexListView<FXCheckBox> {

    @Setter
    private Runnable selectedChanged;

    public void of(List<MysqlProcedure> procedures) {
        List<DataTransportProcedure> list = CollUtil.newArrayList();
        for (MysqlProcedure procedure : procedures) {
            DataTransportProcedure obj = new DataTransportProcedure();
            obj.setName(procedure.getName());
            list.add(obj);
        }
        this.init(list);
    }

    public void init(List<DataTransportProcedure> procedures) {
        this.clearItems();
        if (CollUtil.isNotEmpty(procedures)) {
            for (DataTransportProcedure procedure : procedures) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(procedure.getName());
                checkBox.setSelected(procedure.isSelected());
                checkBox.setProp("data", procedure);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    procedure.setSelected(newValue);
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

    public List<DataTransportProcedure> getSelectedProcedures() {
        List<DataTransportProcedure> list = new ArrayList<>();
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

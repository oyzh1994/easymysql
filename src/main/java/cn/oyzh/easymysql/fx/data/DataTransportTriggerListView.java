package cn.oyzh.easymysql.fx.data;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.trigger.MysqlTrigger;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportTriggerListView extends FXListView<FXCheckBox> {

    @Setter
    private Runnable selectedChanged;

    public void of(List<MysqlTrigger> triggers) {
        List<DataTransportTrigger> list = CollUtil.newArrayList();
        for (MysqlTrigger trigger : triggers) {
            DataTransportTrigger obj = new DataTransportTrigger();
            obj.setName(trigger.getName());
            list.add(obj);
        }
        this.init(list);
    }

    public void init(List<DataTransportTrigger> triggers) {
        this.clearItems();
        if (CollUtil.isNotEmpty(triggers)) {
            for (DataTransportTrigger trigger : triggers) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(trigger.getName());
                checkBox.setSelected(trigger.isSelected());
                checkBox.setProp("data", trigger);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    trigger.setSelected(newValue);
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

    public List<DataTransportTrigger> getSelectedTriggers() {
        List<DataTransportTrigger> list = new ArrayList<>();
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

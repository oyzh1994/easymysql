package cn.oyzh.easymysql.fx.data;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.view.MysqlView;
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
public class DataTransportViewListView extends FlexListView<FXCheckBox> {

    @Setter
    private Runnable selectedChanged;

    public void of(List<MysqlView> views) {
        List<DataTransportView> list = CollUtil.newArrayList();
        for (MysqlView view : views) {
            DataTransportView obj = new DataTransportView();
            obj.setName(view.getName());
            list.add(obj);
        }
        this.init(list);
    }

    public void init(List<DataTransportView> views) {
        this.clearItems();
        if (CollUtil.isNotEmpty(views)) {
            for (DataTransportView view : views) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(view.getName());
                checkBox.setSelected(view.isSelected());
                checkBox.setProp("data", view);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    view.setSelected(newValue);
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

    public List<DataTransportView> getSelectedViews() {
        List<DataTransportView> list = new ArrayList<>();
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

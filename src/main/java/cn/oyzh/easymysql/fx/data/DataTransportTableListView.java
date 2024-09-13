package cn.oyzh.easymysql.fx.data;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.table.MysqlTable;
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
public class DataTransportTableListView extends FlexListView<FXCheckBox> {

    @Setter
    private Runnable selectedChanged;

    public void of(List<MysqlTable> tables) {
        List<DataTransportTable> list = CollUtil.newArrayList();
        for (MysqlTable table : tables) {
            DataTransportTable obj = new DataTransportTable();
            obj.setName(table.getName());
            list.add(obj);
        }
        this.init(list);
    }

    public void init(List<DataTransportTable> tables) {
        this.clearItems();
        if (CollUtil.isNotEmpty(tables)) {
            for (DataTransportTable table : tables) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(table.getName());
                checkBox.setSelected(table.isSelected());
                checkBox.setProp("data", table);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    table.setSelected(newValue);
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

    public List<DataTransportTable> getSelectedTables() {
        List<DataTransportTable> list = new ArrayList<>();
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

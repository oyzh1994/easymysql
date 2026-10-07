package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.table.MysqlTable;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输表列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportTableListView extends FXListView<FXCheckBox> {

    /** 选中变化回调 */
    private Runnable selectedChanged;

    /**
     * 根据表列表构建
     *
     * @param tables 表列表
     */
    public void of(List<MysqlTable> tables) {
        List<DataTransportTable> list = CollectionUtil.newArrayList();
        for (MysqlTable table : tables) {
            DataTransportTable obj = new DataTransportTable();
            obj.setName(table.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 初始化表列表
     *
     * @param tables 表列表
     */
    public void init(List<DataTransportTable> tables) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(tables)) {
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

    /** 获取选中的表列表 */
    public List<DataTransportTable> getSelectedTables() {
        List<DataTransportTable> list = new ArrayList<>();
        for (FXCheckBox item : this.getItems()) {
            if (item.isSelected()) {
                list.add(item.getProp("data"));
            }
        }
        return list;
    }

    /** 获取选中数量 */
    public int getSelectedSize() {
        int size = 0;
        for (FXCheckBox item : this.getItems()) {
            if (item.isSelected()) {
                size++;
            }
        }
        return size;
    }

    /** 获取选中变化回调 */
    public Runnable getSelectedChanged() {
        return selectedChanged;
    }

    /** 设置选中变化回调 */
    public void setSelectedChanged(Runnable selectedChanged) {
        this.selectedChanged = selectedChanged;
    }
}

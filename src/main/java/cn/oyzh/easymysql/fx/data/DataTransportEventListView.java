package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.event.MysqlEvent;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输事件列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportEventListView extends FXListView<FXCheckBox> {

    /** 选中变化回调 */
    private Runnable selectedChanged;

    /**
     * 根据事件列表构建
     *
     * @param events 事件列表
     */
    public void of(List<MysqlEvent> events) {
        List<DataTransportEvent> list = CollectionUtil.newArrayList();
        for (MysqlEvent event : events) {
            DataTransportEvent obj = new DataTransportEvent();
            obj.setName(event.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 初始化事件列表
     *
     * @param events 事件列表
     */
    public void init(List<DataTransportEvent> events) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(events)) {
            for (DataTransportEvent event : events) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(event.getName());
                checkBox.setSelected(event.isSelected());
                checkBox.setProp("data", event);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    event.setSelected(newValue);
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

    /** 获取选中的事件列表 */
    public List<DataTransportEvent> getSelectedEvents() {
        List<DataTransportEvent> list = new ArrayList<>();
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

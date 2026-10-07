package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.trigger.MysqlTrigger;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输触发器列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportTriggerListView extends FXListView<FXCheckBox> {

    /** 选中变化回调 */
    private Runnable selectedChanged;

    /**
     * 根据触发器列表构建
     *
     * @param triggers 触发器列表
     */
    public void of(List<MysqlTrigger> triggers) {
        List<DataTransportTrigger> list = CollectionUtil.newArrayList();
        for (MysqlTrigger trigger : triggers) {
            DataTransportTrigger obj = new DataTransportTrigger();
            obj.setName(trigger.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 初始化触发器列表
     *
     * @param triggers 触发器列表
     */
    public void init(List<DataTransportTrigger> triggers) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(triggers)) {
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

    /** 获取选中的触发器列表 */
    public List<DataTransportTrigger> getSelectedTriggers() {
        List<DataTransportTrigger> list = new ArrayList<>();
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

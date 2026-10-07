package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.view.MysqlView;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输视图列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportViewListView extends FXListView<FXCheckBox> {

    /** 选中变化回调 */
    private Runnable selectedChanged;

    /**
     * 根据视图列表构建
     *
     * @param views 视图列表
     */
    public void of(List<MysqlView> views) {
        List<DataTransportView> list = CollectionUtil.newArrayList();
        for (MysqlView view : views) {
            DataTransportView obj = new DataTransportView();
            obj.setName(view.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 初始化视图列表
     *
     * @param views 视图列表
     */
    public void init(List<DataTransportView> views) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(views)) {
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

    /** 获取选中的视图列表 */
    public List<DataTransportView> getSelectedViews() {
        List<DataTransportView> list = new ArrayList<>();
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

package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.function.MysqlFunction;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输函数列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportFunctionListView extends FXListView<FXCheckBox> {

    /** 选中变化回调 */
    private Runnable selectedChanged;

    /** 获取选中变化回调 */
    public Runnable getSelectedChanged() {
        return selectedChanged;
    }

    /** 设置选中变化回调 */
    public void setSelectedChanged(Runnable selectedChanged) {
        this.selectedChanged = selectedChanged;
    }

    /**
     * 根据函数列表构建
     *
     * @param functions 函数列表
     */
    public void of(List<MysqlFunction> functions) {
        List<DataTransportFunction> list = CollectionUtil.newArrayList();
        for (MysqlFunction function : functions) {
            DataTransportFunction obj = new DataTransportFunction();
            obj.setName(function.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 初始化函数列表
     *
     * @param functions 函数列表
     */
    public void init(List<DataTransportFunction> functions) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(functions)) {
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

    /** 获取选中的函数列表 */
    public List<DataTransportFunction> getSelectedFunctions() {
        List<DataTransportFunction> list = new ArrayList<>();
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
}

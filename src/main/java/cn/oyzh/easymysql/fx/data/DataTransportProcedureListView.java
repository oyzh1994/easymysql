package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.procedure.MysqlProcedure;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输存储过程列表视图
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DataTransportProcedureListView extends FXListView<FXCheckBox> {

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
     * 根据存储过程列表构建
     *
     * @param procedures 存储过程列表
     */
    public void of(List<MysqlProcedure> procedures) {
        List<DataTransportProcedure> list = CollectionUtil.newArrayList();
        for (MysqlProcedure procedure : procedures) {
            DataTransportProcedure obj = new DataTransportProcedure();
            obj.setName(procedure.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 初始化存储过程列表
     *
     * @param procedures 存储过程列表
     */
    public void init(List<DataTransportProcedure> procedures) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(procedures)) {
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

    /** 获取选中的存储过程列表 */
    public List<DataTransportProcedure> getSelectedProcedures() {
        List<DataTransportProcedure> list = new ArrayList<>();
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

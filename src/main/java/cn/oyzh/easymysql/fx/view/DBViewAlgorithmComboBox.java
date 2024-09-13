package cn.oyzh.easymysql.fx.view;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * db视图算法下拉框
 *
 * @author oyzh
 * @since 2024/08/07
 */
public class DBViewAlgorithmComboBox extends FlexComboBox<String> {

    {
        this.addItem("UNDEFINED");
        this.addItem("MERGE");
        this.addItem("TEMPTABLE");
    }

    @Override
    public void select(String obj) {
        if (obj != null) {
            super.select(obj.toUpperCase());
        }
    }
}

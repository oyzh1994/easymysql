package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * 引擎下拉选择框
 *
 * @author oyzh
 * @since 2024/01/26
 */
public class DBEngineComboBox extends FlexComboBox<String> {

    public void init(DBClient client) {
        this.clearItems();
        for (String engine : client.engines()) {
            this.addItem(engine.toUpperCase());
        }
    }

    @Override
    public void select(String engine) {
        if (engine != null) {
            super.select(engine.toUpperCase());
        }
    }

    public boolean isInnoDB() {
        return "innoDB".equalsIgnoreCase(this.getSelectedItem());
    }
}

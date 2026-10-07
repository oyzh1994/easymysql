package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 引擎下拉选择框
 *
 * @author oyzh
 * @since 2024/01/26
 */
public class MysqlEngineComboBox extends FXComboBox<String> {

    /**
     * 初始化引擎列表
     *
     * @param client mysql客户端
     */
    public void init(MysqlClient client) {
        this.clearItems();
        for (String engine : client.engines()) {
            this.addItem(engine.toUpperCase());
        }
    }

    @Override
    public void select(String engine) {
        if (engine != null) {
            super.select(engine.toUpperCase());
        } else {
            super.clearSelection();
        }
    }

    /**
     * 是否为InnoDB引擎
     *
     * @return 结果
     */
    public boolean isInnoDB() {
        return "innoDB".equalsIgnoreCase(this.getSelectedItem());
    }
}

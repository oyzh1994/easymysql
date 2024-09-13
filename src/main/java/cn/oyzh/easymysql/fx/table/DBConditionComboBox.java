package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.condition.MysqlCondition;
import cn.oyzh.easymysql.condition.MysqlConditionUtil;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class DBConditionComboBox extends FlexComboBox<MysqlCondition> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MysqlCondition o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        this.addItem(MysqlConditionUtil.conditions());
    }
}

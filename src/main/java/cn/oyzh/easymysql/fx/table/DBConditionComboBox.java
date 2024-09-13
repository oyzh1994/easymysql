package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.condition.DBCondition;
import cn.oyzh.easymysql.condition.DBConditionUtil;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class DBConditionComboBox extends FlexComboBox<DBCondition> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(DBCondition o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        this.addItem(DBConditionUtil.conditions());
    }
}

package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.store.DBInfoStore;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * db连接库选择框
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DBInfoComboBox extends FlexComboBox<MysqlInfo> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MysqlInfo object) {
                if (object == null) {
                    return null;
                }
                return object.getName();
            }
        });
        this.setItem(DBInfoStore.INSTANCE.load());
    }
}

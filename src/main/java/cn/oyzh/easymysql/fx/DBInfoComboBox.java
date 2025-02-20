package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.store.MysqlConnectStore;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * db连接库选择框
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DBInfoComboBox extends FXComboBox<MysqlConnect> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MysqlConnect object) {
                if (object == null) {
                    return null;
                }
                return object.getName();
            }
        });
        this.setItem(MysqlConnectStore.INSTANCE.load());
    }
}

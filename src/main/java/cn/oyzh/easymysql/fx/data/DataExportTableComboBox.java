package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/8/27
 */
public class DataExportTableComboBox extends FlexComboBox<DataExportTable> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(DataExportTable object) {
                if (object != null) {
                    return object.getName();
                }
                return null;
            }
        });
    }
}

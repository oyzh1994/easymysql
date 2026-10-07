package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * 数据导出表下拉框
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class DataExportTableComboBox extends FXComboBox<DataExportTable> {

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

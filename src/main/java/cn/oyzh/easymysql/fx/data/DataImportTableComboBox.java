package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * @author oyzh
 * @since 2024/8/27
 */
public class DataImportTableComboBox extends FlexComboBox<DataImportFile> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(DataImportFile object) {
                if (object != null) {
                    return object.getTableName();
                }
                return null;
            }
        });
    }

    public String getSelectedTableName() {
        return this.getSelectedItem().getTableName();
    }
}

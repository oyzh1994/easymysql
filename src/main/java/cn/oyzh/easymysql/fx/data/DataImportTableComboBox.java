package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * 数据导入表下拉框
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class DataImportTableComboBox extends FXComboBox<DataImportFile> {

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

    /** 获取选中的表名称 */
    public String getSelectedTableName() {
        return this.getSelectedItem().getTableName();
    }
}

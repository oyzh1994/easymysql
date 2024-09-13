package cn.oyzh.easymysql.fx.table;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.table.MysqlColumn;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

import java.util.List;

/**
 * db字段类型选择框
 *
 * @author oyzh
 * @since 2024/01/16
 */
public class DBColumnComboBox extends FlexComboBox<MysqlColumn> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MysqlColumn o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
    }

    public DBColumnComboBox() {

    }

    public DBColumnComboBox(List<MysqlColumn> columns) {
        this.addItems(columns);
    }

    public void select(String colName) {
        for (MysqlColumn object : this.getItems()) {
            if (StrUtil.equalsIgnoreCase(colName, object.getName())) {
                this.select(object);
                break;
            }
        }
    }

    public String getColumnName() {
        return this.getSelectedItem().getName();
    }
}

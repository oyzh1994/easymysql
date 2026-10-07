package cn.oyzh.easymysql.fx.table;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

import java.util.List;

/**
 * db字段类型选择框
 *
 * @author oyzh
 * @since 2024/01/16
 */
public class MysqlColumnComboBox extends FXComboBox<MysqlColumn> {

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

    /**
     * 构造db字段下拉框
     */
    public MysqlColumnComboBox() {

    }

    /**
     * 构造db字段下拉框
     *
     * @param columns 字段列表
     */
    public MysqlColumnComboBox(List<MysqlColumn> columns) {
        this.addItems(columns);
    }

    /**
     * 根据字段名称选中
     *
     * @param colName 字段名称
     */
    public void select(String colName) {
        for (MysqlColumn object : this.getItems()) {
            if (StringUtil.equalsIgnoreCase(colName, object.getName())) {
                this.select(object);
                break;
            }
        }
    }

    /**
     * 获取选中字段名称
     *
     * @return 字段名称
     */
    public String getColumnName() {
        return this.getSelectedItem().getName();
    }
}

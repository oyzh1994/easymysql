package cn.oyzh.easymysql.fx.record;

import cn.oyzh.fx.gui.text.field.ChooseFileTextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024/7/10
 */
public class DBBinaryTextFiled extends ChooseFileTextField {

    @Getter
    @Setter
    private String columnType;

    public DBBinaryTextFiled(String columnType) {
        this.columnType = columnType;
    }

    @Override
    public void setValue(Object val) {
        super.setValue(format(this.columnType, val));
    }

    public static String format(String columnType, Object o) {
        if (o instanceof byte[] bytes) {
            return "(" + columnType + ")" + " " + NumUtil.formatSize(bytes.length);
        }
        return "(" + columnType + ")";
    }
}

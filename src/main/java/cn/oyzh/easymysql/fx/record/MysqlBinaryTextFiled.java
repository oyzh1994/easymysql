package cn.oyzh.easymysql.fx.record;

import cn.oyzh.common.util.NumberUtil;
import cn.oyzh.fx.gui.text.field.ChooseFileTextField;

/**
 * mysql二进制文本输入框
 *
 * @author oyzh
 * @since 2024/7/10
 */
@Deprecated
public class MysqlBinaryTextFiled extends ChooseFileTextField {

    /**
     * 字段类型
     */
    private String columnType;

    /**
     * 构造mysql二进制文本输入框
     *
     * @param columnType 字段类型
     */
    public MysqlBinaryTextFiled(String columnType) {
        this.columnType = columnType;
    }

    @Override
    public void formatValue() {
        this.setText(format(this.columnType, super.getValue()));
    }

    /**
     * 格式化二进制内容
     *
     * @param columnType 字段类型
     * @param o          内容
     * @return 格式化结果
     */
    public static String format(String columnType, Object o) {
        if (o instanceof byte[] bytes) {
            return "(" + columnType + ")" + " " + NumberUtil.formatSize(bytes.length);
        }
        return "(" + columnType + ")";
    }
}

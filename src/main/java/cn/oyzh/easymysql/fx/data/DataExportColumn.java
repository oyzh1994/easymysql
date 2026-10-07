package cn.oyzh.easymysql.fx.data;

import cn.oyzh.easymysql.mysql.column.MysqlColumn;

/**
 * 数据导出字段
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class DataExportColumn extends MysqlColumn {

    /**
     * 是否选中
     */
    private boolean selected = true;

    /** 是否选中 */
    public boolean isSelected() {
        return selected;
    }

    /** 设置是否选中 */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

package cn.oyzh.easymysql.fx.data;

import cn.oyzh.easymysql.db.table.DBColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author oyzh
 * @since 2024/8/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataExportColumn extends DBColumn {

    /**
     * 是否选中
     */
    private boolean selected = true;
}

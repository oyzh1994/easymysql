package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.controls.table.FXTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据导出表表格视图
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class DataExportTableTableView extends FXTableView<DataExportTable> {

    /** 获取选中的表列表 */
    public List<DataExportTable> getSelectedTables() {
        List<DataExportTable> exportTables = new ArrayList<>();
        for (DataExportTable item : this.getItems()) {
            if (item.isSelected()) {
                exportTables.add(item);
            }
        }
        return exportTables;
    }

    /** 是否存在选中的表 */
    public boolean hasSelectedTable() {
        for (DataExportTable item : this.getItems()) {
            if (item.isSelected()) {
                return true;
            }
        }
        return false;
    }
}

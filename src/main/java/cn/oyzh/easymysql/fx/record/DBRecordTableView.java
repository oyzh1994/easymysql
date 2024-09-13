package cn.oyzh.easymysql.fx.record;

import cn.oyzh.easymysql.db.record.DBRecord;
import cn.oyzh.fx.plus.controls.table.FlexTableView;

/**
 * @author oyzh
 * @since 2024/7/25
 */
public class DBRecordTableView extends FlexTableView<DBRecord> {

    {
        this.setRowFactory(param -> new DBRecordTableRow());
    }
}

package cn.oyzh.easymysql.fx.record;

import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.fx.plus.controls.table.FlexTableView;

/**
 * @author oyzh
 * @since 2024/7/25
 */
public class DBRecordTableView extends FlexTableView<MysqlRecord> {

    {
        this.setRowFactory(param -> new DBRecordTableRow());
    }
}

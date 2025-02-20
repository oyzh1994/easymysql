package cn.oyzh.easymysql.fx.record;

import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.fx.plus.controls.table.FXTableView;

/**
 * @author oyzh
 * @since 2024/7/25
 */
public class DBRecordTableView extends FXTableView<MysqlRecord> {

    {
        this.setRowFactory(param -> new DBRecordTableRow());
    }
}

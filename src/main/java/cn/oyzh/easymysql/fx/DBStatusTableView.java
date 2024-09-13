package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.fx.plus.controls.table.FlexTableView;

/**
 * @author oyzh
 * @since 2024/7/22
 */
public class DBStatusTableView<S extends DBObjectStatus> extends FlexTableView<S> {

    public void clearStatus() throws Exception {
        for (DBObjectStatus object : this.getItems()) {
            object.clearStatus();
        }
    }
}

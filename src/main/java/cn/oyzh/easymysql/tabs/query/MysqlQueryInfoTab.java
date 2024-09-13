package cn.oyzh.easymysql.tabs.query;

import cn.oyzh.easymysql.db.query.MysqlQueryResults;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.fx.plus.tabs.DynamicTab;

/**
 * db查询信息tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MysqlQueryInfoTab extends DynamicTab {

    {
        this.setClosable(false);
    }

    @Override
    protected String url() {
        return MysqlTab.BASE_PATH + "query/mysqlQueryInfoTab.fxml";
    }

    public void init(MysqlQueryResults<?> results) {
        this.controller().init(results);
    }

    @Override
    public MysqlQueryInfoTabController controller() {
        return (MysqlQueryInfoTabController) super.controller();
    }
}

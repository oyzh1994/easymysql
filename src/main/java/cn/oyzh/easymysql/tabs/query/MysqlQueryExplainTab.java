package cn.oyzh.easymysql.tabs.query;

import cn.oyzh.easymysql.db.query.MysqlExplainResult;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * db解释tab
 *
 * @author oyzh
 * @since 2024/08/16
 */
public class MysqlQueryExplainTab extends RichTab {

    {
        this.setClosable(false);
    }

    @Override
    protected String url() {
        return MysqlTab.BASE_PATH + "query/mysqlQueryExplainTab.fxml";
    }

    public void init(String title, MysqlExplainResult result) {
        this.setTitle(title);
        this.controller().init(result);
    }

    @Override
    public MysqlQueryExplainTabController controller() {
        return (MysqlQueryExplainTabController) super.controller();
    }

}

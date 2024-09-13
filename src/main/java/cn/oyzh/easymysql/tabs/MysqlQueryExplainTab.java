package cn.oyzh.easymysql.tabs;

import cn.oyzh.easymysql.db.query.MysqlExplainResult;
import cn.oyzh.fx.plus.tabs.DynamicTab;

/**
 * db解释tab
 *
 * @author oyzh
 * @since 2024/08/16
 */
public class MysqlQueryExplainTab extends DynamicTab {

    {
        this.setClosable(false);
    }

    @Override
    protected String url() {
        return MysqlTab.BASE_PATH + "dbQueryExplainTab.fxml";
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

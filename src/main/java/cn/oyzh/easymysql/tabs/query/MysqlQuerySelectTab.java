package cn.oyzh.easymysql.tabs.query;

import cn.oyzh.easymysql.db.query.MysqlExecuteResult;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.tabs.DynamicTab;

/**
 * db查询tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MysqlQuerySelectTab extends DynamicTab {

    {
        this.setClosable(false);
    }

    @Override
    protected String url() {
        return MysqlTab.BASE_PATH + "query/mysqlQuerySelectTab.fxml";
    }

    public void init(String title, MysqlExecuteResult result, MysqlDatabaseTreeItem dbItem) {
        this.setTitle(title);
        this.controller().init(result, dbItem);
    }

    @Override
    public MysqlQuerySelectTabController controller() {
        return (MysqlQuerySelectTabController) super.controller();
    }
}

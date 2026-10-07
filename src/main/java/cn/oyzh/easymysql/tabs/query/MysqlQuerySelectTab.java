package cn.oyzh.easymysql.tabs.query;

import cn.oyzh.easymysql.mysql.query.MysqlExecuteResult;
import cn.oyzh.easymysql.tabs.MysqlTab;
import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * db查询tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MysqlQuerySelectTab extends RichTab {

    @Override
    protected String url() {
        return MysqlTab.BASE_PATH + "query/mysqlQuerySelectTab.fxml";
    }

    /**
     * 初始化
     *
     * @param title  标题
     * @param result 执行结果
     * @param dbItem 数据库树节点
     */
    public void init(String title, MysqlExecuteResult result, MysqlDatabaseTreeItem dbItem) {
        this.setTitle(title);
        this.controller().init(result, dbItem);
    }

    @Override
    public MysqlQuerySelectTabController controller() {
        return (MysqlQuerySelectTabController) super.controller();
    }


    @Override
    public void initNode() {
        this.setClosable(false);
        super.initNode();
    }
}

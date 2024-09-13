package cn.oyzh.easymysql.tabs;

import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.tabs.DynamicTab;

/**
 * @author oyzh
 * @since 2024-09-12
 */
public abstract class MysqlTab extends DynamicTab {

    public static final String BASE_PATH = "/module/mysql/tabs/";

    protected String getBasePath() {
        return BASE_PATH;
    }

    public abstract MysqlDatabaseTreeItem dbItem() ;
}

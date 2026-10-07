package cn.oyzh.easymysql.tabs;

import cn.oyzh.easymysql.trees.database.MysqlDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * mysql标签页
 *
 * @author oyzh
 * @since 2024-09-12
 */
public abstract class MysqlTab extends RichTab {

    /**
     * 基础路径
     */
    public static final String BASE_PATH = "/tabs/";

    /**
     * 获取基础路径
     *
     * @return 基础路径
     */
    protected String getBasePath() {
        return BASE_PATH;
    }

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public abstract MysqlDatabaseTreeItem dbItem() ;
}

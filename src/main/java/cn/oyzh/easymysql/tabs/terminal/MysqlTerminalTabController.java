package cn.oyzh.easymysql.tabs.terminal;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.terminal.MysqlTerminalPane;
import cn.oyzh.fx.gui.tabs.RichTabController;
import javafx.event.Event;
import javafx.fxml.FXML;

/**
 * mysql命令行tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
public class MysqlTerminalTabController extends RichTabController {

    /**
     * mysql命令行文本域
     */
    @FXML
    private MysqlTerminalPane terminal;

    /**
     * 库名称
     */
    private String dbName;

    /**
     * 初始化
     *
     * @param client mysql客户端
     * @param dbName 库名称
     */
    public void init(MysqlClient client, String dbName) {
        this.terminal.init(client, dbName);
        this.dbName = dbName;
    }

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * db信息
     *
     * @return 当前db信息
     */
    protected MysqlConnect getDbConnect() {
        return this.terminal.getDbConnect();
    }

    /**
     * 获取mysql客户端
     *
     * @return mysql客户端
     */
    public MysqlClient client() {
        return this.terminal.getClient();
    }

    @Override
    public void onTabClosed(Event event) {
        if (this.terminal.isTemporary()) {
            this.client().close();
        }
        super.onTabClosed(event);
    }
}

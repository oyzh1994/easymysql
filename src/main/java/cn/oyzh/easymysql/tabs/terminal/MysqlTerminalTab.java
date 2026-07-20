package cn.oyzh.easymysql.tabs.terminal;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * mysql终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class MysqlTerminalTab extends RichTab {

    public MysqlTerminalTab(MysqlClient client, String dbName) {
        this.init(client, dbName);
    }

    @Override
    public MysqlTerminalTabController controller() {
        return (MysqlTerminalTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/terminal/mysqlTerminalTab.fxml";
    }

    @Override
    public void flushGraphic() {
        TerminalSVGGlyph graphic = (TerminalSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new TerminalSVGGlyph();
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    protected String getTabTitle() {
        MysqlConnect connect = this.dbConnect();
        if (connect != null) {
            return connect.getName();
        }
        return I18nHelper.unnamedConnection();
    }

    /**
     * 初始化
     *
     * @param client mysql客户端
     */
    private void init(MysqlClient client, String dbName) {
        try {
            if (client == null) {
                MysqlConnect connect = new MysqlConnect();
                connect.setName(I18nHelper.unnamedConnection());
                this.flushGraphic();
                this.controller().init(new MysqlClient(connect), dbName);
            } else {
                this.flushGraphic();
                this.controller().init(client, dbName);
            }
            this.flushTitle();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * db信息
     *
     * @return 当前db信息
     */
    public MysqlConnect dbConnect() {
        return this.controller().getDbConnect();
    }

    public MysqlClient client() {
        return this.controller().client();
    }

    public String dbName() {
        return this.controller().getDbName();
    }
}

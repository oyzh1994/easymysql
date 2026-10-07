package cn.oyzh.easymysql.tabs.query;

import cn.oyzh.easymysql.mysql.query.MysqlQueryResult;
import cn.oyzh.easymysql.mysql.query.MysqlQueryResults;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.text.area.FXTextArea;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * db查询信息标签页控制器
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MysqlQueryInfoTabController extends RichTabController {

    /**
     * 根节点
     */
    @FXML
    private FXTextArea infoArea;

    /**
     * 初始化
     *
     * @param results 查询结果
     */
    public void init(MysqlQueryResults<?> results) {
        this.infoArea.clear();
        if (results.isSuccess()) {
            for (MysqlQueryResult result : results.getResults()) {
                this.infoArea.appendLine(result.getSql());
                if (result.isSuccess()) {
                    if (result.getUpdateCount() > 0) {
                        this.infoArea.appendLine("> Affected rows: " + result.getUpdateCount());
                    } else {
                        this.infoArea.appendLine("> OK");
                    }
                } else {
                    this.infoArea.appendLine("> " + result.getMsg());
                }
                this.infoArea.appendLine("> " + I18nHelper.time() + ": " + result.getUsedMs() + "ms");
                this.infoArea.appendLine("");
            }
        } else {
            this.infoArea.appendLine(results.getErrMsg());
        }
    }
}

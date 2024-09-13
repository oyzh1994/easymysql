package cn.oyzh.easymysql.tabs;

import cn.oyzh.easymysql.db.query.DBQueryResult;
import cn.oyzh.easymysql.db.query.DBQueryResults;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import javafx.fxml.FXML;

/**
 * @author oyzh
 * @since 2024/08/12
 */
public class MysqlQueryInfoTabController extends DynamicTabController {

    /**
     * 根节点
     */
    @FXML
    private FlexTextArea infoArea;

    public void init(DBQueryResults<?> results) {
        this.infoArea.clear();
        if (results.isSuccess()) {
            for (DBQueryResult result : results.getResults()) {
                this.infoArea.appendLine(result.sql());
                if (result.success()) {
                    if (result.updateCount() > 0) {
                        this.infoArea.appendLine("> Affected rows: " + result.updateCount());
                    } else {
                        this.infoArea.appendLine("> OK");
                    }
                } else {
                    this.infoArea.appendLine("> " + result.msg());
                }
                this.infoArea.appendLine("> " + I18nHelper.time() + ": " + result.getUsedMs() + "ms");
                this.infoArea.appendLine("");
            }
        } else {
            this.infoArea.appendLine(results.getErrMsg());
        }
    }
}

package cn.oyzh.easymysql.controller.database;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.fx.DBCharsetComboBox;
import cn.oyzh.easymysql.fx.DBCollationComboBox;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import cn.oyzh.fx.gui.text.field.ReadOnlyTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 编辑db库业务
 *
 * @author oyzh
 * @since 2024/01/30
 */
@StageAttribute(
        title = "DB数据库编辑",
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "database/mysqlDatabaseUpdate.fxml"
)
public class MysqlDatabaseUpdateController extends StageController {

    /**
     * 名称
     */
    @FXML
    private ReadOnlyTextField name;

    /**
     * 字符集
     */
    @FXML
    private DBCharsetComboBox charset;

    /**
     * 排序方式
     */
    @FXML
    private DBCollationComboBox collation;

    /**
     * db库对象
     */
    private DBDatabase database;

    /**
     * db连接节点
     */
    private DBConnectTreeItem connectItem;

    /**
     * 编辑db库
     */
    @FXML
    private void save() {
        try {
            DBDatabase database = new DBDatabase();
            database.setName(this.name.getText());
            // 字符集
            String charset = this.charset.getSelectedItem();
            if (!StrUtil.equalsIgnoreCase(charset, this.database.getCharset())) {
                database.setCharset(charset);
            }
            // 排序规则
            String collation = this.collation.getSelectedItem();
            if (!StrUtil.equalsIgnoreCase(charset, this.database.getCollation())) {
                database.setCollation(collation);
            }
            // 修改数据库
            if (this.connectItem.alterDatabase(database)) {
                MysqlEventUtil.databaseUpdated(this.connectItem, database);
                // 更新字符集和排序规则
                this.database.setCharset(charset);
                this.database.setCollation(collation);
                this.closeWindow();
            } else {
                MessageBox.warn("修改数据库失败！");
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // 字符集选中事件
        this.charset.selectedItemChanged((observable, oldValue, newValue) -> {
            this.collation.init(newValue, this.connectItem.client());
            this.collation.select(0);
        });
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        this.database = this.getWindowProp("database");
        this.connectItem = this.getWindowProp("connectItem");

        // 数据库名
        this.name.setText(this.database.getName());

        // 初始化字符集和排序
        this.charset.init(this.connectItem.client());
        this.charset.select(this.database.getCharset());
        this.collation.init(this.database.getCharset(), this.connectItem.client());
        this.collation.select(this.database.getCollation());

        super.onWindowShown(event);
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }
}

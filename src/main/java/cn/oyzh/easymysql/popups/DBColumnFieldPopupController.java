package cn.oyzh.easymysql.popups;

import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.fx.table.DBColumnListView;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.PopupController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ListViewUtil;
import cn.oyzh.fx.plus.window.PopupAttribute;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

import java.util.List;

/**
 * 字段列表弹窗业务
 *
 * @author oyzh
 * @since 2024/07/12
 */
@PopupAttribute(
        value = FXConst.POPUP_PATH + "dbColumnFieldPopup.fxml"
)
public class DBColumnFieldPopupController extends PopupController {

    /**
     * 提交事件
     */
    private Runnable onSubmit;

    /**
     * 值组件
     */
    @FXML
    private DBColumnListView listView;

    /**
     * 提交
     */
    @FXML
    private void submit() {
        try {
            if (this.onSubmit != null) {
                this.onSubmit.run();
            }
            this.closeWindow();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 关闭
     */
    @FXML
    private void close() {
        this.closeWindow();
    }

    /**
     * 上移行
     */
    @FXML
    private void moveUpRow() {
        ListViewUtil.moveUp(this.listView);
    }

    /**
     * 下移行
     */
    @FXML
    private void moveDownRow() {
        ListViewUtil.moveDown(this.listView);
    }

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
        this.onSubmit = this.getWindowProp("onSubmit");
        List<MysqlColumn> columns = this.getWindowProp("columns");
        List<String> selectedColumns = this.getWindowProp("selectedColumns");
        this.listView.init(columns);
        this.listView.select(selectedColumns);
    }
}

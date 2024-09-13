package cn.oyzh.easymysql.controller;

import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.db.view.DBView;
import cn.oyzh.easymysql.trees.MysqlViewTreeItem;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.area.ReadOnlyTextArea;
import cn.oyzh.fx.plus.controls.textfield.ReadOnlyTextField;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * db表信息业务
 *
 * @author oyzh
 * @since 2024/01/30
 */
@StageAttribute(
        title = "DB视图信息",
        modality = Modality.WINDOW_MODAL,
        iconUrls = MysqlConst.ICON_PATH,
        value = FXConst.MODULE_PATH + "mysql/views/mysqlViewInfo.fxml"
)
public class MysqlViewInfoController extends StageController {

    /**
     * 名称
     */
    @FXML
    private ReadOnlyTextField viewName;

    /**
     * 注释
     */
    @FXML
    private ReadOnlyTextArea viewComment;

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.stage.hideOnEscape();
        MysqlViewTreeItem item = this.getWindowProp("item");
        DBView view = item.value();
        this.viewName.setText(view.getName());
        this.viewComment.setText(view.getComment());
    }
}

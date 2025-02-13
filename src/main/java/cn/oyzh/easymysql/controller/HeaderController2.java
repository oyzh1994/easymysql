package cn.oyzh.easymysql.controller;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.dto.Project;
import cn.oyzh.easymysql.controller.data.MysqlDataTransportController;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeMutexes;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class HeaderController2 extends SubStageController {

    /**
     * 数据传输
     */
    @FXML
    private void transport() {
        StageAdapter wrapper = StageManager.getStage(MysqlDataTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(MysqlDataTransportController.class);
        }
    }

    /**
     * 设置
     */
    @FXML
    private void setting() {
        StageAdapter fxView = StageManager.getStage(SettingController2.class);
        if (fxView != null) {
            fxView.toFront();
        } else {
            StageManager.showStage(SettingController2.class, this.stage);
        }
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        StageManager.showStage(AboutController.class, this.stage);
    }

    /**
     * 退出
     */
    @FXML
    private void quit() {
        if (MessageBox.confirm(I18nHelper.quit() + " " + SysConst.projectName())) {
            StageManager.exit();
        }
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
    }

    /**
     * 布局1
     */
    @FXML
    private void layout1() {
        DBEventUtil.layout1();
    }

    /**
     * 布局2
     */
    @FXML
    private void layout2() {
        DBEventUtil.layout2();
    }

}

package cn.oyzh.easymysql.controller.info;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.domain.MysqlGroup;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.easymysql.fx.DBTypeComboBox;
import cn.oyzh.easymysql.store.DBInfoStore;
import cn.oyzh.easymysql.util.DBConnectUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 添加db信息业务
 *
 * @author oyzh
 * @since 2023/12/22
 */
@StageAttribute(
        title = "DB连接新增",
        modality = Modality.WINDOW_MODAL,
        iconUrls = MysqlConst.ICON_PATH,
        value = FXConst.VIEW_PATH + "info/dbInfoAdd.fxml"
)
public class MysqlInfoAddController extends StageController {

    /**
     * tab组件
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * 名称
     */
    @FXML
    private ClearableTextField name;

    /**
     * 类型
     */
    @FXML
    private DBTypeComboBox type;

    /**
     * 用户名
     */
    @FXML
    private ClearableTextField user;

    /**
     * 认证密码
     */
    @FXML
    private ClearableTextField password;

    /**
     * 备注
     */
    @FXML
    private FlexTextArea remark;

    /**
     * 连接ip
     */
    @FXML
    private ClearableTextField hostIp;

    /**
     * 连接端口
     */
    @FXML
    private PortTextField hostPort;

    /**
     * 连接超时
     */
    @FXML
    private NumberTextField connectTimeOut;

    /**
     * 分组
     */
    private MysqlGroup group;

    /**
     * db连接储存对象
     */
    private final DBInfoStore infoStore = DBInfoStore.INSTANCE;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
        String hostIp = this.hostIp.getTextTrim();
        this.tabPane.select(0);
        if (!this.hostPort.validate()) {
            this.tabPane.select(0);
            return null;
        }
        if (!this.hostIp.validate()) {
            this.tabPane.select(0);
            return null;
        }
        hostText = hostIp + ":" + this.hostPort.getValue();
        return hostText;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (StrUtil.isNotBlank(host)) {
            MysqlConnect dbInfo = new MysqlConnect();
            dbInfo.setHost(host);
            dbInfo.setConnectTimeOut(5);
            dbInfo.setUser(this.user.getText());
            dbInfo.setType(this.type.getType());
            dbInfo.setPassword(this.password.getText());
            DBConnectUtil.testConnect(this.stage, dbInfo);
        }
    }

    /**
     * 添加db信息
     */
    @FXML
    private void add() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StrUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        try {
            String name = this.name.getTextTrim();
            MysqlConnect dbInfo = new MysqlConnect();
            dbInfo.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            dbInfo.setHost(host);
            dbInfo.setUser(this.user.getText());
            dbInfo.setType(this.type.getType());
            dbInfo.setRemark(this.remark.getTextTrim());
            dbInfo.setPassword(this.password.getText());
            dbInfo.setGroupId(this.group == null ? null : this.group.getGid());
            dbInfo.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            // 保存数据
            boolean result = this.infoStore.add(dbInfo);
            if (result) {
                DBEventUtil.infoAdded(dbInfo);
                MessageBox.okToast("新增db信息成功!");
                this.closeWindow();
            } else {
                MessageBox.warn("新增db信息失败！");
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.group = this.getWindowProp("group");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }
}

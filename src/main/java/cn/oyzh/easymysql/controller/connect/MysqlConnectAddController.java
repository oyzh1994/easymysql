package cn.oyzh.easymysql.controller.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.domain.MysqlGroup;
import cn.oyzh.easymysql.domain.MysqlSSHConfig;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.fx.DBTypeComboBox;
import cn.oyzh.easymysql.store.MysqlConnectStore;
import cn.oyzh.easymysql.util.DBConnectUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
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
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "connect/mysqlConnectAdd.fxml"
)
public class MysqlConnectAddController extends StageController {

    /**
     * 只读模式
     */
    @FXML
    private FXCheckBox readonly;

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
     * ssh面板
     */
    @FXML
    private FXTab sshTab;

    /**
     * 开启ssh
     */
    @FXML
    private FXToggleSwitch sshForward;

    /**
     * ssh主机地址
     */
    @FXML
    private ClearableTextField sshHost;

    /**
     * ssh主机端口
     */
    @FXML
    private PortTextField sshPort;

    /**
     * ssh主机端口
     */
    @FXML
    private NumberTextField sshTimeout;

    /**
     * ssh主机用户
     */
    @FXML
    private ClearableTextField sshUser;

    /**
     * ssh主机密码
     */
    @FXML
    private ClearableTextField sshPassword;

    /**
     * 分组
     */
    private MysqlGroup group;

    /**
     * db连接储存对象
     */
    private final MysqlConnectStore connectStore = MysqlConnectStore.INSTANCE;

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
     * 获取ssh信息
     *
     * @return ssh连接信息
     */
    private MysqlSSHConfig getSSHConfig() {
        MysqlSSHConfig sshConfig = new MysqlSSHConfig();
        sshConfig.setHost(this.sshHost.getText());
        sshConfig.setUser(this.sshUser.getText());
        sshConfig.setPort(this.sshPort.getIntValue());
        sshConfig.setPassword(this.sshPassword.getText());
        sshConfig.setTimeout(this.sshTimeout.getIntValue());
        return sshConfig;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (StringUtil.isBlank(host) || StringUtil.isBlank(host.split(":")[0])) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
        } else {
            // 创建redis连接
            MysqlConnect redisConnect = new MysqlConnect();
            redisConnect.setHost(host);
            redisConnect.setConnectTimeOut(3);
            redisConnect.setUser(this.user.getText());
            redisConnect.setPassword(this.password.getText());
            redisConnect.setSshForward(this.sshForward.isSelected());
            if (redisConnect.isSSHForward()) {
                redisConnect.setSshConfig(this.getSSHConfig());
            }
            DBConnectUtil.testConnect(this.stage, redisConnect);
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
            MysqlConnect mysqlConnect = new MysqlConnect();
            mysqlConnect.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            mysqlConnect.setHost(host);
            mysqlConnect.setUser(this.user.getText());
            mysqlConnect.setType(this.type.getType());
            mysqlConnect.setRemark(this.remark.getTextTrim());
            mysqlConnect.setPassword(this.password.getText());
            mysqlConnect.setGroupId(this.group == null ? null : this.group.getGid());
            mysqlConnect.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            mysqlConnect.setSshConfig(this.getSSHConfig());
            mysqlConnect.setSshForward(this.sshForward.isSelected());

            // 保存数据
            boolean result = this.connectStore.replace(mysqlConnect);
            if (result) {
                MysqlEventUtil.connectAdded(mysqlConnect);
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void bindListeners() {
        // ssh配置
        this.sshForward.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                NodeGroupUtil.enable(this.sshTab, "ssh");
            } else {
                NodeGroupUtil.disable(this.sshTab, "ssh");
            }
        });
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.group = this.getWindowProp("group");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.connectAddTitle();
    }
}

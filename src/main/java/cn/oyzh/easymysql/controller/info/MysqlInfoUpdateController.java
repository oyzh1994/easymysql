package cn.oyzh.easymysql.controller.info;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.event.DBEventUtil;
import cn.oyzh.easymysql.fx.DBTypeComboBox;
import cn.oyzh.easymysql.fx.info.ServiceTypeCombobox;
import cn.oyzh.easymysql.store.DBInfoStore;
import cn.oyzh.easymysql.util.DBConnectUtil;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.controls.textfield.PortTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;

/**
 * db信息修改业务
 *
 * @author oyzh
 * @since 2023/12/22
 */
@StageAttribute(title = "DB连接修改", modality = Modality.WINDOW_MODAL, iconUrls = MysqlConst.ICON_PATH, value = FXConst.VIEW_PATH + "info/dbInfoUpdate.fxml")
public class MysqlInfoUpdateController extends StageController {

    /**
     * tab组件
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * db信息
     */
    private MysqlInfo dbInfo;

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
     * 服务名称
     */
    @FXML
    private ClearableTextField serviceName;

    /**
     * 服务类型
     */
    @FXML
    private ServiceTypeCombobox serviceType;

    /**
     * 备注
     */
    @FXML
    private FlexTextArea remark;

    /**
     * 连接超时
     */
    @FXML
    private NumberTextField connectTimeOut;

    /**
     * 服务组件
     */
    @FXML
    private FlexHBox serviceBox;

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
            MysqlInfo dbInfo = new MysqlInfo();
            dbInfo.setHost(host);
            dbInfo.setConnectTimeOut(5);
            dbInfo.setUser(this.user.getText());
            dbInfo.setType(this.type.getType());
            dbInfo.setPassword(this.password.getText());
            // 服务名
            if (this.serviceType.getSelectedIndex() == 0) {
                dbInfo.setSid(null);
                dbInfo.setServiceName(this.serviceName.getTextTrim());
            } else if (this.serviceType.getSelectedIndex() == 1) {// sid
                dbInfo.setSid(this.serviceName.getTextTrim());
                dbInfo.setServiceName(null);
            }
            DBConnectUtil.testConnect(this.stage, dbInfo);
        }
    }

    /**
     * 修改db信息
     */
    @FXML
    private void update() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StrUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        String name = this.name.getTextTrim();
        this.dbInfo.setName(name);
        Number connectTimeOut = this.connectTimeOut.getValue();

        this.dbInfo.setHost(host.trim());
        this.dbInfo.setUser(this.user.getText());
        this.dbInfo.setType(this.type.getType());
        this.dbInfo.setRemark(this.remark.getTextTrim());
        this.dbInfo.setPassword(this.password.getText());
        this.dbInfo.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());

        // 服务名
        if (this.serviceBox.isVisible()) {
            if (this.serviceType.getSelectedIndex() == 0) {
                this.dbInfo.setSid(null);
                this.dbInfo.setServiceName(this.serviceName.getTextTrim());
            } else if (this.serviceType.getSelectedIndex() == 1) {// sid
                this.dbInfo.setSid(this.serviceName.getTextTrim());
                this.dbInfo.setServiceName(null);
            }
        } else {
            this.dbInfo.setSid(null);
            this.dbInfo.setServiceName(null);
        }

        // 保存数据
        if (this.infoStore.update(this.dbInfo)) {
            DBEventUtil.infoUpdated(this.dbInfo);
            MessageBox.okToast("修改db信息成功!");
            this.closeWindow();
        } else {
            MessageBox.warn("修改db信息失败！");
        }
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.type.selectedItemChanged((observableValue, dbDialect, t1) -> {
            if (this.type.isOracle()) {
                this.serviceBox.display();
            } else {
                this.serviceBox.disappear();
            }
        });
    }

    @Override
    public void onStageShown(@NonNull WindowEvent event) {
        super.onStageShown(event);
        this.dbInfo = this.getWindowProp("info");
        this.name.setText(this.dbInfo.getName());
        this.user.setText(this.dbInfo.getUser());
        this.hostIp.setText(this.dbInfo.hostIp());
        this.type.selectType(this.dbInfo.getType());
        this.remark.setText(this.dbInfo.getRemark());
        this.hostPort.setValue(this.dbInfo.hostPort());
        this.password.setText(this.dbInfo.getPassword());
        this.serviceName.setText(this.dbInfo.serviceName());
        this.serviceType.init(this.dbInfo.checkServiceType());
        this.connectTimeOut.setValue(this.dbInfo.getConnectTimeOut());
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageInitialize(StageAdapter stage) {
        super.onStageInitialize(stage);
        this.serviceBox.managedBindVisible();
    }
}

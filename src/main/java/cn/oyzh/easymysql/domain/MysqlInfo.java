package cn.oyzh.easymysql.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.ssh.SSHConnectInfo;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * db信息
 *
 * @author oyzh
 * @since 2020/3/6
 */
@ToString
public class MysqlInfo implements Comparable<MysqlInfo>, ObjectComparator<MysqlInfo> {

    /**
     * 数据id
     */
    @Getter
    @Setter
    private String id;

    /**
     * 连接地址
     */
    @Getter
    @Setter
    private String host;

    /**
     * 名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 认证用户
     */
    @Getter
    @Setter
    private String user;

    /**
     * 类型
     */
    @Getter
    @Setter
    private String type;

    /**
     * 认证密码
     */
    @Getter
    @Setter
    private String password;

    /**
     * 备注信息
     */
    @Getter
    @Setter
    private String remark;

    /**
     * 只读模式
     */
    @Setter
    @Getter
    private Boolean readonly;

    /**
     * 分组id
     */
    @Getter
    @Setter
    private String groupId;

    /**
     * 收藏的表
     */
    @Getter
    @Setter
    private List<String> collects;

    /**
     * 连接超时时间
     */
    @Setter
    private Integer connectTimeOut;

    /**
     * 是否开启ssh转发
     */
    @Setter
    @Getter
    private Boolean sshForward;

    /**
     * ssh信息
     */
    @Setter
    @Getter
    private SSHConnectInfo sshInfo;

    @Setter
    @Getter
    private String sid;

    @Setter
    @Getter
    private String serviceName;

    /**
     * 复制对象
     *
     * @param info db信息
     * @return 当前对象
     */
    public MysqlInfo copy(@NonNull MysqlInfo info) {
        this.name = info.name;
        this.host = info.host;
        this.user = info.user;
        this.type = info.type;
        this.remark = info.remark;
        this.groupId = info.groupId;
        this.sshInfo = info.sshInfo;
        this.readonly = info.readonly;
        this.password = info.password;
        this.collects = info.collects;
        this.sshForward = info.sshForward;
        this.connectTimeOut = info.connectTimeOut;
        return this;
    }

    /**
     * 是否ssh转发
     *
     * @return 结果
     */
    public boolean isSSHForward() {
        return BooleanUtil.isTrue(this.sshForward);
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return BooleanUtil.isTrue(this.readonly);
    }

    /**
     * 是否被收藏
     *
     * @param path 路径
     * @return 结果
     */
    public boolean isCollect(@NonNull String path) {
        return CollUtil.isNotEmpty(this.collects) && this.collects.contains(path);
    }

    /**
     * 添加收藏
     *
     * @param path 路径
     */
    public void addCollect(@NonNull String path) {
        if (this.collects == null) {
            this.collects = new ArrayList<>();
        }
        if (!this.collects.contains(path)) {
            this.collects.add(path);
        }
    }

    /**
     * 取消收藏
     *
     * @param path 路径
     * @return 结果
     */
    public boolean removeCollect(@NonNull String path) {
        if (this.collects != null) {
            return this.collects.remove(path);
        }
        return false;
    }

    /**
     * 获取连接超时
     *
     * @return 连接超时时间
     */
    public Integer getConnectTimeOut() {
        return this.connectTimeOut == null || this.connectTimeOut < 1 ? 5 : this.connectTimeOut;
    }

    /**
     * 获取连接超时毫秒值
     *
     * @return 连接超时时间毫秒值
     */
    public int connectTimeOutMs() {
        return this.getConnectTimeOut() * 1000;
    }

    @Override
    public int compareTo(MysqlInfo o) {
        if (o == null) {
            return 1;
        }
        return this.name.compareToIgnoreCase(o.getName());
    }

    /**
     * 获取连接ip
     *
     * @return 连接ip
     */
    public String hostIp() {
        if (StrUtil.isNotBlank(this.host) && this.host.contains(":")) {
            return this.host.split(":")[0];
        }
        return "";
    }

    /**
     * 获取连接端口
     *
     * @return 连接端口
     */
    public int hostPort() {
        try {
            if (StrUtil.isNotBlank(this.host) && !this.host.contains(",") && this.host.contains(":")) {
                return Integer.parseInt(this.host.split(":")[1]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean compare(MysqlInfo t1) {
        if (t1 == null) {
            return false;
        }
        return StrUtil.equals(this.name, t1.name);
    }

    public String serviceName() {
        return this.sid == null ? this.serviceName : this.sid;
    }

    public String checkServiceType() {
        return this.sid == null ? "sid" : "serviceName";
    }
}

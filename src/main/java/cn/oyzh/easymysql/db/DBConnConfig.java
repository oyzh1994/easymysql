package cn.oyzh.easymysql.db;

/**
 * 数据库连接配置
 *
 * @author oyzh
 * @since 2024-09-06
 */
public class DBConnConfig {

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 服务id
     */
    private String sid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 获取连接字符串
     *
     * @param dialect 数据库方言
     * @return 连接字符串
     */
    public String getConnectionString(DBDialect dialect) {
        return "jdbc:mysql://" + this.host + ":" + this.port + "/";
    }

    /**
     * 获取主机地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取服务id
     *
     * @return 服务id
     */
    public String getSid() {
        return sid;
    }

    /**
     * 设置服务id
     *
     * @param sid 服务id
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取服务名称
     *
     * @return 服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 设置服务名称
     *
     * @param serviceName 服务名称
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


}

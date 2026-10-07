package cn.oyzh.easymysql.dto;


/**
 * db连接
 *
 * @author oyzh
 * @since 2023/8/10
 */
public class MysqlConnect {

    /**
     * 地址
     */
    private String host = "127.0.0.1";

    /**
     * 端口
     */
    private int port = 3306;

    /**
     * 用户
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * db索引
     */
    private int db = 0;

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置地址
     *
     * @param host 地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取用户
     *
     * @return 用户
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置用户
     *
     * @param user 用户
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取db索引
     *
     * @return db索引
     */
    public int getDb() {
        return db;
    }

    /**
     * 设置db索引
     *
     * @param db db索引
     */
    public void setDb(int db) {
        this.db = db;
    }
}

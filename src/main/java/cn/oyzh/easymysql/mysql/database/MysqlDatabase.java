package cn.oyzh.easymysql.mysql.database;


/**
 * MySQL数据库连接信息
 *
 * @author oyzh
 * @since 2023/8/10
 */
public class MysqlDatabase {

    /**
     * 名称
     */
    private String name;

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
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
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
}

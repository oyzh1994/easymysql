package cn.oyzh.easymysql.dto;

import lombok.Data;

/**
 * db连接
 *
 * @author oyzh
 * @since 2023/8/10
 */
@Data
public class DBConnect {

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
}

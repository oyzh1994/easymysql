package cn.oyzh.easymysql.db.database;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * db连接
 *
 * @author oyzh
 * @since 2023/8/10
 */
@Data
@Accessors(chain = true, fluent = true)
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
}

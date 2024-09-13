package cn.oyzh.easymysql.db;

import lombok.Data;

/**
 * @author oyzh
 * @since 2024-09-06
 */
@Data
public class DBConnConfig {

    private String host;

    private Integer port;

    private String sid;

    private String username;

    private String serviceName;

    public String getConnectionString(DBDialect dialect) {
        return "jdbc:mysql://" + this.host + ":" + this.port + "/";
    }
}

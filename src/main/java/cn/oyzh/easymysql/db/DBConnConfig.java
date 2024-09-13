package cn.oyzh.easymysql.db;

import cn.hutool.core.util.StrUtil;
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
        if (dialect == DBDialect.MYSQL) {
            return "jdbc:mysql://" + this.host + ":" + this.port + "/";
        }
        if (dialect == DBDialect.ORACLE) {
            String url = "jdbc:oracle:thin:@";
            if (StrUtil.isNotBlank(this.serviceName)) {
                url = url + "//" + this.host + ":" + this.port + "/" + this.serviceName;
            } else if (StrUtil.isNotBlank(this.sid)) {
                url = url + this.host + ":" + this.port + ":" + this.sid;
            }
            return url;
        }
        if (dialect == DBDialect.MARIADB) {
            return "jdbc:mariadb://" + this.host + ":" + this.port + "/";
        }
        if (dialect == DBDialect.MSSQL) {
            return "jdbc:sqlserver://" + this.host + ":" + this.port + ";encrypt=true;trustServerCertificate=true;";
        }
        return null;
    }
}

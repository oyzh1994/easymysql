package cn.oyzh.easymysql.db;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * 连接管理器
 *
 * @author oyzh
 * @since 2024/01/28
 */
public class DBConnectionManager {

    /**
     * 服务连接
     */
    private Connection serverConnection;

    /**
     * 库连接
     */
    private final Map<String, Connection> connections = new HashMap<>();

    /**
     * 添加库连接
     *
     * @param dbName     库名称
     * @param connection 连接
     */
    public void addConnection(String dbName, Connection connection) {
        this.connections.put(dbName, connection);
    }

    /**
     * 添加schema连接
     *
     * @param dbName     库名称
     * @param schema     schema名称
     * @param connection 连接
     */
    public void addSchemaConnection(String dbName, String schema, Connection connection) {
        this.connections.put(dbName + "_" + schema, connection);
    }

    /**
     * 添加函数连接
     *
     * @param dbName     库名称
     * @param schema     schema名称
     * @param connection 连接
     */
    public void addFunctionConnection(String dbName, String schema, Connection connection) {
        this.connections.put(dbName + "_" + schema + "_function", connection);
    }

    /**
     * 添加存储过程连接
     *
     * @param dbName     库名称
     * @param schema     schema名称
     * @param connection 连接
     */
    public void addProcedureConnection(String dbName, String schema, Connection connection) {
        this.connections.put(dbName + "_" + schema + "_procedure", connection);
    }

    /**
     * 获取库连接
     *
     * @param dbName 库名称
     * @return 连接
     */
    public Connection getConnection(String dbName) {
        return this.connections.get(dbName);
    }

    /**
     * 获取schema连接
     *
     * @param dbName 库名称
     * @param schema schema名称
     * @return 连接
     */
    public Connection getSchemaConnection(String dbName, String schema) {
        return this.connections.get(dbName + "_" + schema);
    }

    /**
     * 获取函数连接
     *
     * @param dbName 库名称
     * @param schema schema名称
     * @return 连接
     */
    public Connection getFunctionConnection(String dbName, String schema) {
        return this.connections.get(dbName + "_" + schema + "_function");
    }

    /**
     * 获取存储过程连接
     *
     * @param dbName 库名称
     * @param schema schema名称
     * @return 连接
     */
    public Connection getProcedureConnection(String dbName, String schema) {
        return this.connections.get(dbName + "_" + schema + "_procedure");
    }

    /**
     * 是否存在库连接
     *
     * @param dbName 库名称
     * @return 结果
     */
    public boolean hasConnection(String dbName) {
        return this.connections.containsKey(dbName);
    }

    /**
     * 销毁全部连接
     */
    public void destroy() {
        if (this.serverConnection != null) {
            try {
                this.serverConnection.close();
            } catch (SQLException ignored) {
            }
        }
        for (Connection value : connections.values()) {
            try {
                value.close();
            } catch (SQLException ignored) {
            }
        }
        this.connections.clear();
    }

    /**
     * 获取服务连接
     *
     * @return 服务连接
     */
    public Connection getServerConnection() {
        return serverConnection;
    }

    /**
     * 设置服务连接
     *
     * @param serverConnection 服务连接
     */
    public void setServerConnection(Connection serverConnection) {
        this.serverConnection = serverConnection;
    }

    /**
     * 获取全部库连接
     *
     * @return 库连接集合
     */
    public Map<String, Connection> getConnections() {
        return connections;
    }
}

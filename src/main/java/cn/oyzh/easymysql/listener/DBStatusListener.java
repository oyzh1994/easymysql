package cn.oyzh.easymysql.listener;

import javafx.beans.value.ChangeListener;

import java.util.UUID;

/**
 * 数据库状态监听器
 *
 * @author oyzh
 * @since 2024/7/23
 */
public abstract class DBStatusListener implements ChangeListener<Object> {

    /**
     * 监听键
     */
    private final String key;

    /**
     * 构造方法
     */
    public DBStatusListener() {
        this.key = UUID.randomUUID().toString();
        DBStatusListenerManager.addListener(this);
    }

    /**
     * 构造方法
     *
     * @param key 监听键
     */
    public DBStatusListener(String key) {
        this.key = key;
        DBStatusListenerManager.addListener(this);
    }

    /**
     * 构造方法
     *
     * @param dbName    数据库名称
     * @param tableName 表名称
     */
    public DBStatusListener( String dbName,  String tableName) {
        this(dbName + ":" + ":" + tableName);
    }

    /**
     * 构造方法
     *
     * @param dbName    数据库名称
     * @param schema    模式名称
     * @param tableName 表名称
     */
    public DBStatusListener( String dbName,  String schema,  String tableName) {
        this(dbName + ":" + schema + ":" + tableName);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DBStatusListenerManager.removeListener(this);
    }

    /**
     * 获取监听键
     *
     * @return 监听键
     */
    public String getKey() {
        return key;
    }
}

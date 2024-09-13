package cn.oyzh.easymysql.listener;

import cn.oyzh.easymysql.listener.DBListenerManager;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author oyzh
 * @since 2024/7/23
 */
public abstract class DBListener implements ChangeListener<Object> {

    @Getter
    private final String key;

    public DBListener(@NonNull String key) {
        this.key = key;
        DBListenerManager.addListener(this);
    }

    public DBListener(@NonNull String dbName, @NonNull String schema, @NonNull String tableName) {
        this(dbName + ":" + schema + ":" + tableName);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DBListenerManager.removeListener(this);
    }
}

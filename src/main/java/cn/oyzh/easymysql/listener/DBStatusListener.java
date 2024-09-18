package cn.oyzh.easymysql.listener;

import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

/**
 * @author oyzh
 * @since 2024/7/23
 */
public abstract class DBStatusListener implements ChangeListener<Object> {

    @Getter
    private final String key;

    public DBStatusListener() {
        this.key = UUID.randomUUID().toString();
        DBStatusListenerManager.addListener(this);
    }

    public DBStatusListener(@NonNull String key) {
        this.key = key;
        DBStatusListenerManager.addListener(this);
    }

    public DBStatusListener(@NonNull String dbName, @NonNull String tableName) {
        this(dbName + ":" + ":" + tableName);
    }

    public DBStatusListener(@NonNull String dbName, @NonNull String schema, @NonNull String tableName) {
        this(dbName + ":" + schema + ":" + tableName);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DBStatusListenerManager.removeListener(this);
    }
}

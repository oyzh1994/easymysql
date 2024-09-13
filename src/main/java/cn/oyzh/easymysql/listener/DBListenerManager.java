package cn.oyzh.easymysql.listener;

import javafx.beans.property.Property;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import org.fxmisc.richtext.GenericStyledArea;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/7/23
 */
public class DBListenerManager {

    private static final Map<String, DBListener> LISTENERS = new HashMap<>();

    public static void addListener(DBListener listener) {
        if (listener != null) {
            LISTENERS.put(listener.getKey(), listener);
        }
    }

    public static void removeListener(DBListener listener) {
        if (listener != null) {
            LISTENERS.remove(listener.getKey());
        }
    }

    public static DBListener getListener(String key) {
        return LISTENERS.get(key);
    }

    public static void bindListener(Object node, DBListener listener) {
        if (node instanceof GenericStyledArea<?, ?, ?> node1) {
            node1.textProperty().addListener((observable, oldValue, newValue) -> {
                if (listener != null) {
                    listener.changed(observable, oldValue, newValue);
                }
            });
        } else if (node instanceof TextInputControl node1) {
            node1.textProperty().addListener((observable, oldValue, newValue) -> {
                if (listener != null) {
                    listener.changed(observable, oldValue, newValue);
                }
            });
        } else if (node instanceof ComboBox<?> node1) {
            node1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (listener != null) {
                    listener.changed(observable, oldValue, newValue);
                }
            });
        } else if (node instanceof CheckBox checkBox) {
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (listener != null) {
                    listener.changed(observable, oldValue, newValue);
                }
            });
        } else if (node instanceof Property<?> property) {
            property.addListener((observable, oldValue, newValue) -> {
                if (listener != null) {
                    listener.changed(observable, oldValue, newValue);
                }
            });
        }
    }
}

package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.listener.DBStatusListener;
import cn.oyzh.easymysql.listener.DBStatusListenerManager;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * @author oyzh
 * @since 2024/07/22
 */
public class DBStatusTableView<S extends DBObjectStatus> extends FlexTableView<S> {

    public void clearStatus() throws Exception {
        for (DBObjectStatus object : this.getItems()) {
            object.clearStatus();
        }
    }

    @Getter
    @Setter
    private DBStatusListener statusListener;

    {
        this.itemList().addListener((ListChangeListener<S>) c -> {
            if (this.statusListener == null) {
                return;
            }
            if (!c.next()) {
                return;
            }
            if (c.wasReplaced()) {
                ObservableList<S> list = (ObservableList<S>) c.getList();
                if (list != null) {
                    for (S status : list) {
                        status.statusProperty().addListener(this.statusListener);
                    }
                }
            } else if (c.wasAdded()) {
                List<DBObjectStatus> list = (List<DBObjectStatus>) c.getAddedSubList();
                if (list != null) {
                    for (DBObjectStatus status : list) {
                        status.statusProperty().addListener(this.statusListener);
                    }
                }
            } else if (c.wasRemoved()) {
                List<DBObjectStatus> list = (List<DBObjectStatus>) c.getRemoved();
                if (list != null) {
                    for (DBObjectStatus status : list) {
                        status.statusProperty().removeListener(this.statusListener);
                    }
                }
            }
        });
    }


}

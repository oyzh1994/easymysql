package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.listener.DBStatusListener;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import javafx.collections.ListChangeListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * db状态表格视图
 *
 * @author oyzh
 * @since 2024/07/22
 */
public class DBStatusTableView<S extends DBObjectStatus> extends FXTableView<S> {

    /**
     * 删除项列表
     */
    private List<S> deleteItems;

    /**
     * 重置状态
     *
     * @throws Exception 异常
     */
    public void reset() throws Exception {
        this.deleteItems = null;
        this.clearStatus();
    }

    /**
     * 清除状态
     *
     * @throws Exception 异常
     */
    public void clearStatus() throws Exception {
        for (DBObjectStatus object : this.getItems()) {
            object.clearStatus();
        }
    }

    /**
     * 状态监听器
     */
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
                List<? extends S> list = c.getList();
                if (list != null) {
                    for (S status : list) {
                        status.statusProperty().addListener(this.statusListener);
                    }
                }
            } else if (c.wasAdded()) {
                List<? extends S> list = c.getAddedSubList();
                if (list != null) {
                    for (DBObjectStatus status : list) {
                        status.statusProperty().addListener(this.statusListener);
                    }
                }
                this.statusListener.changed(null, null, null);
            } else if (c.wasRemoved()) {
                List<? extends S> list = c.getRemoved();
                if (list != null) {
                    for (S status : list) {
                        if (!status.isCreated()) {
                            if (this.deleteItems == null) {
                                this.deleteItems = new CopyOnWriteArrayList<>();
                            }
                            this.deleteItems.add(status);
                        }
                        status.statusProperty().removeListener(this.statusListener);
                    }
                }
            }
        });
    }

    /** 获取删除项列表 */
    public List<S> getDeleteItems() {
        return deleteItems;
    }

    /** 设置删除项列表 */
    public void setDeleteItems(List<S> deleteItems) {
        this.deleteItems = deleteItems;
    }

    /** 获取状态监听器 */
    public DBStatusListener getStatusListener() {
        return statusListener;
    }

    /** 设置状态监听器 */
    public void setStatusListener(DBStatusListener statusListener) {
        this.statusListener = statusListener;
    }
}

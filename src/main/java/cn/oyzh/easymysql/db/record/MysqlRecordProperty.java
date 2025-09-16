package cn.oyzh.easymysql.db.record;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.event.MysqlEventUtil;
import cn.oyzh.easymysql.exception.DBException;
import cn.oyzh.easymysql.listener.DBStatusListener;
import cn.oyzh.easymysql.listener.DBStatusListenerManager;
import cn.oyzh.easymysql.util.DBNodeUtil;
import cn.oyzh.easymysql.util.DBRecordUtil;
import cn.oyzh.fx.plus.node.NodeUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * db表记录属性
 *
 * @author oyzh
 * @since 2024/01/31
 */
public class MysqlRecordProperty extends SimpleObjectProperty<Object> {

    /**
     * 是否变更
     */
    private SimpleBooleanProperty changedProperty;

    /**
     * 表字段
     */
    private MysqlColumn column;

    /**
     * 原始数据
     */
    private Object original;

    /**
     * 设置为null标志位
     */
    private boolean setToNullFlag;

    /**
     * 只读模式
     */
    private final boolean readonly;

    public MysqlRecordProperty(MysqlColumn column, Object value) {
        this(column, value, false);
    }

    public MysqlRecordProperty(MysqlColumn column, Object value, boolean readonly) {
        super(value);
        this.column = column;
        if (!readonly) {
            this.original = value;
        }
        this.readonly = readonly;
    }

    @Override
    public Object get() {
        if (this.readonly || !this.isChanged() || this.node == null) {
            return super.get();
        }
        if (this.setToNullFlag) {
            return null;
        }
        try {
            return DBNodeUtil.getNodeVal(this.node);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public void set(Object newValue) {
        super.set(newValue);
        if (this.node != null) {
            DBNodeUtil.setNodeVal(node, newValue);
        }
    }

    private Node node;

    @Override
    public Object getValue() {
        if (this.readonly) {
            return DBRecordUtil.formatValue(super.getValue(), this.column);
        }
        if (this.node == null) {
            this.node = DBRecordUtil.getNode(this, super.get(), this.column);
            TableViewUtil.rowOnCtrlS(this.node);
            TableViewUtil.selectRowOnMouseClicked(this.node);
        }
        return this.node;
    }

    /**
     * 抛弃
     */
    public void discard() {
        if (this.isChanged() && this.node != null) {
            DBNodeUtil.setNodeVal(this.node, super.get());
        }
        this.setChanged(false);
    }

    public SimpleBooleanProperty changedProperty() {
        if (this.changedProperty == null) {
            this.changedProperty = new SimpleBooleanProperty();
        }
        return this.changedProperty;
    }

    public boolean isChanged() {
        return this.changedProperty != null && this.changedProperty.get();
    }

    public void setChanged(boolean changed) {
        this.changedProperty().set(changed);
        DBStatusListener listener;
        if (column.getSchema() != null) {
            listener = DBStatusListenerManager.getListener(column.getDbName() + ":" + column.getSchema() + ":" + column.getTableName());
        } else {
            listener = DBStatusListenerManager.getListener(column.getDbName() + ":" + column.getTableName());
        }
        if (listener != null) {
            listener.changed(null, null, null);
        }
        this.setToNullFlag = false;
    }

    public void updateOriginal() throws Exception {
        if (this.node != null) {
            super.set(DBNodeUtil.getNodeVal(this.node));
            this.original = super.get();
        }
    }

    public Node getControl() {
        return this.node;
    }

    public void vCopy() {
        ClipboardUtil.copy(this.node);
    }

    public void vPaste() {
        ClipboardUtil.paste(this.node);
    }

    public void vDelete() {
        MysqlEventUtil.recordDelete();
    }

    public void vSetToNull() {
        if (this.node instanceof TextField textField) {
            // 如果内容为空，则直接设置变更
            if (StrUtil.isEmpty(textField.getText())) {
                this.setChanged(true);
            } else {
                textField.clear();
            }
            textField.setPromptText(DBRecordUtil.nullPromptText());
            NodeUtil.unFocus(this.node);
        }
        this.setToNullFlag = true;
    }

    public void vSetToEmptyString() {
        if (this.node instanceof TextField textField) {
            // 如果内容为空，则直接设置变更
            if (StrUtil.isEmpty(textField.getText())) {
                this.setChanged(true);
            } else {
                textField.setText("");
            }
            textField.setPromptText("");
            NodeUtil.unFocus(this.node);
        }
    }

    public MysqlColumn getColumn() {
        return column;
    }

    public void setColumn(MysqlColumn column) {
        this.column = column;
    }

    public Object getOriginal() {
        return original;
    }

    public void setOriginal(Object original) {
        this.original = original;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}

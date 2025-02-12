package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.fx.gui.text.field.SelectTextFiled;

/**
 * @author oyzh
 * @since 2024/7/12
 */
public class MysqlDefaultValueTextFiled extends SelectTextFiled {

    private boolean editableFlag;

    {
        this.selectIndexChanged((observable, oldValue, newValue) -> {
            if (this.editableFlag) {
                this.setEditable(newValue.intValue() == 0);
            }
        });
    }

    public void init(MysqlColumn column) {
        this.init(column, null);
    }

    public void init(MysqlColumn column, String defaultValue) {
        this.clear();
        this.clearData();
        if (column.supportEnum()) {
            this.editableFlag = false;
            this.setEditable(false);
            this.setItemList(column.getValueList());
            this.addData("NULL");
            if (defaultValue != null) {
                this.selectItem(defaultValue);
            } else {
                this.selectIndex(this.getItemSize());
            }
        } else {
            this.editableFlag = true;
            this.addData("");
            this.addData("EMPTY STRING");
            this.addData("NULL");
            if (defaultValue != null) {
                this.setEditable(true);
                this.setText(defaultValue);
            } else {
                this.setEditable(false);
                this.selectIndex(2);
            }
        }
    }

    public String getValue() {
        if (this.isEditable()) {
            return super.getTextTrim();
        }
        String text = super.getTextTrim();
        if ("NULL".equalsIgnoreCase(text)) {
            return null;
        }
        if ("EMPTY STRING".equalsIgnoreCase(text)) {
            return "";
        }
        return text;
    }
}

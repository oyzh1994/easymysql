package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.fx.plus.controls.select.SelectTextFiled;

/**
 * @author oyzh
 * @since 2024/7/12
 */
public class DBDefaultValueTextFiled extends SelectTextFiled {

    private boolean editableFlag = true;

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
        if (column.supportString() || column.supportBit()) {
            this.editableFlag = true;
            this.setEditable(false);
            this.addData("");
            this.addData("EMPTY STRING");
            this.addData("NULL");
        } else if (column.supportEnum()) {
            this.editableFlag = false;
            this.setEditable(false);
            this.setDataList(column.getValueList());
            this.addData("NULL");
        } else {
            this.editableFlag = true;
            this.setEditable(false);
            this.addData("");
            this.addData("NULL");
        }
        if (defaultValue != null) {
            this.setEditable(true);
            this.setText(defaultValue);
        } else if (this.editableFlag) {
            this.setEditable(true);
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

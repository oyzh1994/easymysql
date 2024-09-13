package cn.oyzh.easymysql.fx.table;

import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.fx.plus.controls.select.SelectTextFiled;

/**
 * @author oyzh
 * @since 2024/7/12
 */
public class DBDefaultValueTextFiled extends SelectTextFiled {

    {
        this.addData("");
        this.selectIndexChanged((observable, oldValue, newValue) -> this.setEditable(newValue.intValue() == 0));
    }

    public void init(DBColumn column) {
        if (column.supportString() || column.supportBit()) {
            this.addData("EMPTY STRING");
            this.addData("NULL");
        } else if (column.supportEnum()) {
            this.setEditable(false);
            this.setDataList(column.getValueList());
            this.addData("NULL");
        } else {
            this.addData("NULL");
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

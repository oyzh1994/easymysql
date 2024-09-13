package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * db类型选择框
 *
 * @author oyzh
 * @since 2023/12/15
 */
public class DBTypeComboBox extends FlexComboBox<DBDialect> {

    {
        this.setItem(DBDialect.valueList());
    }

    public String getType() {
        return this.getSelectedItem().name();
    }

    public boolean isMysql() {
        return this.getSelectedItem() == DBDialect.MYSQL;
    }

    public boolean isOracle() {
        return this.getSelectedItem() == DBDialect.ORACLE;
    }

    public void selectType(String type) {
        if (type != null) {
            for (DBDialect item : this.getItems()) {
                if (item.name().equals(type)) {
                    this.select(item);
                    break;
                }
            }
        }

    }

}

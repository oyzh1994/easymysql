package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * db类型选择框
 *
 * @author oyzh
 * @since 2023/12/15
 */
public class DBTypeComboBox extends FXComboBox<DBDialect> {

    {
        this.setItem(DBDialect.valueList());
    }

    /** 获取类型名称 */
    public String getType() {
        return this.getSelectedItem().name();
    }

    /** 是否mysql类型 */
    public boolean isMysql() {
        return this.getSelectedItem() == DBDialect.MYSQL;
    }

    /**
     * 选择类型
     *
     * @param type 类型名称
     */
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

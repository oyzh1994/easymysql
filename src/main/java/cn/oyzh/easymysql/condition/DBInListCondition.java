package cn.oyzh.easymysql.condition;

import cn.oyzh.easymysql.util.DBUtil;

/**
 * 在列表条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class DBInListCondition extends DBCondition {

    public final static DBInListCondition INSTANCE = new DBInListCondition();

    public DBInListCondition() {
        super("在列表", "IN");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return this.getValue() + " (" + DBUtil.wrapData(condition) + ")";
        }
        return super.wrapCondition(condition);
    }
}

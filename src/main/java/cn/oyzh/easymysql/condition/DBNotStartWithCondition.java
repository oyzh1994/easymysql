package cn.oyzh.easymysql.condition;

/**
 * 不是开始以条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBNotStartWithCondition extends DBCondition {

    public final static DBNotStartWithCondition INSTANCE = new DBNotStartWithCondition();

    public DBNotStartWithCondition() {
        super("不是开始以", "NOT LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition("%" + condition);
        }
        return super.wrapCondition(condition);
    }
}

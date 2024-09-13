package cn.oyzh.easymysql.condition;

/**
 * 不是结束以条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBNotEndWithCondition extends DBCondition {

    public final static DBNotEndWithCondition INSTANCE = new DBNotEndWithCondition();

    public DBNotEndWithCondition() {
        super("不是结束以", "NOT LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition(condition + "%");
        }
        return super.wrapCondition(condition);
    }
}

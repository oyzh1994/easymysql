package cn.oyzh.easymysql.condition;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBContainsCondition extends DBCondition {

    public final static DBContainsCondition INSTANCE = new DBContainsCondition();

    public DBContainsCondition() {
        super("包含", "LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition("%" + condition + "%");
        }
        return super.wrapCondition(condition);
    }
}

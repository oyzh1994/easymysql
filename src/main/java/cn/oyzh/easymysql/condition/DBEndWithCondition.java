package cn.oyzh.easymysql.condition;

/**
 * 结束以条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBEndWithCondition extends DBCondition {

    public final static DBEndWithCondition INSTANCE = new DBEndWithCondition();

    public DBEndWithCondition() {
        super("结束以", "LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition(condition + "%");
        }
        return super.wrapCondition(condition);
    }
}

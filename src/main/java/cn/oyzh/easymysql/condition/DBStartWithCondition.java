package cn.oyzh.easymysql.condition;

/**
 * 开始以条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBStartWithCondition extends DBCondition {

    public final static DBStartWithCondition INSTANCE = new DBStartWithCondition();

    public DBStartWithCondition() {
        super("开始以", "LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition("%" + condition);
        }
        return super.wrapCondition(condition);
    }
}

package cn.oyzh.easymysql.condition;

/**
 * 等于条件
 * @author oyzh
 * @since 2024/6/27
 */
public class DBEqCondition extends DBCondition {

    public final static DBEqCondition INSTANCE = new DBEqCondition();

    public DBEqCondition() {
        super("等于", "=");
    }
}

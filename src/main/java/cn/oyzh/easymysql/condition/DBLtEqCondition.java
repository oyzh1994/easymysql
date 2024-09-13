package cn.oyzh.easymysql.condition;

/**
 * 小于等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBLtEqCondition extends DBCondition {

    public final static DBLtEqCondition INSTANCE = new DBLtEqCondition();

    public DBLtEqCondition() {
        super("小于等于", "<=");
    }
}

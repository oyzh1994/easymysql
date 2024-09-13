package cn.oyzh.easymysql.condition;

/**
 * 大于等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBGtEqCondition extends DBCondition {

    public final static DBGtEqCondition INSTANCE = new DBGtEqCondition();

    public DBGtEqCondition() {
        super("大于等于", ">=");
    }
}

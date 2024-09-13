package cn.oyzh.easymysql.condition;

/**
 * 不等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBNotEqCondition extends DBCondition {

    public final static DBNotEqCondition INSTANCE = new DBNotEqCondition();

    public DBNotEqCondition() {
        super("不等于", "!=");
    }
}

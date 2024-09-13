package cn.oyzh.easymysql.condition;

/**
 * 小于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBLtCondition extends DBCondition {

    public final static DBLtCondition INSTANCE = new DBLtCondition();

    public DBLtCondition() {
        super("小于", "<");
    }
}

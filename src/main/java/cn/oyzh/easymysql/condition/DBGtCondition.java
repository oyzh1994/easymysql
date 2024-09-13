package cn.oyzh.easymysql.condition;

/**
 * 大于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBGtCondition extends DBCondition {

    public final static DBGtCondition INSTANCE = new DBGtCondition();

    public DBGtCondition() {
        super("大于", ">");
    }
}

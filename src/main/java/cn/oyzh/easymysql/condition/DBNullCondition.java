package cn.oyzh.easymysql.condition;

/**
 * 是NULL条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBNullCondition extends DBCondition {

    public final static DBNullCondition INSTANCE = new DBNullCondition();

    public DBNullCondition() {
        super("是NULL", "IS NULL", false);
    }
}

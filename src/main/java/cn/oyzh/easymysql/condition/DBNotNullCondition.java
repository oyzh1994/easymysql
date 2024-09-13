package cn.oyzh.easymysql.condition;

/**
 * 不是NULL条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBNotNullCondition extends DBCondition {

    public final static DBNotNullCondition INSTANCE = new DBNotNullCondition();

    public DBNotNullCondition() {
        super("不是NULL", "IS NOT NULL", false);
    }
}

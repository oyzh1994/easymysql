package cn.oyzh.easymysql.condition;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBNotEmptyCondition extends DBCondition {

    public final static DBNotEmptyCondition INSTANCE = new DBNotEmptyCondition();

    public DBNotEmptyCondition() {
        super("不是空的", "!=''", false);
    }

}

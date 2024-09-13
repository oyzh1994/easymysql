package cn.oyzh.easymysql.condition;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DBEmptyCondition extends DBCondition {

    public final static DBEmptyCondition INSTANCE = new DBEmptyCondition();

    public DBEmptyCondition() {
        super("是空的", "=''", false);
    }

}

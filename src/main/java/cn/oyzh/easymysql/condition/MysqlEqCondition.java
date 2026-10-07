package cn.oyzh.easymysql.condition;

/**
 * 等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlEqCondition extends MysqlCondition {

    /**
     * 等于条件实例
     */
    public final static MysqlEqCondition INSTANCE = new MysqlEqCondition();

    /**
     * 构造等于条件
     */
    public MysqlEqCondition() {
        super("等于", "=");
    }
}

package cn.oyzh.easymysql.condition;

/**
 * 大于等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlGtEqCondition extends MysqlCondition {

    /**
     * 大于等于条件实例
     */
    public final static MysqlGtEqCondition INSTANCE = new MysqlGtEqCondition();

    /**
     * 构造方法
     */
    public MysqlGtEqCondition() {
        super("大于等于", ">=");
    }
}

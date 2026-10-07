package cn.oyzh.easymysql.condition;

/**
 * 不等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlNotEqCondition extends MysqlCondition {

    /**
     * 不等于条件实例
     */
    public final static MysqlNotEqCondition INSTANCE = new MysqlNotEqCondition();

    /**
     * 构造方法
     */
    public MysqlNotEqCondition() {
        super("不等于", "!=");
    }
}

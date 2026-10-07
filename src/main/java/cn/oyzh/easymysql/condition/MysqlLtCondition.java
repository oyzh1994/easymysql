package cn.oyzh.easymysql.condition;

/**
 * 小于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlLtCondition extends MysqlCondition {

    /**
     * 小于条件实例
     */
    public final static MysqlLtCondition INSTANCE = new MysqlLtCondition();

    /**
     * 构造方法
     */
    public MysqlLtCondition() {
        super("小于", "<");
    }
}

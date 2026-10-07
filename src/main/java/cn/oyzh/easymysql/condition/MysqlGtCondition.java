package cn.oyzh.easymysql.condition;

/**
 * 大于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlGtCondition extends MysqlCondition {

    /**
     * 大于条件实例
     */
    public final static MysqlGtCondition INSTANCE = new MysqlGtCondition();

    /**
     * 构造方法
     */
    public MysqlGtCondition() {
        super("大于", ">");
    }
}

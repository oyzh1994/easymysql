package cn.oyzh.easymysql.condition;

/**
 * 不是NULL条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlNotNullCondition extends MysqlCondition {

    /**
     * 不是NULL条件实例
     */
    public final static MysqlNotNullCondition INSTANCE = new MysqlNotNullCondition();

    /**
     * 构造方法
     */
    public MysqlNotNullCondition() {
        super("不是NULL", "IS NOT NULL", false);
    }
}

package cn.oyzh.easymysql.condition;

/**
 * 是NULL条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlNullCondition extends MysqlCondition {

    /**
     * 是NULL条件实例
     */
    public final static MysqlNullCondition INSTANCE = new MysqlNullCondition();

    /**
     * 构造方法
     */
    public MysqlNullCondition() {
        super("是NULL", "IS NULL", false);
    }
}

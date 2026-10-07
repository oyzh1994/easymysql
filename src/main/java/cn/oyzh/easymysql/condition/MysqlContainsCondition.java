package cn.oyzh.easymysql.condition;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlContainsCondition extends MysqlCondition {

    /**
     * 包含条件实例
     */
    public final static MysqlContainsCondition INSTANCE = new MysqlContainsCondition();

    /**
     * 构造方法
     */
    public MysqlContainsCondition() {
        super("包含", "LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition("%" + condition + "%");
        }
        return super.wrapCondition(condition);
    }
}

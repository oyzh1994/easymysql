package cn.oyzh.easymysql.condition;

/**
 * 不以指定值开始条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlNotStartWithCondition extends MysqlCondition {

    /**
     * 不以指定值开始条件实例
     */
    public final static MysqlNotStartWithCondition INSTANCE = new MysqlNotStartWithCondition();

    /**
     * 构造方法
     */
    public MysqlNotStartWithCondition() {
        super("不是开始以", "NOT LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition("%" + condition);
        }
        return super.wrapCondition(condition);
    }
}

package cn.oyzh.easymysql.condition;

/**
 * 以指定值开始条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlStartWithCondition extends MysqlCondition {

    /**
     * 以指定值开始条件实例
     */
    public final static MysqlStartWithCondition INSTANCE = new MysqlStartWithCondition();

    /**
     * 构造方法
     */
    public MysqlStartWithCondition() {
        super("开始以", "LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition("%" + condition);
        }
        return super.wrapCondition(condition);
    }
}

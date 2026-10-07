package cn.oyzh.easymysql.condition;

/**
 * 以指定值结束条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlEndWithCondition extends MysqlCondition {

    /**
     * 以指定值结束条件实例
     */
    public final static MysqlEndWithCondition INSTANCE = new MysqlEndWithCondition();

    /**
     * 构造方法
     */
    public MysqlEndWithCondition() {
        super("结束以", "LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition(condition + "%");
        }
        return super.wrapCondition(condition);
    }
}

package cn.oyzh.easymysql.condition;

/**
 * 不以指定值结束条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlNotEndWithCondition extends MysqlCondition {

    /**
     * 不以指定值结束条件实例
     */
    public final static MysqlNotEndWithCondition INSTANCE = new MysqlNotEndWithCondition();

    /**
     * 构造方法
     */
    public MysqlNotEndWithCondition() {
        super("不是结束以", "NOT LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition(condition + "%");
        }
        return super.wrapCondition(condition);
    }
}

package cn.oyzh.easymysql.condition;

/**
 * 不为空条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlNotEmptyCondition extends MysqlCondition {

    /**
     * 不为空条件实例
     */
    public final static MysqlNotEmptyCondition INSTANCE = new MysqlNotEmptyCondition();

    /**
     * 构造方法
     */
    public MysqlNotEmptyCondition() {
        super("不是空的", "!=''", false);
    }

}

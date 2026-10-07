package cn.oyzh.easymysql.condition;

/**
 * 为空条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlEmptyCondition extends MysqlCondition {

    /**
     * 为空条件实例
     */
    public final static MysqlEmptyCondition INSTANCE = new MysqlEmptyCondition();

    /**
     * 构造方法
     */
    public MysqlEmptyCondition() {
        super("是空的", "=''", false);
    }

}

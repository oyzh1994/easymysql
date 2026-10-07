package cn.oyzh.easymysql.condition;

import cn.oyzh.easymysql.util.DBUtil;

/**
 * 不在列表条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class MysqlNotInListCondition extends MysqlCondition {

    /**
     * 不在列表条件实例
     */
    public final static MysqlNotInListCondition INSTANCE = new MysqlNotInListCondition();

    /**
     * 构造方法
     */
    public MysqlNotInListCondition() {
        super("不在列表", "NOT IN");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return this.getValue() + " (" + DBUtil.wrapData(condition) + ")";
        }
        return super.wrapCondition(condition);
    }
}

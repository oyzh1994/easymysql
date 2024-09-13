package cn.oyzh.easymysql.condition;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.util.DBUtil;

import java.util.Collection;

/**
 * 不介于列表条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class DBNotBetweenCondition extends DBCondition {

    public final static DBNotBetweenCondition INSTANCE = new DBNotBetweenCondition();

    public DBNotBetweenCondition() {
        super("不介于", "NOT BETWEEN");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition instanceof Object[] arr) {
            return this.getValue() + " " + DBUtil.wrapData(arr[0]) + " AND " + DBUtil.wrapData(arr[1]);
        }
        if (condition instanceof Collection coll) {
            return this.getValue() + " " + DBUtil.wrapData(CollUtil.get(coll, 0)) + " AND " + DBUtil.wrapData(CollUtil.get(coll, 1));
        }
        return super.wrapCondition(condition);
    }
}

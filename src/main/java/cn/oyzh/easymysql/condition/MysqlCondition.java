package cn.oyzh.easymysql.condition;

import cn.oyzh.easymysql.util.DBUtil;
import lombok.Data;

/**
 * 条件
 *
 * @author oyzh
 * @since 2024/06/26
 */
@Data
public abstract class MysqlCondition {

    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 需要条件标志位
     */
    private boolean requireCondition = true;

    public MysqlCondition() {

    }

    public MysqlCondition(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public MysqlCondition(String name, String value, boolean requireCondition) {
        this.name = name;
        this.value = value;
        this.requireCondition = requireCondition;
    }

    public String wrapCondition() {
        return this.wrapCondition(null);
    }

    public String wrapCondition(Object condition) {
        if (this.requireCondition) {
            return condition == null ? this.getValue() : this.getValue() + " " +  DBUtil.wrapData(condition);
        }
        return this.getValue();
    }
}

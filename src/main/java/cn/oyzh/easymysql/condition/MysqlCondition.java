package cn.oyzh.easymysql.condition;

import cn.oyzh.easymysql.util.DBUtil;

/**
 * 条件
 *
 * @author oyzh
 * @since 2024/06/26
 */
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

    /**
     * 构造方法
     */
    public MysqlCondition() {

    }

    /**
     * 构造方法
     *
     * @param name  名称
     * @param value 值
     */
    public MysqlCondition(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 构造方法
     *
     * @param name             名称
     * @param value            值
     * @param requireCondition 是否需要条件
     */
    public MysqlCondition(String name, String value, boolean requireCondition) {
        this.name = name;
        this.value = value;
        this.requireCondition = requireCondition;
    }

    /**
     * 包装条件
     *
     * @return 条件内容
     */
    public String wrapCondition() {
        return this.wrapCondition(null);
    }

    /**
     * 包装条件
     *
     * @param condition 条件值
     * @return 条件内容
     */
    public String wrapCondition(Object condition) {
        if (this.requireCondition) {
            return condition == null ? this.getValue() : this.getValue() + " " +  DBUtil.wrapData(condition);
        }
        return this.getValue();
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取值
     *
     * @return 值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 是否需要条件
     *
     * @return 结果
     */
    public boolean isRequireCondition() {
        return requireCondition;
    }

    /**
     * 设置是否需要条件
     *
     * @param requireCondition 是否需要条件
     */
    public void setRequireCondition(boolean requireCondition) {
        this.requireCondition = requireCondition;
    }
}

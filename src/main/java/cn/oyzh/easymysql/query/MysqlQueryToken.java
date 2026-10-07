package cn.oyzh.easymysql.query;


import cn.oyzh.common.util.StringUtil;

/**
 * 查询提示词
 *
 * @author oyzh
 * @since 2024/8/15
 */
public class MysqlQueryToken {

    /**
     * 结束位置
     */
    private int endIndex;

    /**
     * 开始位置
     */
    private int startIndex;

    /**
     * 内容
     */
    private String content;

    /**
     * 1 空格
     * 2 .
     * 3 `
     */
    private Character token;

    /**
     * 是否为空
     *
     * @return 结果
     */
    public boolean isEmpty() {
        return StringUtil.isEmpty(this.content);
    }

    /**
     * 是否不为空
     *
     * @return 结果
     */
    public boolean isNotEmpty() {
        return StringUtil.isNotEmpty(this.content);
    }

    /**
     * 是否可能是关键字
     *
     * @return 结果
     */
    public boolean isPossibilityKeyword() {
        return ' ' == this.token || '\n' == this.token || '\0' == this.token;
    }

    /**
     * 是否可能是表
     *
     * @return 结果
     */
    public boolean isPossibilityTable() {
        return true;
    }

    /**
     * 是否可能是视图
     *
     * @return 结果
     */
    public boolean isPossibilityView() {
        return true;
    }

    /**
     * 是否可能是函数
     *
     * @return 结果
     */
    public boolean isPossibilityFunction() {
        return true;
    }

    /**
     * 是否可能是存储过程
     *
     * @return 结果
     */
    public boolean isPossibilityProcedure() {
        return true;
    }

    /**
     * 是否可能是字段
     *
     * @return 结果
     */
    public boolean isPossibilityColumn() {
        return true;
        // return '`' == this.token || '.' == this.token;
    }

    /**
     * 是否可能是数据库
     *
     * @return 结果
     */
    public boolean isPossibilityDatabase() {
        return '`' == this.token || ' ' == this.token;
    }

    /**
     * 获取结束位置
     *
     * @return 结束位置
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * 设置结束位置
     *
     * @param endIndex 结束位置
     */
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    /**
     * 获取开始位置
     *
     * @return 开始位置
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * 设置开始位置
     *
     * @param startIndex 开始位置
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取分隔符
     *
     * @return 分隔符
     */
    public Character getToken() {
        return token;
    }

    /**
     * 设置分隔符
     *
     * @param token 分隔符
     */
    public void setToken(Character token) {
        this.token = token;
    }
}

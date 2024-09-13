package cn.oyzh.easymysql.query;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.ToString;

/**
 * @author oyzh
 * @since 2024/8/15
 */
@Data
@ToString
public class DBQueryToken {

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

    public boolean isEmpty() {
        return StrUtil.isEmpty(this.content);
    }

    public boolean isNotEmpty() {
        return StrUtil.isNotEmpty(this.content);
    }

    public boolean isPossibilityKeyword() {
        return ' ' == this.token;
    }

    public boolean isPossibilityTable() {
        return true;
    }

    public boolean isPossibilityView() {
        return true;
    }

    public boolean isPossibilityFunction() {
        return true;
    }

    public boolean isPossibilityProcedure() {
        return true;
    }

    public boolean isPossibilityColumn() {
        return true;
        // return '`' == this.token || '.' == this.token;
    }

    public boolean isPossibilityDatabase() {
        return '`' == this.token || ' ' == this.token;
    }
}

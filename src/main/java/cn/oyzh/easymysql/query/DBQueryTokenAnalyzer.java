package cn.oyzh.easymysql.query;


import cn.oyzh.common.util.ArrayUtil;
import cn.oyzh.common.util.StringUtil;

/**
 * db查询文本域
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class DBQueryTokenAnalyzer {

    public static final DBQueryTokenAnalyzer INSTANCE = new DBQueryTokenAnalyzer();

    public DBQueryToken currentToken(String content, int currentIndex) {
        try {
            if (StringUtil.isEmpty(content)) {
                return null;
            }
            if (currentIndex <= 0) {
                return null;
            }
            if (currentIndex > content.length()) {
                return null;
            }
            DBQueryToken token = new DBQueryToken();
            // 截取字符串
            content = content.substring(0, currentIndex);
            // 当前位置
            int tokenIndex = 0;
            Character tokenType = null;
            char[] chars = ArrayUtil.reverse(content.toCharArray());
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                // 遇到换行符则停止
                if (c == '\n') {
                    return null;
                }
                // 寻找操作符
                if (c == ' ' || c == '`' || c == '.') {
                    tokenType = c;
                    tokenIndex = chars.length - i - 1;
                    break;
                }
            }
            if (tokenType == null) {
                return null;
            }
            String tokenContent = content.substring(tokenIndex);
            token.setToken(tokenType);
            token.setEndIndex(currentIndex);
            token.setStartIndex(tokenIndex + 1);
            token.setContent(tokenContent.trim());
            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

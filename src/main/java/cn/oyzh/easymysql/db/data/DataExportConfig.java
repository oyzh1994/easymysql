package cn.oyzh.easymysql.db.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

/**
 * @author oyzh
 * @since 2024/09/02
 */
@Data
@Accessors(chain = true, fluent = true)
public class DataExportConfig {

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 字段作为属性
     */
    private boolean fieldToAttr;

    /**
     * 包含列标题
     */
    private boolean includeFields = true;

    /**
     * 记录分割符号
     */
    private String recordSeparator = System.lineSeparator();

    /**
     * 字段分割符号
     */
    private String fieldSeparator = ";";

    /**
     * 文本识别符号
     */
    private String txtIdentifier = "\"";

    /**
     * 字符集
     */
    private String charset = StandardCharsets.UTF_8.displayName();

    /**
     * 早期版本
     */
    private boolean earlyVersion;

}

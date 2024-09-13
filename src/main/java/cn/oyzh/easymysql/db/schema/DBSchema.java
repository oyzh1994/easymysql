package cn.oyzh.easymysql.db.schema;

import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class DBSchema {

    /**
     * 模式名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 库名称
     */
    @Setter
    @Getter
    private String dbName;

}

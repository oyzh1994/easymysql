package cn.oyzh.easymysql.db;


import com.alibaba.druid.DbType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库类型(方言)
 *
 * @author oyzh
 * @since 2024/2/20
 */
public enum DBDialect {
    /**
     * mysql
     */
    MYSQL;

    /**
     * 获取数据库类型
     *
     * @return 数据库类型
     */
    public DbType dbType() {
        switch (this) {
            case MYSQL:
                return DbType.mysql;
            default:
                return DbType.mysql;
        }
    }

    /**
     * 获取全部数据库类型
     *
     * @return 数据库类型列表
     */
    public static List<DBDialect> valueList() {
        List<DBDialect> list = new ArrayList<>();
        Collections.addAll(list, values());
        return list;
    }


}

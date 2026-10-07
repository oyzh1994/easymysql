package cn.oyzh.easymysql.mysql.index;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.Collection;

/**
 * MySQL表索引列表
 *
 * @author oyzh
 * @since 2024/01/24
 */
public class MysqlIndexes extends DBObjectList<MysqlIndex> {

    /**
     * 构建索引列表
     */
    public MysqlIndexes() {

    }

    /**
     * 构建索引列表
     *
     * @param list 索引列表
     */
    public MysqlIndexes(Collection<MysqlIndex> list) {
        super.addAll(list);
    }
}

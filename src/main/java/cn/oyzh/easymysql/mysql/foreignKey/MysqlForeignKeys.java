package cn.oyzh.easymysql.mysql.foreignKey;

import cn.oyzh.easymysql.db.DBObjectList;

import java.util.Collection;

/**
 * db外键列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MysqlForeignKeys extends DBObjectList<MysqlForeignKey> {

    /**
     * 构建外键列表
     */
    public MysqlForeignKeys() {

    }

    /**
     * 根据外键集合构建外键列表
     *
     * @param list 外键集合
     */
    public MysqlForeignKeys(Collection<MysqlForeignKey> list) {
        super.addAll(list);
    }
}




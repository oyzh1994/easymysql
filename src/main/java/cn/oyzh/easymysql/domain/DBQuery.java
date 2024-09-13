package cn.oyzh.easymysql.domain;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.Data;
import lombok.NonNull;

/**
 * db查询
 *
 * @author oyzh
 * @since 2024/02/18
 */
@Data
public class DBQuery implements Comparable<DBQuery>, ObjectComparator<DBQuery> {

    /**
     * 数据id
     */
    private String id;

    /**
     * 连接id
     */
    private String iid;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    /**
     * 复制对象
     *
     * @param query db信息
     * @return 当前对象
     */
    public DBQuery copy(@NonNull DBQuery query) {
        this.iid = query.iid;
        this.name = query.name;
        this.dbName = query.dbName;
        this.content = query.content;
        return this;
    }

    @Override
    public int compareTo(DBQuery t1) {
        if (t1 == null) {
            return 1;
        }
        return StrUtil.compare(t1.id, this.id, true);
    }

    @Override
    public boolean compare(DBQuery t1) {
        if (t1 == null) {
            return false;
        }
        return StrUtil.equals(this.id, t1.id);
    }

    public boolean isNew() {
        return this.getId() == null;
    }
}

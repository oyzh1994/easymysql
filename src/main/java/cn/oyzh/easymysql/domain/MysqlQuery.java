package cn.oyzh.easymysql.domain;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.util.ObjectComparator;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * db查询
 *
 * @author oyzh
 * @since 2024/02/18
 */
@Data
@Table("t_query")
public class MysqlQuery implements Serializable, Comparable<MysqlQuery>, ObjectComparator<MysqlQuery> {

    /**
     * 数据id
     */
    @Column
    @PrimaryKey
    private String uid;

    /**
     * 连接id
     */
    @Column
    private String iid;

    /**
     * 数据库名称
     */
    @Column
    private String dbName;

    /**
     * 名称
     */
    @Column
    private String name;

    /**
     * 内容
     */
    @Column
    private String content;

    /**
     * 复制对象
     *
     * @param query db信息
     * @return 当前对象
     */
    public MysqlQuery copy(@NonNull MysqlQuery query) {
        this.iid = query.iid;
        this.name = query.name;
        this.dbName = query.dbName;
        this.content = query.content;
        return this;
    }

    @Override
    public int compareTo(MysqlQuery t1) {
        if (t1 == null) {
            return 1;
        }
        return StrUtil.compare(t1.uid, this.uid, true);
    }

    @Override
    public boolean compare(MysqlQuery t1) {
        if (t1 == null) {
            return false;
        }
        return StrUtil.equals(this.uid, t1.uid);
    }

    public boolean isNew() {
        return this.getUid() == null;
    }
}

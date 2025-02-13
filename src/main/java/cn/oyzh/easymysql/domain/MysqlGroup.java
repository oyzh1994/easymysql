package cn.oyzh.easymysql.domain;


import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.util.ObjectComparator;
import cn.oyzh.fx.plus.domain.TreeGroup;
import cn.oyzh.store.jdbc.Table;

/**
 * @author oyzh
 * @since 2023/12/15
 */
@Table("t_group")
public class MysqlGroup extends TreeGroup implements ObjectComparator<MysqlGroup> {

    public MysqlGroup() {
        super();
    }

    public MysqlGroup(String name, String groupId, boolean expand) {
        super(name, groupId, expand);
    }

    @Override
    public boolean compare(MysqlGroup t1) {
        if (t1 == null) {
            return false;
        }
        return StrUtil.equals(this.getName(), t1.getName());
    }
}

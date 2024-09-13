package cn.oyzh.easymysql.domain;


import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.util.ObjectComparator;
import cn.oyzh.fx.plus.domain.TreeGroup;

/**
 * @author oyzh
 * @since 2023/12/15
 */
public class DBGroup extends TreeGroup implements ObjectComparator<DBGroup> {

    public DBGroup() {
        super();
    }

    public DBGroup(String name, String groupId, boolean expand) {
        super(name, groupId, expand);
    }

    @Override
    public boolean compare(DBGroup t1) {
        if (t1 == null) {
            return false;
        }
        return StrUtil.equals(this.getName(), t1.getName());
    }
}

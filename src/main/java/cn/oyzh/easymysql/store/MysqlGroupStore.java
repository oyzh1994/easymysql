package cn.oyzh.easymysql.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.domain.MysqlGroup;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.QueryParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * db分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class MysqlGroupStore extends JdbcStandardStore<MysqlGroup> {

    /**
     * 当前实例
     */
    public static final MysqlGroupStore INSTANCE = new MysqlGroupStore();

    /**
     * 加载列表
     *
     * @return 分组列表
     */
    public List<MysqlGroup> load() {
        return super.selectList();
    }

    /**
     * 替换
     *
     * @param group 分组
     * @return 结果
     */
    public boolean replace(MysqlGroup group) {
        if (group != null) {
            if (this.exist(group.getName()) || super.exist(group.getGid())) {
                return this.update(group);
            }
            return this.insert(group);
        }
        return false;
    }

    /**
     * 根据名称删除
     *
     * @param name 分组名称
     * @return 结果
     */
    public boolean delete(String name) {
        if (StringUtil.isNotBlank(name)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("name", name));
            return this.delete(param);
        }
        return false;
    }

    /**
     * 是否存在此分组信息
     *
     * @param name 分组信息
     * @return 结果
     */
    public boolean exist(String name) {
        if (StringUtil.isNotBlank(name)) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected Class<MysqlGroup> modelClass() {
        return MysqlGroup.class;
    }
}

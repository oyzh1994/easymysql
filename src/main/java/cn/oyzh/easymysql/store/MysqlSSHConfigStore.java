package cn.oyzh.easymysql.store;

import cn.oyzh.easymysql.domain.MysqlSSHConfig;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class MysqlSSHConfigStore extends JdbcStandardStore<MysqlSSHConfig> {

    /**
     * 当前实例
     */
    public static final MysqlSSHConfigStore INSTANCE = new MysqlSSHConfigStore();

    public boolean replace(MysqlSSHConfig model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            return super.update(model);
        }
        return this.insert(model);
    }

    @Override
    protected Class<MysqlSSHConfig> modelClass() {
        return MysqlSSHConfig.class;
    }

    public void deleteByIid(String iid) {
        DeleteParam param = new DeleteParam();
        param.addQueryParam(QueryParam.of("iid", iid));
        super.delete(param);
    }

    public MysqlSSHConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}

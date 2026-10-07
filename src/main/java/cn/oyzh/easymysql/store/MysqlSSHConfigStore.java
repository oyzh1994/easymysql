package cn.oyzh.easymysql.store;

import cn.oyzh.easymysql.domain.MysqlSSHConfig;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.QueryParam;

/**
 * db SSH配置存储
 *
 * @author oyzh
 * @since 2024/09/26
 */
public class MysqlSSHConfigStore extends JdbcStandardStore<MysqlSSHConfig> {

    /**
     * 当前实例
     */
    public static final MysqlSSHConfigStore INSTANCE = new MysqlSSHConfigStore();

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
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

    /**
     * 根据连接id删除
     *
     * @param iid 连接id
     */
    public void deleteByIid(String iid) {
        DeleteParam param = new DeleteParam();
        param.addQueryParam(QueryParam.of("iid", iid));
        super.delete(param);
    }

    /**
     * 根据连接id获取
     *
     * @param iid 连接id
     * @return ssh配置
     */
    public MysqlSSHConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}

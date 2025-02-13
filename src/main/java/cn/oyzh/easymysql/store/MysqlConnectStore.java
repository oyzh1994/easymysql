package cn.oyzh.easymysql.store;

import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.domain.MysqlSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class MysqlConnectStore extends JdbcStandardStore<MysqlConnect> {

    /**
     * 当前实例
     */
    public static final MysqlConnectStore INSTANCE = new MysqlConnectStore();

    /**
     * ssh配置存储
     */
    private final MysqlSSHConfigStore sshConfigStore = MysqlSSHConfigStore.INSTANCE;

    /**
     * 加载列表
     *
     * @return redis连接列表
     */
    public List<MysqlConnect> load() {
        return super.selectList();
    }

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
    public boolean replace(MysqlConnect model) {
        boolean result = false;
        if (model != null) {
            if (super.exist(model.getId())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }

            // ssh处理
            MysqlSSHConfig sshConfig = model.getSshConfig();
            if (sshConfig != null) {
                sshConfig.setIid(model.getId());
                this.sshConfigStore.replace(sshConfig);
            } else {
                this.sshConfigStore.deleteByIid(model.getId());
            }
        }
        return result;
    }

    @Override
    public boolean delete(MysqlConnect model) {
        boolean result = super.delete(model);
        // 删除关联配置
        if (result) {
            this.sshConfigStore.deleteByIid(model.getId());
        }
        return result;
    }

    @Override
    protected Class<MysqlConnect> modelClass() {
        return MysqlConnect.class;
    }
}

package cn.oyzh.easymysql.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.domain.MysqlSetting;
import cn.oyzh.store.jdbc.JdbcKeyValueStore;
import cn.oyzh.store.json.ObjectFileStore;
import lombok.NonNull;


/**
 * db设置储存
 *
 * @author oyzh
 * @since 2022/8/26
 */
public class MysqlSettingStore extends JdbcKeyValueStore<MysqlSetting> {

    /**
     * 当前实例
     */
    public static final MysqlSettingStore INSTANCE = new MysqlSettingStore();

    /**
     * 当前设置
     */
    public static final MysqlSetting SETTING = INSTANCE.load();

    public MysqlSetting load() {
        MysqlSetting setting = null;
        try {
            setting = super.select();
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("load setting error", ex);
        }
        if (setting == null) {
            setting = new MysqlSetting();
        }
        return setting;
    }

    public boolean replace(MysqlSetting model) {
        if (model != null) {
            return this.update(model);
        }
        return false;
    }

    @Override
    protected Class<MysqlSetting> modelClass() {
        return MysqlSetting.class;
    }
}

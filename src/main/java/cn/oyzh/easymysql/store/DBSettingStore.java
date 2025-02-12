package cn.oyzh.easymysql.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.JulLog;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.domain.MysqlSetting;
import cn.oyzh.fx.common.store.ObjectFileStore;
import lombok.NonNull;


/**
 * db设置储存
 *
 * @author oyzh
 * @since 2022/8/26
 */
public class DBSettingStore extends ObjectFileStore<MysqlSetting> {

    /**
     * 当前实例
     */
    public static final DBSettingStore INSTANCE = new DBSettingStore();

    /**
     * 当前设置
     */
    public static final MysqlSetting SETTING = INSTANCE.load();

    {
        this.filePath(MysqlConst.STORE_PATH + "db_setting.json");
        JulLog.info("dbSettingStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized MysqlSetting load() {
        MysqlSetting setting = null;
        try {
            // 读取配置文件内容
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isNotBlank(text)) {
                // 将配置文件内容解析为dbSetting对象
                setting = JSONUtil.toBean(text, MysqlSetting.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 如果解析失败，则返回一个新的dbSetting对象
        if (setting == null) {
            setting = new MysqlSetting();
        }
        return setting;
    }

    @Override
    public boolean update(@NonNull MysqlSetting setting) {
        return this.saveData(setting);
    }
}

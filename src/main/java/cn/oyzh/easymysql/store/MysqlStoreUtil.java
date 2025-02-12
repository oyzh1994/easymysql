package cn.oyzh.easymysql.store;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.store.jdbc.JdbcConst;
import cn.oyzh.store.jdbc.JdbcDialect;
import cn.oyzh.store.jdbc.JdbcManager;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-23
 */
@UtilityClass
public class MysqlStoreUtil {

    /**
     * 执行初始化
     */
    public static void init() {
        JdbcConst.dbCacheSize(65535);
        JdbcConst.dbPageSize(1024);
        JdbcConst.dbDialect(JdbcDialect.H2);
        JdbcConst.dbFile(MysqlConst.STORE_PATH + "db");
        try {
            JdbcManager.takeoff();
        } catch (Exception ex) {
            if (StringUtil.containsAny(ex.getMessage(), "Database may be already in use")) {
                MessageBox.warn(I18nHelper.programTip1());
            }
        }
    }
}

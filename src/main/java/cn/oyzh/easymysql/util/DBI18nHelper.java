package cn.oyzh.easymysql.util;

import cn.oyzh.fx.plus.i18n.I18nResourceBundle;

/**
 * @author oyzh
 * @since 2024/07/26
 */
public class DBI18nHelper {

    public static final String TABLE_TIP2 = "db.table.tip2";

    public static String tableTip2() {
        return I18nResourceBundle.i18nString(TABLE_TIP2);
    }

}

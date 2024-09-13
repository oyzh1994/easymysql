package cn.oyzh.easymysql;

import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * db常量对象
 *
 * @author oyzh
 * @since 2023/06/16
 */
@UtilityClass
public class MysqlConst {

    // /**
    //  * tab基础地址
    //  */
    // public final static String TAB_BASE_PATH = "/tabs/";
    //
    // /**
    //  * fxml基础地址
    //  */
    // public final static String FXML_BASE_PATH = "/views/";
    //
    // /**
    //  * popup基础地址
    //  */
    // public final static String POPUP_BASE_PATH = "/popups/";

    /**
     * 数据保存路径
     */
    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easymysql" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/db_clip.png";

}

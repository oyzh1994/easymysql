package cn.oyzh.easymysql;

import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easymysql.controller.MainController;
import cn.oyzh.easymysql.exception.DBExceptionParser;
import cn.oyzh.easymysql.store.DBSettingStore;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.i18n.I18nManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.spring.SpringApplication;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2023/12/22
 */
@SpringBootApplication(scanBasePackages = "cn.oyzh",
        exclude = {
                AopAutoConfiguration.class,
                CacheAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                MessageSourceAutoConfiguration.class,
                TaskExecutionAutoConfiguration.class,
                TaskSchedulingAutoConfiguration.class,
                SqlInitializationAutoConfiguration.class,
        }
)
@EnableSpringUtil
public class EasyMysqlApp extends SpringApplication implements CommandLineRunner, DisposableBean {

    public static void main(String[] args) {
        launchSpring(EasyMysqlApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 禁用css日志
            FXUtil.disableCSSLogger();
            // 应用区域
            I18nManager.apply(DBSettingStore.SETTING.getLocale());
            // 应用字体
            FontManager.apply(DBSettingStore.SETTING.fontConfig());
            // 应用主题
            ThemeManager.apply(DBSettingStore.SETTING.themeConfig());
            // 应用透明度
            OpacityManager.apply(DBSettingStore.SETTING.getOpacity());
            // 注册异常处理器
            MessageBox.registerExceptionParser(DBExceptionParser.INSTANCE);
            // 开始执行业务
            super.start(primaryStage);
            // 显示主页面
            StageManager.showStage(MainController.class);
            // 开启定期gc
            SystemUtil.gcInterval(60_000);
            // 设置stage全部关闭后不自动销毁进程
            Platform.setImplicitExit(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        StaticLog.info("EasyDBApp destroyed.");
    }

    @Override
    public void run(String... args) {
        StaticLog.info("EasyDBApp started.");
    }
}

package cn.oyzh.easymysql.generator.event;

import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.mysql.event.MysqlEvent;

/**
 * 事件创建sql生成器
 *
 * @author oyzh
 * @since 2024/09/09
 */
public abstract class EventCreateSqlGenerator {

    /**
     * 数据库方言
     */
    private DBDialect dialect;

    /**
     * 构造方法
     *
     * @param dialect 数据库方言
     */
    public EventCreateSqlGenerator(DBDialect dialect) {
        this.dialect = dialect;
    }

    /**
     * 生成sql
     *
     * @param event 事件
     * @return sql语句
     */
    public abstract String generate(MysqlEvent event);

    /**
     * 生成sql
     *
     * @param dialect 数据库方言
     * @param event   事件
     * @return sql语句
     */
    public static String generate(DBDialect dialect, MysqlEvent event) {
        return switch (dialect) {
            case MYSQL -> new MysqlEventCreateSqlGenerator().generate(event);
            default -> null;
        };
    }

    /**
     * 获取数据库方言
     *
     * @return 数据库方言
     */
    public DBDialect getDialect() {
        return dialect;
    }

    /**
     * 设置数据库方言
     *
     * @param dialect 数据库方言
     */
    public void setDialect(DBDialect dialect) {
        this.dialect = dialect;
    }
}

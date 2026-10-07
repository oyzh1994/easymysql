package cn.oyzh.easymysql.sql;

import cn.oyzh.easymysql.db.DBDialect;

import java.util.List;

/**
 * sql解析器
 *
 * @author oyzh
 * @since 2024/1/26
 */
public abstract class DBSqlParser {

    /**
     * sql内容
     */
    protected final String sqlContent;

    /**
     * 数据库方言
     */
    protected final DBDialect dialect;

    /**
     * 构造方法
     *
     * @param sqlContent sql内容
     * @param dialect    数据库方言
     */
    public DBSqlParser(String sqlContent, DBDialect dialect) {
        this.sqlContent = sqlContent;
        this.dialect = dialect;
    }

    /**
     * 移除注释
     *
     * @return 移除注释后的sql内容
     */
    public abstract String removeComment();

    // public abstract DBSqlNodes parseNode() throws Exception;

    /**
     * 是否单条sql
     *
     * @return 结果
     */
    public abstract boolean isSingle();

    /**
     * 是否查询语句
     *
     * @return 结果
     */
    public abstract boolean isSelect();

    /**
     * 是否全字段查询
     *
     * @return 结果
     */
    public abstract boolean isFullColumn();

    /**
     * 解析sql
     *
     * @return sql列表
     * @throws Exception 异常
     */
    public abstract List<String> parseSql() throws Exception;

    /**
     * 解析单条sql
     *
     * @return sql语句
     * @throws Exception 异常
     */
    public abstract String parseSingleSql() throws Exception;

    /**
     * 格式化sql
     *
     * @return sql语句
     * @throws Exception 异常
     */
    public abstract String prettySql() throws Exception;

    /**
     * 格式化sql
     *
     * @param sql     sql语句
     * @param dialect 数据库方言
     * @return sql语句
     * @throws Exception 异常
     */
    public static String prettySql(String sql, DBDialect dialect) throws Exception {
        return getParser(sql, dialect).prettySql();
    }

    /**
     * 解析sql
     *
     * @param sql     sql语句
     * @param dialect 数据库方言
     * @return sql列表
     * @throws Exception 异常
     */
    public static List<String> parseSql(String sql, DBDialect dialect) throws Exception {
        return getParser(sql, dialect).parseSql();
    }

    /**
     * 获取解析器
     *
     * @param sql     sql语句
     * @param dialect 数据库方言
     * @return sql解析器
     * @throws Exception 异常
     */
    public static DBSqlParser getParser(String sql, DBDialect dialect) throws Exception {
        return new DruidSqlParser(sql, dialect);
    }
}

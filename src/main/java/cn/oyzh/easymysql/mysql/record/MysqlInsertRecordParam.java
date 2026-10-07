package cn.oyzh.easymysql.mysql.record;

/**
 * MySQL新增记录参数
 *
 * @author oyzh
 * @since 2024-09-13
 */
public class MysqlInsertRecordParam {

    /**
     * 库名称
     */
    private String dbName;

    /**
     * 模式名称
     */
    private String schema;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 记录数据
     */
    private MysqlRecordData record;

    /**
     * 记录主键
     */
    private MysqlRecordPrimaryKey primaryKey;

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置库名称
     *
     * @param dbName 库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取模式名称
     *
     * @return 模式名称
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置模式名称
     *
     * @param schema 模式名称
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名称
     *
     * @param tableName 表名称
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取记录数据
     *
     * @return 记录数据
     */
    public MysqlRecordData getRecord() {
        return record;
    }

    /**
     * 设置记录数据
     *
     * @param record 记录数据
     */
    public void setRecord(MysqlRecordData record) {
        this.record = record;
    }

    /**
     * 获取记录主键
     *
     * @return 记录主键
     */
    public MysqlRecordPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * 设置记录主键
     *
     * @param primaryKey 记录主键
     */
    public void setPrimaryKey(MysqlRecordPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }
}

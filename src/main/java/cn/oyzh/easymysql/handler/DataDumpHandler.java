package cn.oyzh.easymysql.handler;

import cn.oyzh.common.date.DateHelper;
import cn.oyzh.common.file.FastFileWriter;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.MysqlConnect;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 数据转储处理器
 *
 * @author oyzh
 * @since 2024/08/22
 */
public abstract class DataDumpHandler extends DataHandler {

    /**
     * 数据类型
     * 0 数据和结构
     * 1 仅结构
     */
    protected Byte dataType;

    /**
     * 库名称
     */
    protected String dbName;

    /**
     * 转储文件
     */
    protected File dumpFile;

    /**
     * 文件写入器
     */
    protected FastFileWriter fileWriter;

    /**
     * db客户端
     */
    protected MysqlClient dbClient;

    /**
     * 1. 库
     * 2. 表
     */
    protected Byte dumpType;

    /**
     * 表名称
     */
    protected String tableName;

    /**
     * 连接信息
     */
    protected MysqlConnect dbInfo;

    /**
     * 查询限制
     */
    protected int queryLimit = 1000;

    /**
     * 方言
     */
    private DBDialect dialect;

    /**
     * 构造方法
     *
     * @param dbClient db客户端
     * @param dbName   库名称
     */
    public DataDumpHandler(MysqlClient dbClient, String dbName) {
        this.dbClient = dbClient;
        this.dbName = dbName;
    }

    /**
     * 设置转储文件
     *
     * @param dumpFile 转储文件
     * @return 当前对象
     * @throws IOException 异常
     */
    public DataDumpHandler dumpFile(File dumpFile) throws IOException {
        this.dumpFile = dumpFile;
        if (this.fileWriter != null) {
            this.fileWriter.close();
        }
        this.fileWriter = new FastFileWriter(dumpFile);
        return this;
    }


    /**
     * 执行转储
     *
     * @throws Exception 异常
     */
    public abstract void doDump() throws Exception;

    /**
     * 写入头部
     *
     * @throws IOException 异常
     */
    protected void writeHeader() throws IOException {
        String version = this.dbClient.selectVersion();
        String clientCharacter = this.dbClient.selectClientCharacter();
        String header = "/*\n";
        header += " EasyDB Data Transfer";
        header += "\n\n";
        header += " Source Server : " + this.dbInfo.getName();
        header += "\n";
        header += " Source Server Type : " + this.dbClient.dialect().name();
        header += "\n";
        header += " Source Server Version : " + version;
        header += "\n";
        header += " Source Host : " + this.dbInfo.getHost();
        header += "\n";
        header += " Source Schema : " + this.dbName;
        header += "\n\n";
        header += " Target Server Type : " + this.dbClient.dialect().name();
        header += "\n";
        header += " Target Server Version : " + version;
        header += "\n";
        header += " File Encoding : " + clientCharacter;
        header += "\n\n";
        header += " Date : " + DateHelper.formatDateTimeSimple();
        header += "\n";
        header += "*/";

        header += "\n\n";
        header += "SET NAMES " + clientCharacter + ";";
        header += "\n";
        header += "SET FOREIGN_KEY_CHECKS = 0;";
        this.fileWriter.writeLines(List.of(header));
    }

    /**
     * 写入尾部
     *
     * @throws IOException 异常
     */
    protected void writeTail() throws IOException {
        String tail = "\n";
        tail += "SET FOREIGN_KEY_CHECKS = 1;";
        this.fileWriter.appendLines(List.of(tail));
    }

    /**
     * 是否转储记录
     *
     * @return 结果
     */
    public boolean isDumpRecord() {
        return this.dataType == 0;
    }

    /**
     * 创建新的处理器
     *
     * @param dbClient db客户端
     * @param dbName   数据库
     * @return DataDumpHandler
     */
    public static DataDumpHandler newHandler(MysqlClient dbClient, String dbName) {
        DataDumpHandler handler = switch (dbClient.dialect()) {
            case MYSQL -> new MysqlDataDumpHandler( dbClient, dbName);
            default -> null;
        };
        if (handler != null) {
            handler.dialect=dbClient.dialect();
        }
        return handler;
    }

    /**
     * 获取数据类型
     *
     * @return 数据类型
     */
    public Byte getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型
     *
     * @param dataType 数据类型
     */
    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

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
     * 获取转储文件
     *
     * @return 转储文件
     */
    public File getDumpFile() {
        return dumpFile;
    }

    /**
     * 设置转储文件
     *
     * @param dumpFile 转储文件
     */
    public void setDumpFile(File dumpFile) {
        this.dumpFile = dumpFile;
    }

    /**
     * 获取文件写入器
     *
     * @return 文件写入器
     */
    public FastFileWriter getFileWriter() {
        return fileWriter;
    }

    /**
     * 设置文件写入器
     *
     * @param fileWriter 文件写入器
     */
    public void setFileWriter(FastFileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public MysqlClient getDbClient() {
        return dbClient;
    }

    /**
     * 设置db客户端
     *
     * @param dbClient db客户端
     */
    public void setDbClient(MysqlClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * 获取转储类型
     *
     * @return 转储类型
     */
    public Byte getDumpType() {
        return dumpType;
    }

    /**
     * 设置转储类型
     *
     * @param dumpType 转储类型
     * @return 当前对象
     */
    public DataDumpHandler setDumpType(Byte dumpType) {
        this.dumpType = dumpType;
        return this;
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
     * @return 当前对象
     */
    public DataDumpHandler setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public MysqlConnect getDbInfo() {
        return dbInfo;
    }

    /**
     * 设置连接信息
     *
     * @param dbInfo 连接信息
     * @return 当前对象
     */
    public DataDumpHandler setDbInfo(MysqlConnect dbInfo) {
        this.dbInfo = dbInfo;
        return this;
    }

    /**
     * 获取查询限制
     *
     * @return 查询限制
     */
    public int getQueryLimit() {
        return queryLimit;
    }

    /**
     * 设置查询限制
     *
     * @param queryLimit 查询限制
     * @return 当前对象
     */
    public DataDumpHandler setQueryLimit(int queryLimit) {
        this.queryLimit = queryLimit;
        return this;
    }

    /**
     * 获取方言
     *
     * @return 方言
     */
    public DBDialect getDialect() {
        return dialect;
    }

    /**
     * 设置方言
     *
     * @param dialect 方言
     */
    public void setDialect(DBDialect dialect) {
        this.dialect = dialect;
    }
}


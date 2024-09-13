package cn.oyzh.easymysql.handler.dump;

import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.easymysql.handler.DataHandler;
import cn.oyzh.fx.common.util.DateHelper;
import cn.oyzh.fx.common.util.FastFileWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/08/22
 */
@Slf4j
public abstract class DataDumpHandler extends DataHandler {

    /**
     * 数据类型
     * 0 数据和结构
     * 1 仅结构
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected Byte dataType;

    /**
     * 库名称
     */
    @Getter
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
    @Getter
    @Accessors(fluent = true, chain = true)
    protected DBClient dbClient;

    /**
     * 1. 库
     * 2. 表
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected Byte dumpType;

    /**
     * 表名称
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected String tableName;

    /**
     * 连接信息
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected MysqlInfo dbInfo;

    /**
     * 查询限制
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected int queryLimit = 1000;

    /**
     * 方言
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    private DBDialect dialect;

    public DataDumpHandler(DBClient dbClient, String dbName) {
        this.dbClient = dbClient;
        this.dbName = dbName;
    }

    /**
     * 设置转储文件
     *
     * @param dumpFile 转储文件
     * @return 当前对象
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
     */
    protected void writeHeader() throws IOException {
        String version = this.dbClient.selectVersion();
        String clientCharacter = this.dbClient.selectClientCharacter();
        String header = "/*\n";
        header += " EasyDB Data Transfer";
        header += "\n\n";
        header += " Source Server : " + this.dbInfo.getName();
        header += "\n";
        header += " Source Server Type : " + this.dbClient().dialect().name();
        header += "\n";
        header += " Source Server Version : " + version;
        header += "\n";
        header += " Source Host : " + this.dbInfo.getHost();
        header += "\n";
        header += " Source Schema : " + this.dbName;
        header += "\n\n";
        header += " Target Server Type : " + this.dbClient().dialect().name();
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
     */
    protected void writeTail() throws IOException {
        String tail = "\n";
        tail += "SET FOREIGN_KEY_CHECKS = 1;";
        this.fileWriter.appendLines(List.of(tail));
    }

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
    public static DataDumpHandler newHandler(DBClient dbClient, String dbName) {
        DataDumpHandler handler = switch (dbClient.dialect()) {
            case MYSQL -> new MysqlDataDumpHandler(dbClient, dbName);
            default -> null;
        };
        if (handler != null) {
            handler.dialect(dbClient.dialect());
        }
        return handler;
    }
}


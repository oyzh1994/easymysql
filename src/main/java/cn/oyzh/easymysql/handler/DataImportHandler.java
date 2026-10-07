package cn.oyzh.easymysql.handler;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.mysql.column.MysqlColumns;
import cn.oyzh.easymysql.mysql.column.MysqlSelectColumnParam;
import cn.oyzh.easymysql.mysql.data.MysqlCsvTypeFileReader;
import cn.oyzh.easymysql.mysql.data.MysqlDataImportConfig;
import cn.oyzh.easymysql.mysql.data.MysqlDataImportHelper;
import cn.oyzh.easymysql.mysql.data.MysqlExcelTypeFileReader;
import cn.oyzh.easymysql.mysql.data.MysqlJsonTypeFileReader;
import cn.oyzh.easymysql.mysql.data.MysqlTxtTypeFileReader;
import cn.oyzh.easymysql.mysql.data.MysqlTypeFileReader;
import cn.oyzh.easymysql.mysql.data.MysqlXmlTypeFileReader;
import cn.oyzh.easymysql.mysql.record.MysqlRecord;
import cn.oyzh.easymysql.fx.data.DataImportFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据导入处理器
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class DataImportHandler extends DataHandler {

    /**
     * 库名称
     */
    private String dbName;

    /**
     * 文件类型
     * sql
     * xml
     * csv
     * excel
     */
    private String fileType;

    /**
     * db客户端
     */
    private MysqlClient dbClient;

    /**
     * 读取限制
     */
    private int readLimit = 1000;

    /**
     * 批量处理限制
     */
    private int batchLimit = 200;

    /**
     * 导入文件
     */
    private List<DataImportFile> files;

    /**
     * 导入配置
     */
    private final MysqlDataImportConfig config;

    /**
     * 构造数据导入处理器
     *
     * @param dbClient 数据库客户端
     * @param dbName   库名称
     */
    public DataImportHandler(MysqlClient dbClient, String dbName) {
        this.dbClient = dbClient;
        this.dbName = dbName;
        this.config = new MysqlDataImportConfig();
    }

    /**
     * 是否sql类型
     *
     * @return 结果
     */
    public boolean isSqlType() {
        return "sql".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否xml类型
     *
     * @return 结果
     */
    public boolean isXmlType() {
        return "xml".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否csv类型
     *
     * @return 结果
     */
    public boolean isCsvType() {
        return "csv".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否xls类型
     *
     * @return 结果
     */
    public boolean isExcelType() {
        return "excel".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否json类型
     *
     * @return 结果
     */
    public boolean isJsonType() {
        return "json".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否txt类型
     *
     * @return 结果
     */
    public boolean isTxtType() {
        return "txt".equalsIgnoreCase(this.fileType);
    }

    /**
     * 执行导入
     *
     * @throws Exception 异常
     */
    public void doImport() throws Exception {
        this.message("Import Starting");
        if (CollectionUtil.isNotEmpty(this.files)) {
            for (DataImportFile file : files) {
                this.checkInterrupt();
                this.importRecord(file);
            }
            this.processed(files.size());
        }
        this.message("Import Finished");
    }

    /**
     * 导入表
     *
     * @throws Exception 异常
     */
    protected void importRecord(DataImportFile file) throws Exception {
        String tableName = file.getTargetTableName();
        this.message("Importing Table " + tableName);
        this.message("Importing Records of Table " + tableName);
        // 复制模式
        if (this.config.isCopyMode()) {
            this.dbClient.clearTable(this.dbName, tableName);
        }
        try (MysqlTypeFileReader reader = this.initReader(file.getFile())) {
            // 获取数据库表字段
            MysqlColumns dbColumns = new MysqlColumns(this.dbClient.selectColumns(new MysqlSelectColumnParam(this.dbName, tableName)));
            if (!dbColumns.isEmpty()) {
                while (true) {
                    this.checkInterrupt();
                    long start1 = System.currentTimeMillis();
                    List<MysqlRecord> records = this.readRecords(reader, this.readLimit);
                    if (CollectionUtil.isEmpty(records)) {
                        break;
                    }
                    long end1 = System.currentTimeMillis();
                    JulLog.info("读取耗时: {}ms", (end1 - start1));
                    long start2 = System.currentTimeMillis();
                    this.writeRecord(dbColumns, records);
                    long end2 = System.currentTimeMillis();
                    JulLog.info("写入耗时: {}ms", (end2 - start2));
                    this.processed(records.size());
                }
            }
            // 收尾批量插入
            this.doBatchInsert();
        } finally {
            this.insertList = null;
            this.message("Importing Table " + tableName + " From -> " + file.getFilePath());
        }
    }

    /**
     * 初始化文件读取器
     *
     * @param file 文件
     * @return 文件读取器
     * @throws Exception 异常
     */
    private MysqlTypeFileReader initReader(File file) throws Exception {
        if (this.isCsvType()) {
            return new MysqlCsvTypeFileReader(file, this.config);
        }
        if (this.isJsonType()) {
            return new MysqlJsonTypeFileReader(file, this.config);
        }
        if (this.isXmlType()) {
            return new MysqlXmlTypeFileReader(file, this.config);
        }
        if (this.isExcelType()) {
            return new MysqlExcelTypeFileReader(file, this.config);
        }
        if (this.isTxtType()) {
            return new MysqlTxtTypeFileReader(file, this.config);
        }
        return null;
    }

    /**
     * 读取记录
     *
     * @param reader 文件读取器
     * @param count  读取数量
     * @return 记录列表
     * @throws Exception 异常
     */
    private List<MysqlRecord> readRecords(MysqlTypeFileReader reader, int count) throws Exception {
        List<MysqlRecord> records = new ArrayList<>();
        List<Map<String, Object>> list = reader.readObjects(count);
        for (Map<String, Object> objectMap : list) {
            MysqlRecord record = new MysqlRecord(null);
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                record.putValue(entry.getKey(), entry.getValue());
            }
            records.add(record);
        }
        return records;
    }

    /**
     * 写入记录
     *
     * @param columns 字段列表
     * @param records 记录列表
     */
    private void writeRecord(MysqlColumns columns, List<MysqlRecord> records) throws Exception {
        List<String> sqlList = MysqlDataImportHelper.toInsertSql(columns, records, this.config);
        this.addInsertSql(sqlList);
    }

    /**
     * 插入集合
     */
    private List<String> insertList;

    /**
     * 添加插入sql
     *
     * @param sqlList 插入sql列表
     */
    private void addInsertSql(List<String> sqlList) {
        if (CollectionUtil.isNotEmpty(sqlList)) {
            if (this.insertList == null) {
                this.insertList = new ArrayList<>();
            }
            this.insertList.addAll(sqlList);
            if (this.insertList.size() >= this.batchLimit) {
                this.doBatchInsert();
            }
        }
    }

    /**
     * 执行批量插入
     */
    private void doBatchInsert() {
        if (CollectionUtil.isNotEmpty(this.insertList)) {
            try {
                if (this.insertList.size() <= this.batchLimit) {
                    this.doBatchInsert(this.insertList, false);
                } else {
                    List<List<String>> lists = CollectionUtil.split(this.insertList, this.batchLimit);
                    List<Runnable> tasks = new ArrayList<>();
                    for (List<String> list : lists) {
                        tasks.add(() -> this.doBatchInsert(list, true));
                    }
                    ThreadUtil.submit(tasks);
                }
            } finally {
                this.insertList.clear();
            }
        }
    }

    /**
     * 执行批量插入
     *
     * @param sqlList  sql列表
     * @param parallel 是否并发
     */
    private void doBatchInsert(List<String> sqlList, boolean parallel) {
        try {
            int result = this.dbClient.insertBatch(this.dbName, sqlList, parallel);
            this.processedIncr(result);
        } catch (Exception ex) {
            this.processedDecr(sqlList.size());
            throw ex;
        }
    }

    /**
     * 设置日期格式
     *
     * @param dateFormat 日期格式
     */
    public void dateFormat(String dateFormat) {
        if (StringUtil.isBlank(dateFormat)) {
            this.config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            this.config.setDateFormat(dateFormat);
        }
    }

    /**
     * 设置导入模式
     *
     * @param importMode 导入模式
     */
    public void importMode(String importMode) {
        this.config.setImportMode(importMode);
    }

    /**
     * 设置字段索引
     *
     * @param columnIndex 字段索引
     */
    public void columnIndex(int columnIndex) {
        this.config.setColumnIndex(columnIndex);
    }

    /**
     * 设置数据起始索引
     *
     * @param dataStartIndex 数据起始索引
     */
    public void dataStartIndex(int dataStartIndex) {
        this.config.setDataStartIndex(dataStartIndex);
    }

    /**
     * 设置字段标签
     *
     * @param recordLabel 字段标签
     */
    public void recordLabel(String recordLabel) {
        this.config.setRecordLabel(recordLabel);
    }

    /**
     * 设置属性作为字段
     *
     * @param attrToColumn 属性作为字段
     */
    public void attrToColumn(boolean attrToColumn) {
        this.config.setAttrToColumn(attrToColumn);
    }

    /**
     * 设置记录分隔符
     *
     * @param recordSeparator 记录分隔符
     */
    public void recordSeparator(String recordSeparator) {
        this.config.setRecordSeparator(recordSeparator);
    }

    /**
     * 设置文本标识符
     *
     * @param txtIdentifier 文本标识符
     */
    public void txtIdentifier(String txtIdentifier) {
        this.config.setTxtIdentifier(txtIdentifier);
    }

    /**
     * 设置字段分隔符
     *
     * @param fieldSeparator 字段分隔符
     */
    public void fieldSeparator(String fieldSeparator) {
        this.config.setFieldSeparator(fieldSeparator);
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
     * 获取文件类型
     *
     * @return 文件类型
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * 设置文件类型
     *
     * @param fileType 文件类型
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * 获取数据库客户端
     *
     * @return 数据库客户端
     */
    public MysqlClient getDbClient() {
        return dbClient;
    }

    /**
     * 设置数据库客户端
     *
     * @param dbClient 数据库客户端
     */
    public void setDbClient(MysqlClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * 获取读取限制
     *
     * @return 读取限制
     */
    public int getReadLimit() {
        return readLimit;
    }

    /**
     * 设置读取限制
     *
     * @param readLimit 读取限制
     */
    public void setReadLimit(int readLimit) {
        this.readLimit = readLimit;
    }

    /**
     * 获取批量处理限制
     *
     * @return 批量处理限制
     */
    public int getBatchLimit() {
        return batchLimit;
    }

    /**
     * 设置批量处理限制
     *
     * @param batchLimit 批量处理限制
     */
    public void setBatchLimit(int batchLimit) {
        this.batchLimit = batchLimit;
    }

    /**
     * 获取导入文件列表
     *
     * @return 导入文件列表
     */
    public List<DataImportFile> getFiles() {
        return files;
    }

    /**
     * 设置导入文件列表
     *
     * @param files 导入文件列表
     */
    public void setFiles(List<DataImportFile> files) {
        this.files = files;
    }

    /**
     * 获取导入配置
     *
     * @return 导入配置
     */
    public MysqlDataImportConfig getConfig() {
        return config;
    }
}


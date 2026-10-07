# handler 包代码审查

> 包路径：`cn.oyzh.easymysql.handler`
> 覆盖类数：9（含 3 个抽象基类与对应的 3 个 Mysql 实现类）

## DataHandler
- 职责：数据处理基类，提供中断控制、消息回传与进度回传能力，供导出/导入/转储/传输/运行 sql 等子类复用。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | interrupt | AtomicBoolean | 中断标志位（懒初始化） |
  | messageHandler | Consumer<String> | 消息处理器 |
  | processedHandler | Consumer<Integer> | 进度处理器（增量） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void interrupt(boolean)` / `void interrupt()` | 设置中断标志 | 懒创建或设置 AtomicBoolean |
  | `protected void checkInterrupt()` | 检查中断 | 若已置位则抛 `InterruptedException` |
  | `protected void exception(Exception)` | 发送异常 | 中断异常直接抛；否则 `messageHandler.accept(ex.getMessage())`，无 handler 时 printStackTrace |
  | `protected void message(String)` | 发送消息 | `messageHandler.accept` |
  | `protected void processed(int)` | 更新进度（增量） | `processedHandler.accept` |
  | `protected void processedIncr()/processedIncr(int)` | 进度递增 | 取绝对值后 `processed` |
  | `protected void processedDecr()/processedDecr(int)` | 进度递减 | `processed(-n)` |
  | `getInterrupt/setInterrupt`、`getMessageHandler/setMessageHandler`、`getProcessedHandler/setProcessedHandler` | 访问器 | setMessageHandler 返回 this 支持链式 |

- 调用链：`DataExportHandler.exportTable → checkInterrupt → (抛 InterruptedException)`
- 调用链：`DataHandler.exception → messageHandler.accept`

## DataExportHandler (extends DataHandler)
- 职责：数据导出处理器，将表记录按 sql/json/xml/csv/html/xls/txt 等格式分页查询并写入文件。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dbName | String | 库名称 |
  | fileType | String | 文件类型（sql/json/xml/csv/html/xls/xlsx/txt） |
  | dbClient | MysqlClient | db 客户端 |
  | queryLimit | int | 分页查询条数（默认 1000） |
  | tables | List<DataExportTable> | 待导出表 |
  | config | MysqlDataExportConfig | 导出配置（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isSqlType/isXmlType/isCsvType/isHtmlType/isXlsType/isXlsxType/isExcelType/isJsonType/isTxtType()` | 类型判定 | 比较 fileType（忽略大小写），isExcelType=isXlsType||isXlsxType |
  | `void doExport()` | 执行导出 | 遍历 tables，逐个 `exportTable` 并 processedIncr |
  | `private MysqlTypeFileWriter initWriter(String, MysqlColumns)` | 初始化写入器 | 按类型返回 MysqlSql/Excel/Html/Json/Xml/Csv/TxtTypeFileWriter |
  | `protected void exportTable(DataExportTable)` | 导出单表 | try-with-resources 打开 writer，写头；循环分页 `dbClient.selectRecords(param)`，写记录，`start += queryLimit`，写尾 |
  | `private void writeHeader/writeRecord/writeTail(...)` | 写头/写记录/写尾 | 委托 writer.writeHeader/writeObjects/writeTrial+close；writeRecord 将记录 `toMap` 后统一写入 |
  | `dateFormat/recordSeparator/txtIdentifier/fieldSeparator/includeFields/fieldToAttr/earlyVersion(...)` | 配置透传 | 写入 config |
  | `getDbName/setDbName`、`getFileType/setFileType`、`getDbClient/setDbClient`、`getQueryLimit/setQueryLimit`、`getTables/setTables`、`getConfig` | 访问器 | 读写字段 |

- 调用链：`DataExportHandler.doExport → exportTable → initWriter → writer.writeHeader/writeObjects/writeTrial`

## DataImportHandler (extends DataHandler)
- 职责：数据导入处理器，从 csv/json/xml/excel/txt 文件分页读取记录并批量写入数据库。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dbName | String | 库名称 |
  | fileType | String | 文件类型（sql/xml/csv/excel/json/txt） |
  | dbClient | MysqlClient | db 客户端 |
  | readLimit | int | 单次读取条数（默认 1000） |
  | batchLimit | int | 批量插入阈值（默认 200） |
  | files | List<DataImportFile> | 待导入文件 |
  | config | MysqlDataImportConfig | 导入配置（final） |
  | insertList | List<String> | 待执行插入 SQL 缓冲 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isSqlType/isXmlType/isCsvType/isExcelType/isJsonType/isTxtType()` | 类型判定 | 比较 fileType |
  | `void doImport()` | 执行导入 | 遍历 files 调 `importRecord`，最后 processed(files.size()) |
  | `protected void importRecord(DataImportFile)` | 导入单文件 | 复制模式先 `clearTable`；打开 reader，取目标表字段，循环 `readRecords`→`writeRecord`，最后 `doBatchInsert` |
  | `private MysqlTypeFileReader initReader(File)` | 初始化读取器 | 按类型返回 Csv/Json/Xml/Excel/TxtTypeFileReader |
  | `private List<MysqlRecord> readRecords(MysqlTypeFileReader, int)` | 读取记录 | reader.readObjects 后逐条 putValue 组装 MysqlRecord |
  | `private void writeRecord(MysqlColumns, List<MysqlRecord>)` | 生成插入 SQL | `MysqlDataImportHelper.toInsertSql` 后加入缓冲 |
  | `private void addInsertSql(List<String>)` | 加入缓冲 | 超过 batchLimit 触发 `doBatchInsert` |
  | `private void doBatchInsert()` / `doBatchInsert(List<String>, boolean)` | 批量插入 | 超过阈值时 `CollectionUtil.split` 后 `ThreadUtil.submit` 并发执行，否则单批调用 `dbClient.insertBatch` |
  | `dateFormat/importMode/columnIndex/dataStartIndex/recordLabel/attrToColumn/recordSeparator/txtIdentifier/fieldSeparator(...)` | 配置透传 | 写入 config |
  | 各类 getter/setter（dbName/fileType/dbClient/readLimit/batchLimit/files/config） | 访问器 | 读写字段 |

- 调用链：`DataImportHandler.doImport → importRecord → readRecords → writeRecord → addInsertSql → doBatchInsert → dbClient.insertBatch`

## DataDumpHandler (abstract, extends DataHandler)
- 职责：数据转储处理器抽象基类，定义转储文件写入、头部/尾部生成与子类工厂。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dataType | Byte | 0 数据和结构 / 1 仅结构 |
  | dbName | String | 库名称 |
  | dumpFile | File | 转储文件 |
  | fileWriter | FastFileWriter | 文件写入器 |
  | dbClient | MysqlClient | db 客户端 |
  | dumpType | Byte | 1 库 / 2 表 |
  | tableName | String | 表名称 |
  | dbInfo | MysqlConnect | 连接信息 |
  | queryLimit | int | 分页查询条数（默认 1000） |
  | dialect | DBDialect | 方言（private） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DataDumpHandler dumpFile(File)` | 设置转储文件 | 关闭旧 writer 后 `new FastFileWriter` |
  | `abstract void doDump()` | 执行转储 | 由子类实现 |
  | `protected void writeHeader()` | 写头部 | 拼装版本/主机/库等注释头与 `SET NAMES`、`SET FOREIGN_KEY_CHECKS=0` |
  | `protected void writeTail()` | 写尾部 | 追加 `SET FOREIGN_KEY_CHECKS = 1;` |
  | `boolean isDumpRecord()` | 是否转储记录 | `dataType == 0` |
  | `static DataDumpHandler newHandler(MysqlClient, String)` | 工厂 | 按 dialect 返回 `MysqlDataDumpHandler`（MYSQL）并设置 dialect |
  | 各类 getter/setter（dataType/dbName/dumpFile/fileWriter/dbClient/dumpType/tableName/dbInfo/queryLimit/dialect） | 访问器 | 多个 setter 返回 this 支持链式 |

- 调用链：`DataDumpHandler.newHandler → MysqlDataDumpHandler.<init>`
- 调用链：`MysqlDataDumpHandler.doDump → writeHeader → writeTail → fileWriter.close`

## MysqlDataDumpHandler (extends DataDumpHandler)
- 职责：MySQL 数据转储处理器实现，按库或表维度输出结构（含记录）到 sql 文件。
- 字段：无新增（继承父类）
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void doDump()` | 执行转储 | 校验参数后 writeHeader；dumpType==1 时依次 dumpTable/dumpView/dumpFunction/dumpProcedure/dumpTrigger/dumpEvent；dumpType==2 时 `selectFullTable` 后 dumpTable；writeTail 并 close |
  | `protected void dumpTable()` | 转储全部表 | `selectTables(full)` 后逐表 dumpTable |
  | `protected void dumpTable(MysqlTable)` | 转储单表 | 写表结构注释、`DROP TABLE IF EXISTS` 与 createDefinition；`isDumpRecord` 时 dumpRecord |
  | `protected void dumpRecord(String)` | 转储记录 | 分页 selectRecords，`DBDataUtil.toInsertSql` 后 appendLines |
  | `protected void dumpView/dumpFunction/dumpProcedure/dumpTrigger/dumpEvent()` | 转储各类对象 | 分别用 `showCreateView/Function/Procedure/Trigger` 与 `event.getCreateDefinition()`，配合 delimiter 包裹写入 |

- 调用链：`MysqlDataDumpHandler.doDump → dumpTable → dumpRecord → DBDataUtil.toInsertSql → fileWriter.appendLines`

## DataTransportHandler (abstract, extends DataHandler)
- 职责：数据传输处理器抽象基类，持有源/目标客户端及待传输对象清单与批量插入逻辑。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | sourceClient / targetClient | MysqlClient | 来源/目标客户端 |
  | sourceDatabase / targetDatabase | String | 来源库/目标库 |
  | selectLimit | int | 分页查询条数（默认 5000） |
  | batchLimit | int | 批量插入阈值（默认 250） |
  | views/tables/triggers/functions/procedures/events | List<DataTransport…> | 待传输对象清单 |
  | dialect | DBDialect | 方言（private） |
  | insertList | List<String> | 插入 SQL 缓冲 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `abstract void doTransport()` | 执行传输 | 由子类实现 |
  | `protected void addInsertSql(List<String>)` | 加入缓冲 | 达 batchLimit 触发 doBatchInsert |
  | `protected void doBatchInsert()` / `doBatchInsert(List<String>, boolean)` | 批量插入 | split 后 ThreadUtil.submit 并发，否则 `targetClient.insertBatch` |
  | `static DataTransportHandler newHandler(DBDialect)` | 工厂 | 按 dialect 返回 `MysqlDataTransportHandler` |
  | 各类 getter/setter | 访问器 | 读写字段 |

- 调用链：`DataTransportHandler.newHandler → MysqlDataTransportHandler.<init>`

## MysqlDataTransportHandler (extends DataTransportHandler)
- 职责：MySQL 数据传输处理器实现，将源库对象（表/视图/函数/过程/触发器/事件）在目标库删除并重建，表记录分批传输。
- 字段：无新增
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void doTransport()` | 执行传输 | 目标库先 `SET FOREIGN_KEY_CHECKS=0`，依次传输表/视图/函数/过程/触发器/事件，最后恢复外键检查 |
  | `private void transportTable(String)` | 传输表 | DROP+CREATE（showCreateTable）+ 分页 selectRecords → `DBDataUtil.toInsertSql` → addInsertSql |
  | `private void transportView/transportFunction/transportProcedure/transportTrigger/transportEvent(String)` | 传输各类对象 | 各自 DROP 后由 `sourceClient.showCreateXxx` 取定义并在目标库执行 |

- 调用链：`MysqlDataTransportHandler.doTransport → transportTable → sourceClient.selectRecords → DBDataUtil.toInsertSql → addInsertSql → doBatchInsert → targetClient.insertBatch`

## DataRunSqlFileHandler (abstract, extends DataHandler)
- 职责：运行 sql 文件处理器抽象基类，提供 sql 文件设置、插入批量缓冲与子类工厂。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dbName | String | 库名称 |
  | sqlFile | File | sql 文件 |
  | dbClient | MysqlClient | db 客户端 |
  | dbInfo | MysqlConnect | 连接信息 |
  | insertLimit | int | 插入缓冲阈值（默认 5000） |
  | batchLimit | int | 单批最大条数（默认 250） |
  | continueWithErrors | boolean | 遇错是否继续（默认 true） |
  | dialect | DBDialect | 方言（private） |
  | insertList | List<String> | 插入 SQL 缓冲 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DataRunSqlFileHandler sqlFile(File)` | 设置 sql 文件 | 赋值并返回 this |
  | `abstract void runSqlFile()` | 运行 sql 文件 | 由子类实现 |
  | `protected void addInsertSql(String)` | 加入缓冲 | 达 insertLimit 触发 doBatchInsert |
  | `protected void doBatchInsert()` / `doBatchInsert(List<String>, boolean)` | 批量插入 | split 后并发或单批 `dbClient.insertBatch` |
  | `static DataRunSqlFileHandler newHandler(MysqlClient, String)` | 工厂 | 按 dialect 返回 `MysqlDataRunSqlFileHandler` |
  | 各类 getter/setter（dbName/sqlFile/dbClient/dbInfo/insertLimit/batchLimit/continueWithErrors/dialect） | 访问器 | 读写字段 |

- 调用链：`DataRunSqlFileHandler.newHandler → MysqlDataRunSqlFileHandler.<init>`

## MysqlDataRunSqlFileHandler (extends DataRunSqlFileHandler)
- 职责：MySQL 版 sql 文件逐行解析执行器，处理注释、INSERT 缓冲、CREATE 多行语句与 delimiter 包裹的函数/过程/触发器等。
- 字段：无新增
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void runSqlFile()` | 逐行读取并执行 sql 文件 | 用 `FileUtil.getReader` 读取；跳过 `-- `、`#`、`/* */` 注释；`INSERT INTO` 走 addInsertSql 缓冲；`SET `/`DROP ` 直接 `executeSqlSimple`；CREATE 语句单行直接执行、多行以 createFlag1 拼接至 `;`；`delimiter ;` 切换 createFlag2 以处理函数/触发器/过程/事件；异常时 `exception(ex)` 且按 `continueWithErrors` 决定是否中断；末尾 `doBatchInsert` |

- 调用链：`MysqlDataRunSqlFileHandler.runSqlFile → addInsertSql → doBatchInsert → dbClient.insertBatch`
- 调用链：`runSqlFile → dbClient.executeSqlSimple（SET/DROP/CREATE 单行）`

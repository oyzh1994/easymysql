# mysql 包代码审查文档

包路径：`cn.oyzh.easymysql.mysql`（含子包 `check`、`column`、`data`、`database`、`event`、`foreignKey`、`function`、`index`、`procedure`、`query`、`record`、`routine`、`table`、`trigger`、`view`）。

本包是 MySQL 的模型层与客户端封装：`MysqlClient` 是数据库操作入口，其余类为表/字段/索引/外键/触发器/事件/存储程序/记录/数据导入导出等模型与参数对象。

> 说明：`data/MysqlDataExportHelper.java` 为整文件注释的死代码，已跳过。

---

## 根包 cn.oyzh.easymysql.mysql

## MysqlClient
- 职责：数据库客户端封装，统一提供连接管理与全部数据库操作（查询、记录增删改、表/视图/库/函数/过程/事件/触发器管理）。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbConnect` | `MysqlConnect` | 数据库连接信息 |
  | `connectionManager` | `DBConnectionManager` | 连接管理器（缓存服务端/库/模式/函数/过程连接） |
  | `connConfig` | `DBConnConfig` | 连接配置（host、port 等） |
  | `state` | `ReadOnlyObjectWrapper<DBConnState>` | 连接状态 |
  | `properties` | `Map<String,Object>` | 客户端属性缓存（如 version） |
  | `TABLE_TYPES` | `String[]`（static final） | 表类型集合 |
  | `VIEW_TYPES` | `String[]`（static final） | 视图类型集合 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlClient(MysqlConnect)` | 构造 | 监听 `stateProperty`，CLOSED/CONNECTED 时派发 `MysqlEventUtil` 事件 |
  | `isInvalid(Connection)` | 判断连接无效 | 空/已关闭或 `!isValid(10)` |
  | `connection()` / `connection(dbName)` / `connection(dbName,schema)` | 获取连接 | 从 `connectionManager` 取，无效则 `initConnection` 重建并回填 |
  | `functionConnection/procedureConnection(dbName,schema)` | 函数/过程专用连接 | 同上，走独立缓存 |
  | `newConnection(dbName)` | 新建连接 | 直接 `initConnection` |
  | `initConnection(config,dbName,user,pwd)` | 初始化连接 | 加载 `com.mysql.cj.jdbc.Driver`，拼接 JDBC URL 与参数后 `DriverManager.getConnection` |
  | `start()` | 连接数据库 | 先 `initClient`，置 CONNECTING → `connection().isValid` → CONNECTED/FAILED |
  | `initClient()` | 初始化客户端 | 由 `dbConnect` 取 host/port 写入 `connConfig` |
  | `close()` | 关闭连接 | `connectionManager.destroy()` 后置 CLOSED |
  | `isClosed/isConnected/isConnecting` | 状态判断 | |
  | `state()/stateProperty()/addStateListener` | 状态读取与监听 | |
  | `isReadonly()/throwReadonlyException()` | 只读校验 | 只读时抛 `ReadonlyOperationException` |
  | `connectName()` | 连接名称 | `dbConnect.getName()` |
  | `tableSize/viewSize(dbName)` | 表/视图数量 | 通过 `DatabaseMetaData.getTables` 统计 |
  | `executeSql(dbName,sql)` | 执行多语句 | `DBSqlParser` 解析→逐条 `statement.execute`，解析结果到 `MysqlExecuteResult`，失败回滚 |
  | `executeSingleSql/explainSql/executeSqlSimple` | 单条执行/执行计划/简化执行 | 基于解析器 |
  | `insertBatch(...)`（2 重载） | 批量插入 | 逐条或并行执行 |
  | `selectTables(param)` | 表列表 | 全量走 `information_schema.TABLES`，否则 `getTables` |
  | `selectTable/selectFullTable(param)` | 表信息/完整表 | |
  | `selectColumns(param)` | 字段列表 | `SHOW FULL COLUMNS` → `MysqlColumn.parseKey/parseType/parseExtra/parseCollation`，按位置排序 |
  | `selectRecords/selectRecord/selectRecordCount(param)` | 记录查询 | 拼 `SELECT *`/`COUNT(*)`，条件由 `MysqlConditionUtil.buildCondition`，支持 LIMIT |
  | `insertRecord(param)` | 插入记录 | 拼 INSERT，几何字段用 `ST_GeomFromText(?)`，主键需回填时取生成键/`MysqlHelper.lastInsertId` |
  | `updateRecord/deleteRecord(param)` | 更新/删除记录 | 参数化执行 |
  | `showCreateTable/View/Function/Procedure/Trigger/Event(...)` | 获取创建语句 | 走 `MysqlHelper` 或直查 |
  | `engines/charsets/collation(charset)` | 引擎/字符集/排序规则 | |
  | `databases/database(dbName)` | 库列表/单个库 | |
  | `views/view/dropView/existView/createView/alertView` | 视图管理 | |
  | `indexes/checks/foreignKeys(dbName,tableName)` | 索引/检查约束/外键 | |
  | `viewColumns/viewRecords(...)` | 视图字段/记录 | 可更新性由 `MysqlHelper.isViewUpdatable` 判定 |
  | `createTable(param)` | 建表 | `MysqlTableCreateSqlGenerator.generateSql` → `DBSqlParser.parseSql` → 逐条执行、提交 |
  | `alertTable(param)` | 改表 | `MysqlTableAlertSqlGenerator.generateSql`，空 SQL 直接返回 |
  | `existTable/existDatabase/existView/existPrimaryKey` | 存在性判断 | |
  | `renameTable/renameEvent` | 重命名 | 直接 RENAME 语句 |
  | `clearTable/truncateTable/dropTable` | 清空/截断/删表 | |
  | `createDatabase/alterDatabase/dropDatabase/databaseCollation` | 库管理 | |
  | `functions/procedures/selectFunction/selectProcedure/createFunction/...` | 函数/过程管理 | 用 `MysqlFunctionSqlGenerator`/`MysqlProcedureSqlGenerator` |
  | `triggers(dbName)` / `triggers(dbName,tableName)` | 触发器列表 | 查 `INFORMATION_SCHEMA.TRIGGERS` |
  | `events/selectEvent/eventSize/createEvent/alertEvent/dropEvent` | 事件管理 | 用 `EventCreateSqlGenerator`/`EventAlertSqlGenerator` |
  | `isSupportFeature/isSupportCheckFeature/isSupportEventFeature` | 特性支持 | CHECK 需版本 ≥ 8.0.16 |
  | `selectVersion()` | 数据库版本 | 缓存于 `properties` |
  | `selectClientCharacter()` | 客户端字符集 | |
  | `cloneTable(dbName,tableName,includeRecord)` | 克隆表 | 返回克隆表名 |
  | `dbType()/dialect()` | 类型/方言 | Druid `DbType` 与 `DBDialect` |
  | `getDbConnect()` | 连接信息 | |
- 调用链：
  - `MysqlClient.start → initClient → connection → initConnection → DBConnectionManager`
  - `MysqlClient.createTable → MysqlTableCreateSqlGenerator.generateSql → DBSqlParser.parseSql`
  - `MysqlClient.executeSql → DBSqlParser.getParser → MysqlExecuteResult.parseResult`

## MysqlHelper
- 职责：MySQL 元数据/DDL 查询与解析辅助工具（全部静态方法）。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getFunctionDefinition/showCreateFunction(connection,name)` | 函数创建语句 | 查 `SHOW CREATE FUNCTION` |
  | `showCreateProcedure/showCreateTrigger/showCreateEvent/showCreateTable/showCreateView(...)` | 各对象创建语句 | |
  | `listRoutineParam/listFunctionParam/listProcedureParam(...)` | 存储程序参数 | 解析参数为 `MysqlRoutineParam` |
  | `getProcedureDefiner(connection,name)` | 定义者 | |
  | `isViewUpdatable/getViewInfo(...)` | 视图可更新性/信息 | |
  | `getGeometryString(connection,val)` | 几何值字符串 | |
  | `getCharsetAndCollation(...)` / `columnType(...)` | 字段字符集排序/类型 | |
  | `hasPrimaryKey(connection,db,table)` | 是否有主键 | |
  | `isZeroFill/getKeySize(showTableDefinition,col)` | 零填充/键长度解析 | 从建表语句文本解析 |
  | `lastInsertId(connection)` | 最后插入 ID | |
  | `parseColumns(resultSet[,excludes])` | 解析结果集字段 | 生成 `MysqlColumns` |
- 调用链：`MysqlClient.selectColumns → (SHOW FULL COLUMNS)` / `MysqlHelper.parseColumns → MysqlColumns`

## 子包 cn.oyzh.easymysql.mysql.check

## MysqlCheck
- 职责：数据库检查约束模型。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbName` | `String` | 库名称 |
  | `tableName` | `String` | 表名称 |
  | `name` | `String` | 约束名称 |
  | `clause` | `String` | 约束子语句 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlCheck()/MysqlCheck(String name)` | 构造 | |
  | `setName/isNameChanged/originalName` | 名称管理与变更判断 | 依赖 `DBObjectStatus` |
  | `setClause/isClauseChanged` | 子语句管理与变更判断 | |
  | `copy(MysqlCheck)` | 复制 | `ObjectCopier` |
  | `isInvalid()` | 是否无效 | 名称或子语句为空 |
  | `getDbName/setDbName/getTableName/setTableName/getName/getClause` | 存取器 | |
- 调用链：`MysqlClient.checks → MysqlChecks → MysqlCheck`

## MysqlCheckControl
- 职责：检查约束编辑控件（继承 `MysqlCheck`，为每个属性生成输入控件）。
- 字段：无（继承）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getNameControl()/getClauseControl()` | 名称/子语句控件 | `ClearableTextField` |
  | `of(MysqlCheck)` / `of(List<MysqlCheck>)` | 转换为控件/控件列表 | |
- 调用链：`MysqlCheckControl.of → MysqlCheck.copy`

## MysqlChecks
- 职责：检查约束列表（`DBObjectList<MysqlCheck>`）。
- 字段：无。
- 方法：构造函数（无参/带 `List`）。
- 调用链：`MysqlClient.checks → MysqlChecks`

## 子包 cn.oyzh.easymysql.mysql.column

## MysqlColumn
- 职责：数据库字段模型，含类型解析与丰富的类型能力判断。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbName` | `String` | 库名称 |
  | `schema` | `String` | 模式名称 |
  | `tableName` | `String` | 表名称 |
  | `size` | `Integer` | 字段长度 |
  | `typeProperty` | `StringProperty` | 字段类型 |
  | `value` | `String` | 字段值 |
  | `comment` | `String` | 注释 |
  | `nullable` | `Boolean` | 是否可空 |
  | `unsigned` | `Boolean` | 是否无符号 |
  | `zeroFill` | `Boolean` | 是否零填充 |
  | `updateOnCurrentTimestamp` | `Boolean` | 是否随当前时间戳更新 |
  | `position` | `Integer` | 字段位置 |
  | `primaryKeyProperty` | `SimpleBooleanProperty` | 是否主键属性 |
  | `primaryKeySize` | `Integer` | 主键键长度 |
  | `defaultValue` | `Object` | 默认值 |
  | `digits` | `Integer` | 小数位 |
  | `autoIncrement` | `Boolean` | 是否自动递增 |
  | `name` | `String` | 字段名称 |
  | `charset` | `String` | 字段字符集 |
  | `collation` | `String` | 字段排序规则 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlColumn()/MysqlColumn(String name)` | 构造 | |
  | `parseKey/parseType/parseExtra/parseCollation(String)` | 解析 `SHOW FULL COLUMNS` 信息 | 由 `MysqlClient.selectColumns` 调用 |
  | `initColumn(type,extra)` | 初始化字段信息 | |
  | `setType/getType/typeProperty` | 类型读写 | |
  | `supportSize/suggestSize/supportGeometry/supportCharset/supportUnsigned/supportDigits/supportInteger/supportAutoIncrement/supportDefaultValue/supportTimestamp/supportValue/supportZeroFill/supportBit/supportJson/supportKeySize/supportString/supportBinary/supportEnum` | 各类类型能力判定 | 基于类型字符串判断 |
  | `getValueList()` | 枚举/集合值列表 | |
  | `isYearType/isDateType/isTimeType/isGeometryType` | 类型归类 | |
  | `minValue/maxValue/exampleValue` | 取值范围/示例值 | 用于记录编辑 |
  | `setDefaultValue/getDefaultValueString` | 默认值 | |
  | `isNameChanged/isColumnChanged/isPrimaryKeyChanged` | 变更判断 | |
  | `isInvalid()` | 是否无效 | 名称或类型为空 |
  | `copy(MysqlColumn)` | 复制 | |
  | `initStatus()` | 初始化状态 | 覆写 `DBObjectStatus` |
  | `hasComment/hasDefaultValue/isNullable/isUnsigned/isZeroFill/isAutoIncrement/isPrimaryKey` 及各 `get/set` | 属性存取 | |
- 调用链：`MysqlClient.selectColumns → MysqlColumn.parseType/parseKey/parseCollation`；`MysqlColumnControl → MysqlColumn.copy`

## MysqlColumnControl
- 职责：字段编辑控件（继承 `MysqlColumn`，生成各属性输入控件）。
- 字段：无（继承）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getNameControl/getCommentControl` | 名称/注释控件 | `ClearableTextField` |
  | `getSizeControl/getDigitsControl` | 长度/小数位控件 | `NumberTextField` |
  | `getTypeControl` | 类型控件 | `MysqlFiledTypeComboBox` |
  | `getNullableControl/getPrimaryKeyControl` | 可空/主键控件 | `FXCheckBox` |
  | `of(MysqlColumn)` / `of(List<MysqlColumn>)` | 转换为控件/控件列表 | |
- 调用链：`MysqlColumnControl.of → MysqlColumn.copy`

## MysqlColumns
- 职责：字段列表（`DBObjectList<MysqlColumn>`），提供主键/查找/排序等聚合操作。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlColumns()/MysqlColumns(List<MysqlColumn>)` | 构造 | |
  | `primaryKeys()` | 主键字段列表 | |
  | `primaryKeyChanged()` | 主键是否变更 | |
  | `column(name)/index(name)` | 按名称取字段/索引 | |
  | `sortOfPosition()` | 按位置排序 | `MysqlClient.selectColumns` 使用 |
  | `tableName()/dbName()` | 取表名/库名 | |
  | `columnNames()` | 字段名称列表 | |
  | `hasPrimaryKey()` | 是否有主键 | |
- 调用链：`MysqlClient.selectColumns → MysqlColumns.sortOfPosition`

## MysqlSelectColumnParam
- 职责：字段查询参数。
- 字段：`dbName`(String 库名称)、`schema`(String 模式名称)、`tableName`(String 表名称)。
- 方法：三个构造函数（无参、库+表、库+模式+表）与各 `get/set`。
- 调用链：`MysqlClient.selectColumns(param)`

## 子包 cn.oyzh.easymysql.mysql.data

## MysqlCsvTypeFileReader
- 职责：CSV 类型文件读取器。
- 字段：`columns`(List<String> 字段列表)、`config`(MysqlDataImportConfig 导入配置)、`reader`(SkipAbleFileReader 文件读取器)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlCsvTypeFileReader(File,config)` | 构造 | |
  | `init()` | 初始化并读取表头 | |
  | `readObject()` | 读取一行对象 | `parseLine` 后映射为 Map |
  | `close()` | 关闭 | |
- 调用链：`MysqlCsvTypeFileReader.readObject → MysqlTypeFileReader.parseLine`

## MysqlCsvTypeFileWriter
- 职责：CSV 类型文件写入器。
- 字段：`columns`(MysqlColumns)、`config`(MysqlDataExportConfig)、`writer`(LineFileWriter)。
- 方法：`MysqlCsvTypeFileWriter(filePath,config,columns)` 构造；`writeHeader()`（写字段名）；`writeObject(Map)`（`formatLine` 拼接）；`close()`。
- 调用链：`MysqlCsvTypeFileWriter.writeObject → MysqlTypeFileWriter.formatLine`

## MysqlDataExportConfig
- 职责：数据导出配置。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dateFormat` | `String` | 日期格式 |
  | `fieldToAttr` | `boolean` | 字段作为属性 |
  | `includeFields` | `boolean` | 是否包含列标题（默认 true） |
  | `recordSeparator` | `String` | 记录分隔符（默认系统换行） |
  | `fieldSeparator` | `String` | 字段分隔符（默认 `;`） |
  | `txtIdentifier` | `String` | 文本识别符（默认 `"`） |
  | `charset` | `String` | 字符集（默认 UTF-8） |
  | `earlyVersion` | `boolean` | 早期版本 |
- 方法：全部为上述字段 `get/set`（`isFieldToAttr/isIncludeFields/isEarlyVersion` 等）。
- 调用链：`MysqlDataExportConfig → MysqlTypeFileWriter.parameterized`

## MysqlDataImportConfig
- 职责：数据导入配置。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dateFormat` | `String` | 日期格式 |
  | `importMode` | `String` | 导入模式（1 追加 / 2 复制，默认 2） |
  | `columnIndex` | `int` | 字段索引（默认 0） |
  | `dataStartIndex` | `int` | 数据起始索引（默认 1） |
  | `recordLabel` | `String` | 记录标签 |
  | `attrToColumn` | `boolean` | 属性作为字段 |
  | `recordSeparator` | `String` | 记录分隔符 |
  | `fieldSeparator` | `String` | 字段分隔符（默认 `;`） |
  | `txtIdentifier` | `String` | 文本识别符（默认 `"`） |
  | `charset` | `String` | 字符集（默认 UTF-8） |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isAppendMode()/isCopyMode()` | 模式判断 | |
  | `fieldSeparatorChar()/txtIdentifierChar()` | 取分隔/识别字符 | |
  | 各 `get/set` | 属性存取 | |
- 调用链：`MysqlDataImportConfig → MysqlCsvTypeFileReader/TxtTypeFileReader`

## MysqlDataImportHelper
- 职责：数据导入辅助（静态方法）。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `parameterized(MysqlColumn,value,config)` | 值参数化 | 按字段类型（几何/日期/二进制/bit/枚举/字符串/数值）转换 |
  | `toInsertSql(columns,records,config)` | 记录转插入 SQL | 生成 INSERT 语句列表 |
- 调用链：`MysqlDataImportHelper.toInsertSql → MysqlDataImportHelper.parameterized → MysqlRecord.getValue`

## MysqlExcelTypeFileReader
- 职责：Excel 类型文件读取器。
- 字段：`workbook`(Workbook)、`columns`(List<String>)、`config`(MysqlDataImportConfig)、`currentRowIndex`(Integer)。
- 方法：构造函数；`init()`（打开工作簿、读表头）；`readObject()`（逐行读取）；`close()`。
- 调用链：`MysqlExcelTypeFileReader.readObject → Workbook/Sheet`

## MysqlExcelTypeFileWriter
- 职责：Excel 类型文件写入器。
- 字段：`columns`(MysqlColumns)、`config`(MysqlDataExportConfig)、`workbook`(Workbook)、`xlsRowIndex`(int，默认 1)、`filePath`(String)。
- 方法：构造函数；`writeHeader()`；`writeObject(object,flush)`（私有，写行）；`writeObject(Map)`；`writeObjects(List)`；`close()`；`parameterized(column,value,config)`（覆写参数化）。
- 调用链：`MysqlExcelTypeFileWriter.writeObjects → writeObject → parameterized`

## MysqlHtmlTypeFileWriter
- 职责：HTML 类型文件写入器。
- 字段：`columns`(MysqlColumns)、`config`(MysqlDataExportConfig)、`writer`(LineFileWriter)。
- 方法：构造函数；`writeHeader()`（写表头 HTML）；`writeTrial()`（写尾部）；`writeObject(Map)`；`close()`。
- 调用链：`MysqlHtmlTypeFileWriter.writeObject → MysqlTypeFileWriter.formatLine`

## MysqlJsonTypeFileReader
- 职责：JSON 类型文件读取器。
- 字段：`reader`(JSONReader)、`config`(MysqlDataImportConfig)。
- 方法：构造函数；`init()`；`readObject()`；`close()`。
- 调用链：`MysqlJsonTypeFileReader.readObject → JSONReader`

## MysqlJsonTypeFileWriter
- 职责：JSON 类型文件写入器。
- 字段：`columns`(MysqlColumns)、`config`(MysqlDataExportConfig)、`writer`(LineFileWriter)、`firstWrite`(boolean，默认 true)。
- 方法：构造函数；`writeHeader()`；`writeTrial()`；`writeObject(Map)`；`close()`；`parameterized(...)`。
- 调用链：`MysqlJsonTypeFileWriter.writeObject → parameterized`

## MysqlSqlTypeFileWriter
- 职责：SQL 类型文件写入器（生成 INSERT 语句）。
- 字段：`columns`(MysqlColumns)、`config`(MysqlDataExportConfig)、`writer`(LineFileWriter)。
- 方法：构造函数；`writeObject(Map)`（拼 `INSERT INTO ... VALUES`）；`close()`；`parameterized(...)`。
- 调用链：`MysqlSqlTypeFileWriter.writeObject → DBUtil.wrap → parameterized`

## MysqlTxtTypeFileReader
- 职责：TXT 类型文件读取器。
- 字段：`columns`(List<String>)、`config`(MysqlDataImportConfig)、`reader`(SkipAbleFileReader)。
- 方法：构造函数；`init()`；`readObject()`；`close()`。
- 调用链：`MysqlTxtTypeFileReader.readObject → MysqlTypeFileReader.parseLine`

## MysqlTxtTypeFileWriter
- 职责：TXT 类型文件写入器。
- 字段：`columns`(MysqlColumns)、`config`(MysqlDataExportConfig)、`writer`(LineFileWriter)。
- 方法：构造函数；`writeHeader()`；`writeObject(Map)`；`close()`。
- 调用链：`MysqlTxtTypeFileWriter.writeObject → MysqlTypeFileWriter.formatLine`

## MysqlTypeFileReader
- 职责：文件读取器抽象基类。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init()` | 初始化（默认空实现） | |
  | `readObject()` | 抽象：读取一个对象 | |
  | `readObjects(int count)` | 读取多个对象 | 循环 `readObject` |
  | `parseLine(line,txtIdentifier,fieldSeparator)` | 解析一行数据 | 支持文本识别符转义 |
- 调用链：`MysqlTypeFileReader.readObjects → readObject`

## MysqlTypeFileWriter
- 职责：文件写入器抽象基类。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init()` | 初始化（默认空实现） | |
  | `parameterized(column,value,config)` | 值参数化 | 默认返回原值，子类可覆写 |
  | `writeHeader()` | 写头（默认空实现） | |
  | `writeTrial()` | 写尾（默认空实现） | |
  | `writeObject(Map)` | 抽象：写一个对象 | |
  | `writeObjects(List<Map>)` | 写多个对象 | 循环 `writeObject` |
  | `formatLine(Object[]/List,sep,identifier,recordSep)` | 格式化一行 | 拼接字段与转义 |
- 调用链：`MysqlTypeFileWriter.writeObjects → writeObject → formatLine`

## MysqlXmlTypeFileReader
- 职责：XML 类型文件读取器。
- 字段：`reader`(XMLEventReader)、`config`(MysqlDataImportConfig)。
- 方法：构造函数；`init()`；`readObject()`；`close()`。
- 调用链：`MysqlXmlTypeFileReader.readObject → XMLEventReader`

## MysqlXmlTypeFileWriter
- 职责：XML 类型文件写入器。
- 字段：`columns`(MysqlColumns)、`config`(MysqlDataExportConfig)、`writer`(LineFileWriter)。
- 方法：构造函数；`writeHeader()`；`writeTrial()`；`writeObject(Map)`；`close()`；`parameterized(...)`。
- 调用链：`MysqlXmlTypeFileWriter.writeObject → parameterized`

## 子包 cn.oyzh.easymysql.mysql.database

## MysqlDatabase
- 职责：MySQL 数据库连接信息（名称/端口/用户/密码）。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 名称 |
  | `port` | `int` | 端口（默认 3306） |
  | `user` | `String` | 用户 |
  | `password` | `String` | 密码 |
- 方法：`getName/setName/getPort/setPort/getUser/setUser/getPassword/setPassword`。
- 调用链：`MysqlDatabase → MysqlClient`

## 子包 cn.oyzh.easymysql.mysql.event

## MysqlEvent
- 职责：MySQL 事件模型（单次/循环），解析与格式化时间字段。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 名称 |
  | `type` | `String` | 类型（ONE TIME 单次 / RECURRING 循环） |
  | `intervalValue` | `Integer` | 循环值 |
  | `intervalField` | `String` | 循环类型 |
  | `status` | `String` | 状态 |
  | `definer` | `String` | 定义者 |
  | `executeAt` | `Object` | 单次执行时间 |
  | `starts` | `Object` | 定期开始时间 |
  | `startIntervalValue` | `Integer` | 开始循环值 |
  | `startIntervalField` | `String` | 开始循环类型 |
  | `ends` | `Object` | 定期结束时间 |
  | `endIntervalValue` | `Integer` | 结束循环值 |
  | `endIntervalField` | `String` | 结束循环类型 |
  | `dbName` | `String` | 数据库名称 |
  | `comment` | `String` | 注释 |
  | `definition` | `String` | 定义 |
  | `onCompletion` | `String` | 完成时行为 |
  | `createDefinition` | `String` | 创建定义 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `copy(MysqlEvent)` | 复制 | |
  | `compare(MysqlEvent)` | 比较 | `ObjectComparator` |
  | `isNew()` | 是否新事件 | 无定义时视为新事件 |
  | `setCreateDefinition(String)` | 设置创建定义并从解析定义者 | |
  | `isOnTimeType()/isRecurringType()` | 类型判断 | 依据 `type` |
  | `executeAt()/starts()/ends()` | 格式化时间 | |
  | `isEnable()/isPreserve()` | 状态/完成保留判断 | |
  | `getStatus()` | 覆写状态 | |
  | 各属性 `get/set` | 存取器 | |
- 调用链：`MysqlClient.events/selectEvent → MysqlEvent.setCreateDefinition`；`MysqlEventUtil.eventAlerted → MysqlEventAlertedEvent`

## MysqlEvents
- 职责：事件列表（`DBObjectList<MysqlEvent>`）。
- 字段：无。
- 方法：构造函数（无参/带 `List`）。
- 调用链：`MysqlClient.events → MysqlEvents`

## 子包 cn.oyzh.easymysql.mysql.foreignKey

## MysqlForeignKey
- 职责：表外键模型。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 外键名称 |
  | `columns` | `List<String>` | 外键字段列表 |
  | `primaryKeyDatabaseProperty` | `SimpleStringProperty` | 引用库名称 |
  | `primaryKeyTableProperty` | `SimpleStringProperty` | 引用表名称 |
  | `primaryKeyColumns` | `List<String>` | 引用字段列表 |
  | `deletePolicy` | `String` | 删除策略 |
  | `updatePolicy` | `String` | 更新策略 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `originalName()` | 原始名称 | 变更判断用 |
  | `primaryKeyDatabaseProperty()/primaryKeyTableProperty()` | 引用库/表属性 | |
  | `setDeletePolicy/setUpdatePolicy/setName/setColumns` | 存取 | |
  | `setPrimaryKeyDatabase/getPrimaryKeyDatabase/setPrimaryKeyTable/getPrimaryKeyTable/setPrimaryKeyColumns` | 引用信息读写 | |
  | `addColumn/addPrimaryKeyColumn(String)` | 追加字段 | |
  | `copy(MysqlForeignKey)` | 复制 | |
  | `isInvalid()` | 是否无效 | 名称/字段/引用表/引用库为空 |
  | 各 `get` | 存取器 | |
- 调用链：`MysqlClient.foreignKeys → MysqlForeignKeys → MysqlForeignKey`

## MysqlForeignKeyControl
- 职责：外键编辑控件（继承 `MysqlForeignKey`，生成引用库/表/字段等控件）。
- 字段：`dbName`(String 库名称)、`dbClient`(MysqlClient)、`columnList`(List<MysqlColumn> 字段列表)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `setDbName/setDbClient/setColumnList` | 存取 | |
  | `getNameControl()` | 名称控件 | `FXTextField` |
  | `getColumnControl()/getPrimaryKeyColumnControl()` | 外键/引用字段控件 | `MysqlFieldTextFiled` |
  | `getPrimaryKeyDatabaseControl()` | 引用库控件 | `DBDatabaseComboBox` |
  | `getPrimaryKeyTableControl()` | 引用表控件 | `MysqlTableComboBox` |
  | `getDeletePolicyControl()/getUpdatePolicyControl()` | 策略控件 | `MysqlForeignKeyPolicyComboBox` |
  | `of(MysqlForeignKey)` / `of(List<...>)` | 转控件/控件列表 | |
  | `getPrimaryKeyDatabase()` | 覆写取引用库 | 当为空时返回当前 `dbName` |
- 调用链：`MysqlForeignKeyControl.of → MysqlForeignKey.copy`

## MysqlForeignKeys
- 职责：外键列表（`DBObjectList<MysqlForeignKey>`）。
- 字段：无。
- 方法：构造函数（无参/带 `Collection`）。
- 调用链：`MysqlClient.foreignKeys → MysqlForeignKeys`

## 子包 cn.oyzh.easymysql.mysql.function

## MysqlFunction
- 职责：MySQL 函数模型（继承 `MysqlRoutineSchema`，含返回参数）。
- 字段：`returnParam`(MysqlRoutineParam 返回参数)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `setParams(List<MysqlRoutineParam>)` | 覆写设置参数 | 首个参数作为返回参数、其余为入参 |
  | `getReturnType()` | 返回类型 | 取 `returnParam` 类型 |
  | `copy(MysqlFunction)` | 复制 | |
  | `getReturnParam/setReturnParam` | 返回参数读写 | |
- 调用链：`MysqlHelper.listFunctionParam → MysqlFunction.setParams`

## 子包 cn.oyzh.easymysql.mysql.index

## MysqlIndex
- 职责：表索引模型。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `seqIndex` | `int` | 索引顺序 |
  | `type` | `String` | 类型（normal/unique/fulltext/spatial） |
  | `method` | `String` | 方式（空/btree/hash） |
  | `comment` | `String` | 注释 |
  | `name` | `String` | 名称 |
  | `columns` | `List<IndexColumn>` | 索引字段列表 |
- 内部类 `IndexColumn` 字段：`columnName`(String 字段名)、`subPart`(Integer 子部分)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `originalName()` | 原始名称 | 变更判断用 |
  | `setName/addColumn(column,subPart)/setColumns/setType/setMethod/setComment` | 存取与追加 | |
  | `isUnique()` | 是否唯一索引 | 依据 `type` |
  | `type(type,noneUnique)` | 依类型与非唯一标志设置类型及方式 | |
  | `typeName()/methodName()` | 类型/方式名称 | |
  | `copy(MysqlIndex)` | 复制 | |
  | `isInvalid()` | 是否无效 | 名称/类型/字段为空 |
  | `getSeqIndex/getType/getMethod/getComment/getName/getColumns` | 存取器 | |
  | `IndexColumn.equals(Object)` 及 `get/set` | 索引字段 | |
- 调用链：`MysqlClient.indexes → MysqlIndexes → MysqlIndex`；`MysqlIndexControl.of → MysqlIndex.copy`

## MysqlIndexControl
- 职责：索引编辑控件（继承 `MysqlIndex`）。
- 字段：`columnList`(List<MysqlColumn> 字段列表)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `setColumnList(List<MysqlColumn>)` | 存取 | |
  | `getNameControl/getCommentControl` | 名称/注释控件 | `ClearableTextField` |
  | `getColumnControl` | 字段控件 | `MysqlIndexFieldTextFiled` |
  | `getTypeControl/getMethodControl` | 类型/方式控件 | `MysqlIndexTypeComboBox`/`MysqlIndexMethodComboBox` |
  | `of(MysqlIndex)` / `of(List<MysqlIndex>)` | 转控件/控件列表 | |
- 调用链：`MysqlIndexControl.of → MysqlIndex.copy`

## MysqlIndexes
- 职责：索引列表（`DBObjectList<MysqlIndex>`）。
- 字段：无。
- 方法：构造函数（无参/带 `Collection`）。
- 调用链：`MysqlClient.indexes → MysqlIndexes`

## 子包 cn.oyzh.easymysql.mysql.procedure

## MysqlProcedure
- 职责：MySQL 存储过程模型（继承 `MysqlRoutineSchema`）。
- 字段：无（继承）。
- 方法：`copy(MysqlProcedure)` 复制。
- 调用链：`MysqlHelper.listProcedureParam → MysqlProcedure.setParams`

## 子包 cn.oyzh.easymysql.mysql.query

## MysqlExecuteResult
- 职责：SQL 执行结果（继承 `MysqlQueryResult`）。
- 字段：`fullColumn`(boolean 是否全字段)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `hasResult()` | 是否有结果 | 覆写 |
  | `parseResult(resultSet,connection,readonly)` | 解析结果 | 解析为 `MysqlColumns` 与 `MysqlRecord` 列表 |
  | `setFullColumn/isFullColumn` | 全字段标记 | |
- 调用链：`MysqlClient.executeSql → MysqlExecuteResult.parseResult`

## MysqlExplainResult
- 职责：`EXPLAIN` 执行计划结果（继承 `MysqlQueryResult`）。
- 字段：无。
- 方法：`parseResult(resultSet,connection,readonly)` 解析。
- 调用链：`MysqlClient.explainSql → MysqlExplainResult.parseResult`

## MysqlQueryResult
- 职责：查询结果抽象基类。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `sql` | `String` | SQL 语句 |
  | `used` | `long` | 耗时（微秒） |
  | `msg` | `String` | 消息 |
  | `updateCount` | `int` | 变更总数 |
  | `success` | `boolean` | 是否成功 |
  | `columns` | `MysqlColumns` | 字段列表 |
  | `records` | `List<MysqlRecord>` | 行列表 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `hasResult()` | 是否有结果 | |
  | `parseResult(resultSet,connection)` | 解析（委托三参） | |
  | `parseResult(resultSet,connection,readonly)` | 抽象解析 | 子类实现 |
  | `dbName()/tableName()` | 库/表名称 | 从 `columns` 推导 |
  | `getPrimaryKey()` | 主键字段 | |
  | `isUpdatable()` | 是否可更新 | |
  | `getCount()/getUsedMs()/columnList()` | 计数/耗时/字段列表 | |
  | 各 `get/set` | 存取器 | |
- 调用链：`MysqlQueryResult.parseResult → MysqlColumns/MysqlRecord`

## MysqlQueryResults
- 职责：查询结果集容器（泛型 `R extends MysqlQueryResult`）。
- 字段：`errMsg`(String 错误消息)、`results`(List<R> 结果列表)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `addResult(R)` | 添加结果 | |
  | `isEmpty()` | 是否为空 | |
  | `isSuccess()` | 是否成功 | |
  | `parseError(Exception)` | 解析错误 | 填充 `errMsg` |
  | `getErrMsg/setErrMsg/getResults/setResults` | 存取器 | |
- 调用链：`MysqlClient.executeSql → MysqlQueryResults.addResult`

## 子包 cn.oyzh.easymysql.mysql.record

## MysqlDeleteRecordParam
- 职责：删除记录参数。
- 字段：`dbName`(String)、`schema`(String)、`tableName`(String)、`record`(MysqlRecordData)、`primaryKey`(MysqlRecordPrimaryKey)。
- 方法：各 `get/set`。
- 调用链：`MysqlClient.deleteRecord(param)`

## MysqlInsertRecordParam
- 职责：新增记录参数。
- 字段：`dbName`(String)、`schema`(String)、`tableName`(String)、`record`(MysqlRecordData)、`primaryKey`(MysqlRecordPrimaryKey)。
- 方法：各 `get/set`。
- 调用链：`MysqlClient.insertRecord(param)`

## MysqlRecord
- 职责：表记录模型，维护字段值属性与变更状态。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `readonly` | `boolean` | 是否只读 |
  | `editable` | `boolean` | 是否可编辑 |
  | `columns` | `MysqlColumns` | 字段列表 |
  | `properties` | `HashMap<String,MysqlRecordProperty>` | 字段数据 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 四个构造函数（columns/readonly 组合） | 构造 | |
  | `getColumns()` | 字段列表 | |
  | `putValue(String/MysqlColumn,value)` | 添加数据 | 生成 `MysqlRecordProperty` |
  | `getValue(column)/getOriginal(column)` | 取值/原始值 | |
  | `columns()` | 字段名集合 | |
  | `getProperty(key)/hasProperty(recordProperty)` | 属性 | |
  | `clear()` | 清空 | |
  | `update(Map)` | 更新数据 | |
  | `isChanged()/clearStatus()` | 变更状态 | 覆写 `DBObjectStatus` |
  | `discard()` | 抛弃变更 | |
  | `copy(MysqlRecord)` | 复制 | |
  | `getRecordData()/getChangedRecordData()/getOriginalRecordData()` | 记录数据 | 生成 `MysqlRecordData` |
  | `isColumnChanged(column)` | 指定字段是否变更 | |
  | `toMap()` | 转 Map | |
  | `destroy()` | 销毁 | `Destroyable` |
  | `isEditable/setEditable` | 可编辑状态 | |
- 调用链：`MysqlClient.selectRecords → MysqlRecord.putValue → MysqlRecordProperty`

## MysqlRecordData
- 职责：记录数据（字段到值的 Map 封装）。
- 字段：`dataList`(Map<MysqlColumn,Object> 数据集合)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `columns()/notNullColumns()` | 字段名集合/非空字段集合 | |
  | `column(column)/hasValue(column)/value(column)` | 字段查询 | |
  | `put(MysqlColumn,value)` | 添加数据 | |
  | `isEmpty()` | 是否为空 | |
  | `isTypeGeometry(column)` | 是否几何类型 | |
  | `remove(column)` | 移除 | |
  | `entries()/values()/columnSize()` | 条目/值/数量 | |
  | `notNull(column)` | 是否非空 | |
- 调用链：`MysqlRecord.getRecordData → MysqlRecordData.put`

## MysqlRecordFilter
- 职责：记录过滤条件（同时承载条件编辑控件）。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `Object` | 值 |
  | `enabled` | `boolean` | 是否启用（默认 true） |
  | `joinSymbol` | `String` | 连接符号 |
  | `condition` | `MysqlCondition` | 条件 |
  | `column` | `MysqlColumn` | 字段 |
  | `columns` | `List<MysqlColumn>` | 字段列表 |
  | `valueBox` | `FXHBox` | 值组件容器 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `value()` | 取值 | 从值组件读取 |
  | `getValueControl()` | 值组件 | 按条件类型 `updateValueControl` |
  | `updateValueControl()`（私有） | 更新值组件 | |
  | `getColumnControl/getConditionControl/getEnabledControl/getJoinSymbolControl` | 编辑控件 | `MysqlColumnComboBox` 等 |
  | `column()` | 字段名 | |
  | `condition()` | 生成条件字符串 | |
  | `isRequireCondition()` | 是否需要条件 | |
  | 各 `get/set` | 存取器 | |
- 调用链：`MysqlConditionUtil.buildCondition(filters) → MysqlRecordFilter.condition/value`

## MysqlRecordPrimaryKey
- 职责：记录主键模型，支持自动递增值回填。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `data` | `Object` | 当前数据 |
  | `columnName` | `String` | 字段名称 |
  | `column` | `MysqlColumn` | 字段 |
  | `returnData` | `Object` | 自动递增返回值 |
  | `originalData` | `Object` | 编辑前原始数据 |
  | `autoIncrement` | `boolean` | 是否自动递增 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(MysqlColumn,record)` | 初始化主键 | 从字段与记录取值 |
  | `data()/originalData()` | 数据/原始数据 | |
  | `shouldReturnData()` | 是否需回填 | 自动递增且无数据 |
  | `isChanged()` | 主键是否变更 | |
  | 各 `get/set` | 存取器 | |
- 调用链：`MysqlClient.insertRecord → MysqlRecordPrimaryKey.setReturnData`

## MysqlRecordProperty
- 职责：记录字段属性（`SimpleObjectProperty<Object>`），承载值与变更状态、编辑控件。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `changedProperty` | `SimpleBooleanProperty` | 是否变更 |
  | `column` | `MysqlColumn` | 表字段 |
  | `record` | `MysqlRecord` | 表记录 |
  | `original` | `Object` | 原始数据 |
  | `setToNullFlag` | `boolean` | 置 null 标志 |
  | `readonly` | `boolean` | 只读模式 |
  | `node` | `Node` | 编辑节点 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlRecordProperty(record,column,value,readonly)` | 构造 | |
  | `get()/set(newValue)/getValue()` | 值读写 | 变更时标记 `changed` |
  | `discard()` | 抛弃变更 | 恢复原始值 |
  | `changedProperty()/isChanged()/setChanged` | 变更状态 | |
  | `updateOriginal()` | 更新原始数据 | |
  | `getControl()` | 编辑控件 | 按字段类型生成控件 |
  | `vCopy()/vPaste()` | 复制/粘贴 | 剪贴板 |
  | `vCopyAsInsertSql()/vCopyAsUpdateSql()` | 复制为 SQL | |
  | `vSetToNull()/vSetToEmptyString()` | 置 null/空串 | |
  | `getColumn/setColumn/getOriginal/setOriginal/isReadonly/getNode` | 存取器 | |
  | `destroy()` | 销毁 | 释放控件 |
- 调用链：`MysqlRecord.putValue → MysqlRecordProperty`；`MysqlRecordProperty.getControl → FX* 控件`

## MysqlSelectRecordParam
- 职责：记录查询参数。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `start` | `Long` | 起始位置 |
  | `limit` | `Long` | 查询条数 |
  | `dbName` | `String` | 库名称 |
  | `schema` | `String` | 模式名称 |
  | `tableName` | `String` | 表名称 |
  | `readonly` | `boolean` | 是否只读 |
  | `columns` | `List<MysqlColumn>` | 字段列表 |
  | `filters` | `List<MysqlRecordFilter>` | 过滤条件 |
  | `primaryKey` | `MysqlRecordPrimaryKey` | 记录主键 |
- 方法：`hasPageControl()`（是否含分页）与各 `get/set`。
- 调用链：`MysqlClient.selectRecords(param)`

## MysqlUpdateRecordParam
- 职责：更新记录参数。
- 字段：`dbName`(String)、`schema`(String)、`tableName`(String)、`record`(MysqlRecordData 原始数据)、`updateRecord`(MysqlRecordData 待更新数据)、`primaryKey`(MysqlRecordPrimaryKey)。
- 方法：各 `get/set`。
- 调用链：`MysqlClient.updateRecord(param)`

## 子包 cn.oyzh.easymysql.mysql.routine

## MysqlRoutineParam
- 职责：存储程序参数（可含类型/模式/长度/字符集等及编辑控件）。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbClient` | `MysqlClient` | 客户端 |
  | `name` | `String` | 名称 |
  | `typeProperty` | `StringProperty` | 类型 |
  | `mode` | `String` | 模式（IN/OUT/INOUT） |
  | `size` | `Integer` | 长度 |
  | `digits` | `Integer` | 小数位 |
  | `value` | `String` | 值 |
  | `charsetProperty` | `StringProperty` | 字符集 |
  | `collation` | `String` | 排序 |
  | `charsetControl` | `DBCharsetComboBox` | 字符集控件 |
  | `digitsControl` | `NumberTextField` | 小数位控件 |
  | `sizeControl` | `NumberTextField` | 长度控件 |
  | `valueControl` | `DBEnumTextFiled` | 值控件 |
  | `collationControl` | `DBCollationComboBox` | 排序控件 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getType/setType/getCharset/setCharset` | 类型/字符集 | 走属性 |
  | `getNameControl/getTypeControl/getCharsetControl/getDigitsControl/getSizeControl/getValueControl/getCollationControl/getModeControl` | 各编辑控件 | `MysqlFiledTypeComboBox`、`MysqlParamModeComboBox` 等 |
  | `getValueList()` | 值列表 | |
  | `isReturnParam()` | 是否为返回值参数 | 模式为空时视为返回参数 |
  | `getDefinition()` | 生成参数定义 | |
  | `setDtdIdentifier(String)` | 解析类型定义 | 拆分长度/小数位/字符集 |
  | `getName/setName/getMode/setMode/getSize/setSize/getDigits/setDigits/getValue/setValue/getCollation/setCollation` | 存取器 | |
  | `typeProperty()/charsetProperty()` | 属性 | |
  | `setDbClient` | 存取客户端 | |
- 调用链：`MysqlHelper.listRoutineParam → MysqlRoutineParam.setDtdIdentifier`

## MysqlRoutineSchema
- 职责：存储程序（过程/函数）基类模型。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `params` | `List<MysqlRoutineParam>` | 参数列表 |
  | `dbName` | `String` | 库名称 |
  | `comment` | `String` | 注释 |
  | `definer` | `String` | 定义者 |
  | `securityType` | `String` | 安全性 |
  | `characteristic` | `String` | 特征 |
  | `nameProperty` | `SimpleStringProperty` | 程序名称 |
  | `definitionProperty` | `SimpleStringProperty` | 程序定义 |
  | `createDefinitionProperty` | `SimpleStringProperty` | 程序创建定义 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `nameProperty()/setName/getName` | 名称 | |
  | `definitionProperty()/setDefinition/getDefinition` | 定义 | |
  | `createDefinitionProperty()` | 创建定义属性 | |
  | `setCreateDefinition(String)` | 设置创建定义并解析定义者与注释 | |
  | `getCreateDefinition()` | 创建定义 | |
  | `compare(MysqlRoutineSchema)` | 比较 | `ObjectComparator` |
  | `isNew()` | 是否新数据 | |
  | `getParams/setParams/getDbName/setDbName/getComment/setComment/getDefiner/setDefiner/getSecurityType/setSecurityType/getCharacteristic/setCharacteristic` | 存取器 | |
- 调用链：`MysqlClient.functions/procedures → MysqlRoutineSchema.setCreateDefinition`

## 子包 cn.oyzh.easymysql.mysql.table

## MysqlAlertTableParam
- 职责：修改表参数（聚合表、字段、索引、外键、触发器、检查器）。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `table` | `MysqlTable` | 表 |
  | `checks` | `MysqlChecks` | 检查器集合 |
  | `columns` | `MysqlColumns` | 字段集合 |
  | `indexes` | `MysqlIndexes` | 索引集合 |
  | `triggers` | `MysqlTriggers` | 触发器集合 |
  | `foreignKeys` | `MysqlForeignKeys` | 外键集合 |
  | `existPrimaryKey` | `boolean` | 是否存在主键 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `dbName()` | 库名称 | 取自 `table` |
  | `hasColumns/hasIndex/hasForeignKey/hasCheck/hasTrigger` | 存在性判断 | |
  | `primaryKeys()` | 主键字段列表 | |
  | `primaryKeyChanged()/columnChanged()` | 主键/字段是否变更 | |
  | `tableName()/setTableName/getTable/setTable` | 表信息 | |
  | 各集合 `get/set` | 存取器 | |
  | `isExistPrimaryKey/setExistPrimaryKey` | 主键存在标志 | |
- 调用链：`MysqlClient.alertTable → MysqlTableAlertSqlGenerator.generateSql`

## MysqlCreateTableParam
- 职责：创建表参数（聚合表、字段、索引、外键、触发器、检查器）。
- 字段：`table`(MysqlTable)、`checks`(MysqlChecks)、`columns`(MysqlColumns)、`indexes`(MysqlIndexes)、`triggers`(MysqlTriggers)、`foreignKeys`(MysqlForeignKeys)。
- 方法：`dbName()`、`hasColumns/hasIndex/hasForeignKey/hasCheck/hasTrigger`、`primaryKeys()`、`tableName/setTableName`、各集合 `get/set`。
- 调用链：`MysqlClient.createTable → MysqlTableCreateSqlGenerator.generateSql`

## MysqlSelectTableParam
- 职责：查询表参数。
- 字段：`full`(boolean 是否全量)、`dbName`(String)、`tableName`(String)。
- 方法：`isFull/setFull/getDbName/setDbName/getTableName/setTableName`。
- 调用链：`MysqlClient.selectTables(param)`

## MysqlTable
- 职责：表模型（引擎/字符集/排序/行格式/注释等）。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `hasPrimaryKey` | `boolean` | 是否存在主键 |
  | `rowFormat` | `String` | 行格式 |
  | `autoIncrement` | `Long` | 自动递增值 |
  | `createDefinition` | `String` | 表创建定义 |
  | `engine` | `String` | 引擎 |
  | `charset` | `String` | 字符集 |
  | `collation` | `String` | 排序规则 |
  | `dbName` | `String` | 库名称 |
  | `schema` | `String` | 模式名称 |
  | `nameProperty` | `SimpleStringProperty` | 表名称 |
  | `commentProperty` | `SimpleStringProperty` | 表注释 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `setEngine/isEngineChanged/setCharset/isCharsetChanged/setCollation/isCollationChanged/setRowFormat/isRowFormatChanged/setAutoIncrement/isAutoIncrementChanged` | 属性设置与变更判断 | |
  | `hasCharset/hasCollation/hasEngine/hasRowFormat/hasAutoIncrement` | 存在性判断 | |
  | `setCharsetAndCollation(collation)` | 依排序规则同时设字符集与排序 | |
  | `copy(MysqlTable)` | 复制 | |
  | `isInnoDB()` | 是否 InnoDB | |
  | `nameProperty()/setName/getName/commentProperty()/setComment/getComment` | 名称/注释 | |
  | `compare(MysqlTable)/isNew()` | 比较/是否新 | |
  | `isHasPrimaryKey/setHasPrimaryKey/getRowFormat/getAutoIncrement/getCreateDefinition/setCreateDefinition/getEngine/getCharset/getCollation/getDbName/setDbName/getSchema/setSchema` | 存取器 | |
- 调用链：`MysqlClient.selectTables → MysqlTable.setCharsetAndCollation`

## 子包 cn.oyzh.easymysql.mysql.trigger

## MysqlTrigger
- 职责：表触发器模型。
- 字段：`name`(String 名称)、`policy`(String 策略)、`definition`(String 定义)、`tableName`(String 表名)。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `originalName()` | 原始名称 | |
  | `setName/setPolicy/setDefinition/setTableName` | 存取 | |
  | `setPolicy(timing,manipulation)` | 由时机与操作合成策略 | 如 `BEFORE INSERT` |
  | `copy(MysqlTrigger)` | 复制 | |
  | `isInvalid()` | 是否无效 | |
  | `getName/getPolicy/getDefinition/getTableName` | 存取器 | |
- 调用链：`MysqlClient.triggers → MysqlTrigger.setPolicy`

## MysqlTriggerControl
- 职责：触发器编辑控件（继承 `MysqlTrigger`）。
- 字段：无（继承）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getNameControl()` | 名称控件 | `ClearableTextField` |
  | `getPolicyControl()` | 策略控件 | `MysqlTriggerPolicyComboBox` |
  | `getDefinitionControl()` | 定义控件 | `EnlargeTextFiled` |
  | `of(MysqlTrigger)` / `of(List<MysqlTrigger>)` | 转控件/控件列表 | |
- 调用链：`MysqlTriggerControl.of → MysqlTrigger.copy`

## MysqlTriggers
- 职责：触发器列表（`DBObjectList<MysqlTrigger>`）。
- 字段：无。
- 方法：构造函数（无参/带 `List`）。
- 调用链：`MysqlClient.triggers → MysqlTriggers`

## 子包 cn.oyzh.easymysql.mysql.view

## MysqlView
- 职责：视图模型（含算法、检查选项、可更新性、字段）。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `definer` | `String` | 定义者 |
  | `algorithm` | `String` | 算法 |
  | `updatable` | `boolean` | 是否可变更 |
  | `checkOption` | `String` | 检查选项 |
  | `securityType` | `String` | 安全性 |
  | `definitionProperty` | `SimpleStringProperty` | 视图定义 |
  | `dbName` | `String` | 库名称 |
  | `schema` | `String` | 模式名称 |
  | `columns` | `MysqlColumns` | 表字段 |
  | `nameProperty` | `SimpleStringProperty` | 名称 |
  | `commentProperty` | `SimpleStringProperty` | 注释 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `definitionProperty()/setDefinition/getDefinition` | 视图定义 | |
  | `copy(MysqlView)` | 复制 | |
  | `hasCheckOption()` | 是否存在检查选项 | |
  | `nameProperty()/setName/getName/commentProperty()/setComment/getComment` | 名称/注释 | |
  | `primaryKeyChanged()/primaryKeys()/hasPrimaryKey()` | 主键相关 | |
  | `hasColumns()/columns()/getColumns/setColumns` | 字段 | |
  | `hasComment()` | 是否有注释 | |
  | `compare(MysqlView)/isNew()` | 比较/是否新 | |
  | `removeColumn(MysqlColumn)` | 移除字段 | |
  | `getDefiner/setDefiner/getAlgorithm/setAlgorithm/isUpdatable/setUpdatable/getCheckOption/setCheckOption/getSecurityType/setSecurityType` | 存取器 | |
  | `getDbName/setDbName/getSchema/setSchema` | 库/模式 | |
  | `getDefinitionProperty/setDefinitionProperty/definitionPropertyProperty` | 定义属性存取 | |
- 调用链：`MysqlClient.views/view → MysqlView.copy`；`MysqlHelper.isViewUpdatable → MysqlView.setUpdatable`

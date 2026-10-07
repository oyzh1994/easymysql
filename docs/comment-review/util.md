# util 包代码审查

> 包路径：`cn.oyzh.easymysql.util`
> 覆盖类数：9

## DBColumnUtil
- 职责：MySQL 字段类型（数据库类型）的元信息工具类，集中定义各类型的支持能力并对外提供静态查询；内含私有内部类 `DBColumnField`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | COLUMN_FIELD | List<DBColumnField> | 静态注册表，存放全部字段类型定义（static 块初始化） |

  **内部类 `DBColumnField` 字段**：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | name | String | 类型名称 |
  | maxValue / minValue | Long | 最大/最小取值（如 BIT 为 1/0） |
  | suggestSize | Integer | 推荐字段长度 |
  | exampleValue | String | 示例值（几何类型用） |
  | supportBit/supportSize/supportJson/supportEnum/supportValue/supportBinary/supportDigits/supportString/supportKeySize/supportInteger/supportCharset/supportUnsigned/supportZeroFill/supportGeometry/supportTimestamp/supportDefaultValue/supportAutoIncrement | boolean | 该类型的各项能力开关 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static { ... }`（static 块） | 注册所有内建类型 | 逐个 new DBColumnField 并设置能力位，再 `putFiled` 加入 COLUMN_FIELD |
  | `private static void putFiled(DBColumnField)` | 注册字段定义 | `COLUMN_FIELD.add(...)` |
  | `static List<String> fields()` | 获取类型名称列表 | parallelStream map getName |
  | `static boolean supportSize/supportUnsigned/supportJson/supportKeySize/supportString/supportValue/supportZeroFill/supportBit/supportBinary/supportDigits/supportDefaultValue/supportGeometry/supportEnum/supportCharset/supportTimestamp/supportInteger/supportAutoIncrement(String type)` | 查询某类型是否具备对应能力 | 遍历 COLUMN_FIELD，按名称忽略大小写匹配后返回对应标志 |
  | `static Integer suggestSize(String type)` | 推荐长度 | 匹配返回 suggestSize |
  | `static Object exampleValue(String type)` | 示例值 | 匹配返回 exampleValue（未命中返回 false） |
  | `static Long minValue/maxValue(String type)` | 最小/最大值 | 匹配返回 |
  | `static boolean isYearType/isDateType/isTimeType/isPolygonType/isMultiPolygonType/isPointType/isMultiPointType/isLineStringType/isMultiLineStringType/isGeomCollectionType/isGeometryType(String type)` | 具体类型判定 | 字符串忽略大小写比较 |
  | `static Object defaultValue(String type)` | 默认值 | 支持默认值前提下按 digits→0.0、integer→0、string→""、json→"{'a':1}"、binary→new byte[]{} 返回 |

- 调用链：`DBNodeUtil.generateNode → MysqlColumn.supportJson/supportString... → DBColumnUtil.supportXxx`
- 调用链：`DBRecordUtil.getNode → MysqlColumn.supportBinary → DBColumnUtil.supportBinary`

## DBConnectUtil
- 职责：数据库连接相关的工具类，提供连接测试、关闭与命令行式参数解析。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static void testConnect(StageAdapter view, MysqlConnect dbInfo)` | 异步测试连接 | 线程内禁用页面、置等待光标；`DBClientUtil.newClient` 建客户端后 `start()`，`isConnected()` 决定 toast 文案，finally 恢复页面 |
  | `static void close(MysqlClient client, boolean async)` | 关闭连接 | 已连接时按 async 决定 `ThreadUtil.start` 或直接 `client.close()` |
  | `static cn.oyzh.easymysql.dto.MysqlConnect parse(String input)` | 解析 `-h -p -a -n` 形式的连接参数 | 按空格切分，遇标志位设置 type，下一词按类型填入 host/port/password/db（db 解析为 int） |

- 调用链：`DBConnectUtil.testConnect → DBClientUtil.newClient → MysqlClient.start/isConnected/close`

## DBDataUtil
- 职责：数据库数据转换工具类，将记录值按不同导出格式（json/xml/csv/sql/html/xls）参数化并生成插入/更新 SQL。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static String escapeQuotes(String)` | 转义引号与换行 | 逐字符将 `"`→`\"`、`\`→`\\`、`\r`→`\r`、`\n`→`\n`（单引号保持不变） |
  | `static Object parameterizedForJson/parameterizedForXml/parameterizedForHtml(MysqlColumn, Object)` | json/xml/html 参数化 | 按 geometry/date/timestamp/json/binary/bit/enum/string/integer 分派 |
  | `static Object parameterizedForCsv(MysqlColumn, Object)` | csv 参数化 | 字符串值加双引号包裹 |
  | `static Object parameterizedForSql(MysqlColumn, Object)` | sql 参数化 | null→"NULL"；geometry→`ST_GeomFromText('..')`；binary→`0x..`；bit→`b'..'`；string→单引号包裹 |
  | `static Object parameterizedForXls(MysqlColumn, Object)` | xls 参数化 | timestamp 用 `yyyy/M/dd HH:mm:ss` |
  | `static String toInsertSql(MysqlColumns, MysqlRecord, boolean)` | 单条插入 SQL | 取 `toInsertSql(list)` 首个结果 |
  | `static List<String> toInsertSql(MysqlColumns, List<MysqlRecord>[, boolean includeFields])` | 批量插入 SQL | 用 `DBUtil.wrap(tableName, MYSQL)` 拼 INSERT，逐字段 `parameterizedForSql` |
  | `static String toUpdateSql(MysqlColumns, MysqlRecord)` | 更新 SQL | 由 `DBUtil.initPrimaryKey` 取主键；无主键时用全部字段作 WHERE 并加 LIMIT 1；geometry 字段用 `ST_GeomFromText(..)` |
  | `static List<Map<String,Object>> toInsertJson/toInsertXml(...)` | 生成 map 列表 | 逐记录逐字段参数化放入 map |
  | `static List<List<Object>> toInsertCsv/toInsertHtml/toInsertXls(...)` | 生成行列表 | 逐记录逐字段参数化放入 list |

- 调用链：`DBDataUtil.toInsertSql → columns.sortOfPosition → parameterizedForSql → DBUtil.wrap`
- 调用链：`DBDataUtil.toUpdateSql → DBUtil.initPrimaryKey → parameterizedForSql`

## DBExportUtil
- 职责：db 导出工具类（占位类，当前无任何成员与方法）。
- 字段：无
- 方法：无
- 调用链：`（暂无真实调用）`

## DBI18nHelper
- 职责：db 相关国际化文案帮助类。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static String tableTip2()` | 表提示2 | `I18nResourceBundle.i18nString("db.table.tip2")` |
  | `static String tableTip3()` | 表提示3 | 同上，key=`db.table.tip3` |
  | `static String tableTip4()` | 表提示4 | 同上，key=`db.table.tip4` |

- 调用链：`DBI18nHelper.tableTip2 → I18nResourceBundle.i18nString`

## DBNodeUtil
- 职责：数据库字段（值）与 JavaFX 输入控件之间的桥接工具类，负责按字段类型生成输入节点、读取/设置节点值、生成字段标签。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static Object getNodeVal(Node)` | 读取节点值 | 依节点实际类型（各 TextField/TextArea/ComboBox）取 value 或 text |
  | `static void setNodeVal(Node, Object)` | 设置节点值 | 依节点类型 setValue/setText/select |
  | `static Node generateNode(MysqlColumn)` | 生成字段节点 | 委托 `generateNode(column, true)` |
  | `static Node generateNode(MysqlColumn, boolean handlerDefaultValue)` | 按字段类型生成输入控件 | json→Editor、string→ClearableTextField、bit→BitTextField、integer→NumberTextField、digits→DecimalTextField、year/time/date/timestamp→对应控件、binary→ChooseFileTextField；随后 handlerDigits/handlerComment/handlerDefaultValue |
  | `static List<FXLabel> generateTags(MysqlColumn)` | 生成字段标签 | 按 nullable/autoIncrement/updateOnCurrentTimestamp/primaryKey/unsigned/zeroFill 生成带样式的 FXLabel |
  | `static void handlerDigits(Node, Integer)` | 处理小数位 | DecimalTextField 且位数>0 时 `setScaleLen` |
  | `static void handlerComment(Node, String)` | 处理注释 | TextInputControl 时设置 promptText |
  | `static void handlerDefaultValue(Node, Object)` | 处理默认值 | DigitalTextField/ComboBox/TextInputControl 分别赋值 |
  | `static void handlerExampleValue(Node, Object)` | 处理示例值 | DigitalTextField/ChooseFileTextField/TextInputControl 赋值 |

- 调用链：`DBNodeUtil.generateNode → MysqlColumn.supportJson/... → (控件构造) → handlerDefaultValue`

## DBRecordUtil
- 职责：数据库记录展示工具类，按字段类型生成/格式化记录编辑节点，并提供字段右键菜单。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static Node getNode(MysqlRecordProperty, Object, MysqlColumn)` | 生成记录字段节点 | 依字段能力选择 JsonTextFiled/BinaryTextFiled/SelectTextFiled/NumberTextField/...；TextField 时设置空值提示与右键菜单，并监听文本变化置 `property.setChanged(true)` |
  | `static String formatValue(Object, MysqlColumn)` | 格式化字段值 | 按字段能力调用对应控件的静态 `format`；无类型时按 CharSequence/byte[]/Date 处理 |
  | `static String nullPromptText()` | 空值提示文本 | 返回 `"(Null)"` |
  | `static double suitableColumnWidth(MysqlColumn)` | 计算合适列宽 | 用 `FontUtil.textWidth` 计算名称与类型宽度取大者 +30 |
  | `static ContextMenu getColumnContextMenu(MysqlRecordProperty)` | 获取字段右键菜单 | 包装 `getColumnMenuItem` 结果 |
  | `static List<FXMenuItem> getColumnMenuItem(MysqlRecordProperty)` | 获取字段菜单项 | 复制/粘贴/置空/置空串/复制为 Insert/复制为 Update，绑定 property::vXxx |

- 调用链：`DBRecordUtil.getNode → MysqlColumn.supportBinary/... → BinaryTextFiled.setValue → property.setChanged(true)`

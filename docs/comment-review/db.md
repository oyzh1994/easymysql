# db 包代码审查

> 包路径：`cn.oyzh.easymysql.db`
> 覆盖类数：10（DBEvents.java、MysqlDBClient.java 为整文件注释的死代码，已跳过）

## DBClientUtil
- 职责：数据库客户端的静态工厂封装，按方言创建客户端实例。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static MysqlClient newClient(MysqlConnect info)` | 根据连接信息新建数据库客户端 | 当 `info.getType()` 为空或 `DBDialect.valueOf(type)==MYSQL` 时返回 `new MysqlClient(info)`，否则返回 `null` |

- 调用链：`DBConnectUtil.testConnect → DBClientUtil.newClient → MysqlClient.<init>`

## DBConnConfig
- 职责：数据库连接配置的载体（主机、端口、服务等）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | host | String | 主机地址 |
  | port | Integer | 端口 |
  | sid | String | 服务 id |
  | username | String | 用户名 |
  | serviceName | String | 服务名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String getConnectionString(DBDialect dialect)` | 获取连接字符串 | 直接拼接并返回 `"jdbc:mysql://" + host + ":" + port + "/"`（当前未使用 dialect 分支） |
  | `getHost/setHost`、`getPort/setPort`、`getSid/setSid`、`getUsername/setUsername`、`getServiceName/setServiceName` | 标准 getter/setter | 直接读写字段 |

- 调用链：`DBConnConfig.getConnectionString → String 拼接`

## DBConnState
- 职责：数据库连接状态的枚举。
- 字段（枚举常量）：

  | 常量 | 含义 |
  |---|---|
  | NOT_INITIALIZED | 未初始化（isConnected=false） |
  | CONNECTED | 已连接（isConnected=true） |
  | CONNECTING | 连接中（false） |
  | CLOSED | 已关闭（false） |
  | FAILED | 失败（false） |
  | BROKEN | 错误（false） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `abstract boolean isConnected()` | 是否已连接 | 由每个枚举常量实现，仅 CONNECTED 返回 true |

- 调用链：`DBConnState.isConnected → 各枚举常量实现`

## DBConnectManager
- 职责：连接与连接树节点的管理接口，定义对连接项（`DBConnectTreeItem`）的增删查。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void addConnect(MysqlConnect)` | 添加连接 | 抽象方法，由实现类完成 |
  | `default void addConnects(List<MysqlConnect>)` | 批量添加连接 | 遍历列表逐个调用 `addConnect` |
  | `void addConnectItem(DBConnectTreeItem)` | 添加连接键 | 抽象方法 |
  | `void addConnectItems(List<DBConnectTreeItem>)` | 批量添加连接键 | 抽象方法 |
  | `boolean delConnectItem(DBConnectTreeItem)` | 删除连接键 | 抽象方法 |
  | `List<DBConnectTreeItem> getConnectItems()` | 获取连接键列表 | 抽象方法 |
  | `default List<DBConnectTreeItem> getConnectedItems()` | 获取已连接的连接节点 | 对 `getConnectItems()` 并行流按 `isConnected` 过滤 |

- 调用链：`DBConnectManager.getConnectedItems → getConnectItems → 过滤 isConnected`

## DBConnectionManager
- 职责：JDBC 连接的持有与缓存管理，按库/schema/函数/过程维度维护连接。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | serverConnection | Connection | 服务级连接 |
  | connections | Map<String, Connection> | 库连接缓存，key 形如 `dbName`、`dbName_schema`、`dbName_schema_function`、`dbName_schema_procedure` |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void addConnection(String dbName, Connection)` | 添加库连接 | `connections.put(dbName, connection)` |
  | `void addSchemaConnection(String dbName, String schema, Connection)` | 添加 schema 连接 | key = `dbName + "_" + schema` |
  | `void addFunctionConnection(String dbName, String schema, Connection)` | 添加函数连接 | key = `dbName_schema_function` |
  | `void addProcedureConnection(String dbName, String schema, Connection)` | 添加存储过程连接 | key = `dbName_schema_procedure` |
  | `Connection getConnection(String dbName)` | 获取库连接 | `connections.get(dbName)` |
  | `Connection getSchemaConnection(String dbName, String schema)` | 获取 schema 连接 | 按组合 key 获取 |
  | `Connection getFunctionConnection(...)` / `getProcedureConnection(...)` | 获取函数/过程连接 | 按组合 key 获取 |
  | `boolean hasConnection(String dbName)` | 是否存在库连接 | `containsKey` |
  | `void destroy()` | 销毁全部连接 | 关闭 serverConnection 与所有 connections（忽略 SQLException），清空 map |
  | `getServerConnection/setServerConnection`、`getConnections` | 访问器 | 读写字段 |

- 调用链：`DBConnectionManager.destroy → Connection.close（逐个）`

## DBDatabase
- 职责：数据库（库）信息模型，含名称、字符集、排序规则，支持 JavaFX 属性绑定。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | name | String | 库名称 |
  | charsetProperty | SimpleStringProperty | 字符集（懒初始化属性） |
  | collationProperty | SimpleStringProperty | 排序规则（懒初始化属性） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `SimpleStringProperty charsetProperty()` / `collationProperty()` | 获取属性对象 | 懒创建 SimpleStringProperty |
  | `setCharset/getCharset`、`setCollation/getCollation` | 读写字符集/排序 | 基于属性对象 |
  | `void setCharsetAndCollation(String collation)` | 由排序规则同时推得字符集 | 取 `collation.split("_")[0]` 作为 charset；含 `_` 时设置 collation，否则置 null |
  | `getName/setName` | 库名称读写 | 直接读写 |

- 调用链：`MysqlDBClient.databases → DBDatabase.setCharsetAndCollation → setCharset/setCollation`

## DBDialect
- 职责：数据库方言（类型）枚举，目前仅 MYSQL。
- 字段（枚举常量）：`MYSQL`
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DbType dbType()` | 获取 Druid 方言类型 | switch 返回 `DbType.mysql`（default 亦为 mysql） |
  | `static List<DBDialect> valueList()` | 获取全部方言列表 | `Collections.addAll(list, values())` |

- 调用链：`DBUtil.wrap(..., dialect) → DBDialect.MYSQL 判断`

## DBFeature
- 职责：数据库特性枚举。
- 字段（枚举常量）：

  | 常量 | 含义 |
  |---|---|
  | CHECK | 检查约束 |
  | EVENT | 事件 |

- 方法：无
- 调用链：`DBFeature.CHECK/EVENT → 特性判断`

## DBObjectList
- 职责：数据库对象状态列表（抽象类，继承 `ArrayList<S>`），按新增/变更/删除状态分类筛选对象。
- 字段（常量）：

  | 常量 | 类型 | 含义 |
  |---|---|---|
  | TYPE_NORMAL | byte | 正常=0 |
  | TYPE_DELETED | byte | 已删除=1 |
  | TYPE_CREATED | byte | 已新增=2 |
  | TYPE_CHANGED | byte | 已变更=3 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean isChanged()` | 是否存在变更对象 | 遍历元素判断 `getStatus()` 非空 |
  | `List<S> createdList()` | 已新增对象列表 | stream filter `isCreated` |
  | `List<S> changedList()` | 已变更对象列表 | stream filter `isChanged` |
  | `List<S> deletedList()` | 已删除对象列表 | stream filter `isDeleted` |
  | `List<S> normalList()` | 正常对象列表 | stream filter `isNormal` |
  | `List<S> filterList(byte... types)` | 按类型过滤 | 按类型分别调用对应 list 并合并 |
  | `boolean add(S s)` | 覆盖 add | 非 null 才加入 |
  | `remove(S)` / `contains(S)` | 覆盖 remove/contains | 委托父类 |
  | `hasDeleted/hasCreated/hasChanged/hasNormal` | 是否存在对应状态对象 | 遍历判断 |
  | `static isDeleted/isCreated/isChanged/isNormal(DBObjectStatus)` | 状态判定 | 依据 isDeleted/isCreated/isChanged 组合判断，普通状态需三者皆否 |

- 调用链：`DBObjectList.isCreated(status) → DBObjectStatus.isCreated/isDeleted`

## DBObjectStatus
- 职责：数据库对象的状态基类，维护变更/新增/删除标记、原始数据快照与状态符号（+ *）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | changedProperty | SimpleBooleanProperty | 是否变更 |
  | deletedProperty | SimpleBooleanProperty | 是否删除 |
  | createdProperty | SimpleBooleanProperty | 是否新增 |
  | changedFlag | Map<String, Boolean> | 各字段变更标记 |
  | originalData | Map<String, Object> | 原始数据快照 |
  | statusProperty | SimpleStringProperty | 状态符号（`+` 新增 / `*` 变更 / 空 正常） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `changedProperty/deletedProperty/createdProperty/statusProperty` | 获取属性对象 | 懒初始化 |
  | `private changedFlag()` | 变更标记集合 | 懒创建 HashMap |
  | `private setChangedFlag(String, Boolean)` | 设置字段变更标记 | true 则 put，否则 remove；随后 `setChanged(!isEmpty)` |
  | `protected clearChangedFlag()` | 清除变更标记 | 清空 map |
  | `protected originalData()/getOriginalData(key)/putOriginalData(key,value)` | 原始数据快照读写 | put 时若已存在则比较并 `setChangedFlag`，否则记录 |
  | `clearOriginalData()` | 清除快照 | 清空 map |
  | `protected checkOriginalData(key, currentData)` | 校验是否变化 | `!Objects.equals(getOriginalData(key), currentData)` |
  | `void initStatus()` | 初始化状态 | 空实现，供子类覆盖 |
  | `setChanged/isChanged`、`setDeleted/isDeleted`、`setCreated/isCreated` | 状态读写 | setter 后调用 `updateStatus()` |
  | `clearStatus()` | 清除状态 | 三态复位并清空变更标记 |
  | `updateStatus()` | 更新状态符号 | created→`+`；changed→`*`；否则空串 |
  | `String getStatus()` | 获取状态符号 | 读取 statusProperty |

- 调用链：`DBObjectStatus.setChanged → updateStatus → statusProperty.set`
- 调用链：`putOriginalData → checkOriginalData → setChangedFlag → setChanged`

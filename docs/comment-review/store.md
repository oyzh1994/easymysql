# store 包代码审查

> 包路径：`cn.oyzh.easymysql.store`
> 覆盖类数：7（DBGroupStore、DBInfoStore、DBPageInfoStore、DBQueryStore、DBSearchHistoryStore、DBSettingStore 为整文件注释死代码，已跳过）
> 说明：以下 Store 均继承 `cn.oyzh.store.jdbc` 提供的 Jdbc 存储基类，通过 H2 本地库持久化实体。

## MysqlConnectStore
- 职责：db 连接实体（`MysqlConnect`）的持久化存储，并级联维护关联的 SSH 配置。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlConnectStore | 单例实例（static final） |
  | sshConfigStore | MysqlSSHConfigStore | SSH 配置存储（final，取自其单例） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `List<MysqlConnect> load()` | 加载全部连接 | 委托 `super.selectList()` |
  | `boolean replace(MysqlConnect model)` | 新增或更新连接 | `super.exist(id)` 决定 update 或 insert；随后处理 sshConfig：非空则设 iid 后 `sshConfigStore.replace`，否则 `deleteByIid` |
  | `boolean delete(MysqlConnect model)` | 删除连接 | 委托 `super.delete`，成功后级联 `sshConfigStore.deleteByIid` |
  | `protected Class<MysqlConnect> modelClass()` | 模型类型 | 返回 `MysqlConnect.class` |

- 调用链：`MysqlConnectStore.replace → super.exist → insert/update → MysqlSSHConfigStore.replace`
- 调用链：`MysqlConnectStore.delete → super.delete → MysqlSSHConfigStore.deleteByIid`

## MysqlGroupStore
- 职责：db 分组实体（`MysqlGroup`）的持久化存储。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlGroupStore | 单例实例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `List<MysqlGroup> load()` | 加载分组列表 | `super.selectList()` |
  | `boolean replace(MysqlGroup group)` | 新增或更新分组 | 按名称或 gid 存在则 update，否则 insert |
  | `boolean delete(String name)` | 按名称删除 | 构造 `DeleteParam` + `QueryParam("name", name)` 后删除 |
  | `boolean exist(String name)` | 是否存在分组 | 用 name 参数 map 调 `super.exist` |
  | `protected Class<MysqlGroup> modelClass()` | 模型类型 | 返回 `MysqlGroup.class` |

- 调用链：`MysqlGroupStore.replace → exist(gid)/exist(name) → insert/update`

## MysqlQueryStore
- 职责：db 查询实体（`MysqlQuery`）的持久化存储。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlQueryStore | 单例实例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `List<MysqlQuery> list(String iid, String dbName)` | 按连接 id 与库名查询 | 组装 `SelectParam`（iid、dbName 两个 QueryParam）后 `super.selectList` |
  | `boolean replace(MysqlQuery model)` | 新增或更新 | `exist(model.getUid())` 不满足则 insert，否则 update |
  | `boolean deleteByIid(String iid)` | 按连接 id 删除 | `StringUtil.isEmpty(iid)` 为真时构造 DeleteParam 删除（注意此处判断条件为 isEmpty） |
  | `protected Class<MysqlQuery> modelClass()` | 模型类型 | 返回 `MysqlQuery.class` |

- 调用链：`MysqlQueryStore.list → SelectParam.addQueryParam(iid/dbName) → super.selectList`

## MysqlSSHConfigStore
- 职责：db SSH 配置实体（`MysqlSSHConfig`）的持久化存储。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlSSHConfigStore | 单例实例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean replace(MysqlSSHConfig model)` | 新增或更新 | `super.exist(iid)` 决定 update 或 insert |
  | `void deleteByIid(String iid)` | 按连接 id 删除 | 构造 DeleteParam 删除 |
  | `MysqlSSHConfig getByIid(String iid)` | 按连接 id 查询 | `super.selectOne(QueryParam.of("iid", iid))` |
  | `protected Class<MysqlSSHConfig> modelClass()` | 模型类型 | 返回 `MysqlSSHConfig.class` |

- 调用链：`MysqlConnectStore.replace → MysqlSSHConfigStore.replace → super.exist/update/insert`

## MysqlSettingStore
- 职责：db 设置实体（`MysqlSetting`）的键值存储。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlSettingStore | 单例实例 |
  | SETTING | MysqlSetting | 全局当前设置（实例化时即 `INSTANCE.load()`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlSetting load()` | 加载设置 | `super.select()`，异常时打印日志；null 时返回 `new MysqlSetting()` |
  | `boolean replace(MysqlSetting model)` | 更新设置 | 非空则 `update(model)` |
  | `protected Class<MysqlSetting> modelClass()` | 模型类型 | 返回 `MysqlSetting.class` |

- 调用链：`EasyMysqlApp.init → MysqlSettingStore.SETTING（= INSTANCE.load）`

## MysqlStoreUtil
- 职责：本地存储（H2 Jdbc）初始化工具类。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static void init()` | 初始化存储 | 设置 `JdbcConst` 缓存/分页/方言(H2)/文件路径（`MysqlConst.STORE_PATH + "db"`），调用 `JdbcManager.takeoff()`；若异常信息含 `Database may be already in use` 则弹提示 |

- 调用链：`EasyMysqlApp.main → MysqlStoreUtil.init → JdbcManager.takeoff`

## ShellTerminalHistoryStore
- 职责：shell 终端历史实体（`ShellTerminalHistory`）的持久化存储。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | ShellTerminalHistoryStore | 单例实例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean replace(ShellTerminalHistory model)` | 保存历史 | 直接 `insert(model)`（历史记录只增不改） |
  | `protected Class<ShellTerminalHistory> modelClass()` | 模型类型 | 返回 `ShellTerminalHistory.class` |

- 调用链：`ShellTerminalHistoryStore.replace → super.insert`

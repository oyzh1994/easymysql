# terminal 包代码审查

> 包路径：`cn.oyzh.easymysql.terminal`、`cn.oyzh.easymysql.terminal.basic`
> 覆盖类数：13
> 说明：终端 UI 基于 `cn.oyzh.fx.terminal` 框架，`MysqlTerminalPane` 为唯一终端面板，其余类为按键/鼠标/帮助/历史/补全等处理器与命令处理器。

## MysqlTerminalPane
- 职责：MySQL 终端文本域（继承 `TerminalPane`），承载命令行输入输出、SQL 执行、结果集格式化与连接状态提示。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | client | MysqlClient | mysql 客户端 |
  | dbConnect | MysqlConnect | db 连接信息（domain 实体） |
  | stateChangeListener | ChangeListener\<DBConnState\> | 客户端连接状态监听器 |
  | dbName | String | 当前选中的库名称 |
  | TERMINAL_NAME | String | 终端名常量 `"mysql"` |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `Font getEditorFont()` | 获取编辑器字体 | 读取 `MysqlSettingStore.SETTING.terminalFontConfig()` 转字体 |
  | `void init(MysqlClient client, String dbName)` | 初始化 | 记录 client/dbConnect/dbName，`FXUtil.runLater` 输出欢迎语、刷新提示符，按 `isTemporary()` 走临时/常驻初始化 |
  | `void flushPrompt()` | 刷新提示符 | 拼接 `连接名@host` + 连接中/已连接/普通后缀并 `prompt(...)` |
  | `String terminalName()` | 终端名 | 返回 `TERMINAL_NAME` |
  | `boolean isTemporary()` | 是否临时连接 | client 为空或 dbConnect 无 id |
  | `boolean isConnected()/isConnecting()/isClosed()` | 连接状态 | 委托 `client.isConnected/isConnecting/isClosed` |
  | `void initByTemporary()` | 临时连接初始化 | 输出提示、`appendByPrompt`、`enableInput`、移动光标 |
  | `void initByPermanent()` | 常驻连接初始化 | 刷新提示符后启用输入 |
  | `void initStatListener()` | 注册状态监听 | 监听 `client.stateProperty`，按 CONNECTED/CLOSED/CONNECTING/FAILED 输出文案并 `enableInput` |
  | `void enableInput()` | 启用输入 | 连接中不启用；已连接或临时连接时才 `super.enableInput` |
  | `void outputPrompt()` | 输出提示符 | 非连接中时调用父类 |
  | `void fontSizeIncr()/fontSizeDecr()` | 字体缩放 | 调用父类后 `saveFontSize` |
  | `void saveFontSize()` | 保存字体大小 | 写回 `MysqlSettingStore` |
  | `void destroy()` | 销毁 | 移除状态监听并置空，调用父类 |
  | `TerminalExecuteResult eval(String input)` | 执行 SQL | 无库名返回提示；`client.executeSql(dbName, input)`；按成功/无结果/结果集拼接文本（`Query OK/OK/ERROR`）；异常存入 result |
  | `String formatResultSet(MysqlExecuteResult)` | 格式化结果集 | 计算各列宽度，输出表头、分隔线、数据行，末尾附行数与耗时 |
  | `TerminalCommandHandler findHandler(String input)` | 查找命令处理器 | `TerminalManager.findHandler`，未命中则构造匿名处理器直接 `terminal.eval(input)` |
  | `void initNode()` | 初始化节点 | 注册 key/help/mouse/history/complete 处理器 |

- 调用链：`MysqlTerminalPane.eval → MysqlClient.executeSql → MysqlQueryResults<MysqlExecuteResult>` → `formatResultSet`
- 调用链：`MysqlClient 状态变更 → stateChangeListener → flushPrompt/outputLine`

## MysqlTerminalCommandHandler
- 职责：mysql 终端命令处理器抽象基类，绑定命令类型 `C` 与终端面板 `MysqlTerminalPane`。
- 字段：无
- 方法：无（仅泛型声明，继承 `BaseTerminalCommandHandler`）

- 调用链：`MysqlShowDatabases/ShowTables/Use...TerminalCommandHandler → MysqlTerminalCommandHandler`

## MysqlTerminalCompleteHandler
- 职责：终端命令补全处理器，提供 SQL 关键字提示与补全。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | SQL_KEYWORDS | String[] | 内建 SQL 关键字表（SELECT/FROM/.../ENUM） |
  | INSTANCE | MysqlTerminalCompleteHandler | 单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlTerminalCommandHandler<TerminalCommand> newCommandHandler(String name)` | 新建命令处理器 | 返回匿名处理器，`execute` 中调用 `terminal.eval(command.getCommand())` |
  | `List<...> findCommandHandlers(MysqlTerminalPane, String line)` | 查找候选 | line 为空时全部关键字；否则先父类查找，为空时按大写前缀匹配关键字 |
  | `boolean completion(String line, MysqlTerminalPane)` | 执行补全 | 按候选 0/1/多分别 `noMatch/oneMatch/multiMatch` |

- 调用链：`MysqlTerminalPane.initNode → completeHandler(MysqlTerminalCompleteHandler.INSTANCE) → completion → terminal.eval`

## MysqlTerminalHistoryHandler
- 职责：mysql 终端历史命令处理器，负责历史命令的读写与缓存。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlTerminalHistoryHandler | 单例 |
  | cecheList | List\<ShellTerminalHistory\> | 内存缓存历史（容量 24） |
  | historyStore | ShellTerminalHistoryStore | 历史持久化存储 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void clearHistory()` | 清空历史 | 清空 store 与缓存 |
  | `List<ShellTerminalHistory> listHistory()` | 列出历史 | 缓存为空时从 `historyStore.selectList()` 载入并缓存 |
  | `void addHistory(TerminalHistory)` | 追加历史 | 构造 `ShellTerminalHistory`（记录行、时间），插入 store 并加入缓存 |

- 调用链：`MysqlTerminalPane.initNode → historyHandler → historyStore.insert/selectList`

## MysqlTerminalHelpHandler
- 职责：mysql 终端帮助处理器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlTerminalHelpHandler | 单例 |

- 方法：无（继承 `BaseTerminalHelpHandler<MysqlTerminalPane>`）

- 调用链：`MysqlTerminalPane.initNode → helpHandler`

## MysqlTerminalKeyHandler
- 职责：mysql 终端按键处理器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlTerminalKeyHandler | 单例 |

- 方法：无（实现 `TerminalKeyHandler<MysqlTerminalPane>`，逻辑在接口默认实现）

- 调用链：`MysqlTerminalPane.initNode → keyHandler`

## MysqlTerminalMouseHandler
- 职责：mysql 终端鼠标处理器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlTerminalMouseHandler | 单例 |

- 方法：无（实现 `TerminalMouseHandler<MysqlTerminalPane>`）

- 调用链：`MysqlTerminalPane.initNode → mouseHandler`

## MysqlTerminalManager
- 职责：mysql 终端命令处理器注册中心。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static void registerHandlers()` | 注册处理器 | 向 `TerminalManager` 注册标准命令（Help/Clear）与基础命令（ShowDatabases/ShowDbs/ShowTables/Use） |

- 调用链：`EasyMysqlApp.start → TerminalManager.setLoadHandler(mysql, MysqlTerminalManager::registerHandlers) → registerHandlers`

## MysqlTerminalUtil
- 职责：终端工具类（当前为空壳，预留）。
- 字段：无
- 方法：无

- 调用链：无

## MysqlShowDatabasesTerminalCommandHandler
- 职责：`show databases;` 命令处理器，列出所有数据库。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `TerminalCommand parseCommand(String line, String[] args)` | 构造命令 | 设置 args 与 command |
  | `boolean checkArgs(String[])` | 校验参数 | 非空且长度 2 |
  | `String commandName()` | 命令名 | `"show"` |
  | `String commandSubName()` | 子命令名 | `"databases;"` |
  | `TerminalExecuteResult execute(TerminalCommand, MysqlTerminalPane)` | 执行 | `terminal.getClient().databases()` 取库列表，按行尾符拼接名称返回 |

- 调用链：`终端输入 → 命令框架 → execute → MysqlClient.databases → TerminalExecuteResult`

## MysqlShowDbsTerminalCommandHandler
- 职责：`show dbs;` 命令处理器，`show databases` 的别名。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String commandSubName()` | 子命令名 | 返回 `"dbs;"`（其余复用父类） |

- 调用链：`MysqlShowDbsTerminalCommandHandler → MysqlShowDatabasesTerminalCommandHandler.execute`

## MysqlShowTablesTerminalCommandHandler
- 职责：`show tables;` 命令处理器。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `TerminalCommand parseCommand(...)` | 构造命令 | 同 ShowDatabases |
  | `boolean checkArgs(...)` | 校验参数 | 长度 2 |
  | `String commandName()` | 命令名 | `"show"` |
  | `String commandSubName()` | 子命令名 | `"tables;"` |
  | `TerminalExecuteResult execute(...)` | 执行 | 直接 `terminal.eval("SHOW TABLES;")` |

- 调用链：`execute → MysqlTerminalPane.eval("SHOW TABLES;") → MysqlClient.executeSql`

## MysqlUseTerminalCommandHandler
- 职责：`use <database>` 命令处理器，切换当前库。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `TerminalCommand parseCommand(...)` | 构造命令 | 设置 args/command |
  | `boolean checkArgs(...)` | 校验参数 | 长度 2 |
  | `String commandName()` | 命令名 | `"use"` |
  | `TerminalExecuteResult execute(...)` | 执行 | `terminal.setDbName(args[1])`，返回 `"Database changed"` |

- 调用链：`execute → MysqlTerminalPane.setDbName → 后续 eval 使用该 dbName`

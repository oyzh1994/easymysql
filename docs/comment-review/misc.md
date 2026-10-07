# misc 包代码审查

> 包路径：`cn.oyzh.easymysql`（根包）、`cn.oyzh.easymysql.exception`、`cn.oyzh.easymysql.listener`、`cn.oyzh.easymysql.sql`
> 覆盖类数：11
> 说明：合并收录任务范围内未被其它文档覆盖的小包。`cn.oyzh.easymysql.search` 下 3 个文件为整文件注释死代码（DBSearchHandler、DBSearchHistoryPopup、DBSearchParam），已跳过。

## exception 包

### DBException
- 职责：db 业务异常，继承 `RuntimeException`。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBException()` | 无参构造 | `super()` |
  | `DBException(String message)` | 信息构造 | `super(message)` |
  | `DBException(Exception ex)` | 异常构造 | `super(ex)` |

- 调用链：`DBException → RuntimeException`

### ReadonlyOperationException
- 职责：只读模式不支持操作的异常，继承 `DBException`。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ReadonlyOperationException()` | 默认构造 | 调用 `this("只读模式不支持此操作")` |
  | `ReadonlyOperationException(String msg)` | 信息构造 | `super(msg)` |

- 调用链：`ReadonlyOperationException → DBException → RuntimeException`

### DBExceptionParser
- 职责：异常信息解析器，实现 `Function<Throwable,String>`，将异常转为面向用户的中文提示。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | DBExceptionParser | 全局单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String apply(Throwable e)` | 解析异常 | 空返回 null；`DBException` 直接返回 message；`RuntimeException` 有 cause 时下钻；`SSHException` 中 `Auth fail` 返回「ssh认证失败...」否则返回其 message；非法参数/不支持操作返回 message；其余打印堆栈后返回 message |

- 调用链：`EasyMysqlApp.init → MessageBox.registerExceptionParser(DBExceptionParser.INSTANCE) → apply`

## listener 包

### DBStatusListenerManager
- 职责：数据库状态监听器管理器，按 key 注册/查找监听器并提供节点绑定。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | LISTENERS | Map\<String, DBStatusListener\> | 静态监听器注册表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static void addListener(DBStatusListener)` | 添加 | 非空时 `LISTENERS.put(key, listener)` |
  | `static void removeListener(DBStatusListener)` | 移除 | 非空时按 key 移除 |
  | `static DBStatusListener getListener(String key)` | 查找 | `LISTENERS.get(key)` |
  | `static void bindListener(Object node, DBStatusListener)` | 绑定节点 | 按 `TextInputControl`/`ComboBox`/`CheckBox`/`Property` 分别监听文本/选中项/勾选/属性变化并回调 `listener.changed(...)` |

- 调用链：`new DBStatusListener(...) → DBStatusListenerManager.addListener → 节点变化 → listener.changed`

### DBStatusListener
- 职责：抽象数据库状态监听器，实现 `ChangeListener<Object>`，构造时自动注册，废弃时自动注销。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | key | String | 监听键（UUID 或 db:table 拼接） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBStatusListener()` | 默认构造 | 生成随机 UUID 为 key 并 `addListener(this)` |
  | `DBStatusListener(String key)` | 指定 key | 使用 key 并注册 |
  | `DBStatusListener(String dbName, String tableName)` | 库+表 | 拼接 `dbName + ":" + ":" + tableName` |
  | `DBStatusListener(String dbName, String schema, String tableName)` | 库+模式+表 | 拼接 `dbName + ":" + schema + ":" + tableName` |
  | `finalize()` | 回收 | `DBStatusListenerManager.removeListener(this)` |
  | `String getKey()` | 获取 key | 返回 key |

- 调用链：`DBStatusListener 构造 → DBStatusListenerManager.addListener →（回收）finalize → removeListener`

## sql 包

### DBSqlParser
- 职责：SQL 解析器抽象基类，定义注释移除、单条/查询/全字段判定、解析与格式化等能力。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | sqlContent | String | sql 内容（protected final） |
  | dialect | DBDialect | 数据库方言（protected final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBSqlParser(String sqlContent, DBDialect dialect)` | 构造 | 保存 sqlContent/dialect |
  | `String removeComment()`（抽象） | 移除注释 | 子类实现 |
  | `boolean isSingle()`（抽象） | 是否单条 | 子类实现 |
  | `boolean isSelect()`（抽象） | 是否查询 | 子类实现 |
  | `boolean isFullColumn()`（抽象） | 是否全字段 | 子类实现 |
  | `List<String> parseSql()`（抽象） | 解析为 sql 列表 | 子类实现 |
  | `String parseSingleSql()`（抽象） | 解析单条 sql | 子类实现 |
  | `String prettySql()`（抽象） | 格式化 sql | 子类实现 |
  | `static String prettySql(String sql, DBDialect)` | 静态格式化 | `getParser(...).prettySql()` |
  | `static List<String> parseSql(String sql, DBDialect)` | 静态解析 | `getParser(...).parseSql()` |
  | `static DBSqlParser getParser(String sql, DBDialect)` | 获取解析器 | 返回 `new DruidSqlParser(sql, dialect)` |

- 调用链：`DBSqlParser.getParser → DruidSqlParser`

### DruidSqlParser
- 职责：基于 Druid 的 SQL 解析器实现。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dbType | DbType | Druid 数据库类型（MYSQL → mysql，其余 null） |
  | single | Boolean | 是否单条缓存 |
  | select | Boolean | 是否查询缓存 |
  | sqlStatements | List\<SQLStatement\> | 解析后的语句列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DruidSqlParser(String sqlContent, DBDialect dialect)` | 构造 | 按 dialect switch 得到 dbType |
  | `String removeComment()` | 移除注释 | `DBUtil.removeComment(sqlContent)` |
  | `boolean isSingle()` | 是否单条 | 有缓存取缓存，否则 `sqlStatements.size() == 1` |
  | `boolean isSelect()` | 是否查询 | 有缓存取缓存；否则用 `SchemaStatVisitor` 取 tables，判断首个 `TableStat` 是否为 Select |
  | `boolean isFullColumn()` | 是否全字段 | 用 `SchemaStatVisitor` 取 columns，存在名为 `*` 的列返回 true |
  | `List<String> parseSql()` | 解析 | 先移除注释；`SHOW VARIABLES LIKE`/`SHOW CREATE EVENT` 直接原样返回并置 single/select=true；否则 `SQLUtils.parseStatements(..., SkipComments)` 逐条 toString 并把 `\n` 替换为空格 |
  | `String parseSingleSql()` | 解析单条 | `SQLUtils.parseSingleStatement`，加入 sqlStatements，去换行返回 |
  | `String prettySql()` | 格式化 | `SQLUtils.format`，保留注释与选择列表原串 |

- 调用链：`DruidSqlParser.parseSql → DBUtil.removeComment → SQLUtils.parseStatements → SQLStatement.toString`

## 根包（cn.oyzh.easymysql）

### EasyMysqlApp
- 职责：程序主入口，继承 `FXApplication`，负责启动初始化、主页展示与系统托盘。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | PROJECT | Project | 项目信息（静态，`Project.load()`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static void main(String[] args)` | 程序入口 | 设置项目名、`MysqlStoreUtil.init()`、配置 store/cache/icon 路径，注册事件总线与同步/异步/默认事件配置，`launch(EasyMysqlApp.class, args)` |
  | `void init()` | 初始化 | 设置 FX 实例，应用区域/字体/主题/透明度，`MessageBox.registerExceptionParser(DBExceptionParser.INSTANCE)`，调用父类 |
  | `void start(Stage)` | 启动 | 设置终端加载器 `TerminalManager.setLoadHandler(mysql, MysqlTerminalManager::registerHandlers)`，`SystemUtil.gcInterval(5000)` |
  | `void showMainView()` | 显示主页面 | `StageManager.showStage(MainController.class)` |
  | `void initSystemTray()` | 初始化托盘 | 校验支持/已存在；`TrayManager.init`，添加打开主页/设置/退出菜单项，绑定鼠标左键显示主页，`TrayManager.show()` |
  | `private void showMain()` | 显示主页 | 已有 Stage 则 `toFront`，否则 `StageManager.showStage(MainController.class)` |
  | `private void showSetting()` | 显示设置 | 已有 Stage 则 `toFront`，否则 `StageManager.showStage(SettingController2.class, 主 Stage)` |

- 调用链：`EasyMysqlBootstrap.main → EasyMysqlApp.main → init → start → showMainView → StageManager.showStage(MainController)`
- 调用链：`托盘点击 → showMain/showSetting → StageManager`

### EasyMysqlBootstrap
- 职责：程序启动器，唯一的顶层 main 转发入口。
- 字段：无
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static void main(String[] args)` | 入口转发 | `EasyMysqlApp.main(args)` |

- 调用链：`EasyMysqlBootstrap.main → EasyMysqlApp.main`

### MysqlConst
- 职责：db 常量对象，集中定义存储路径与图标资源。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | STORE_PATH | String | 数据保存路径（`~/.easymysql/`） |
  | CACHE_PATH | String | 缓存保存路径（STORE_PATH 下 cache/） |
  | ICON_PATH | String | 应用 icon 地址 `/image/db_clip.png` |
  | TRAY_ICON_PATH | String | 托盘 icon 地址 `/image/db_clip.png` |

- 方法：无（纯常量类）

- 调用链：`EasyMysqlApp.main → MysqlConst.STORE_PATH/CACHE_PATH/ICON_PATH`

### MysqlStyle
- 职责：db 样式文件常量对象。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | MAIN | String | 主页样式文件路径 `/css/main.css` |

- 方法：无（纯常量类）

- 调用链：主页加载样式时引用 `MysqlStyle.MAIN`

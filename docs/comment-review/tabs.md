# tabs 包代码审查文档

> 包路径：`cn.oyzh.easymysql.tabs`
> 说明：标签页（Tab）层。`MysqlTabPane` 承载所有标签页，`MysqlTabEventListener` 订阅事件驱动标签打开/关闭/刷新；每个具体 Tab 为一个显示壳（继承 `MysqlTab`/`RichTab`），配套一个 `*Controller` 负责业务逻辑与 SQL 生成。
> 已跳过整文件被注释掉的死代码：`tabs/table/MysqlTableUpdateTab.java`、`tabs/table/MysqlTableUpdateTabController.java`。

---

## 标签页外壳

## MysqlTab

- 职责：所有业务标签页的抽象基类，统一定义资源基础路径与数据库树节点访问入口。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | BASE_PATH | static final String | 资源基础路径 `/tabs/` |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | getBasePath() | 返回基础路径 | 返回 `BASE_PATH` |
  | dbItem() | 抽象：获取数据库树节点 | 由子类实现 |

- 调用链：`MysqlTab.dbItem() → MysqlDatabaseTreeItem`

## MysqlTabPane

- 职责：标签面板容器，管理主页标签与终端标签，并订阅终端打开/关闭事件。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | listener | MysqlTabEventListener | 标签页事件监听器（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | register() | 注册事件 | `listener.register()` + `FXEventListener.super.register()` |
  | unregister() | 注销事件 | `listener.unregister()` + 父类 |
  | flushHomeTab() | 刷新主页标签 | 空则 `initHomeTab()`；多于一个则 `closeHomeTab()` |
  | getHomeTab() | 获取主页标签 | 遍历 `getTabs()` 找 `DBHomeTab` |
  | initHomeTab() | 初始化主页标签 | 无主页标签时 `addTab(new DBHomeTab())` |
  | closeHomeTab() | 关闭主页标签 | `removeTab(homeTab)` |
  | getTerminalTab(client, dbName) | 获取终端标签 | 匹配 client 与 dbName |
  | terminalOpen(DBTerminalOpenEvent) | 终端打开事件 | 复用或新建 `MysqlTerminalTab` 并 `select` |
  | terminalClose(DBTerminalCloseEvent) | 终端关闭事件 | 移除同 client 的终端标签 |

- 调用链：
  - `MysqlTabPane.terminalOpen → MysqlTerminalTab(client, dbName)`
  - `MysqlTabPane.register() → MysqlTabEventListener.register()`

## MysqlTabEventListener

- 职责：标签页事件监听器，按事件查找/创建/选中/关闭对应标签页。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | tabPane | MysqlTabPane | 所属标签面板（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlTabEventListener(MysqlTabPane) | 构造 | 保存 tabPane |
  | getTabs/addTab/select/removeTab | 标签读写 | 转发 tabPane |
  | getMysqlTabs()/getMysqlTabs(dbItem) | 收集 MysqlTab | 按实例或 dbItem 过滤 |
  | getMysqlTableRecordTab / getMysqlTableDesignTab / getViewRecordTab / getMysqlViewDesignTab / getMysqlFunctionTab / getMysqlProcedureTab / getMysqlEventTab / getMysqlQueryMainTab | 各类标签查找 | 按 dbItem+名称或 queryId 遍历匹配 |
  | onMysqlTableOpen | 表打开 | 建/取 `MysqlTableRecordTab`，`tab.init(item)` |
  | onMysqlTableRenamed | 表重命名 | `tab.flushTitle()` |
  | onMysqlTableCleared / onMysqlTableTruncated | 清空/截断 | `tab.reload()` |
  | onMysqlTableDropped | 表删除 | 关闭记录标签与设计标签 |
  | onMysqlTableFiltered | 表过滤 | `tab.setFilters` + `reload` |
  | onMysqlTableAlerted | 表变更 | `tab.flush()` + `reload` |
  | onMysqlTableDesign | 表设计 | 建/取 `MysqlTableDesignTab`，`init` 后 `select` |
  | onMysqlViewOpen / onMysqlViewFiltered / viewAlerted / onViewRenamed | 视图事件 | 记录标签打开/过滤/刷新/改标题 |
  | onMysqlViewDesign | 视图设计 | 建/取 `MysqlViewDesignTab` |
  | onMysqlQueryAdd / onMysqlQueryDeleted / onMysqlQueryOpen / onMysqlQueryRenamed | 查询事件 | 新建/删除/打开/改标题 `MysqlQueryMainTab` |
  | onMysqlDatabaseClosed / onMysqlDatabaseDropped | 库关闭/删除 | 移除该库全部标签 |
  | onMysqlFunctionDesign / onMysqlProcedureDesign / onMysqlEventDesign | 函数/过程/事件设计 | 建/取对应设计标签并选中 |
  | onConnectionClosed | 连接关闭 | 移除全部 MysqlTab |

- 调用链：
  - `MysqlTableOpenEvent → MysqlTabEventListener.onMysqlTableOpen → MysqlTableRecordTab.init`
  - `MysqlQueryOpenEvent → MysqlTabEventListener.onMysqlQueryOpen → MysqlQueryMainTab.init`
  - `ConnectionClosed → 移除全部 MysqlTab`

---

## home / terminal

## DBHomeTab

- 职责：数据库主页标签页。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | DBHomeTab() | 构造 | `super()` 后 `flush()` |
  | url() | 资源路径 | `/tabs/home/dbHomeTab.fxml` |
  | flushGraphic() | 图标 | `HomeSVGGlyph("13")` |
  | getTabTitle() | 标题 | `I18nResourceBundle.i18nString("base.title.home")` |

- 调用链：`MysqlTabPane.initHomeTab() → DBHomeTab`

## DBHomeTabController

- 职责：主页内容控制器，展示软件/环境信息并提供新增连接等入口。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | softInfo | FXLabel | 软件信息展示 |
  | jdkInfo | FXLabel | 环境信息展示 |
  | project | Project | 项目对象（`Project.load()`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | initialize | 初始化 | 设置软件版本与 JDK 信息 |
  | addConnect() | 新增连接 | `MysqlEventUtil.addConnect()` |
  | addGroup() | 添加分组 | `MysqlEventUtil.addGroup()` |
  | changelog() | 更新日志 | `MysqlEventUtil.changelog()` |

- 调用链：`DBHomeTabController.addConnect → MysqlEventUtil.addConnect → DBTreeView.addConnect`

## MysqlTerminalTab

- 职责：MySQL 终端标签页外壳。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlTerminalTab(client, dbName) | 构造 | `init(client, dbName)` |
  | controller() | 控制器 | 转 `MysqlTerminalTabController` |
  | url() | 资源路径 | `/tabs/terminal/mysqlTerminalTab.fxml` |
  | flushGraphic() | 图标 | `TerminalSVGGlyph` |
  | getTabTitle() | 标题 | 连接名或「未命名连接」 |
  | init(client, dbName) | 初始化 | client 为空时创建临时 `MysqlConnect` 与 `MysqlClient`，再 `controller().init` |
  | dbConnect()/client()/dbName() | 上下文 | 委托 controller |

- 调用链：`MysqlTabPane.terminalOpen → MysqlTerminalTab.init → MysqlTerminalTabController.init`

## MysqlTerminalTabController

- 职责：终端标签内容控制器，桥接 `MysqlTerminalPane`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | terminal | MysqlTerminalPane | 命令行文本域 |
  | dbName | String | 库名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(client, dbName) | 初始化 | `terminal.init(client, dbName)` |
  | getDbName() | 库名 | 返回 dbName |
  | getDbConnect() | 连接信息 | `terminal.getDbConnect()` |
  | client() | 客户端 | `terminal.getClient()` |
  | onTabClosed(event) | 关闭标签 | 临时终端则 `client().close()` |

- 调用链：`MysqlTerminalTabController.onTabClosed → terminal.isTemporary() → client().close()`

---

## query

## MysqlQueryMainTab

- 职责：SQL 查询主标签页外壳。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../query/mysqlQueryMainTab.fxml` |
  | flushGraphic() | 图标 | `QuerySVGGlyph("13")` |
  | flushTitle() | 标题 | 未保存时前缀 `* ` |
  | query()/queryId() | 查询对象/id | 委托 controller |
  | dbItem()/dbName()/connectName() | 上下文 | 委托 controller |
  | init(query, item) | 初始化 | `controller().init(this, query, item)` + `flush` |
  | controller() | 控制器 | 转 `MysqlQueryMainTabController` |

- 调用链：`MysqlTabEventListener.onMysqlQueryOpen → MysqlQueryMainTab.init → MysqlQueryMainTabController.init`

## MysqlQueryMainTabController

- 职责：SQL 查询主标签业务，负责 SQL 编辑、运行/解释、结果标签生成、查询保存。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | query | MysqlQuery | 查询对象 |
  | dbItem | MysqlDatabaseTreeItem | 数据库树节点 |
  | unsaved | boolean | 未保存标志位 |
  | queryArea | MysqlQueryEditor | 查询文本域 |
  | resultTabPane | FXTabPane | 结果标签面板 |
  | root | FXVBox | 根节点 |
  | infoTab | MysqlQueryInfoTab | 结果信息标签 |
  | tab | MysqlQueryMainTab | 所属标签页 |
  | splitPane | FXSplitPane | 分割面板 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(tab, query, dbItem) | 初始化 | 设置文本、方言、文本变更监听；`MysqlQueryUtil.updateIndex` |
  | initialize | 初始化 | 结果标签切换监听、`queryArea.setRunCallback(this::run)` |
  | clearTabs() | 清理结果标签 | 移除除 infoTab 外全部 |
  | pretty() | 美化 SQL | `queryArea.pretty()` |
  | run() | 运行 | 选中文本优先，`StageManager.showMask(doRun)` |
  | doRun(sql) | 执行 SQL | `dbItem.executeSql` → `initInfoTab` + 每个结果 `initSelectTab` |
  | explain() | 解释 | `dbItem.explainSql` → `initInfoTab` + `initExplainTab` |
  | initInfoTab(results) | 信息标签 | `infoTab.init(results)` |
  | initSelectTab(result, title) | 结果标签 | 建 `MysqlQuerySelectTab` 并 init |
  | initExplainTab(result, title) | 解释标签 | 建 `MysqlQueryExplainTab` 并 init |
  | save() | 保存查询 | 新则 `MysqlQueryStore.insert` + `getQueryTypeChild().addQuery`，否则 update |
  | queryKeyPressed(e) | 快捷键 | Ctrl+S 保存、Ctrl+R 运行 |
  | showNode(type) | 布局切换 | 控制 splitPane 分割位置 |
  | getQuery()/getDbItem()/isUnsaved() | 取值 | — |

- 调用链：
  - `run() → doRun(sql) → dbItem.executeSql(sql) → initSelectTab(result) → MysqlQuerySelectTab`
  - `save() → MysqlQueryStore.insert/update`

## MysqlQuerySelectTab

- 职责：查询结果集标签页外壳（不可关闭）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../query/mysqlQuerySelectTab.fxml` |
  | init(title, result, dbItem) | 初始化 | 设置标题并 `controller().init(result, dbItem)` |
  | controller() | 控制器 | 转 `MysqlQuerySelectTabController` |
  | initNode() | 节点初始化 | `setClosable(false)` |

- 调用链：`MysqlQueryMainTabController.initSelectTab → MysqlQuerySelectTab.init`

## MysqlQuerySelectTabController

- 职责：查询结果集控制器，展示数据并支持可更新结果集的增改删（按主键）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | root | FXVBox | 根节点 |
  | sql/used/count | FXText | SQL、耗时、计数展示 |
  | recordTable | MysqlRecordTableView | 数据表单 |
  | dbItem | MysqlDatabaseTreeItem | 数据库树节点 |
  | result | MysqlExecuteResult | 执行结果 |
  | add/delete/apply/discard | SVGGlyph | 操作按钮 |
  | changeListener | DBStatusListener | 记录变更监听器 |
  | columns | List&lt;MysqlColumn&gt; | 字段列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(result, dbItem) | 初始化 | 可更新时创建 changeListener 并显示按钮；`initDataList()` |
  | initDataList() | 初始化数据 | `initColumns` + `initRecords` + sql/耗时/计数 |
  | initColumns(columns) | 初始化列 | 状态列 + `MysqlRecordColumn` |
  | initRecords(records) | 填充记录 | `recordTable.setItem` |
  | addRecord() | 新增行 | 建 `MysqlRecord`（默认值填充）加入表 |
  | insertRecord(record) | 插入记录 | `client().insertRecord` + 主键回显 |
  | updateRecord(record) | 更新记录 | 有主键按主键更新，否则按全部字段更新 |
  | initPrimaryKey(record) | 构造主键 | `result.getPrimaryKey()` → `MysqlRecordPrimaryKey` |
  | apply() | 应用变更 | 遍历记录，新增则 insert，变更则 update |
  | discard() | 丢弃变更 | 移除新增行、回滚变更行 |
  | reload() | 刷新 | 确认后 `dbItem.executeSingleSql` + `initDataList` |
  | deleteRecord()/doDeleteRecord(record) | 删除记录 | 按主键或原始数据删除 |
  | onTabClosed | 关闭标签 | 移除 changeListener |
  | bindListeners() | 绑定监听 | 选中可编辑、按钮联动、Ctrl+S 应用 |

- 调用链：
  - `apply() → insertRecord/updateRecord → dbItem.client().insertRecord/updateRecord`
  - `reload() → dbItem.executeSingleSql(result.getSql())`

## MysqlQueryInfoTab

- 职责：查询结果信息标签页外壳（不可关闭）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../query/mysqlQueryInfoTab.fxml` |
  | init(results) | 初始化 | `controller().init(results)` |
  | controller() | 控制器 | 转 `MysqlQueryInfoTabController` |
  | initNode() | 节点初始化 | `setClosable(false)` |

- 调用链：`MysqlQueryMainTabController.initInfoTab → MysqlQueryInfoTab.init`

## MysqlQueryInfoTabController

- 职责：将执行结果逐条渲染为文本（SQL、影响行数/OK、错误、耗时）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | infoArea | FXTextArea | 信息展示区域 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(results) | 渲染结果 | 成功遍历 `getResults()` 输出 SQL/影响行数/耗时；失败输出 `getErrMsg()` |

- 调用链：`MysqlQueryInfoTabController.init → MysqlQueryResults.getResults`

## MysqlQueryExplainTab

- 职责：SQL 解释结果标签页外壳（不可关闭）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../query/mysqlQueryExplainTab.fxml` |
  | init(title, result) | 初始化 | 设置标题并 `controller().init(result)` |
  | controller() | 控制器 | 转 `MysqlQueryExplainTabController` |
  | initNode() | 节点初始化 | `setClosable(false)` |

- 调用链：`MysqlQueryMainTabController.initExplainTab → MysqlQueryExplainTab.init`

## MysqlQueryExplainTabController

- 职责：渲染 EXPLAIN 结果表与统计信息。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | sql/used/count | FXText | SQL、耗时、计数展示 |
  | recordTable | MysqlRecordTableView | 数据表单 |
  | result | MysqlExplainResult | 解释结果 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(result) | 初始化 | `initDataList()` |
  | initDataList() | 初始化数据 | 列、记录、sql/耗时/计数 |
  | initColumns(columns) | 初始化列 | 状态列 + `MysqlRecordColumn` |
  | initRecords(records) | 填充记录 | `recordTable.setItem` |

- 调用链：`MysqlQueryExplainTabController.init → MysqlExplainResult.columnList/getRecords`

---

## event / function / procedure（设计类标签）

## MysqlEventDesignTab

- 职责：事件设计标签页外壳。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../event/mysqlEventDesignTab.fxml` |
  | flushGraphic() | 图标 | `SVGGlyph("/font/event.svg")` |
  | flushTitle() | 标题 | `库名-事件名`，未保存加 `* ` |
  | event()/eventName() | 事件对象/名称 | 委托 controller |
  | dbItem() | 数据库树节点 | 委托 controller |
  | init(event, item) | 初始化 | `controller().init` + `flush` |
  | controller() | 控制器 | 转 `MysqlEventDesignTabController` |
  | isUnsaved() | 是否未保存 | 委托 controller |
  | onTabCloseRequest(event) | 关闭确认 | 未保存则确认后再关闭 |

- 调用链：`MysqlTabEventListener.onMysqlEventDesign → MysqlEventDesignTab.init`

## MysqlEventDesignTabController

- 职责：事件设计业务，编辑事件定义与调度计划，生成创建/修改 SQL 预览并保存。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | event | MysqlEvent | 事件对象 |
  | dbItem | MysqlDatabaseTreeItem | 数据库树节点 |
  | definition/preview | DBEditor | 定义/预览编辑器 |
  | planType | FXToggleGroup | 计划类型 |
  | onetimeType/onetime/onetimeInterval/onetimeIntervalValue/onetimeIntervalType | 控件 | 单次执行相关 |
  | loopType/loopIntervalValue/loopIntervalType/loopStart/loopStartTime/loopStartInterval/loopStartIntervalValue/loopStartIntervalType | 控件 | 周期循环相关 |
  | loopEnd/loopEndTime/loopEndInterval/loopEndIntervalValue/loopEndIntervalType | 控件 | 周期结束相关 |
  | tabPane | FXTabPane | 面板 |
  | comment/definer/status/onCompletion | 控件 | 注释/定义者/状态/完成时 |
  | listener | DBStatusListener | 数据监听器 |
  | unsaved/newData/initiating | boolean | 未保存/新数据/初始化中标志位 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(event, dbItem) | 初始化 | 建监听器、`initInfo`、`DBStatusListenerManager.bindListener` 绑定各控件 |
  | initDBListener() | 建监听器 | `new DBStatusListener(...)` 变更时 `initChangedFlag` |
  | initChangedFlag() | 变更标志 | 非初始化中则 `unsaved=true` + `flushTab()` |
  | initInfo() | 填充数据 | 依据单次/周期类型回填控件 |
  | save()/doSave() | 保存 | `tempData()`；新则 `dbItem.createEvent`，否则 `dbItem.alertEvent`，触发 `MysqlEventUtil` |
  | tempData() | 组装临时事件 | 比较差异后设置字段；按类型设置执行时间/间隔 |
  | bindListeners() | 控件联动 | 单选/复选控制控件禁用状态 |
  | initialize | 初始化 | Ctrl+S 绑定、面板切换生成预览 |
  | initPreview() | 生成预览 | `EventCreateSqlGenerator`/`EventAlertSqlGenerator.generate` |
  | isUnsaved/setUnsaved | 读写未保存 | — |

- 调用链：
  - `doSave() → dbItem.createEvent/alertEvent → MysqlEventUtil.eventAdded/eventAlerted`
  - `initPreview() → EventCreateSqlGenerator.generate(dialect, temp)`

## MysqlFunctionDesignTab

- 职责：函数设计标签页外壳。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../function/mysqlFunctionDesignTab.fxml` |
  | flushGraphic() | 图标 | `FunctionSVGGlyph("12")` |
  | flushTitle() | 标题 | `函数名@库名(连接名)`，未保存加 `* ` |
  | functionName() | 函数名 | `controller().getFunction().getName()` |
  | dbItem()/dbName()/connectName() | 上下文 | 委托 controller / dbItem |
  | init(function, item) | 初始化 | `controller().init` + `flush` |
  | controller() | 控制器 | 转 `MysqlFunctionDesignTabController` |
  | isUnsaved() | 是否未保存 | 委托 controller |
  | onTabCloseRequest(event) | 关闭确认 | 未保存则确认 |

- 调用链：`MysqlTabEventListener.onMysqlFunctionDesign → MysqlFunctionDesignTab.init`

## MysqlFunctionDesignTabController

- 职责：函数设计业务，编辑定义/参数/返回值，生成 SQL 预览并保存。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | function | MysqlFunction | 函数对象 |
  | dbItem | MysqlDatabaseTreeItem | 数据库树节点 |
  | definition/preview | DBEditor | 定义/预览编辑器 |
  | tabPane | FXTabPane | 面板 |
  | comment/definer/securityType/characteristic | 控件 | 注释/定义者/安全性/特征 |
  | paramTable | DBStatusTableView&lt;MysqlRoutineParam&gt; | 参数表 |
  | returnType/returnValues/returnDigits/returnSize/returnCharset | 控件 | 返回值相关 |
  | listener | DBStatusListener | 数据监听器 |
  | unsaved/newData/initiating | boolean | 标志位 |
  | listChangeListener | ListChangeListener | 数据列表监听器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(function, dbItem) | 初始化 | `StageManager.showMask(doInit)` |
  | doInit() | 执行初始化 | 非新数据重新查询；初始化字符集、监听器、绑定控件 |
  | initDBListener/initChangedFlag | 监听器/变更标志 | 变更触发 `flushTab` |
  | initInfo() | 填充数据 | 回填定义/参数/返回值；新数据给默认定义 |
  | save()/doSave() | 保存 | `tempData()`；新则 `dbItem.createFunction`，否则 `alertFunction` |
  | tempData() | 组装临时函数 | 设置参数、返回值参数 `MysqlRoutineParam` |
  | initialize | 初始化 | Ctrl+S、返回值类型联动、面板切换预览 |
  | initPreview() | 生成预览 | `MysqlFunctionSqlGenerator.INSTANCE.generate(temp)` |
  | addParam/deleteParam/moveParamUp/moveParamDown | 参数维护 | 增删/移动 `paramTable` 项 |
  | bindListeners() | 绑定 | 参数列表变化初始化参数表 |
  | initParamTable() | 初始化参数 | 为参数设置 dbClient |
  | getFunction/getDbItem/setDbItem/isUnsaved/setUnsaved | 存取 | — |

- 调用链：
  - `doSave() → dbItem.createFunction/alertFunction → MysqlEventUtil.functionAdded/funtionAlerted`
  - `initPreview() → MysqlFunctionSqlGenerator.INSTANCE.generate(temp)`

## MysqlProcedureDesignTab

- 职责：存储过程设计标签页外壳。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../procedure/mysqlProcedureDesignTab.fxml` |
  | flushGraphic() | 图标 | `ProcedureSVGGlyph("12")` |
  | flushTitle() | 标题 | `过程名@库名(连接名)`，未保存加 `* ` |
  | procedure()/procedureName() | 过程对象/名称 | 委托 controller |
  | dbItem()/dbName()/connectName() | 上下文 | 委托 controller / dbItem |
  | init(procedure, item) | 初始化 | `controller().init` + `flush` |
  | controller() | 控制器 | 转 `MysqlProcedureDesignTabController` |
  | isUnsaved() | 是否未保存 | 委托 controller |
  | onTabCloseRequest(event) | 关闭确认 | 未保存则确认 |

- 调用链：`MysqlTabEventListener.onMysqlProcedureDesign → MysqlProcedureDesignTab.init`

## MysqlProcedureDesignTabController

- 职责：存储过程设计业务，编辑定义/参数，生成 SQL 预览并保存。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | procedure | MysqlProcedure | 过程对象 |
  | dbItem | MysqlDatabaseTreeItem | 数据库树节点 |
  | definition/preview | DBEditor | 定义/预览编辑器 |
  | tabPane | FXTabPane | 面板 |
  | comment/definer/securityType/characteristic | 控件 | 注释/定义者/安全性/特征 |
  | paramTable | DBStatusTableView&lt;MysqlRoutineParam&gt; | 参数表 |
  | listener | DBStatusListener | 数据监听器 |
  | unsaved/newData/initiating | boolean | 标志位 |
  | listChangeListener | ListChangeListener | 数据列表监听器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(procedure, dbItem) | 初始化 | `StageManager.showMask(doInit)` |
  | doInit() | 执行初始化 | 非新数据重新查询；建监听器、绑定控件 |
  | initDBListener/initChangedFlag | 监听器/变更标志 | 变更触发 `flushTab` |
  | initInfo() | 填充数据 | 回填定义/参数；新数据给默认定义 |
  | save()/doSave() | 保存 | 新则 `dbItem.createProcedure`，否则 `alertProcedure` |
  | tempData() | 组装临时过程 | 设置参数等 |
  | initialize | 初始化 | Ctrl+S、面板切换预览 |
  | initPreview() | 生成预览 | `MysqlProcedureSqlGenerator.INSTANCE.generate(temp)` |
  | addParam/deleteParam/moveParamUp/moveParamDown | 参数维护 | 增删/移动参数项 |
  | bindListeners()/initParamTable() | 绑定/初始化参数 | 为参数设置 dbClient |
  | getDbItem/setDbItem/isUnsaved/setUnsaved | 存取 | — |

- 调用链：
  - `doSave() → dbItem.createProcedure/alertProcedure → MysqlEventUtil.procedureAdded/procedureAlerted`
  - `initPreview() → MysqlProcedureSqlGenerator.INSTANCE.generate(temp)`

---

## table

## MysqlTableColumnExtraController

- 职责：表字段扩展配置子控制器，按字段类型控制默认值/值/字符集/自增等控件显示与回写。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | defaultValueBox/defaultValue | FXHBox/MysqlDefaultValueTextFiled | 默认值 |
  | valueBox/value | FXHBox/DBEnumTextFiled | 字段值 |
  | primaryKeySizeBox/primaryKeySize | FXHBox/NumberTextField | 主键长度 |
  | zeroFillBox/zeroFill | FXHBox/FXCheckBox | 填充零 |
  | autoIncrementBox/autoIncrement | FXHBox/FXCheckBox | 自动递增 |
  | unsignedBox/unsigned | FXHBox/FXCheckBox | 无符号 |
  | currentTimestampBox/currentTimestamp | FXHBox/FXCheckBox | 按时间戳更新 |
  | charsetBox/charset | FXHBox/DBCharsetComboBox | 字符集 |
  | collationBox/collation | FXHBox/DBCollationComboBox | 排序方式 |
  | column | MysqlColumn | 当前字段 |
  | dbClient | MysqlClient | 客户端 |
  | ignoreChanged | boolean | 是否忽略变更 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | apply() | 回写字段 | 依据各控件可见性将值写回 `column` |
  | bindListeners() | 绑定监听 | 各控件变化调用 `apply`；字符集变化初始化排序 |
  | init(column, dbClient) | 初始化 | 移除旧监听、`doInit`、监听类型变化 |
  | listenColumnTypeChanged(...) | 类型变化 | 重新 `doInit` |
  | doInit() | 初始化显示与值 | 按 `column.support*` 决定控件显示/隐藏与取值 |
  | initialize | 初始化 | `managedBindVisible` 绑定各 box |

- 调用链：`MysqlTableDesignTabController.columnTable 选中 → tableColumnExtraController.init → apply`

## MysqlTableDesignTab

- 职责：表设计标签页外壳（不可关闭）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../table/mysqlTableDesignTab.fxml` |
  | flushGraphic() | 图标 | `SVGGlyph("/font/table.svg")` |
  | flushTitle() | 标题 | `库名-表名`，未保存加 `* ` |
  | tableName()/dbName() | 表名/库名 | 委托 controller |
  | init(table, dbItem) | 初始化 | `StageManager.showMask` 内 `controller().init` + `flush` |
  | controller() | 控制器 | 转 `MysqlTableDesignTabController` |
  | isUnsaved() | 是否未保存 | 委托 controller |
  | onTabCloseRequest(event) | 关闭确认 | 未保存则确认 |
  | dbItem() | 数据库树节点 | 委托 controller |
  | initNode() | 节点初始化 | `setClosable(false)` |

- 调用链：`MysqlTabEventListener.onMysqlTableDesign → MysqlTableDesignTab.init`

## MysqlTableDesignTabController

- 职责：表设计业务（`ParentTabController`），维护字段/索引/外键/触发器/检查五类表单，生成建表或改表 SQL 并保存。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | tabPane | FXTabPane | 面板 |
  | tableEngine/tableCharset/tableCollation | 控件 | 引擎/字符集/排序 |
  | tableRowFormatBox/tableRowFormat | FXHBox/MysqlRowFormatComboBox | 行格式 |
  | tableAutoIncrementBox/tableAutoIncrement | FXHBox/NumberTextField | 自动递增值 |
  | tableComment | FXTextArea | 表注释 |
  | sqlPreview | DBEditor | SQL 预览 |
  | columnTable | DBStatusTableView&lt;MysqlColumnControl&gt; | 字段表 |
  | indexTable | DBStatusTableView&lt;MysqlIndexControl&gt; | 索引表 |
  | foreignKeyTable | DBStatusTableView&lt;MysqlForeignKeyControl&gt; | 外键表 |
  | triggerTable | DBStatusTableView&lt;MysqlTriggerControl&gt; | 触发器表 |
  | checkTable | DBStatusTableView&lt;MysqlCheckControl&gt; | 检查表 |
  | mysqlTable | MysqlTable | 表对象 |
  | dbItem | MysqlDatabaseTreeItem | 数据库树节点 |
  | listener | DBStatusListener | 数据监听器 |
  | unsaved/newData/initiating | boolean | 标志位 |
  | tableColumnExtraController | MysqlTableColumnExtraController | 字段扩展子控制器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | initCreateParam()/initAlertParam() | 构造参数 | 委托 `initParam(true/false)` |
  | initParam(isCreate) | 组装表参数 | 比较差异设置表属性；收集字段/索引/外键/触发器/检查（含删除项）；返回 create/alert 参数 |
  | save()/doSave() | 保存 | 逐表校验 invalid；新则 `dbItem.createTable`，否则 `alterTable`，触发 `MysqlEventUtil` |
  | initChangedFlag() | 变更标志 | 非初始化中则 `unsaved=true` + `flushTab()` |
  | resetTable() | 重置表单 | 各表 `reset()` |
  | initInfo(table) | 初始化信息 | 新数据走 `initNew`，否则 `selectFullTable` + `initNormal` |
  | initNew() | 新表初始化 | 显示新增操作组、默认引擎 innoDB |
  | initNormal() | 已有表初始化 | 回填引擎/字符集/排序、字段/索引/外键/触发器/检查 |
  | addColumn/deleteColumn、addIndex/deleteIndex、addForeignKey/deleteForeignKey、addTrigger/deleteTrigger、addCheck/deleteCheck | 各类增删 | 增：建控件 `setCreated(true)`；删：确认后 `removeItem` + `setDeleted(true)` |
  | initTable() | 初始化列表控件 | 绑定各表 Ctrl+S、字段列表变化刷新索引/外键 |
  | bindListeners() | 绑定监听 | 字符集/引擎联动、tab 下标切换操作组、监听器绑定各表、列选中初始化扩展控制器 |
  | initIndexTable()/initForeignKeyTable() | 初始化索引/外键 | 设置列列表、库名、客户端 |
  | initPreview() | 生成预览 | `MysqlTableCreateSqlGenerator` / `MysqlTableAlertSqlGenerator` |
  | init(table, dbItem) | 初始化 | 引擎初始化、`initInfo`、绑定监听、按支持性移除检查 tab |
  | doAdd()/doDelete()/doMoveUp()/doMoveDown() | 统一操作 | 按当前选中 tab 分发到对应增删移 |
  | tableName()/dbName() | 表名/库名 | 来自 `mysqlTable` |
  | getSubControllers() | 子控制器 | 返回 `tableColumnExtraController` |
  | getDbItem()/isUnsaved() | 取值 | — |

- 调用链：
  - `doSave() → initCreateParam()/initAlertParam() → dbItem.createTable/alterTable`
  - `initPreview() → MysqlTableCreateSqlGenerator.generateSql(param)`
  - `columnTable 选中 → tableColumnExtraController.init`

## MysqlTableRecordTab

- 职责：表记录标签页外壳。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../table/mysqlTableRecordTab.fxml` |
  | flushGraphic() | 图标 | `TableSVGGlyph("13")` |
  | flushTitle() | 标题 | `表名@库名(连接名)` |
  | init(item) | 初始化 | `controller().init(item)` + `flush` |
  | controller() | 控制器 | 转 `MysqlTableRecordTabController` |
  | reload() | 刷新 | `controller().reload()` |
  | client()/item()/tableName()/dbItem()/dbName() | 上下文 | 委托 controller/item |
  | setFilters(filters) | 设置过滤 | `controller().setFilters(filters)` |

- 调用链：`MysqlTabEventListener.onMysqlTableOpen → MysqlTableRecordTab.init`

## MysqlTableRecordTabController

- 职责：表记录控制器，分页展示记录、过滤、按主键/全字段增改删并处理主键回显。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | root | FXVBox | 根节点 |
  | itemProperty | ObjectProperty&lt;MysqlTableTreeItem&gt; | 表树节点属性 |
  | pageData | Paging&lt;MysqlRecord&gt; | 分页数据 |
  | filter/missPrimaryKey/apply/discard | SVGGlyph | 过滤/缺主键警告/应用/抛弃按钮 |
  | pageBox | PageBox&lt;MysqlRecord&gt; | 分页组件 |
  | recordTable | MysqlRecordTableView | 数据表单 |
  | filters | List&lt;MysqlRecordFilter&gt; | 过滤列表 |
  | changeListener | DBStatusListener | 记录变更监听器 |
  | columns | MysqlColumns | 字段列表 |
  | setting | MysqlSetting | 设置常量 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(item) | 初始化 | 建立节点属性与关闭监听；`reload()`；建 changeListener |
  | getItem() | 表树节点 | 返回 itemProperty 值 |
  | initDataList(pageNo) | 初始化数据 | `item.recordPage(...)` → `pageBox.setPaging` + `initRecords` |
  | enabledFilters() | 启用过滤 | 过滤 `isEnabled` |
  | initColumns(columns) | 初始化列 | 状态列 + `MysqlRecordColumn` |
  | initRecords(records) | 填充记录 | `recordTable.setItem` |
  | addRecord() | 新增行 | 建记录（默认值填充）加入表 |
  | insertRecord(record) | 插入 | 有主键按主键插入并回显，否则直接插入 |
  | updateRecord(record) | 更新 | 有主键按主键，否则按全字段更新 |
  | initPrimaryKey(record) | 构造主键 | `item.getPrimaryKey()` |
  | apply()/discard() | 应用/丢弃变更 | 遍历记录 insert/update 或回滚 |
  | reload()/doReload() | 刷新 | 确认后重载列与数据、显示缺主键警告 |
  | filter() | 过滤弹窗 | `MysqlTableRecordFilterPopupController`，提交后过滤重载 |
  | nextPage/prevPage/lastPage/firstPage/pageJump/pageSetting | 分页操作 | 操作 `pageData` / `PageBox` |
  | deleteRecord()/doDeleteRecord(record) | 删除记录 | 按主键或原始数据删除 |
  | onTabClosed | 关闭 | 移除 changeListener |
  | bindListeners() | 绑定 | 按钮联动、选中可编辑、Ctrl+S 应用 |
  | getFilters()/setFilters() | 过滤列表 | — |

- 调用链：
  - `reload() → doReload() → getItem().recordPage(...) → MysqlTableTreeItem.recordPage`
  - `apply() → insertRecord/updateRecord → MysqlTableTreeItem.insertRecord/updateRecord`

---

## view

## MysqlViewDesignTab

- 职责：视图设计标签页外壳。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../view/mysqlViewDesignTab.fxml` |
  | flushGraphic() | 图标 | `ViewSVGGlyph("13")` |
  | flushTitle() | 标题 | `库名-视图名`，未保存加 `* ` |
  | dbName()/viewName() | 库名/视图名 | 委托 controller |
  | dbItem() | 数据库树节点 | 委托 controller |
  | init(view, item) | 初始化 | `controller().init` + `flush` |
  | controller() | 控制器 | 转 `MysqlViewDesignTabController` |
  | isUnsaved() | 是否未保存 | 委托 controller |
  | onTabCloseRequest(event) | 关闭确认 | 未保存则确认 |

- 调用链：`MysqlTabEventListener.onMysqlViewDesign → MysqlViewDesignTab.init`

## MysqlViewDesignTabController

- 职责：视图设计业务，编辑定义者/算法/安全性/检查选项/定义，保存建改视图。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dbView | MysqlView | 视图对象 |
  | dbItem | MysqlDatabaseTreeItem | 数据库树节点 |
  | definer | FXTextField | 定义者 |
  | algorithm | MysqlViewAlgorithmComboBox | 算法 |
  | securityType | DBSecurityTypeComboBox | 安全性 |
  | checkOption | MysqlViewCheckOptionComboBox | 检查选项 |
  | definition | DBEditor | 定义 |
  | listener | DBStatusListener | 数据监听器 |
  | unsaved/newData/initiating | boolean | 标志位 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | initInfo() | 填充数据 | 回填定义者/算法/安全性/检查选项/定义；新数据给默认值 |
  | init(view, dbItem) | 初始化 | 建监听器、`initInfo`、绑定控件 |
  | initDBListener/initChangedFlag | 监听器/变更标志 | 变更触发 `flushTab` |
  | save()/doSave() | 保存 | 组装临时视图；新则 `dbItem.createView`，否则 `alertView`，触发 `MysqlEventUtil` |
  | initialize | 初始化 | Ctrl+S 绑定 |
  | dbName()/viewName()/getDbItem()/isUnsaved() | 取值 | — |

- 调用链：`doSave() → dbItem.createView/alertView → MysqlEventUtil.viewAdded/viewAlerted`

## MysqlViewRecordTab

- 职责：视图记录标签页外壳。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | openedTime | long | 标签打开时间（`System.currentTimeMillis()`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | url() | 资源路径 | `.../view/mysqlViewRecordTab.fxml` |
  | flushGraphic() | 图标 | `ViewSVGGlyph("13")` |
  | flushTitle() | 标题 | `视图名@库名(连接名)` |
  | init(item) | 初始化 | `controller().init(item)` + `flush` |
  | controller() | 控制器 | 转 `MysqlViewRecordTabController` |
  | reload() | 刷新 | `controller().reload()` |
  | item()/client()/viewName()/dbItem() | 上下文 | 委托 controller/item |
  | setFilters(filters) | 设置过滤 | `controller().setFilters(filters)` |

- 调用链：`MysqlTabEventListener.onMysqlViewOpen → MysqlViewRecordTab.init`

## MysqlViewRecordTabController

- 职责：视图记录控制器，分页展示记录、过滤与增改删（仅可更新视图）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | root | FXVBox | 根节点 |
  | itemProperty | ObjectProperty&lt;MysqlViewTreeItem&gt; | 视图树节点属性 |
  | pageData | Paging&lt;MysqlRecord&gt; | 分页数据 |
  | filter/missPrimaryKey/apply/discard | SVGGlyph | 过滤/缺主键警告/应用/抛弃按钮 |
  | pageBox | PageBox&lt;MysqlRecord&gt; | 分页组件 |
  | recordTable | MysqlRecordTableView | 数据表单 |
  | filters | List&lt;MysqlRecordFilter&gt; | 过滤列表 |
  | changeListener | DBStatusListener | 记录变更监听器 |
  | columns | MysqlColumns | 字段列表 |
  | setting | MysqlSetting | 设置常量 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | init(item) | 初始化 | 建节点属性与关闭监听；`reload()`；`isUpdatable()` 时建 changeListener 并显示操作组 |
  | getItem() | 视图树节点 | 返回 itemProperty 值 |
  | initDataList(pageNo) | 初始化数据 | `item.recordPage(...)` |
  | enabledFilters() | 启用过滤 | 过滤 `isEnabled` |
  | initColumns/initRecords | 列/记录 | 同上表记录逻辑 |
  | addRecord/insertRecord/updateRecord/initPrimaryKey | 增改与主键 | 与表记录逻辑一致，走 `MysqlViewTreeItem` |
  | apply()/discard() | 应用/丢弃 | 遍历记录 insert/update 或回滚 |
  | reload()/doReload() | 刷新 | 确认后重载列与数据 |
  | filter() | 过滤弹窗 | `MysqlViewRecordFilterPopupController` |
  | nextPage/prevPage/lastPage/firstPage/pageJump/pageSetting | 分页操作 | 操作 `pageData` / `PageBox` |
  | deleteRecord()/doDeleteRecord(record) | 删除记录 | 按主键或原始数据删除 |
  | onTabClosed | 关闭 | 移除 changeListener |
  | bindListeners() | 绑定 | 按钮联动、选中可编辑、Ctrl+S 应用 |
  | getFilters()/setFilters() | 过滤列表 | — |

- 调用链：
  - `reload() → doReload() → getItem().recordPage(...) → MysqlViewTreeItem.recordPage`
  - `apply() → MysqlViewTreeItem.insertRecord/updateRecord`

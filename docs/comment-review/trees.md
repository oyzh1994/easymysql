# trees 包代码审查文档

> 包路径：`cn.oyzh.easymysql.trees`
> 说明：数据库连接树的模型层。按「根节点 → 分组/连接 → 数据库 → 表/视图/函数/过程/事件/查询/终端 类型节点 → 具体对象节点」组织，节点负责菜单、加载子节点与转发 MysqlClient 调用。
> 已跳过整文件被注释掉的死代码：`DBTreeCell.java`、`DBTreeItemValue.java`、`MysqlTreeEventListener.java`。

---

## 基础与容器

## DBTreeItem

- 职责：所有 db 树节点的抽象基类，统一树视图类型。
- 字段：无（继承 `RichTreeItem<V>`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | DBTreeItem(RichTreeView treeView) | 构造 | 调用 `super(treeView)` |
  | getTreeView() | 返回强类型的 `DBTreeView` | 覆盖父类，向下转型 `super.getTreeView()` |

- 调用链：`DBTreeView.setRoot(DBRootTreeItem) → DBTreeItem.getTreeView() → DBTreeView`

## DBTreeItemFilter

- 职责：树节点的过滤谓词（目前实现为放行全部）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | onlyCollect | boolean | 是否仅看收藏 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | test(RichTreeItem&lt;?&gt; item) | 过滤判定 | 不可过滤节点（`!item.isFilterable()`）直接放行；其余恒返回 true（原搜索逻辑已注释） |
  | isOnlyCollect/setOnlyCollect | 读写 onlyCollect | — |

- 调用链：`DBTreeView.getItemFilter() → DBTreeItemFilter.test(item)`

## DBTreeView

- 职责：数据库树视图，持有根节点 `DBRootTreeItem`，并订阅连接/分组相关事件。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | searching | volatile boolean | 搜索中标志位 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | getItemFilter() | 惰性创建并返回 `DBTreeItemFilter` | 首次访问创建过滤器 |
  | DBTreeView() | 构造 | 设置拖拽标识 `db_tree_drag`、单选模式、cellFactory 为 `RichTreeCell`，`setRoot(new DBRootTreeItem(this))` 并展开 |
  | root() | 返回根节点 | 向下转型 `DBRootTreeItem` |
  | closeConnects() | 关闭全部已连接节点 | 遍历 `root().getConnectedItems()`，`ThreadUtil.start(item::closeConnect)` |
  | onInfoUpdate(MysqlConnectUpdatedEvent) | 连接修改事件 | 在根节点直接子项与分组内查找同一 `MysqlConnect` 并刷新 value |
  | addConnect(DBAddConnectEvent) | 添加连接事件 | `StageManager.showStage(MysqlConnectAddController)` |
  | addGroup(DBAddGroupEvent) | 添加分组事件 | `root().addGroup()` |
  | infoAdded(MysqlConnectAddedEvent) | 连接新增事件 | `root().addConnect(event.data())` |
  | infoUpdated(MysqlConnectUpdatedEvent) | 连接变更事件 | `root().infoUpdate(event.data())` |

- 调用链：
  - `DBTreeView() → DBRootTreeItem.initChildes() → MysqlConnectStore/MysqlGroupStore.load()`
  - `MysqlConnectAddedEvent → DBTreeView.infoAdded → DBRootTreeItem.addConnect`

---

## root / group / connect

## DBRootTreeItem

- 职责：树根节点，加载分组与连接、维护连接集合，并实现拖拽与导入导出。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | connectStore | MysqlConnectStore | 连接信息持久化（常量 INSTANCE） |
  | groupStore | MysqlGroupStore | 分组持久化（常量 INSTANCE） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | DBRootTreeItem(DBTreeView) | 构造 | 设置 `DBRootTreeItemValue`，`initChildes()` |
  | initChildes() | 加载分组与连接子节点 | `groupStore.load()` 建 `DBGroupTreeItem`；`connectStore.load()` → `addConnects` |
  | getMenuItems() | 根菜单 | 添加连接/导出连接/导入连接/添加分组；无子节点时禁用导出 |
  | exportConnect() | 导出连接 | `MysqlInfoExport.fromConnects` → `FileUtil.writeUtf8String(export.toJSONString(), file)` |
  | dragFile(List&lt;File&gt;) / importConnect() | 拖入/选择文件 | 校验后交 `parseConnect` |
  | parseConnect(File) | 解析连接 json | 校验存在/非目录/json/非空 → `MysqlInfoExport.fromJSON` → 逐条 `connectStore.insert` + `addConnect` |
  | addConnect() | 打开新增连接窗口 | `StageManager.showStage(MysqlConnectAddController)` |
  | addGroup() | 新增分组 | 校验名称非空且不重复后 `groupStore.insert` 并 `addChild(new DBGroupTreeItem)` |
  | getGroupItem(String groupId) | 按分组 id 查节点 | 遍历分组流匹配 `getGid` |
  | getGroupItems() | 收集分组子节点 | 遍历 `richChildren()` 筛 `DBGroupTreeItem` |
  | infoAdd/infoUpdate(MysqlConnect) | 连接新增/变更 | 直接子项或分组内查同一对象并替换 value |
  | addConnect(MysqlConnect) | 添加连接 | 有分组则加入分组，否则 `super.addChild(new DBConnectTreeItem)` |
  | addConnectItem(DBConnectTreeItem) | 加入连接节点 | 去除 groupId、`connectStore.update`、addChild、扩展 |
  | addConnectItems(List) | 批量加入 | `addChild` + 扩展 |
  | delConnectItem(DBConnectTreeItem) | 删除连接 | `connectStore.delete` 成功后 `removeChild` |
  | getConnectItems() | 收集全部连接节点 | 递归分组收集 |
  | getConnectedItems() | 收集已连接节点 | 仅 `isConnected()` 的连接节点 |
  | allowDrop()/allowDropNode/onDropNode | 拖拽支持 | 仅接受 `DBConnectTreeItem`，落点移除并重新加入 |

- 调用链：
  - `DBRootTreeItem.initChildes() → MysqlGroupStore.load() → DBGroupTreeItem`
  - `DBConnectTreeItem.repeatConnect() → connectManager().addConnect(dbInfo) → DBRootTreeItem.addConnect`
  - `DBGroupTreeItem.delete() → parent().addConnectItems(childes) → DBRootTreeItem.addConnectItems`

## DBRootTreeItemValue

- 职责：根节点显示值（图标与名称）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | name() | 名称 | `I18nHelper.database()` |
  | graphic() | 图标 | 惰性创建 `SVGGlyph("/font/database.svg")` |

- 调用链：`DBRootTreeItem.setValue(DBRootTreeItemValue) → name()/graphic()`

## DBGroupTreeItem

- 职责：连接分组树节点，管理组内连接的新增、拖拽转移与删除，实现 `DBConnectManager`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlGroup | 分组对象（final） |
  | connectStore | MysqlConnectStore | 连接持久化 |
  | groupStore | MysqlGroupStore | 分组持久化 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | DBGroupTreeItem(MysqlGroup, DBTreeView) | 构造 | 设置展开态，监听展开/收缩事件并 `groupStore.update` |
  | getMenuItems() | 分组菜单 | 添加连接/重命名/删除分组 |
  | rename() | 重命名分组 | 校验非空、`groupStore.exist` 去重后 `update` + `refresh` |
  | delete() | 删除分组 | 确认后 `groupStore.delete`；子连接清除 groupId 并转移到父节点，最后 `remove()` |
  | addConnect() | 打开新增连接窗口 | `MysqlConnectAddController`，传 `group` 属性 |
  | parent() | 父节点 | 转型为 `DBRootTreeItem` |
  | addConnect(MysqlConnect) | 新增连接节点 | `new DBConnectTreeItem` → `addConnectItem` |
  | addConnectItem(DBConnectTreeItem) | 加入并同步 groupId | 非包含时设置 groupId 并 `connectStore.update` |
  | addConnectItems(List) | 批量加入 | `addChild` |
  | delConnectItem(DBConnectTreeItem) | 删除连接 | `connectStore.delete` 后 `removeChild` |
  | getConnectItems() | 收集连接节点 | 遍历 `richChildren()` |
  | allowDrop()/allowDropNode/onDropNode | 拖拽 | 拒绝对同组连接落点；落点移除并 `addConnectItem` |
  | value() | 分组对象 | 返回 value |

- 调用链：
  - `DBGroupTreeItem.delete() → parent().addConnectItems(childes)`
  - `DBRootTreeItem.getConnectItems() → DBGroupTreeItem.getConnectItems()`

## DBGroupTreeItemValue

- 职责：分组节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item() | 强类型节点 | 转 `DBGroupTreeItem` |
  | name() | 名称 | `item().value().getName()` |
  | graphic() | 图标 | `GroupSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 图标颜色 | 有子节点返回 `Color.DEEPSKYBLUE` |

- 调用链：`DBGroupTreeItem.setValue(DBGroupTreeItemValue)`

## DBConnectTreeItem

- 职责：连接树节点，负责连接的建立/关闭/编辑/复制/删除/重命名，并加载数据库子节点。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlConnect | 连接信息 |
  | client | MysqlClient | 数据库客户端 |
  | canceled | boolean | 已取消连接标志位 |
  | connectStore | MysqlConnectStore | 连接持久化（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | DBConnectTreeItem(MysqlConnect, DBTreeView) | 构造 | `this.value(value)` |
  | reloadChild() | 重建子节点 | `clearChild()` + `loadChild()` |
  | getMenuItems() | 连接菜单 | 依据连接中/已连接/未连接三种状态组装菜单项 |
  | addDatabase() | 新增数据库窗口 | `MysqlDatabaseAddController`，取 `databaseName` 后 `addDatabase(name)` |
  | addDatabase(String) | 添加数据库节点 | `client.database(name)` → `addChild(new MysqlDatabaseTreeItem)` |
  | cancelConnect() | 取消连接 | `canceled=true`，异步 `client.close()` + `stopWaiting` |
  | connect() | 建立连接 | `TaskBuilder`：`client.start()`，失败告警 `closeConnect(false)`，成功 `loadChild()`，`onFinish refresh`、`onSuccess expend` |
  | closeConnect()/closeConnect(boolean waiting) | 关闭连接 | `client.close()` + `clearChild()`；等待模式用 Task 包裹 |
  | editConnect() | 编辑连接 | 已连接先确认关闭，`MysqlConnectUpdateController` 传 `info` 并 display |
  | repeatConnect() | 复制连接 | `MysqlConnect.copy`，清空收藏，`connectStore.insert` 后 `connectManager().addConnect` |
  | delete() | 删除连接 | 确认后关闭，`connectManager().delConnectItem` 成功则 `MysqlEventUtil.connectDeleted` |
  | rename() | 重命名 | 校验后 `connectStore.update` + `setValue(new DBConnectTreeItemValue(this))` |
  | value(MysqlConnect)/value() | 设置/获取连接 | setter 中 `DBClientUtil.newClient(value)` 并刷新显示值 |
  | isConnected()/isConnecting() | 状态判定 | 委托 client |
  | connectManager() | 父管理节点 | 父节点转型 `DBConnectManager` |
  | loadChild() | 加载数据库列表 | `client.databases()` → `MysqlDatabaseTreeItem` 列表 → `setChild` + `expend` |
  | onPrimaryDoubleClick() | 双击 | 未连接则 `connect()` |
  | existDatabase/createDatabase/alterDatabase/dropDatabase/databaseCollation | 数据库操作 | 均委托 `client` |
  | type()/getClient() | 类型/客户端 | 委托 value/client |

- 调用链：
  - `DBConnectTreeItem.onPrimaryDoubleClick() → connect() → client.start() → loadChild()`
  - `DBConnectTreeItem.delete() → connectManager().delConnectItem(this) → MysqlEventUtil.connectDeleted`
  - `DBConnectTreeItem.addDatabase(name) → client.database(name) → MysqlDatabaseTreeItem`

## DBConnectTreeItemValue

- 职责：连接节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item() | 强类型节点 | 转 `DBConnectTreeItem` |
  | name() | 名称 | `item().value().getName()` |
  | graphic() | 图标 | `MysqlSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |

- 调用链：`DBConnectTreeItem.value(...) → setValue(DBConnectTreeItemValue)`

---

## database / table / view

## MysqlDatabaseTreeItem

- 职责：数据库树节点，是所有库内对象（表/视图/函数/过程/事件/查询/终端）的父节点，并集中转发 MysqlClient 的库级操作。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | DBDatabase | 当前数据库对象（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlDatabaseTreeItem(DBDatabase, RichTreeView) | 构造 | 不可排序、可过滤，设置 `MysqlDatabaseTreeItemValue` |
  | value()/dbName()/userName() | 基本信息 | value / `value.getName()` / `info().getUser()` |
  | parent() | 父节点 | 转 `DBConnectTreeItem` |
  | getMenuItems() | 菜单 | 关闭库（有子节点时）、编辑库、删除库、转储数据、运行 sql 文件 |
  | runSqlFile() | 运行 sql 文件 | `MysqlRunSqlFileController` 传 dbInfo/dbName/dbClient |
  | dump() | 转储 | `MysqlDataDumpController`，dumpType=1 |
  | delete() | 删除数据库 | 确认后 `parent().dropDatabase` → `MysqlEventUtil.databaseDropped` → `remove()` |
  | editDB() | 编辑数据库 | `MysqlDatabaseUpdateController` 传 database/connectItem |
  | closeDB() | 关闭数据库 | 清子节点、折叠、`setLoaded(false)`、`MysqlEventUtil.databaseClosed` |
  | loadChild() | 加载类型子节点 | 依次添加 Tables/Views/Functions/Procedures/Events/Queries/Terminal 七类节点 |
  | getTableTypeChild/getTableChild | 表类型/表节点 | 遍历 `richChildren()` 定位 |
  | getQueryTypeChild/getFunctionTypeChild/getFunctionChild | 查询/函数节点 | 同上 |
  | getProcedureTypeChild/getProcedureChild | 过程节点 | 同上 |
  | getEventTypeChild/getEventChild | 事件节点 | 同上 |
  | getViewTypeChild/getViewChild | 视图节点 | 同上 |
  | client()/info() | 客户端/连接信息 | `parent().getClient()` / `parent().value()` |
  | tableSize()/viewSize() | 表/视图数量 | 委托 client，异常时告警 |
  | infoName()/connectName() | 连接名 | `info().getName()` |
  | onPrimaryDoubleClick() | 双击 | 未加载则 `loadChild()` |
  | createTable(两重载)/createTableParam | 建表 | 组装 `MysqlCreateTableParam` → `client().createTable` |
  | alterTable(两重载)/alterTableParam | 改表 | 组装 `MysqlAlertTableParam`（含 `existPrimaryKey`）→ `client().alertTable` |
  | existPrimaryKey/selectFullTable/existTable(Deprecated) | 表元数据 | 委托 client |
  | renameTable/renameEvent | 重命名 | 委托 client |
  | clearTable/truncateTable/dropTable | 表操作 | 委托 client |
  | executeSql/executeSingleSql/explainSql | SQL 执行 | 委托 client |
  | createFunction/dropFunction/selectFunction/alertFunction | 函数 | 委托 client |
  | selectProcedure/alertProcedure/createProcedure/dropProcedure | 过程 | 委托 client |
  | selectView/createView/alertView/dropView/existView | 视图 | 委托 client |
  | selectEvent/alertEvent/createEvent/dropEvent | 事件 | 委托 client |
  | selectTable/selectFullTable/selectRecord/deleteRecord | 查询 | 委托 client |
  | isSupportCheckFeature/dialect | 特性/方言 | 委托 client |
  | checks/triggers/columns/indexes/foreignKeys | 表结构 | 组装参数后委托 client |
  | dbConnect()/cloneTable() | 连接信息/克隆表 | 委托 client |
  | itemVisible() | 可见性 | `isVisible()` |

- 调用链：
  - `MysqlDatabaseTreeItem.loadChild() → MysqlTablesTreeItem/MysqlViewsTreeItem/... → setChild`
  - `MysqlTableTreeItem.rename() → dbItem().renameTable(old, new) → client().renameTable`
  - `MysqlDatabaseTreeItem.createTable(param) → client().createTable(param)`

## MysqlDatabaseTreeItemValue

- 职责：数据库节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item() | 强类型节点 | 转 `MysqlDatabaseTreeItem` |
  | name() | 名称 | `item().dbName()` |
  | graphic() | 图标 | `DatabaseSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |

- 调用链：`MysqlDatabaseTreeItem → MysqlDatabaseTreeItemValue.name()`

## MysqlTablesTreeItem

- 职责：数据库下的「表」类型节点，负责表列表的增量加载与数据导入导出入口。
- 字段：无（继承树节点属性）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlTablesTreeItem(RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlTablesTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlDatabaseTreeItem` |
  | getMenuItems() | 菜单 | 新增表/重载/导出数据/导入数据 |
  | exportData()/importData() | 打开导入导出窗口 | `MysqlDataExportController`/`MysqlDataImportController` 传 dumpType/dbInfo/dbName/dbClient |
  | addTable() | 新增表 | 新建 `MysqlTable` → `MysqlEventUtil.designTable` |
  | itemVisible() | 可见性 | `isVisible()` |
  | loadChild() | 增量加载表 | Task：`client().selectTables(dbName)`，无子节点直接建列表；有子节点做删除/新增/更新（`compare` + `copy`），最后 `expend` |
  | reloadChild() | 重载 | `clearChild` + `setLoaded(false)` + `loadChild` |
  | dbName()/client()/tableSize()/info()/infoName() | 上下文转发 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | 未加载则 `loadChild()` |
  | addTable(MysqlTable) | 追加表节点 | `addChild(new MysqlTableTreeItem)` + `sortChild` |

- 调用链：
  - `MysqlTablesTreeItem.loadChild() → client().selectTables(dbName) → MysqlTableTreeItem`
  - `MysqlTableTreeItem.cloneTable → dbItem().getTableTypeChild().addTable`

## MysqlTablesTreeItemValue

- 职责：表类型节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | name() | 名称 | `I18nHelper.table()` |
  | graphic() | 图标 | `TableSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |
  | extra()/extraColor() | 附加文本/颜色 | 显示 `(tableSize)`，颜色 `#228B22` |

- 调用链：`MysqlTablesTreeItem → MysqlTablesTreeItemValue.extra() → tableSize()`

## MysqlTableTreeItem

- 职责：具体表节点，提供表结构设计、记录增删改查、克隆、重命名、清空/截断/删除等操作。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlTable | 表对象（final） |
  | columns | MysqlColumns | 列信息缓存 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlTableTreeItem(MysqlTable, RichTreeView) | 构造 | 设置 `MysqlTableTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlTablesTreeItem` |
  | client()/dbName()/tableName()/info() | 上下文 | 委托 parent/value |
  | getMenuItems() | 菜单 | 打开表/设计/重命名/清空/截断/删除/转储/导出/表信息/克隆表子菜单 |
  | cloneTable(boolean) | 克隆表（带遮罩） | `StageManager.showMask(doCloneTable)` |
  | doCloneTable(boolean) | 执行克隆 | `dbItem().cloneTable` → `selectTable` → `getTableTypeChild().addTable` |
  | dump()/export() | 转储/导出 | 打开对应 Controller，dumpType=2 并传表名 |
  | designTable() | 设计表 | `reloadChild()` + `MysqlEventUtil.designTable` |
  | truncateTable()/clearTableData() | 截断/清空 | 确认后委托 `dbItem()`，触发对应 `MysqlEventUtil` 事件 |
  | delete() | 删除表 | 确认后 `dbItem().dropTable` → 事件 → `remove()` |
  | tableInfo() | 表信息窗口 | `MysqlTableInfoController` 传 tableItem |
  | rename() | 重命名表 | 校验后 `dbItem().renameTable` + 事件 `tableRenamed` |
  | dbItem() | 数据库节点 | `parent().parent()` |
  | recordPage(...) | 分页查询记录 | 组装 `MysqlSelectRecordParam`，`selectRecords`+`selectRecordCount` → `Paging` |
  | columns()/indexes()/checks()/foreignKeys()/triggers() | 表结构 | 委托 client |
  | onPrimaryDoubleClick() | 双击 | `MysqlEventUtil.tableOpen` |
  | getPrimaryKey() | 取主键列（优先自增） | 惰性加载 columns，遍历 `primaryKeys()` |
  | loadChild()/reloadChild() | 加载/重载表结构 | `selectTable(full)` 后 `value.copy` |
  | hasPrimaryKey() | 是否无主键 | `columns.primaryKeys().isEmpty()` |
  | insertRecord/deleteRecord/selectRecord/updateRecord(重载) | 记录增删改查 | 组装对应 Param → 委托 client |
  | value() | 表对象 | 返回 value |

- 调用链：
  - `MysqlTableTreeItem.onPrimaryDoubleClick() → MysqlEventUtil.tableOpen → MysqlTableRecordTab`
  - `MysqlTableTreeItem.designTable() → MysqlEventUtil.designTable → MysqlTableDesignTab`
  - `MysqlTableTreeItem.insertRecord(data) → client().insertRecord(param)`

## MysqlTableTreeItemValue

- 职责：表节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item() | 强类型节点 | 转 `MysqlTableTreeItem` |
  | graphic() | 图标 | `TableSVGGlyph` |
  | name() | 名称 | `item().tableName()` |

- 调用链：`MysqlTableTreeItem → MysqlTableTreeItemValue.name()`

## MysqlViewsTreeItem

- 职责：数据库下的「视图」类型节点，负责视图列表增量加载与新建设计入口。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlViewsTreeItem(RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlViewsTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlDatabaseTreeItem` |
  | getMenuItems() | 菜单 | 新增视图/刷新数据 |
  | add() | 新增视图 | `MysqlView` → `MysqlEventUtil.designView` |
  | itemVisible() | 可见性 | `isVisible()` |
  | loadChild() | 增量加载视图 | Task：`client().views(dbName)`，删除/新增/更新（`compare`+`copy`）后 `expend` |
  | reloadChild() | 重载 | clear + loadChild |
  | dbName()/client()/viewSize()/info()/infoName() | 上下文转发 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | 未加载则 loadChild |
  | addView(MysqlView) | 追加视图节点 | `addChild` + `sortChild` |

- 调用链：`MysqlViewsTreeItem.loadChild() → client().views(dbName) → MysqlViewTreeItem`

## MysqlViewsTreeItemValue

- 职责：视图类型节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | name() | 名称 | `I18nHelper.view()` |
  | graphic() | 图标 | `ViewSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |
  | extra()/extraColor() | 附加文本/颜色 | 显示 `(viewSize)`，颜色 `#228B22` |

- 调用链：`MysqlViewsTreeItem → MysqlViewsTreeItemValue.extra()`

## MysqlViewTreeItem

- 职责：具体视图节点，提供视图打开/设计/重命名/删除及记录分页与增删改查。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlView | 视图对象（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlViewTreeItem(MysqlView, RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlViewTreeItemValue` |
  | value()/viewName() | 视图对象/名称 | value / `value.getName()` |
  | parent()/client()/dbName()/info()/infoName() | 上下文 | 委托 parent |
  | viewColumns() | 加载并缓存视图列 | `new MysqlColumns(columns())` 存入 value |
  | getMenuItems() | 菜单 | 打开/设计/重命名/删除 |
  | designView() | 设计视图 | `MysqlEventUtil.designView(value, dbItem())` |
  | delete() | 删除视图 | 确认后 `dbItem().dropView` + `remove()` |
  | dbItem() | 数据库节点 | `parent().parent()` |
  | recordPage(...) | 分页查询 | `client().viewRecords` + `selectRecordCount` → `Paging` |
  | columns() | 视图列 | `new MysqlColumns(client().viewColumns(...))` |
  | onPrimaryDoubleClick() | 双击 | `MysqlEventUtil.viewOpen` |
  | getPrimaryKey() | 取自增列 | 惰性 `viewColumns()` 后遍历 |
  | isUpdatable() | 是否可更新 | `value.isUpdatable()` |
  | insertRecord/deleteRecord/selectRecord/updateRecord(重载) | 记录操作 | 组装 Param → 委托 client |
  | rename() | 重命名视图 | `dbItem().renameTable` + 事件 `viewRenamed` |

- 调用链：
  - `MysqlViewTreeItem.onPrimaryDoubleClick() → MysqlEventUtil.viewOpen → MysqlViewRecordTab`
  - `MysqlViewTreeItem.recordPage(...) → client().viewRecords(...)`

## MysqlViewTreeItemValue

- 职责：视图节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item() | 强类型节点 | 转 `MysqlViewTreeItem` |
  | graphic() | 图标 | `ViewSVGGlyph` |
  | name() | 名称 | `item().viewName()` |

- 调用链：`MysqlViewTreeItem → MysqlViewTreeItemValue.name()`

---

## function / procedure / event / query

## MysqlFunctionsTreeItem

- 职责：数据库下的「函数」类型节点，负责函数列表增量加载与新建入口。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlFunctionsTreeItem(RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlFunctionsTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlDatabaseTreeItem` |
  | getMenuItems() | 菜单 | 新增函数/刷新数据 |
  | add() | 新增函数 | `MysqlFunction` → `MysqlEventUtil.designFunction` |
  | itemVisible() | 可见性 | `isVisible()` |
  | loadChild() | 增量加载函数 | Task：`client().functions(dbName)`，删除/新增/更新后 `expend` |
  | reloadChild() | 重载 | clear + loadChild |
  | dbName()/client()/info()/infoName() | 上下文 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | 未加载则 loadChild |
  | functionSize() | 函数数量 | `client().functionSize(dbName, null)` |
  | addFunction(MysqlFunction) | 追加函数节点 | `addChild` + `sortChild` |

- 调用链：`MysqlFunctionsTreeItem.loadChild() → client().functions(dbName) → MysqlFunctionTreeItem`

## MysqlFunctionsTreeItemValue

- 职责：函数类型节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | name() | 名称 | `I18nHelper.function()` |
  | graphic() | 图标 | `FunctionSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |
  | extra()/extraColor() | 附加文本/颜色 | 显示 `(functionSize)`，颜色 `#228B22` |

- 调用链：`MysqlFunctionsTreeItem → MysqlFunctionsTreeItemValue.extra()`

## MysqlFunctionTreeItem

- 职责：具体函数节点，提供函数设计、删除与结构重载。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlFunction | 函数对象（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlFunctionTreeItem(MysqlFunction, RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlFunctionTreeItemValue` |
  | parent()/client()/info()/dbName()/infoName() | 上下文 | 委托 parent |
  | getMenuItems() | 菜单 | 设计函数/删除函数 |
  | delete() | 删除函数 | 确认后 `dbItem().dropFunction` + `remove()` |
  | dbItem() | 数据库节点 | `parent().parent()` |
  | onPrimaryDoubleClick() | 双击 | `MysqlEventUtil.designFunction` |
  | functionName() | 函数名 | `value.getName()` |
  | reloadChild()/loadChild() | 重载/加载 | `client().selectFunction` 后 `value.copy` |
  | onPrimarySingleClick() | 单击 | 分支均调用 `super.onPrimarySingleClick()` |
  | value() | 函数对象 | 返回 value |

- 调用链：`MysqlFunctionTreeItem.onPrimaryDoubleClick() → MysqlEventUtil.designFunction → MysqlFunctionDesignTab`

## MysqlFunctionTreeItemValue

- 职责：函数节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item()/graphic()/name() | 强类型/图标/名称 | `FunctionSVGGlyph`；`item().functionName()` |

- 调用链：`MysqlFunctionTreeItem → MysqlFunctionTreeItemValue.name()`

## MysqlProceduresTreeItem

- 职责：数据库下的「过程」类型节点，负责过程列表增量加载与新建入口。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlProceduresTreeItem(RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlProceduresTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlDatabaseTreeItem` |
  | getMenuItems() | 菜单 | 新增过程/刷新数据 |
  | add() | 新增过程 | `MysqlProcedure` → `MysqlEventUtil.designProcedure` |
  | itemVisible() | 可见性 | `isVisible()` |
  | loadChild() | 增量加载过程 | Task：`client().procedures(dbName)`，删除/新增/更新后 `expend` |
  | reloadChild() | 重载 | clear + loadChild |
  | dbName()/client()/info()/infoName() | 上下文 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | 未加载则 loadChild |
  | procedureSize() | 过程数量 | `client().procedureSize(dbName, null)` |
  | addProcedure(MysqlProcedure) | 追加过程节点 | `addChild` + `sortChild` |

- 调用链：`MysqlProceduresTreeItem.loadChild() → client().procedures(dbName) → MysqlProcedureTreeItem`

## MysqlProceduresTreeItemValue

- 职责：过程类型节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | name() | 名称 | `I18nHelper.procedure()` |
  | graphic() | 图标 | `ProcedureSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |
  | extra()/extraColor() | 附加文本/颜色 | 显示 `(procedureSize)`，颜色 `#228B22` |

- 调用链：`MysqlProceduresTreeItem → MysqlProceduresTreeItemValue.extra()`

## MysqlProcedureTreeItem

- 职责：具体过程节点，提供过程设计、删除。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlProcedure | 过程对象（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlProcedureTreeItem(MysqlProcedure, RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlProcedureTreeItemValue` |
  | value()/parent()/client()/info() | 取值/上下文 | value；parent 转 `MysqlProceduresTreeItem` |
  | getMenuItems() | 菜单 | 设计过程/删除过程 |
  | delete() | 删除过程 | 确认后 `dbItem().dropProcedure` + `remove()` |
  | dbItem()/dbName()/infoName() | 数据库节点/上下文 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | `MysqlEventUtil.designProcedure` |
  | procedureName() | 过程名 | `value.getName()` |

- 调用链：`MysqlProcedureTreeItem.onPrimaryDoubleClick() → MysqlEventUtil.designProcedure → MysqlProcedureDesignTab`

## MysqlProcedureTreeItemValue

- 职责：过程节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item()/graphic()/name() | 强类型/图标/名称 | `ProcedureSVGGlyph`；`item().procedureName()` |

- 调用链：`MysqlProcedureTreeItem → MysqlProcedureTreeItemValue.name()`

## MysqlEventsTreeItem

- 职责：数据库下的「事件」类型节点，负责事件列表增量加载与新建入口。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlEventsTreeItem(RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlEventsTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlDatabaseTreeItem` |
  | getMenuItems() | 菜单 | 新增事件/刷新数据 |
  | add() | 新增事件 | `MysqlEvent` → `MysqlEventUtil.designEvent` |
  | itemVisible() | 可见性 | `isVisible()` |
  | loadChild() | 增量加载事件 | Task：`client().events(dbName)`，删除/新增/更新后 `expend` |
  | reloadChild() | 重载 | clear + loadChild |
  | dbName()/client()/info()/infoName() | 上下文 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | 未加载则 loadChild |
  | eventSize() | 事件数量 | `client().eventSize(dbName)` |
  | addEvent(MysqlEvent) | 追加事件节点 | `addChild` + `sortChild` |

- 调用链：`MysqlEventsTreeItem.loadChild() → client().events(dbName) → MysqlEventTreeItem`

## MysqlEventsTreeItemValue

- 职责：事件类型节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | name() | 名称 | `I18nHelper.event()` |
  | graphic() | 图标 | `EventSVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |
  | extra()/extraColor() | 附加文本/颜色 | 显示 `(eventSize)`，颜色 `#228B22` |

- 调用链：`MysqlEventsTreeItem → MysqlEventsTreeItemValue.extra()`

## MysqlEventTreeItem

- 职责：具体事件节点，提供事件设计、重命名、删除。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlEvent | 事件对象（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlEventTreeItem(MysqlEvent, RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlEventTreeItemValue` |
  | value()/parent()/client()/info() | 取值/上下文 | value；parent 转 `MysqlEventsTreeItem` |
  | getMenuItems() | 菜单 | 重命名/设计/删除/事件信息 |
  | eventInfo() | 事件信息 | 空实现 |
  | delete() | 删除事件 | 确认后 `dbItem().dropEvent` + `remove()` |
  | dbItem()/dbName()/infoName() | 数据库节点/上下文 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | `MysqlEventUtil.designEvent` |
  | eventName() | 事件名 | `value.getName()` |
  | rename() | 重命名事件 | 校验后 `dbItem().renameEvent` + 事件 `eventRenamed` |

- 调用链：`MysqlEventTreeItem.onPrimaryDoubleClick() → MysqlEventUtil.designEvent → MysqlEventDesignTab`

## MysqlEventTreeItemValue

- 职责：事件节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item()/graphic()/name() | 强类型/图标/名称 | `EventSVGGlyph`；`item().eventName()` |

- 调用链：`MysqlEventTreeItem → MysqlEventTreeItemValue.name()`

## MysqlQueriesTreeItem

- 职责：数据库下的「查询」类型节点，从本地 `MysqlQueryStore` 加载查询列表。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlQueriesTreeItem(RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlQueriesTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlDatabaseTreeItem` |
  | getMenuItems() | 菜单 | 新增查询/刷新数据 |
  | addQuery() | 新增查询 | `MysqlEventUtil.queryAdd(this.parent())` |
  | itemVisible() | 可见性 | `isVisible()` |
  | loadChild() | 加载查询 | Task：`MysqlQueryStore.INSTANCE.list(info().getId(), dbName())` → `MysqlQueryTreeItem` |
  | reloadChild() | 重载 | clear + loadChild |
  | addChild(MysqlQuery) | 追加查询节点 | `addChild(new MysqlQueryTreeItem)` |
  | dbName()/client()/info() | 上下文 | 委托 parent |
  | onPrimaryDoubleClick() | 双击 | 未加载则 loadChild |
  | querySize() | 查询数量 | `MysqlQueryStore.INSTANCE.list(...).size()` |
  | dbConnect() | 连接 | `parent().dbConnect()` |
  | addQuery(MysqlQuery) | 追加并排序 | `addChild` + `sortChild` |

- 调用链：
  - `MysqlQueriesTreeItem.loadChild() → MysqlQueryStore.INSTANCE.list → MysqlQueryTreeItem`
  - `MysqlQueriesTreeItem.addQuery() → MysqlEventUtil.queryAdd`

## MysqlQueriesTreeItemValue

- 职责：查询类型节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | name() | 名称 | `I18nHelper.queries()` |
  | graphic() | 图标 | `QuerySVGGlyph` 并 `disableTheme()` |
  | graphicColor() | 颜色 | 有子节点返回 `Color.GREEN` |
  | extra()/extraColor() | 附加文本/颜色 | 显示 `(querySize)`，颜色 `#228B22` |

- 调用链：`MysqlQueriesTreeItem → MysqlQueriesTreeItemValue.extra()`

## MysqlQueryTreeItem

- 职责：具体查询节点，提供查询打开、重命名、删除（持久化到 `MysqlQueryStore`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | value | MysqlQuery | 查询对象（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlQueryTreeItem(MysqlQuery, RichTreeView) | 构造 | `setFilterable(true)`，设置 `MysqlQueryTreeItemValue` |
  | value()/parent()/client()/info() | 取值/上下文 | value；parent 转 `MysqlQueriesTreeItem` |
  | getMenuItems() | 菜单 | 打开查询/重命名/删除 |
  | delete() | 删除查询 | 确认后 `MysqlQueryStore.INSTANCE.delete` → `remove()` + `MysqlEventUtil.queryDeleted` |
  | rename() | 重命名查询 | 校验后 `MysqlQueryStore.INSTANCE.update` + 事件 `queryRenamed` |
  | dbItem()/dbName() | 数据库节点/名称 | 委托 parent |
  | queryName() | 查询名 | `value.getName()` |
  | onPrimaryDoubleClick() | 双击 | `MysqlEventUtil.queryOpen` |
  | dbConnect() | 连接 | `client().getDbConnect()` |

- 调用链：`MysqlQueryTreeItem.onPrimaryDoubleClick() → MysqlEventUtil.queryOpen → MysqlQueryMainTab`

## MysqlQueryTreeItemValue

- 职责：查询节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | item()/graphic()/name() | 强类型/图标/名称 | `QuerySVGGlyph`；`item().queryName()` |

- 调用链：`MysqlQueryTreeItem → MysqlQueryTreeItemValue.name()`

---

## terminal

## MysqlTerminalTreeItem

- 职责：终端树节点，双击打开终端会话。
- 字段：无（继承 `RichTreeItem<MysqlTerminalTreeItemValue>`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlTerminalTreeItem(RichTreeView) | 构造 | 设置 `MysqlTerminalTreeItemValue` |
  | parent() | 父节点 | 转 `MysqlDatabaseTreeItem` |
  | shellConnect() | 连接信息 | `parent().info()` |
  | client() | 客户端 | `parent().client()` |
  | onPrimaryDoubleClick() | 双击 | `MysqlEventUtil.terminalOpen(client(), parent().dbName())` |

- 调用链：`MysqlTerminalTreeItem.onPrimaryDoubleClick() → MysqlEventUtil.terminalOpen → 终端 Tab`

## MysqlTerminalTreeItemValue

- 职责：终端节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | graphic() | 图标 | `TerminalSVGGlyph` |
  | name() | 名称 | `I18nHelper.terminal()` |

- 调用链：`MysqlTerminalTreeItem → MysqlTerminalTreeItemValue.name()`

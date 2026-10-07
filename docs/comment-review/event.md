# event 包代码审查文档

包路径：`cn.oyzh.easymysql.event`（含子包 `connect`、`database`、`event`、`function`、`group`、`procedure`、`query`、`record`、`table`、`terminal`、`tree`、`view`）。

本包为事件定义层：`MysqlEventUtil` 是事件派发工具类，其余类均为继承 `cn.oyzh.event.Event<T>` 的事件对象。事件对象通常携带一个树节点（`MysqlDatabaseTreeItem`/`MysqlTableTreeItem` 等）或业务对象，供监听器识别来源与目标。

> 说明：以下类为整文件被注释的死代码，已跳过：根包的 `DBEventGroups`、`DBEventTypes`、`DBEventUtil`、`DBFilterMainEvent`、`DBLeftCollapseEvent`、`DBLeftExtendEvent`、`DBSearchFinishEvent`、`DBSearchFireEvent`、`DBSearchStartEvent`、`TreeChildChangedEvent`、`TreeChildFilterEvent`，以及 `record/RecordDeleteEvent`。

---

## 根包 cn.oyzh.easymysql.event

## MysqlEventUtil
- 职责：MySQL 事件工具类，负责创建事件对象并投递（`EventUtil.post`）。
- 字段：无（仅静态方法）。
- 方法（均为 `public static`，模式为：new 事件 → `event.data(...)`/`setDbItem(...)` → `EventUtil.post(event)`）：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `tableOpen(MysqlTableTreeItem,MysqlDatabaseTreeItem)` | 打开表事件 | 投递 `MysqlTableOpenEvent` |
  | `tableAdded(MysqlDatabaseTreeItem)` | 表已新增 | `MysqlTableAddedEvent` |
  | `tableAlerted(String,MysqlDatabaseTreeItem)` | 表已提示 | `MysqlTableAlertedEvent` |
  | `tableRenamed(MysqlTableTreeItem,MysqlDatabaseTreeItem)` | 表已更名 | `MysqlTableRenamedEvent` |
  | `tableCleared(MysqlTableTreeItem,MysqlDatabaseTreeItem)` | 表已清空 | `MysqlTableClearedEvent` |
  | `tableTruncated(MysqlTableTreeItem,MysqlDatabaseTreeItem)` | 表已截断 | `MysqlTableTruncatedEvent` |
  | `tableDropped(MysqlTableTreeItem,MysqlDatabaseTreeItem)` | 表已删除 | `MysqlTableDroppedEvent` |
  | `tableFiltered(MysqlTableTreeItem,List<MysqlRecordFilter>)` | 表已过滤 | `MysqlTableFilteredEvent` |
  | `viewOpen(MysqlViewTreeItem,MysqlDatabaseTreeItem)` | 打开视图 | `MysqlViewOpenEvent` |
  | `viewAdded(MysqlDatabaseTreeItem)` | 视图已新增 | `MysqlViewAddedEvent` |
  | `viewAlerted(String,MysqlDatabaseTreeItem)` | 视图已提示 | `MysqlViewAlertedEvent` |
  | `viewRenamed(MysqlViewTreeItem,MysqlDatabaseTreeItem)` | 视图已更名 | `MysqlViewRenamedEvent` |
  | `viewFiltered(MysqlViewTreeItem,List<MysqlRecordFilter>)` | 视图已过滤 | `MysqlViewFilteredEvent` |
  | `designView(MysqlView,MysqlDatabaseTreeItem)` | 视图设计 | `MysqlViewDesignEvent` |
  | `designTable(MysqlTable,MysqlDatabaseTreeItem)` | 表设计 | `MysqlTableDesignEvent` |
  | `procedureAdded/procedureAlerted/designProcedure(...)` | 存储过程新增/提示/设计 | `MysqlProcedure*Event` |
  | `functionAdded/functionAlerted/designFunction(...)` | 函数新增/提示/设计 | `MysqlFunction*Event` |
  | `eventAdded/eventAlerted/designEvent/eventRenamed(...)` | 事件新增/提示/设计/更名 | `MysqlEvent*Event` |
  | `queryAdd/queryAdded/queryDeleted/queryOpen/queryRenamed(...)` | 查询新建/新增/删除/打开/更名 | `MysqlQuery*Event` |
  | `databaseAdded/databaseUpdated/databaseClosed/databaseDropped(...)` | 数据库新增/修改/关闭/删除 | `MysqlDatabase*Event` |
  | `connectAdded/connectDeleted/connectUpdated/addConnect/addGroup/infoDeleted(...)` | 连接新增/删除/修改/加连接/加分组的处理 | `MysqlConnect*Event`、`DBAddConnectEvent`、`DBAddGroupEvent` |
  | `connectionClosed/connectionConnected(MysqlClient)` | 连接关闭/成功 | `DBConnectionClosedEvent`/`DBConnectionConnectedEvent` |
  | `terminalOpen(MysqlClient,String)/terminalClose(MysqlClient)` | 终端打开/关闭 | `DBTerminalOpenEvent`/`DBTerminalCloseEvent` |
  | `treeItemChanged(TreeItem<?>)` | 节点选中变化 | `MysqlTreeItemChangedEvent` |
  | `layout1()/layout2()/changelog()` | 布局/变更日志 | 分别投递 `Layout1Event`/`Layout2Event`/`ChangelogEvent` |
- 调用链：`MysqlClient(状态监听) → MysqlEventUtil.connectionClosed/connectionConnected → EventUtil.post`；`UI 操作 → MysqlEventUtil.* → EventUtil.post → 监听器`

---

## 子包 cn.oyzh.easymysql.event.connect

## DBAddConnectEvent
- 职责：新增连接事件。
- 字段：无（继承 `Event<Object>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.addConnect → DBAddConnectEvent`

## DBConnectionClosedEvent
- 职责：数据库连接关闭事件（实现 `EventFormatter`）。
- 字段：无（继承 `Event<MysqlClient>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `eventFormat()` | 事件文本 | 返回 `[连接名] 客户端已断开` |
  | `dbConnect()` | 取连接信息 | `data().getDbConnect()`（`MysqlConnect`） |
  | `isMysqlType()` | 是否 MySQL 类型 | `data().dialect() == DBDialect.MYSQL` |
- 调用链：`MysqlClient.close → state=CLOSED → MysqlEventUtil.connectionClosed → DBConnectionClosedEvent`

## DBConnectionConnectedEvent
- 职责：数据库连接成功事件（实现 `EventFormatter`）。
- 字段：无（继承 `Event<MysqlClient>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `eventFormat()` | 事件文本 | 返回 `[连接名] 客户端已连接` |
  | `dbConnect()` | 取连接信息 | `data().getDbConnect()` |
- 调用链：`MysqlClient.start → state=CONNECTED → MysqlEventUtil.connectionConnected → DBConnectionConnectedEvent`

## MysqlConnectAddedEvent
- 职责：连接已新增事件（实现 `EventFormatter`）。
- 字段：无（继承 `Event<MysqlConnect>`）。
- 方法：`eventFormat()`。
- 调用链：`MysqlEventUtil.connectAdded → MysqlConnectAddedEvent`

## MysqlConnectDeletedEvent
- 职责：连接已删除事件（实现 `EventFormatter`）。
- 字段：无（继承 `Event<MysqlConnect>`）。
- 方法：`eventFormat()`。
- 调用链：`MysqlEventUtil.connectDeleted → MysqlConnectDeletedEvent`

## MysqlConnectUpdatedEvent
- 职责：连接已修改事件（实现 `EventFormatter`）。
- 字段：无（继承 `Event<MysqlConnect>`）。
- 方法：`eventFormat()`。
- 调用链：`MysqlEventUtil.connectUpdated → MysqlConnectUpdatedEvent`

---

## 子包 cn.oyzh.easymysql.event.database

## MysqlDatabaseAddedEvent
- 职责：数据库已新增事件（实现 `EventFormatter`）。
- 字段：`connectItem`(DBConnectTreeItem 连接树节点)。
- 方法：`eventFormat()`、`getConnectItem/setConnectItem`。
- 调用链：`MysqlEventUtil.databaseAdded → MysqlDatabaseAddedEvent`

## MysqlDatabaseClosedEvent
- 职责：数据库已关闭事件（实现 `EventFormatter`）。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：`eventFormat()`。
- 调用链：`MysqlEventUtil.databaseClosed → MysqlDatabaseClosedEvent`

## MysqlDatabaseDroppedEvent
- 职责：数据库已删除事件（实现 `EventFormatter`）。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：`eventFormat()`。
- 调用链：`MysqlEventUtil.databaseDropped → MysqlDatabaseDroppedEvent`

## MysqlDatabaseUpdatedEvent
- 职责：数据库已修改事件（实现 `EventFormatter`）。
- 字段：`connectItem`(DBConnectTreeItem 连接树节点)。
- 方法：`eventFormat()`、`getConnectItem/setConnectItem`。
- 调用链：`MysqlEventUtil.databaseUpdated → MysqlDatabaseUpdatedEvent`

---

## 子包 cn.oyzh.easymysql.event.event

## MysqlEventAddedEvent
- 职责：MySQL 事件已新增事件。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.eventAdded → MysqlEventAddedEvent`

## MysqlEventAlertedEvent
- 职责：MySQL 事件已提示事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem 数据库树节点)。
- 方法：`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.eventAlerted → MysqlEventAlertedEvent`

## MysqlEventDesignEvent
- 职责：MySQL 事件设计事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`eventName()`（取 `data().getName()`）、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.designEvent → MysqlEventDesignEvent`

## MysqlEventRenamedEvent
- 职责：MySQL 事件已更名事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`eventName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.eventRenamed → MysqlEventRenamedEvent`

---

## 子包 cn.oyzh.easymysql.event.function

## MysqlFunctionAddedEvent
- 职责：函数已新增事件。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.functionAdded → MysqlFunctionAddedEvent`

## MysqlFunctionAlertedEvent
- 职责：函数已提示事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.functionAlerted → MysqlFunctionAlertedEvent`

## MysqlFunctionDesignEvent
- 职责：函数设计事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`functionName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.designFunction → MysqlFunctionDesignEvent`

---

## 子包 cn.oyzh.easymysql.event.group

## DBAddGroupEvent
- 职责：新增分组事件。
- 字段：无（继承 `Event<Object>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.addGroup → DBAddGroupEvent`

---

## 子包 cn.oyzh.easymysql.event.procedure

## MysqlProcedureAddedEvent
- 职责：存储过程已新增事件。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.procedureAdded → MysqlProcedureAddedEvent`

## MysqlProcedureAlertedEvent
- 职责：存储过程已提示事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.procedureAlerted → MysqlProcedureAlertedEvent`

## MysqlProcedureDesignEvent
- 职责：存储过程设计事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`procedureName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.designProcedure → MysqlProcedureDesignEvent`

---

## 子包 cn.oyzh.easymysql.event.query

## MysqlQueryAddEvent
- 职责：新建查询事件。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.queryAdd → MysqlQueryAddEvent`

## MysqlQueryAddedEvent
- 职责：查询已新增事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.queryAdded → MysqlQueryAddedEvent`

## MysqlQueryDeletedEvent
- 职责：查询已删除事件。
- 字段：无（继承 `Event<MysqlQueryTreeItem>`）。
- 方法：`queryId()`（取 `data().value().getUid()`）。
- 调用链：`MysqlEventUtil.queryDeleted → MysqlQueryDeletedEvent`

## MysqlQueryOpenEvent
- 职责：打开查询事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`queryId()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.queryOpen → MysqlQueryOpenEvent`

## MysqlQueryRenamedEvent
- 职责：查询已更名事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`queryName()`、`queryId()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.queryRenamed → MysqlQueryRenamedEvent`

---

## 子包 cn.oyzh.easymysql.event.table

## MysqlTableAddedEvent
- 职责：表已新增事件。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.tableAdded → MysqlTableAddedEvent`

## MysqlTableAlertedEvent
- 职责：表已提示事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.tableAlerted → MysqlTableAlertedEvent`

## MysqlTableClearedEvent
- 职责：表已清空事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`tableName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.tableCleared → MysqlTableClearedEvent`

## MysqlTableDesignEvent
- 职责：表设计事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`dbName()`、`getDbItem/setDbItem`、`tableName()`。
- 调用链：`MysqlEventUtil.designTable → MysqlTableDesignEvent`

## MysqlTableDroppedEvent
- 职责：表已删除事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`tableName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.tableDropped → MysqlTableDroppedEvent`

## MysqlTableFilteredEvent
- 职责：表已过滤事件。
- 字段：`filters`(List<MysqlRecordFilter> 过滤条件)、`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`tableName()`、`getDbItem/setDbItem`、`getFilters/setFilters`。
- 调用链：`MysqlEventUtil.tableFiltered → MysqlTableFilteredEvent`

## MysqlTableOpenEvent
- 职责：打开表事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`tableName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.tableOpen → MysqlTableOpenEvent`

## MysqlTableRenamedEvent
- 职责：表已更名事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`tableName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.tableRenamed → MysqlTableRenamedEvent`

## MysqlTableTruncatedEvent
- 职责：表已截断事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`tableName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.tableTruncated → MysqlTableTruncatedEvent`

---

## 子包 cn.oyzh.easymysql.event.terminal

## DBTerminalCloseEvent
- 职责：终端关闭事件。
- 字段：无（继承 `Event<MysqlClient>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.terminalClose → DBTerminalCloseEvent`

## DBTerminalOpenEvent
- 职责：终端打开事件。
- 字段：`dbName`(String 数据库名称)。
- 方法：`getDbName/setDbName`。
- 调用链：`MysqlEventUtil.terminalOpen → DBTerminalOpenEvent`

---

## 子包 cn.oyzh.easymysql.event.tree

## MysqlTreeItemChangedEvent
- 职责：树节点选中变化事件。
- 字段：无（继承 `Event<TreeItem<?>>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.treeItemChanged → MysqlTreeItemChangedEvent`

---

## 子包 cn.oyzh.easymysql.event.view

## MysqlViewAddedEvent
- 职责：视图已新增事件。
- 字段：无（继承 `Event<MysqlDatabaseTreeItem>`）。
- 方法：无。
- 调用链：`MysqlEventUtil.viewAdded → MysqlViewAddedEvent`

## MysqlViewAlertedEvent
- 职责：视图已提示事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.viewAlerted → MysqlViewAlertedEvent`

## MysqlViewDesignEvent
- 职责：视图设计事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`viewName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.designView → MysqlViewDesignEvent`

## MysqlViewFilteredEvent
- 职责：视图已过滤事件。
- 字段：`filters`(List<MysqlRecordFilter>)、`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`viewName()`、`getFilters/setFilters`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.viewFiltered → MysqlViewFilteredEvent`

## MysqlViewOpenEvent
- 职责：打开视图事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`viewName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.viewOpen → MysqlViewOpenEvent`

## MysqlViewRenamedEvent
- 职责：视图已更名事件。
- 字段：`dbItem`(MysqlDatabaseTreeItem)。
- 方法：`viewName()`、`dbName()`、`getDbItem/setDbItem`。
- 调用链：`MysqlEventUtil.viewRenamed → MysqlViewRenamedEvent`

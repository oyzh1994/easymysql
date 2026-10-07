# fx 包代码审查文档

包路径：`cn.oyzh.easymysql.fx`（含子包 `data`、`event`、`info`、`record`、`routine`、`svg`、`svg.glyph`、`table`、`view`）

本包主要为 JavaFX 界面控件（下拉框、文本框、表格视图、编辑器等）及数据传输对话框所用的模型类。多数类继承自 `cn.oyzh.fx.plus` 下的通用控件（`FXComboBox`、`FXTableView`、`FXListView`、`Editor` 等）。

> 说明：以下类为整文件被注释的死代码，已跳过：`DBSqlTextArea`、`ServiceTypeCombobox`、`ColumnSVGGlyph`、`DatabaseSVGGlyph`、`EventSVGGlyph`、`KeywordsSVGGlyph`、`MysqlSVGGlyph`、`TableSVGGlyph`。

---

## 根包 cn.oyzh.easymysql.fx

## DBCharsetComboBox
- 职责：数据库字符集下拉框。
- 字段：无（继承 `FXComboBox<String>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(MysqlClient client)` | 初始化字符集列表 | `clearItems()` 后先加空项，再遍历 `client.charsets()` 大写加入 |
  | `select(String obj)` | 选中字符集 | 非空时转大写 `super.select`，否则 `clearSelection` |
- 调用链：`DBCharsetComboBox.init → MysqlClient.charsets`

## DBCollationComboBox
- 职责：数据库排序规则下拉框。
- 字段：无（继承 `FXComboBox<String>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(String charset, MysqlClient client)` | 按字符集初始化排序规则 | 用 prop `charset` 记忆当前字符集，变化时才重建；遍历 `client.collation(charset)` 大写加入 |
  | `select(String obj)` | 选中排序规则 | 同字符集下拉框，转大写选中 |
- 调用链：`DBCollationComboBox.init → MysqlClient.collation`

## DBDatabaseComboBox
- 职责：数据库选择框。
- 字段：无（继承 `FXComboBox<String>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(MysqlClient client)` | 初始化数据库列表 | 委托 `init(client, null)` |
  | `init(MysqlClient client, String dbName)` | 初始化并选中指定库 | 取 `client.databases()`，映射为名称列表 `setItem`，`dbName` 非空则选中 |
- 调用链：`DBDatabaseComboBox.init → MysqlClient.databases`

## DBEditor
- 职责：SQL 编辑器控件。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dialect` | `DBDialect` | 当前数据库方言 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initNode()` | 初始化节点 | 调用父类后设置格式类型为 `EditorFormatType.SQL` |
  | `getDialect()/setDialect(DBDialect)` | 方言读写 | 简单存取 |
  | `getEditorFont()` | 获取编辑器字体 | 从 `MysqlSettingStore.SETTING.editorFontConfig()` 经 `FontManager.toFont` 转换 |
- 调用链：`DBEditor.initNode → Editor.setFormatType`；`DBEditor.getEditorFont → MysqlSettingStore.SETTING → FontManager.toFont`

## DBInfoComboBox
- 职责：数据库连接信息选择框。
- 字段：无（继承 `FXComboBox<MysqlConnect>`）。
- 方法：无显式方法；实例初始化块设置转换器（显示 `MysqlConnect.getName()`）并加载 `MysqlConnectStore.INSTANCE.load()` 作为选项。
- 调用链：`DBInfoComboBox(初始化块) → MysqlConnectStore.load`

## DBMsgTextArea
- 职责：数据库消息文本域，展示格式化事件消息。
- 字段：无（继承 `MsgTextArea`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `onEventMsg(EventFormatter)` | 事件消息回调 | `@EventSubscribe`，格式化后按 `yyyy-MM-dd HH:mm:ss 消息` 追加一行 |
- 调用链：`EventFormatter 事件 → DBMsgTextArea.onEventMsg → appendLine`

## DBSecurityTypeComboBox
- 职责：数据库安全类型（DEFINER/INVOKER）下拉框。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `select(String obj)` | 选中安全类型 | 转大写选中/清空 |
- 调用链：无外部调用。

## DBStatusColumn
- 职责：对象状态表格列。
- 字段：无（继承 `FXTableColumn<S,Object>`，`S extends DBObjectStatus`）。
- 方法：构造函数设置 `status` 属性工厂、宽度 25、不可排序/不可调整/不可重排。
- 调用链：`DBStatusColumn → DBObjectStatus.status`

## DBStatusTableView
- 职责：带状态监听的表格视图，跟踪删除项。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `deleteItems` | `List<S>` | 被移除且未创建成功的对象集合 |
  | `statusListener` | `DBStatusListener` | 状态变化监听器 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `reset()` | 重置状态 | 清空 `deleteItems` 并 `clearStatus` |
  | `clearStatus()` | 清除所有项状态 | 遍历 `getItems()` 调 `object.clearStatus()` |
  | `getDeleteItems()/setDeleteItems` `getStatusListener()/setStatusListener` | 存取器 | |
  | 实例初始化块 | 监听项列表变化 | 替换/新增项时为其 `statusProperty` 注册监听；移除项若非 `isCreated` 则加入 `deleteItems` 并注销监听 |
- 调用链：`项列表变更 → DBStatusTableView(监听器) → DBObjectStatus.statusProperty`；`reset → clearStatus → DBObjectStatus.clearStatus`

## DBTypeComboBox
- 职责：数据库类型（方言）选择框。
- 字段：无（继承 `FXComboBox<DBDialect>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 加载方言 | `setItem(DBDialect.valueList())` |
  | `getType()` | 获取选中类型名 | `getSelectedItem().name()` |
  | `isMysql()` | 是否 MySQL | 判断等于 `DBDialect.MYSQL` |
  | `selectType(String type)` | 按名称选中 | 遍历选项匹配 `name()` |
- 调用链：`DBTypeComboBox(初始化块) → DBDialect.valueList`

## MysqlDesignTabPane
- 职责：mysql 设计选项卡面板。
- 字段：无（继承 `FXTabPane`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initNode()` | 初始化节点 | 父类初始化后 `setupRefreshListener()` |
- 调用链：`MysqlDesignTabPane.initNode → setupRefreshListener`

---

## 子包 cn.oyzh.easymysql.fx.data

## DBDumpDataTypeComboBox
- 职责：数据库导出数据类型下拉框（数据+结构/仅结构）。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 加入 `dataAndStructure`、`structure` 两项 | `I18nHelper` |
  | `isFull()` | 是否导出完整数据 | `getSelectedIndex()==0` |
- 调用链：无外部调用。

## DataDateTextFiled
- 职责：日期格式选择文本框。
- 字段：无（继承 `SelectTextFiled<String>`）。
- 方法：实例初始化块加入 12 种日期时间格式模板选项。
- 调用链：无外部调用。

## DataExportColumn
- 职责：数据导出字段模型。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `selected` | `boolean` | 是否被选中（默认 true） |
- 方法：`isSelected()/setSelected(boolean)` 存取器。
- 调用链：`DataExportTable.columns → DataExportColumn.copy`。

## DataExportColumnListView
- 职责：数据导出字段勾选列表视图。
- 字段：无（继承 `FXListView<FXCheckBox>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(List<DataExportColumn> columns)` | 构建勾选框列表 | 每字段生成 `FXCheckBox`，勾选变化回写 `column.setSelected` |
- 调用链：`DataExportColumnListView.init → DataExportColumn.setSelected`

## DataExportTable
- 职责：数据导出表模型，含字段选择、文件路径与扩展名联动。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 表名称 |
  | `columns` | `List<DataExportColumn>` | 字段列表 |
  | `filePathProperty` | `StringProperty` | 导出文件路径 |
  | `selectedProperty` | `BooleanProperty` | 是否选中 |
  | `extensionProperty` | `ObjectProperty<FileExtensionFilter>` | 文件扩展名过滤器 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `selectedProperty()` | 懒加载选中属性 | 选中且无路径时自动 `updateFilePath` |
  | `getSelectedControl()` | 生成选中勾选框 | 双向绑定属性，含防循环标记 |
  | `filePathProperty()/getFilePath()/setFilePath` | 路径读写 | |
  | `getFilePathControl()` | 生成保存文件输入框 | 绑定路径与扩展名属性，`SaveFileTextField` |
  | `extensionProperty()/getExtension()/setExtension` | 扩展名读写 | 变更时 `updateFilePath` |
  | `fileName()` | 生成文件名 | `name + 扩展名去点` |
  | `columns(Collection)` `columns()` `selectedColumns()` `selectedColumnNames()` `hasColumns()` | 字段管理 | 复制 `MysqlColumn` 为 `DataExportColumn` |
  | `updateFilePath()` | 更新默认路径 | 桌面目录 + 文件名 |
  | `getName()/setName` `getColumns()/setColumns` | 存取器 | |
- 调用链：`DataExportTable.getFilePathControl → SaveFileTextField`；`DataExportTable.selectedProperty → updateFilePath → FXChooser.getDesktopDirectory`

## DataExportTableComboBox
- 职责：数据导出表下拉框。
- 字段：无（继承 `FXComboBox<DataExportTable>`）。
- 方法：初始化块设置转换器显示 `getName()`。
- 调用链：无外部调用。

## DataExportTableTableView
- 职责：数据导出表表格视图。
- 字段：无（继承 `FXTableView<DataExportTable>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSelectedTables()` | 获取选中表 | 遍历 `isSelected()` |
  | `hasSelectedTable()` | 是否有选中表 | 同遍历判断 |
- 调用链：无外部调用。

## DataFieldSeparatorComboBox
- 职责：数据字段分隔符下拉框（; , 空格）。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 按索引加入分号/逗号/空格项 | `I18nHelper` |
  | `value()` | 返回实际分隔符 | 按 `getSelectedIndex()` 映射 `;` `,` ` ` |
- 调用链：无外部调用。

## DataImportFile
- 职责：数据导入文件模型，含文件选择与目标表选择控件。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbName` | `String` | 数据库名称 |
  | `dbClient` | `MysqlClient` | 数据库客户端 |
  | `fileProperty` | `ObjectProperty<File>` | 导入文件 |
  | `targetTableName` | `String` | 目标表名称 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `fileProperty()/getFile()/setFile` | 文件属性 | |
  | `getFilePath()/getFileName()` | 路径/文件名 | 取自 `File` |
  | `getFilePathControl()` | 生成选文件输入框 | `ChooseFileTextField`，绑定属性 |
  | `getTargetTableControl()` | 生成目标表下拉框 | `MysqlTableComboBox.init(dbName,…)` |
  | `getTableName()` | 由文件名（去后缀）推导表名 | |
  | `getTargetTableName()/setTargetTableName` | 目标表名（缺省用表名） | |
  | `setDbName/setDbClient` | 存取器 | |
- 调用链：`DataImportFile.getTargetTableControl → MysqlTableComboBox.init → MysqlClient.selectTables`

## DataImportFileTableView
- 职责：数据导入文件表格视图。
- 字段：无（继承 `FXTableView<DataImportFile>`）。
- 方法：无。
- 调用链：无。

## DataImportTableComboBox
- 职责：数据导入表下拉框。
- 字段：无（继承 `FXComboBox<DataImportFile>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 转换器显示 `getTableName()` | |
  | `getSelectedTableName()` | 获取选中表名 | `getSelectedItem().getTableName()` |
- 调用链：无外部调用。

## DataRecordLabelComboBox
- 职责：数据记录标签下拉框（Root/RECORDS）。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isRoot()` | 是否根标签 | `getSelectedIndex()==0` |
- 调用链：无外部调用。

## DataRecordSeparatorComboBox
- 职责：数据记录分隔符下拉框（CRLF/LF/CR），按操作系统默认选中。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 加入三项并按 OS 选择 | `OSUtil.isWindows/isLinux/isMacOS` |
  | `value()` | 返回实际分隔符 | 映射 `\r\n` `\n` `\r` |
- 调用链：无外部调用。

## DataTransportEvent
- 职责：数据传输事件模型。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 事件名称 |
  | `selected` | `boolean` | 是否选中（默认 true） |
- 方法：`getName/setName`、`isSelected/setSelected`。
- 调用链：`DataTransportEventListView.of → DataTransportEvent.setName`。

## DataTransportEventListView
- 职责：数据传输事件勾选列表视图。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `selectedChanged` | `Runnable` | 选中变化回调 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `of(List<MysqlEvent>)` | 由事件列表构建 | 转换为 `DataTransportEvent` 后 `init` |
  | `init(List<DataTransportEvent>)` | 初始化勾选框 | 每项生成 `FXCheckBox`，`setProp("data",…)`，勾选变化回调 |
  | `getSelectedEvents()` | 获取选中事件 | 遍历勾选框取 prop |
  | `getSelectedSize()` | 选中数 | |
  | `getSelectedChanged()/setSelectedChanged` | 回调存取 | |
- 调用链：`DataTransportEventListView.of → MysqlEvent.getName`

## DataTransportFunction
- 职责：数据传输函数模型。
- 字段：`name`(String 函数名称)、`selected`(boolean 是否选中，默认 true)。
- 方法：`getName/setName`、`isSelected/setSelected`。
- 调用链：`DataTransportFunctionListView.of → DataTransportFunction.setName`。

## DataTransportFunctionListView
- 职责：数据传输函数勾选列表视图。
- 字段：`selectedChanged`(Runnable 选中变化回调)。
- 方法：`of(List<MysqlFunction>)`、`init(List<DataTransportFunction>)`、`getSelectedFunctions()`、`getSelectedSize()`、`getSelectedChanged()/setSelectedChanged`。逻辑同事件列表视图。
- 调用链：`DataTransportFunctionListView.of → MysqlFunction.getName`

## DataTransportProcedure
- 职责：数据传输存储过程模型。
- 字段：`name`(String 过程名称)、`selected`(boolean 是否选中，默认 true)。
- 方法：`getName/setName`、`isSelected/setSelected`。
- 调用链：`DataTransportProcedureListView.of → DataTransportProcedure.setName`。

## DataTransportProcedureListView
- 职责：数据传输存储过程勾选列表视图。
- 字段：`selectedChanged`(Runnable)。
- 方法：`of(List<MysqlProcedure>)`、`init(List<DataTransportProcedure>)`、`getSelectedProcedures()`、`getSelectedSize()`、`getSelectedChanged()/setSelectedChanged`。
- 调用链：`DataTransportProcedureListView.of → MysqlProcedure.getName`

## DataTransportTable
- 职责：数据传输表模型。
- 字段：`name`(String 表名称)、`selected`(boolean 是否选中，默认 true)。
- 方法：`getName/setName`、`isSelected/setSelected`。
- 调用链：`DataTransportTableListView.of → DataTransportTable.setName`。

## DataTransportTableListView
- 职责：数据传输表勾选列表视图。
- 字段：`selectedChanged`(Runnable)。
- 方法：`of(List<MysqlTable>)`、`init(List<DataTransportTable>)`、`getSelectedTables()`、`getSelectedSize()`、`getSelectedChanged()/setSelectedChanged`。
- 调用链：`DataTransportTableListView.of → MysqlTable.getName`

## DataTransportTrigger
- 职责：数据传输触发器模型。
- 字段：`name`(String 触发器名称)、`selected`(boolean 是否选中，默认 true)。
- 方法：`getName/setName`、`isSelected/setSelected`。
- 调用链：`DataTransportTriggerListView.of → DataTransportTrigger.setName`。

## DataTransportTriggerListView
- 职责：数据传输触发器勾选列表视图。
- 字段：`selectedChanged`(Runnable)。
- 方法：`of(List<MysqlTrigger>)`、`init(List<DataTransportTrigger>)`、`getSelectedTriggers()`、`getSelectedSize()`、`getSelectedChanged()/setSelectedChanged`。
- 调用链：`DataTransportTriggerListView.of → MysqlTrigger.getName`

## DataTransportView
- 职责：数据传输视图模型。
- 字段：`name`(String 视图名称)、`selected`(boolean 是否选中，默认 true)。
- 方法：`getName/setName`、`isSelected/setSelected`。
- 调用链：`DataTransportViewListView.of → DataTransportView.setName`。

## DataTransportViewListView
- 职责：数据传输视图勾选列表视图。
- 字段：`selectedChanged`(Runnable)。
- 方法：`of(List<MysqlView>)`、`init(List<DataTransportView>)`、`getSelectedViews()`、`getSelectedSize()`、`getSelectedChanged()/setSelectedChanged`。
- 调用链：`DataTransportViewListView.of → MysqlView.getName`

## DataTxtIdentifierComboBox
- 职责：数据文本标识符下拉框（双引号/单引号）。
- 字段：无。
- 方法：初始化块加入 `"` 与 `'`。
- 调用链：无。

---

## 子包 cn.oyzh.easymysql.fx.event

## MysqlEventIntervalTypeCombobox
- 职责：mysql 事件间隔类型下拉框。
- 字段：无。
- 方法：初始化块加入 YEAR/QUARTER/…/MINUTE_SECOND 等间隔类型；`select(String)` 转大写选中。
- 调用链：无外部调用。

## MysqlEventOnCompletionCombobox
- 职责：mysql 事件完成状态下拉框（PRESERVE/NOT PRESERVE）。
- 字段：无。
- 方法：初始化块加入两项；`select` 转大写选中。
- 调用链：无外部调用。

## MysqlEventStatusCombobox
- 职责：mysql 事件状态下拉框，兼容两种状态命名。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 加入 ENABLE/DISABLE/DISABLE ON SLAVE | |
  | `select(String val)` | 兼容映射选中 | ENABLED→0，DISABLED→1，SLAVESIDE_DISABLED→2，否则转大写 |
  | `isSameStatus(String val)` | 判断是否相同状态 | 用 `StringUtil.equalsAnyIgnoreCase` 与选中索引比对 |
- 调用链：`MysqlEventStatusCombobox.select → selectFirst/select(int)`

---

## 子包 cn.oyzh.easymysql.fx.record

## DBJsonTextFiled
- 职责：JSON 文本输入框（已 `@Deprecated`），支持弹窗放大编辑。
- 字段：无（继承 `LimitTextField`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数 | 设置皮肤为 `DBJsonTextFiledSkin` | |
  | `skin()` | 返回皮肤 | `(DBJsonTextFiledSkin) super.skin()` |
  | `createDefaultSkin()` | 创建默认皮肤 | new `DBJsonTextFiledSkin(this)` |
  | `setEnlargeWidth/getEnlargeWidth` `setEnlargeHeight/getEnlargeHeight` | 展开宽高 | 委托皮肤 |
- 调用链：`DBJsonTextFiled → DBJsonTextFiledSkin`

## DBJsonTextFiledSkin
- 职责：JSON 文本输入框皮肤，提供放大弹窗编辑 JSON。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `enlargeWidth` | `double` | 展开宽（默认 350） |
  | `enlargeHeight` | `double` | 展开高（默认 280） |
  | `popup` | `PopupExt` | 放大弹窗 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `onButtonClick(MouseEvent)` | 点击放大按钮 | 创建弹窗，内嵌 JSON 格式 `Editor` 与提交/取消图标，组装 `FXVBox` 后 `showPopup` |
  | `handleHide()` | 隐藏处理 | `popup.hide`、恢复输入框可用、重置按钮色 |
  | `onSubmit(String)` | 提交内容 | 回写文本并 `handleHide` |
  | `getButton()` | 放大按钮 | `EnlargeSVGGlyph` |
  | `updateButtonVisibility()` | 按钮可见性 | 未禁用且可见且有焦点时显示 |
  | `getEnlargeWidth/…/setPopup` | 存取器 | |
  | `dispose()` | 释放 | 销毁 `popup` |
- 调用链：`DBJsonTextFiledSkin.onButtonClick → Editor(JSON) → onSubmit → setText`

## MysqlBinaryTextFiled
- 职责：mysql 二进制字段文本输入框（已 `@Deprecated`），格式化显示二进制大小。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columnType` | `String` | 字段类型 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数 `MysqlBinaryTextFiled(String)` | 设置字段类型 | |
  | `formatValue()` | 格式化值 | `setText(format(columnType, getValue()))` |
  | 静态 `format(String,Object)` | 格式化二进制 | `byte[]` 时返回 `(类型) 大小`，否则 `(类型)` |
- 调用链：`MysqlBinaryTextFiled.formatValue → format → NumberUtil.formatSize`

## MysqlRecordColumn
- 职责：mysql 记录表格字段列，显示字段名/类型/注释并提供右键菜单。
- 字段：无（继承 `FXTableColumn<MysqlRecord,Object>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数 `MysqlRecordColumn(MysqlColumn)` | 构建列 | 设置单元格值工厂 `getProperty(列名)`；构建名称/类型/注释 `FXLabel` 组成 `FXVBox` 作为 graphic；添加“字段信息”“复制字段名”右键项 |
  | `showColumnInfo(MysqlColumn)` | 显示字段信息弹窗 | `PopupManager.parsePopup(MysqlFieldInfoPopupController.class)` |
  | `copyColumnName(MysqlColumn)` | 复制字段名 | `ClipboardUtil.copy` |
- 调用链：`MysqlRecordColumn → MysqlFieldInfoPopupController`；`MysqlRecordColumn → MysqlRecord.getProperty`

## MysqlRecordTableRow
- 职责：mysql 记录表格行。
- 字段：无（继承 `FXTableRow<MysqlRecord>`）。
- 方法：无。
- 调用链：`MysqlRecordTableView.initNode → MysqlRecordTableRow`。

## MysqlRecordTableView
- 职责：mysql 记录表格视图。
- 字段：无（继承 `FXTableView<MysqlRecord>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `hasProperty(MysqlRecordProperty)` | 是否含指定属性 | 遍历记录 `record.hasProperty` |
  | `hasRecord(MysqlRecord)` | 是否含指定记录 | `getItems().contains` |
  | `initNode()` | 初始化节点 | 设置行工厂 `MysqlRecordTableRow` 后父类初始化 |
- 调用链：`MysqlRecordTableView.initNode → MysqlRecordTableRow`；`hasProperty → MysqlRecord.hasProperty`

---

## 子包 cn.oyzh.easymysql.fx.routine

## MysqlCharacteristicCombobox
- 职责：mysql 存储过程特性下拉框。
- 字段：无。
- 方法：初始化块加入 LANGUAGE SQL/CONTAINS SQL/…/SQL SECURITY INVOKER 等特性；`select` 转大写。
- 调用链：无外部调用。

## MysqlParamModeComboBox
- 职责：mysql 参数模式下拉框（IN/OUT/INOUT）。
- 字段：无。
- 方法：初始化块加入三项。
- 调用链：无外部调用。

---

## 子包 cn.oyzh.easymysql.fx.svg.glyph

## WarningSVGGlyph
- 职责：警告图标。
- 字段：无（继承 `SVGGlyph`）。
- 方法：构造函数 `WarningSVGGlyph()` 设置 URL `/font/warning.svg`；`WarningSVGGlyph(String size)` 再设置尺寸。
- 调用链：无外部调用。

---

## 子包 cn.oyzh.easymysql.fx.table

## DBEnumTextFiled
- 职责：枚举字段值文本框，通过弹窗编辑枚举值列表。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `values` | `List<String>` | 枚举值列表 |
  | `popup` | `PopupAdapter` | 枚举编辑弹窗 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数（无参/带 values） | 初始化 | 设置动作 `initPopup` 与提示文本 |
  | `initPopup()` | 初始化弹窗 | `MysqlColumnEnumPopupController`，提交时回读列表并 `initText` |
  | `initText()` | 生成文本 | 拼接 `'v'` 以逗号连接 |
  | `setValues(List<String>)` | 设置枚举值 | 同步列表视图并刷新文本 |
  | `listView()` | 获取弹窗内列表视图 | `popup.content().lookup("#listView")` |
- 调用链：`DBEnumTextFiled.initPopup → MysqlColumnEnumPopupController`；`setValues → initText`

## DBJoinSymbolComboBox
- 职责：连接符下拉框（AND/OR）。
- 字段：无。
- 方法：初始化块加入两项。
- 调用链：无。

## MysqlColumnComboBox
- 职责：字段选择框（显示字段名）。
- 字段：无（继承 `FXComboBox<MysqlColumn>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数（无参/带 columns） | 设置转换器并加载字段 | `addItems(columns)` |
  | `select(String colName)` | 按名称选中 | 忽略大小写匹配 `getName()` |
  | `getColumnName()` | 获取选中字段名 | `getSelectedItem().getName()` |
- 调用链：无外部调用。

## MysqlColumnListView
- 职责：字段勾选列表视图。
- 字段：无（继承 `FXListView<FXCheckBox>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数（无参/带 columns） | 初始化 | 有参时 `init` |
  | `init(List<MysqlColumn>)` | 初始化字段列表 | 委托 `init(columns,null)` |
  | `init(List<MysqlColumn>,List<String> selectedColumns)` | 初始化并预选 | 每字段生成勾选框，`setProp("column",…)` |
  | `getSelectedColumns()` | 获取选中字段 | 并行流过滤 `isSelected` |
  | `getSelectedColumnNames()` | 获取选中字段名 | 映射 `getName` |
  | `select(Collection<String>)` | 按名称勾选 | 忽略大小写匹配 |
- 调用链：`MysqlColumnListView.getSelectedColumns → MysqlColumn.getName`

## MysqlConditionComboBox
- 职责：mysql 条件下拉框。
- 字段：无（继承 `FXComboBox<MysqlCondition>`）。
- 方法：初始化块设置转换器显示 `getName()`，并加入 `MysqlConditionUtil.conditions()`。
- 调用链：`MysqlConditionComboBox(初始化块) → MysqlConditionUtil.conditions`

## MysqlDefaultValueTextFiled
- 职责：字段默认值输入框，区分枚举与普通类型编辑规则。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `editableFlag` | `boolean` | 是否允许编辑标记 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 选中项变化时按首项决定可编辑性 | |
  | `init(MysqlColumn)` | 初始化 | 委托 `init(column,null)` |
  | `init(MysqlColumn,String)` | 按字段类型初始化 | 支持枚举时用 `column.getValueList()` + NULL；否则加 空/EMPTY STRING/NULL 三项并据默认值决定可编辑/选中 |
  | `getValue()` | 获取默认值 | 编辑态取文本；否则 NULL→null、EMPTY STRING→"" |
- 调用链：`MysqlDefaultValueTextFiled.init → MysqlColumn.supportEnum/getValueList`

## MysqlEngineComboBox
- 职责：引擎选择框。
- 字段：无。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(MysqlClient client)` | 初始化引擎列表 | 遍历 `client.engines()` 大写加入 |
  | `select(String engine)` | 选中引擎 | 转大写 |
  | `isInnoDB()` | 是否 InnoDB | 忽略大小写比较 |
- 调用链：`MysqlEngineComboBox.init → MysqlClient.engines`

## MysqlFieldTextFiled
- 职责：字段多选文本框，通过弹窗勾选字段。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `List<MysqlColumn>` | 可选字段列表 |
  | `selectedColumns` | `List<String>` | 已选字段名称列表 |
  | `popup` | `PopupAdapter` | 字段选择弹窗 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数（无参/带 columns,selected） | 初始化 | 设置动作 `initPopup` |
  | `initPopup()` | 初始化弹窗 | `MysqlColumnFieldPopupController`，提交时回读选中字段名 |
  | `setColumns(List<MysqlColumn>)` | 设置字段列表 | 同步列表视图并刷新文本 |
  | `setSelectedColumns(List<String>)` | 设置已选 | 列表视图 `select` 并刷新文本 |
  | `getSelectedColumns()` | 获取已选（缺省空表） | |
  | `initText()` | 生成文本 | 逗号连接已选字段名，设置文本与 tip |
  | `listView()` | 获取弹窗列表视图 | lookup `#listView` |
- 调用链：`MysqlFieldTextFiled.initPopup → MysqlColumnFieldPopupController`；`setColumns → MysqlColumnListView.init`

## MysqlFiledTypeComboBox
- 职责：字段类型选择框，代理 `DBColumnUtil` 的类型能力查询。
- 字段：无（继承 `FXComboBox<String>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 实例初始化块 | 加载字段类型 | `setItem(DBColumnUtil.fields())` |
  | `supportSize/supportCharset/supportUnsigned/supportDigits/supportAutoIncrement/supportDefaultValue/supportTimestamp/supportGeometry/supportJson/supportEnum` | 类型能力判断 | 均委托 `DBColumnUtil.xxx(getSelectedItem())` |
  | `supportValue()` | 是否支持值 | 恒 false |
  | `exampleValue()` | 示例值 | `DBColumnUtil.exampleValue` |
  | `defaultValue()` | 默认值 | `DBColumnUtil.defaultValue` |
  | `select(String type)` | 选中类型 | 转大写 |
- 调用链：`MysqlFiledTypeComboBox → DBColumnUtil.*`

## MysqlForeignKeyPolicyComboBox
- 职责：外键删除/更新策略下拉框。
- 字段：无。
- 方法：初始化块加入 CASCADE/NO ACTION/RESTRICT/SET NULL。
- 调用链：无。

## MysqlIndexColumnListView
- 职责：索引字段列表视图，每行含字段下拉框与子长度输入框。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columnNames` | `List<String>` | 可选字段名称列表 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(MysqlIndex,List<MysqlColumn>)` | 初始化 | 提取字段名，遍历 `dbIndex.getColumns()` 调 `addColumn` |
  | `addColumn(MysqlIndex.IndexColumn)` | 添加一行 | 组合 `FXComboBox<String>` + `NumberTextField` 于 `FXHBox` |
  | `getColumns()` | 读取索引字段 | 逐行解析下拉值与子长度 |
- 调用链：`MysqlIndexColumnListView.init → MysqlIndex.getColumns`；`getColumns → MysqlIndex.IndexColumn`

## MysqlIndexFieldTextFiled
- 职责：索引字段文本框，通过弹窗编辑索引字段与子长度。
- 字段：
  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbIndex` | `MysqlIndex` | 索引 |
  | `columnList` | `List<MysqlColumn>` | 可选字段列表 |
  | `columns` | `List<MysqlIndex.IndexColumn>` | 索引字段列表 |
  | `popup` | `PopupAdapter` | 弹窗 |
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | 构造函数 `MysqlIndexFieldTextFiled(MysqlIndex,List<MysqlColumn>,List<IndexColumn>)` | 初始化 | |
  | `initPopup()` | 初始化弹窗 | `MysqlIndexFieldPopupController`，提交回读 `listView().getColumns()` |
  | `setColumns(List<IndexColumn>)` | 设置并刷新文本 | |
  | `initText()` | 生成文本 | `列名(子长度)` 逗号连接 |
  | `listView()` | 获取弹窗列表视图 | lookup `#listView` |
  | `getColumns()` | 获取索引字段 | |
- 调用链：`MysqlIndexFieldTextFiled.initPopup → MysqlIndexFieldPopupController`；`initText → MysqlIndex.IndexColumn`

## MysqlIndexMethodComboBox
- 职责：索引方法选择框（空/BTREE/HASH）。
- 字段：无。
- 方法：初始化块加入三项。
- 调用链：无。

## MysqlIndexTypeComboBox
- 职责：索引类型选择框（NORMAL/UNIQUE/FULLTEXT/SPATIAL）。
- 字段：无。
- 方法：初始化块加入四项。
- 调用链：无。

## MysqlRowFormatComboBox
- 职责：行格式下拉框。
- 字段：无。
- 方法：初始化块加入 COMPACT/COMPRESSED/DEFAULT/DYNAMIC/FIXED/REDUNDANT；`select` 转大写。
- 调用链：无。

## MysqlTableComboBox
- 职责：表选择框。
- 字段：无（继承 `FXComboBox<String>`）。
- 方法：
  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(String dbName,MysqlClient client)` | 初始化表列表 | 委托三参重载 |
  | `init(String dbName,String tableName,MysqlClient client)` | 初始化并选中 | `client.selectTables(dbName)` 映射表名 |
- 调用链：`MysqlTableComboBox.init → MysqlClient.selectTables`

## MysqlTriggerPolicyComboBox
- 职责：触发器策略下拉框（BEFORE/AFTER + INSERT/UPDATE/DELETE）。
- 字段：无。
- 方法：初始化块加入六项。
- 调用链：无。

---

## 子包 cn.oyzh.easymysql.fx.view

## MysqlViewAlgorithmComboBox
- 职责：视图算法下拉框（UNDEFINED/MERGE/TEMPTABLE）。
- 字段：无。
- 方法：初始化块加入三项；`select` 转大写。
- 调用链：无。

## MysqlViewCheckOptionComboBox
- 职责：视图检查选项下拉框（NONE/CASCADED/LOCAL）。
- 字段：无。
- 方法：初始化块加入三项；`select` 转大写。
- 调用链：无。

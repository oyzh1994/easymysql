# controller 包代码审查文档

> 包路径：`cn.oyzh.easymysql.controller`
> 说明：窗口/舞台控制器层，继承框架的 `StageController` / `SubStageController` / `ParentStageController`，负责界面事件处理、参数装配与转发到树节点/客户端/处理器。
> 已跳过整文件被注释掉的死代码：`HeaderController.java`、`SettingController.java`、`SearchController.java`、`database/MysqlDatabaseInfoController.java`。
> 注：`MainController` 引用的 `HeaderController2`、`SettingController2` 为现存版本。

---

## 主窗口

## MainController

- 职责：应用主窗口控制器，管理窗口生命周期（记忆尺寸/位置、退出方式）与子控制器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | project | Project | 项目信息（`Project.load()`） |
  | headerController | HeaderController2 | 头部子控制器 |
  | mysqlMainController | MysqlMainController | db 主页子控制器 |
  | setting | MysqlSetting | 配置对象（`MysqlSettingStore.SETTING`） |
  | settingStore | MysqlSettingStore | 配置存储（INSTANCE） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | getSubControllers() | 子控制器 | 返回 `mysqlMainController`、`headerController` |
  | onWindowCloseRequest(event) | 关闭请求 | 按退出方式：直接退出 / 询问 / 托盘 |
  | onSystemExit() | 系统退出 | 按设置保存页面尺寸与位置并 `settingStore.replace`；`TrayManager.destroy()` |
  | onStageInitialize(stage) | 舞台初始化 | 恢复上次保存的窗口尺寸与位置 |
  | onWindowShown(event) | 显示 | 调用父类（标题栏内容加载已注释） |
  | getViewTitle() | 标题 | `db.title.main` |

- 调用链：
  - `MainController.getSubControllers() → MysqlMainController / HeaderController2`
  - `onWindowCloseRequest → StageManager.exit()` 或 `TrayManager.show()`

## HeaderController2

- 职责：主页头部控制器，提供数据传输/设置/关于/退出/布局切换等入口。
- 字段：无（继承 `SubStageController`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | transport() | 数据传输 | `MysqlDataTransportController` 已开则前置，否则 `showStage` |
  | setting() | 设置 | `SettingController2` 已开则前置，否则 `showStage` |
  | about() | 关于 | `StageManager.showStage(AboutController.class)` |
  | quit() | 退出 | 确认后 `StageManager.exit()` |
  | tool() | 工具箱 | 空实现 |
  | layout1()/layout2() | 布局切换 | `MysqlEventUtil.layout1()/layout2()` |

- 调用链：`HeaderController2.transport() → StageManager.showStage(MysqlDataTransportController.class)`

## MysqlMainController

- 职责：db 主页控制器，承载左侧连接树与右侧标签面板，处理树选中、布局与退出。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | setting | MysqlSetting | 配置对象 |
  | info | MysqlConnect | 当前激活的 db 信息 |
  | tree | DBTreeView | 左侧 db 树 |
  | tabPaneLeft | FXTabPane | 左侧组件 |
  | tabPane | MysqlTabPane | db 切换面板 |
  | connectController | ConnectController | 连接子控制器 |
  | messageController | MessageController | 消息子控制器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | onInfoUpdate(MysqlConnectUpdatedEvent) | 连接变更 | 若为当前连接则更新窗口标题 |
  | flushViewTitle(info) | 刷新标题 | `stage.appendTitle` 或 `restoreTitle` |
  | treeItemChanged(TreeItem) | 树节点变化 | 按类型取连接信息刷新标题 |
  | onWindowShown(event) | 显示 | 调用父类（事件注册已注释） |
  | onWindowHidden(event) | 隐藏 | `tree.closeConnects()`、保存拉伸、取消 F5 监听 |
  | resizeMainLeft(newWidth) | 左侧重布局 | 设置左侧宽与新布局 |
  | onSystemExit() | 退出 | 保存页面拉伸 |
  | savePageResize() | 保存拉伸 | 按设置保存（持久化已注释） |
  | bindListeners() | 绑定 | 主体逻辑已注释 |
  | treeItemChanged(MysqlTreeItemChangedEvent) | 树节点变化事件 | 按节点类型取连接信息刷新标题 |
  | positionNode() | 定位节点 | `tree.scrollTo(tree.getSelectedItem())` |
  | layout1(Layout1Event)/layout2(Layout2Event) | 布局事件 | 显示/隐藏左侧并调整右侧布局 |
  | getSubControllers() | 子控制器 | 返回 `connectController`、`messageController` |

- 调用链：
  - `MysqlTreeItemChangedEvent → MysqlMainController.treeItemChanged → flushViewTitle`
  - `Layout1Event/Layout2Event → MysqlMainController.layout1/layout2`

## ConnectController

- 职责：左侧连接面板子控制器，处理连接树选中、排序、增删导入导出。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | tree | DBTreeView | 左侧 db 树 |
  | sortPane | SortSVGPane | 排序开关组件 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | positionNode() | 定位节点 | `tree.scrollTo(tree.getSelectedItem())` |
  | onWindowHidden(event) | 隐藏 | `tree.closeConnects()`、取消 F5 监听 |
  | bindListeners() | 绑定 | 树选中事件、文件拖拽、F5 刷新 |
  | addConnect() | 新增连接 | `MysqlEventUtil.addConnect()` |
  | sortTree() | 排序树 | 依据 `sortPane.isAsc()` 切换升降序 |
  | importConnect()/exportConnect() | 导入/导出连接 | `tree.root().importConnect()/exportConnect()` |

- 调用链：
  - `ConnectController.tree 选中 → MysqlEventUtil.treeItemChanged`
  - `ConnectController.importConnect → DBRootTreeItem.importConnect`

## MessageController

- 职责：消息面板子控制器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | msgArea | DBMsgTextArea | 消息文本框 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | clearMsg() | 清空消息 | `msgArea.clear()` |

- 调用链：`MessageController.clearMsg → DBMsgTextArea.clear`

## AboutController

- 职责：关于窗口控制器，展示项目名称/版本/版权信息。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | name/type/version/updateDate/copyright | FXText | 各信息展示 |
  | project | Project | 项目信息 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | onWindowShown(event) | 显示 | 回填项目信息、追加标题、Esc 关闭 |
  | getViewTitle() | 标题 | `base.title.about` |

- 调用链：`HeaderController2.about → StageManager.showStage(AboutController)`

## SettingController2

- 职责：应用设置窗口控制器，配置退出方式/页面记忆/主题/字体/区域/透明度并持久化。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | root | SettingMainPane | 主面板 |
  | exitMode/exitMode0/1/2 | 控件 | 退出方式 |
  | pageSize/pageResize/pageLocation | FXCheckBox | 页面记忆项 |
  | keyLoadLimit | NumberTextField | 键加载限制 |
  | theme/bgColor/fgColor/accentColor | 控件 | 主题与颜色 |
  | bgColorBox/fgColorBox/accentColorBox | FXHBox | 颜色组件容器 |
  | fontSize/fontWeight/fontFamily | 控件 | 通用字体 |
  | editorFontSize/editorFontWeight/editorFontFamily | 控件 | 编辑器字体 |
  | terminalFontSize/terminalFontWeight/terminalFontFamily | 控件 | 终端字体 |
  | locale | LocaleComboBox | 区域 |
  | opacity/titleBarOpacity | FXSlider | 透明度 |
  | setting | MysqlSetting | 配置对象 |
  | settingStore | MysqlSettingStore | 配置存储 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | onWindowShowing(event) | 显示前 | 依据设置回填各控件 |
  | saveSetting() | 保存设置 | 收集控件值写入 `setting`，`settingStore.update` 后应用 I18n/字体/主题/透明度；需重启时 `MysqlProcessUtil.restartApplication()` |
  | checkConfigForRestart(locale) | 重启检查 | 区域变化返回重启提示 |
  | bindListeners() | 绑定 | 颜色框禁用联动、主题切换回填颜色 |
  | onWindowShown(event) | 显示 | 构建左侧设置树、选中 mysql、Esc 关闭 |
  | resetFgColor/resetBgColor/resetAccentColor | 重置颜色 | 取主题默认色 |
  | resetLocale/resetOpacity/resetTitleBarOpacity | 重置项 | 恢复默认 |
  | resetFontFamily/resetFontSize/resetFontWeight（及 editor/terminal 变体） | 重置字体 | 取 `AppSetting` 默认值 |
  | getViewTitle() | 标题 | `I18nHelper.settingTitle()` |

- 调用链：
  - `saveSetting() → MysqlSettingStore.INSTANCE.update → ThemeManager/FontManager/OpacityManager.apply`
  - `HeaderController2.setting → SettingController2`

---

## connect

## MysqlConnectAddController

- 职责：新增数据库连接窗口控制器（含 SSH 隧道配置）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | readonly/tabPane/name/type/user/password/remark | 控件 | 基本信息 |
  | hostIp/hostPort/connectTimeOut | 控件 | 主机与超时 |
  | sshTab/sshForward/sshHost/sshPort/sshTimeout/sshUser/sshPassword | 控件 | SSH 配置 |
  | group | MysqlGroup | 目标分组 |
  | connectStore | MysqlConnectStore | 连接存储 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | getHost() | 拼接主机 | 校验端口/ip 后返回 `ip:port` |
  | getSSHConfig() | 构造 SSH 配置 | `MysqlSSHConfig` |
  | testConnect() | 测试连接 | 组装 `MysqlConnect` → `DBConnectUtil.testConnect` |
  | add() | 新增连接 | 组装并 `connectStore.replace` → `MysqlEventUtil.connectAdded` |
  | bindListeners() | 绑定 | SSH 开关控制 SSH 面板 |
  | onWindowShown(event) | 显示 | 取 `group` 属性、Esc 关闭 |
  | getViewTitle() | 标题 | `I18nHelper.connectAddTitle()` |

- 调用链：
  - `add() → MysqlConnectStore.replace → MysqlEventUtil.connectAdded → DBTreeView.infoAdded`
  - `onWindowShown → this.group = getProp("group")`

## MysqlConnectUpdateController

- 职责：修改数据库连接窗口控制器（含 SSH 配置回填与保存）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | mysqlConnect | MysqlConnect | 待编辑连接（来自 `info` 属性） |
  | readonly/tabPane/name/type/user/password/remark | 控件 | 基本信息 |
  | hostIp/hostPort/connectTimeOut/executeTimeOut | 控件 | 主机与超时 |
  | sshTab/sshForward/sshHost/sshPort/sshTimeout/sshUser/sshPassword | 控件 | SSH 配置 |
  | connectStore | MysqlConnectStore | 连接存储 |
  | sshConfigStore | MysqlSSHConfigStore | SSH 配置存储 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | getHost() | 拼接主机 | 同新增 |
  | getSSHConfig() | 构造 SSH 配置 | `MysqlSSHConfig` |
  | testConnect() | 测试连接 | 组装带 id 的 `MysqlConnect` → `DBConnectUtil.testConnect` |
  | update() | 修改连接 | 更新字段后 `connectStore.replace` → `MysqlEventUtil.connectUpdated` |
  | bindListeners() | 绑定 | hostIp 含 `:` 时自动拆分 ip/端口；SSH 开关控制面板 |
  | onWindowShown(event) | 显示 | 取 `info` 属性，回填连接与 SSH 配置 |
  | getViewTitle() | 标题 | `I18nHelper.connectUpdateTitle()` |

- 调用链：
  - `update() → MysqlConnectStore.replace → MysqlEventUtil.connectUpdated → DBTreeView.infoUpdated`
  - `onWindowShown → sshConfigStore.getByIid(id)`

---

## database / table / view

## MysqlDatabaseAddController

- 职责：新增数据库窗口控制器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | name | ClearableTextField | 库名称 |
  | charset | DBCharsetComboBox | 字符集 |
  | collation | DBCollationComboBox | 排序方式 |
  | connectItem | DBConnectTreeItem | 连接树节点 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | add() | 新增库 | 校验名称与重复，组装 `DBDatabase` → `connectItem.createDatabase`，`MysqlEventUtil.databaseAdded` |
  | bindListeners() | 绑定 | 字符集变化初始化排序 |
  | onWindowShown(event) | 显示 | 取 `connectItem`，初始化字符集 |
  | getViewTitle() | 标题 | `I18nHelper.addDatabase()` |

- 调用链：`add() → DBConnectTreeItem.createDatabase → MysqlEventUtil.databaseAdded`

## MysqlDatabaseUpdateController

- 职责：编辑数据库窗口控制器（字符集/排序）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | name | ReadOnlyTextField | 库名称 |
  | charset | DBCharsetComboBox | 字符集 |
  | collation | DBCollationComboBox | 排序方式 |
  | database | DBDatabase | 库对象 |
  | connectItem | DBConnectTreeItem | 连接树节点 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | save() | 保存 | 比较差异组装 `DBDatabase` → `connectItem.alterDatabase`，`MysqlEventUtil.databaseUpdated` |
  | bindListeners() | 绑定 | 字符集变化初始化排序 |
  | onWindowShown(event) | 显示 | 取 `database`/`connectItem` 属性并回填 |
  | getViewTitle() | 标题 | `I18nHelper.updateDatabase()` |

- 调用链：`save() → DBConnectTreeItem.alterDatabase → MysqlEventUtil.databaseUpdated`

## MysqlTableInfoController

- 职责：表信息窗口控制器，只读展示表元数据。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | tableName/tableEngine/tableCharset/tableCollation | ReadOnlyTextField | 基本信息 |
  | tableRowFormatBox/tableRowFormat | FXVBox/ReadOnlyTextField | 行格式 |
  | tableAutoIncrementBox/tableAutoIncrement | FXVBox/ReadOnlyTextField | 自动递增 |
  | tableComment | ReadOnlyTextArea | 注释 |
  | tableDefinition | Editor | 建表定义 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | onWindowShown(event) | 显示 | 取 `tableItem` → `MysqlTable` 回填各字段；InnoDB 显示行格式、自增显示自增值 |
  | onStageInitialize(stage) | 舞台初始化 | 行格式/自增组件 `managedBindVisible` |
  | getViewTitle() | 标题 | `I18nHelper.tableInfo()` |

- 调用链：`MysqlTableTreeItem.tableInfo → MysqlTableInfoController.onWindowShown`

## MysqlViewInfoController

- 职责：视图信息窗口控制器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | viewName | ReadOnlyTextField | 视图名 |
  | viewComment | ReadOnlyTextArea | 注释 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | onWindowShown(event) | 显示 | 取 `item` → `MysqlView` 回填名称与注释 |
  | getViewTitle() | 标题 | `I18nHelper.viewInfo()` |

- 调用链：`MysqlViewTreeItem（viewInfo） → MysqlViewInfoController`

---

## data

## MysqlDataDumpController

- 职责：数据转储窗口控制器，按库/表将结构或全部数据转储为 SQL。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dbInfo/dbClient | MysqlConnect/MysqlClient | 连接与客户端 |
  | dumpType | int | 1 库、2 表 |
  | stopDumpBtn/dumpStatus/dumpMsg | 控件 | 状态与消息 |
  | connect/database/tableBox/table/dataType | 控件 | 展示与类型 |
  | execTask | Thread | 转储任务线程 |
  | counter | Counter | 计数器 |
  | dumpFile | File | 转储文件 |
  | dumpHandler | DataDumpHandler | 转储处理器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | checkDumpFile() | 检查/选择文件 | 生成默认名并 `FileChooserHelper.save` |
  | doDump() | 执行转储 | 建 `DataDumpHandler` 并设参数 → `ThreadUtil.start` 执行 `doDump` |
  | stopDump() | 结束转储 | 中断线程与处理器 |
  | onWindowShown(event) | 显示 | 取属性回填连接/库/表，表模式显示表框 |
  | onWindowHidden(event) | 隐藏 | `stopDump()` |
  | updateStatus(extraMsg) | 更新状态 | 计数器格式化 |
  | onStageInitialize(stage) | 舞台初始化 | `tableBox.managedBindVisible` |
  | bindListeners() | 绑定 | 数据类型变化清空文件 |
  | getViewTitle() | 标题 | `base.title.dump` |

- 调用链：`doDump() → DataDumpHandler.doDump() → MysqlClient`

## MysqlDataExportController

- 职责：数据导出窗口控制器（五步向导），按文件类型导出表数据。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | step1..step5 | FXVBox | 五个步骤面板 |
  | tableCombobox/tableColumns/exportTableView | 控件 | 表选择与字段 |
  | fileType | FXToggleGroup | 文件类型 |
  | dbClient | MysqlClient | 客户端 |
  | datePreview/dateFormat | 控件 | 日期格式 |
  | recordSeparator/fieldSeparator/txtIdentifier | 控件 | 分隔符/识别符 |
  | includeFields/fieldToAttr/earlyVersion | FXCheckBox | 导出选项 |
  | stopExportBtn/exportStatus/exportMsg | 控件 | 状态与消息 |
  | execTask/counter/exportHandler | 任务/计数/处理器 | 导出执行 |
  | dbName/tableName | String | 库/表名 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | doExport() | 执行导出 | 建 `DataExportHandler` 并设类型/表/分隔符等 → `ThreadUtil.start` 执行 `doExport` |
  | stopExport() | 结束导出 | 中断线程与处理器 |
  | bindListeners() | 绑定 | 表下拉变化初始化字段列、日期格式预览 |
  | flushDatePreview() | 日期预览 | `DateUtil.format` |
  | onWindowShown(event) | 显示 | 取库/客户端/表名属性 |
  | onWindowHidden(event) | 隐藏 | `stopExport()` |
  | updateStatus(extraMsg) | 状态 | 计数器 |
  | onStageInitialize(stage) | 舞台初始化 | 各步骤 `managedBindVisible` |
  | showStep1..showStep5() | 向导切换 | 按文件类型显示/隐藏组件；`showStep2` 加载表、`showStep3` 加载字段 |
  | selectAllTable/unselectAllTable/selectAllFiled/unselectAllField | 全选/取消 | 遍历列表设置选中 |
  | getViewTitle() | 标题 | `I18nHelper.exportTitle()` |

- 调用链：`doExport() → DataExportHandler.doExport() → MysqlClient`

## MysqlDataImportController

- 职责：数据导入窗口控制器（六步向导），将多种格式文件导入表。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | step1..step6 | FXVBox | 六个步骤面板 |
  | importFileTableView | DataImportFileTableView | 导入文件列表 |
  | sourceTableCombobox/targetTableCombobox | 控件 | 源/目标表 |
  | fileType | FXToggleGroup | 文件类型 |
  | dbClient | MysqlClient | 客户端 |
  | stopImportBtn/importStatus/importMsg | 控件 | 状态与消息 |
  | recordLabel/attrToColumn/columnIndex/dataStartIndex | 控件 | 结构化选项 |
  | datePreview/dateFormat | 控件 | 日期格式 |
  | recordSeparator/fieldSeparator/txtIdentifier | 控件 | 分隔符/识别符 |
  | importMode | FXToggleGroup | 导入模式 |
  | execTask/counter/importHandler | 任务/计数/处理器 | 导入执行 |
  | dbName | String | 库名 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | doImport() | 执行导入 | 建 `DataImportHandler` 并设文件/分隔符/模式等 → `ThreadUtil.start` 执行 `doImport` |
  | stopImport() | 结束导入 | 中断线程与处理器 |
  | bindListeners() | 绑定 | 日期预览、目标表变化、文件列表初始化 |
  | initFileTable() | 初始化文件 | 为文件设置库名/客户端 |
  | flushDatePreview() | 日期预览 | `DateUtil.format` |
  | onWindowShown/onWindowHidden | 显示/隐藏 | 取属性；隐藏时 `stopImport()` |
  | updateStatus(extraMsg) | 状态 | 计数器 |
  | onStageInitialize(stage) | 舞台初始化 | 各步骤 `managedBindVisible` |
  | showStep1..showStep6() | 向导切换 | 按文件类型显示/隐藏组件；`showStep4` 加载源/目标表 |
  | addFile()/deleteFile() | 增删文件 | 选择文件加入列表 / 移除选中 |
  | getViewTitle() | 标题 | `I18nHelper.importTitle()` |

- 调用链：`doImport() → DataImportHandler.doImport() → MysqlClient`

## MysqlDataTransportController

- 职责：数据传输窗口控制器（三步向导），将源库的表/视图/函数/过程/触发器/事件传输到目标库。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | step1..step3 | FXVBox | 三个步骤面板 |
  | sourceInfo/targetInfo | DBInfoComboBox | 源/目标连接 |
  | sourceDatabase/targetDatabase | DBDatabaseComboBox | 源/目标库 |
  | sourceInfoName/sourceDatabaseName/targetInfoName/targetDatabaseName | FXLabel | 名称展示 |
  | sourceHost/targetHost/sourceVersion/targetVersion/sourceType/targetType | FXLabel | 主机/版本/类型 |
  | sourceClient/targetClient | MysqlClient | 源/目标客户端 |
  | stopTransportBtn/transportStatus/transportMsg | 控件 | 状态与消息 |
  | tablePane/viewPane/functionPane/procedurePane/triggerPane/eventPane | FXTab | 各类面板 |
  | tableList/viewList/functionList/procedureList/triggerList/eventList | 列表组件 | 各类对象列表 |
  | execTask/counter/transportHandler | 任务/计数/处理器 | 传输执行 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | doTransport() | 执行传输 | 依方言建 `DataTransportHandler`，设置源/目标客户端与各类对象 → `ThreadUtil.start` 执行 `doTransport` |
  | stopTransport() | 结束传输 | 中断线程与处理器 |
  | bindListeners() | 绑定 | 源/目标连接与库变化时建客户端、初始化库列表、清列表；列表选中变化刷新面板文字 |
  | onWindowShown/onWindowHidden | 显示/隐藏 | 隐藏时 `stopTransport()` |
  | updateStatus(extraMsg) | 状态 | 计数器 |
  | onStageInitialize(stage) | 舞台初始化 | 各步骤 `managedBindVisible` |
  | showStep1/showStep2/showStep3 | 向导切换 | `showStep2` 校验源/目标并加载各类对象列表 |
  | clearList() | 清空列表 | 清空各对象列表 |
  | flushPaneText(name) | 刷新面板文字 | 显示 `(已选/总数)` |
  | getViewTitle() | 标题 | `I18nHelper.transportTitle()` |

- 调用链：
  - `bindListeners 源连接变化 → DBClientUtil.newClient + client.start → sourceDatabase.init`
  - `showStep2 → sourceClient.views/events/selectTables/triggers/functions/procedures`
  - `doTransport() → DataTransportHandler.doTransport()`

## MysqlRunSqlFileController

- 职责：运行 SQL 文件窗口控制器，批量执行文件中的 SQL。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dbInfo/dbClient | MysqlConnect/MysqlClient | 连接与客户端 |
  | stopSqlFileBtn/execStatus/execMsg | 控件 | 状态与消息 |
  | connect/database/continueWithErrors/file | 控件 | 展示与选项 |
  | execTask | Thread | 执行线程 |
  | counter | Counter | 计数器 |
  | sqlFileHandler | DataRunSqlFileHandler | SQL 文件处理器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | checkSqlFile() | 检查文件 | 未选择则告警 |
  | runSqlFile() | 执行 SQL 文件 | 建 `DataRunSqlFileHandler` 设参数 → `ThreadUtil.start` 执行 `runSqlFile` |
  | stopSqlFile() | 结束执行 | 中断线程与处理器 |
  | onWindowShown/onWindowHidden | 显示/隐藏 | 取属性；隐藏时 `stopSqlFile()` |
  | updateStatus(extraMsg) | 状态 | 计数器 |
  | onStageInitialize(stage) | 舞台初始化 | 设置文件过滤器为 sql |
  | getViewTitle() | 标题 | `base.runSqlFile` |

- 调用链：`runSqlFile() → DataRunSqlFileHandler.runSqlFile() → MysqlClient`

# query 包代码审查

> 包路径：`cn.oyzh.easymysql.query`
> 覆盖类数：7

## MysqlQueryToken
- 职责：SQL 提示词（token）模型，记录光标所在词的内容、位置与前置分隔符。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | endIndex | int | 结束位置 |
  | startIndex | int | 开始位置 |
  | content | String | 内容 |
  | token | Character | 分隔符（1 空格 / 2 `.` / 3 `` ` ``） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean isEmpty()/isNotEmpty()` | 内容是否为空 | `StringUtil.isEmpty/isNotEmpty(content)` |
  | `boolean isPossibilityKeyword()` | 是否可能关键字 | token 为空格、`\n` 或 `\0` |
  | `isPossibilityTable/isPossibilityView/isPossibilityFunction/isPossibilityProcedure/isPossibilityColumn()` | 是否可能对应对象 | 均返回 true |
  | `boolean isPossibilityDatabase()` | 是否可能数据库 | token 为 `` ` `` 或空格 |
  | getter/setter（endIndex/startIndex/content/token） | 访问器 | 读写字段 |

- 调用链：`MysqlQueryTokenAnalyzer.currentToken → MysqlQueryToken.setToken/setContent`
- 调用链：`MysqlQueryUtil.initPrompts → token.isPossibilityKeyword/isPossibilityTable/...`

## MysqlQueryTokenAnalyzer
- 职责：提示词分析器（单例），根据文本与光标位置反向扫描得到当前 token。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlQueryTokenAnalyzer | 单例实例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlQueryToken currentToken(String content, int currentIndex)` | 计算当前 token | 校验入参后截取到 currentIndex，`ArrayUtil.reverse` 反向扫描找 `\n`/` `/`` ` ``/`.` 作为分隔符；无分隔符则 tokenType 记为 `\0`；据此设置 token 的起点、终点并 trim 内容 |

- 调用链：`MysqlQueryPromptPopup.prompt → MysqlQueryTokenAnalyzer.INSTANCE.currentToken → MysqlQueryToken`

## MysqlQueryPromptItem
- 职责：查询提示项模型，表示一条候选提示（库/表/字段/关键字/视图/函数/过程）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | type | byte | 类型（1 database / 2 table / 3 column / 4 keyword / 5 view / 6 function / 7 procedure） |
  | content | String | 内容 |
  | correlation | double | 相关度 |
  | extContent | String | 额外内容（库名等） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isDatabaseType/isTableType/isColumnType/isKeywordType/isViewType/isFunctionType/isProcedureType()` | 类型判定 | 比较 type 与常量 |
  | `String wrapContent()` | 包装内容 | 字段类型时 `DBUtil.wrap(content, MYSQL)`，否则原样返回 |
  | getter/setter（type/content/correlation/extContent） | 访问器 | 读写字段 |

- 调用链：`MysqlQueryUtil.initPrompts → MysqlQueryPromptItem.setType/setContent/setCorrelation`
- 调用链：`MysqlQueryPromptPopup.autoComplete → item.wrapContent → DBUtil.wrap`

## MysqlQueryPromptPopup (extends FXPopup)
- 职责：SQL 查询提示弹框，监听按键在光标处展示候选提示并支持选择自动补全。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | PROMPT_CODES | static List<KeyCode> | 触发提示的按键集合（字母/数字） |
  | UPDATE_CODES | static List<KeyCode> | 需要更新/隐藏的按键集合（BACK_SPACE/DELETE/SPACE） |
  | onItemSelected | Consumer<MysqlQueryPromptItem> | 选中事件 |
  | token | MysqlQueryToken | 当前 token |
  | promptFlag | AtomicInteger | 提示标志位（用于延迟任务去重） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlQueryPromptPopup()` | 构造 | 设置 autoFix/autoHide，`initContent` 并应用主题 |
  | `protected void initContent()` | 初始化列表组件 | 创建 `MysqlQueryPromptListView`，绑定点选事件为 `pickItem`+hide |
  | `MysqlQueryPromptListView listView()` | 取列表组件 | 取内容首个元素 |
  | `synchronized boolean initPrompts(MysqlQueryToken)` | 初始化候选 | `MysqlQueryUtil.initPrompts(token, 0.5f)` 后填充列表，返回是否非空 |
  | `void prompt(MysqlQueryEditor area, KeyEvent event)` | 提示主逻辑 | 常规键→隐藏；弹框已显示时处理 DOWN/UP/ENTER；更新键或非提示键→隐藏；否则取光标 token，`TaskManager.startDelay(30ms)` 去抖后 `initPrompts` 决定 show/hide |
  | `private void show(MysqlQueryEditor)` | 显示弹框 | `RenderService.submitFXLater` 取光标边界定位 |
  | `void hide()` | 隐藏 | 显示中则 `FXUtil.runWait(super::hide)`，清空 token |
  | `void autoComplete(MysqlQueryEditor editor, MysqlQueryPromptItem item)` | 自动补全 | 用 token 起止位置 `editor.replaceText(..., item.wrapContent())` |
  | `private void pickItem()` | 触发选中 | 取选中项回调 onItemSelected |
  | `private boolean isGeneralKeyEvent(KeyEvent)` | 是否常规快捷键 | Ctrl+S/X/V/C/A/Z/Y/SLASH |
  | `getOnItemSelected/setOnItemSelected` | 访问器 | 读写回调用 |

- 调用链：`MysqlQueryEditor(按键) → MysqlQueryPromptPopup.prompt → MysqlQueryTokenAnalyzer.currentToken → MysqlQueryUtil.initPrompts → listView.init`
- 调用链：`listView 点选 → onItemSelected → MysqlQueryPromptPopup.autoComplete → MysqlQueryEditor.replaceText`

## MysqlQueryPromptListView (extends FXListView<FXHBox>)
- 职责：提示候选列表组件，渲染各类提示项的图标/文本并处理选中与背景高亮。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | currentPickIndex | int | 当前选中索引（volatile） |
  | onItemPicked | Runnable | 节点选中事件 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void select(int index)` | 选中指定索引 | 越界修正后 `super.select` 并 `applyBackground` |
  | `synchronized void pickNext()/pickPrev()` | 选中下一个/上一个 | 基于 currentPickIndex ±1 |
  | `synchronized boolean hasPicked()` | 是否有选中项 | 选中项非空且 currentPickIndex != -1 |
  | `MysqlQueryPromptItem getPickedItem()` | 获取选中提示项 | 从选中 FXHBox 的 prop "item" 取值，并清背景 |
  | `private void applyBackground(int)` | 应用背景色 | 清除旧项背景，高亮新项为 DEEPSKYBLUE |
  | `void init(List<MysqlQueryPromptItem>)` | 初始化列表 | 每项构建 FXHBox（提示标签 + 额外标签），setProp("item") |
  | `private SVGLabel initPromptLabel(MysqlQueryPromptItem)` | 构建提示标签 | 依类型选 DatabaseSVGGlyph/KeywordsSVGGlyph/TableSVGGlyph/ColumnSVGGlyph/ViewSVGGlyph/FunctionSVGGlyph/ProcedureSVGGlyph |
  | `private FXLabel initExtLabel(MysqlQueryPromptItem)` | 构建额外标签 | 表/视图/字段类型显示 extContent |
  | `private void initBox(FXHBox)` | 初始化项容器 | 设高/内边距/手型光标，单击高亮、点选触发 onItemPicked |
  | `getOnItemPicked/setOnItemPicked` | 访问器 | 读写回调 |

- 调用链：`MysqlQueryPromptPopup.initPrompts → MysqlQueryPromptListView.init → initPromptLabel/initExtLabel`

## MysqlQueryEditor (extends SqlEditor)
- 职责：SQL 查询编辑器，集成提示弹框、注释切换、SQL 美化与右键运行。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | promptPopup | MysqlQueryPromptPopup | 提示词组件（final，初始化即创建） |
  | dialect | DBDialect | 方言 |
  | runCallback | Runnable | 运行回调 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | （实例初始化块） | 绑定交互 | 鼠标释放/失焦隐藏弹框；设置选中回调 autoComplete；按键释放时 Ctrl+/ 触发 doComment，否则 `promptPopup.prompt` |
  | `private void doComment()` | 注释/反注释 | 无内容时插入 `-- `；否则按选区行判断是否全为注释（决定加/去注释），重写文本并修正选区 |
  | `void pretty() throws Exception` | 美化 SQL | `DBSqlParser.prettySql(sql, dialect)` 后 setText |
  | `getDialect/setDialect` | 访问器 | 读写方言 |
  | `List<? extends MenuItem> getMenuItems()` | 右键菜单 | 有选中内容时加"运行"项，再拼父类菜单 |
  | `setRunCallback(Runnable)` / `protected void run()` | 运行回调 | run 中执行 runCallback |

- 调用链：`MysqlQueryEditor(按键) → promptPopup.prompt → MysqlQueryTokenAnalyzer.currentToken`
- 调用链：`MysqlQueryEditor.pretty → DBSqlParser.prettySql`

## MysqlQueryUtil
- 职责：查询提示词工具类，缓存库/表/视图/函数/过程索引并计算候选提示的相关度。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | indexStatus | int | 索引状态（0 未初始化 / 1 初始化中 / 2 已初始化） |
  | DB_KEYWORDS | static List<String> | 关键字（static 块装载 DML/DDL/查询/函数关键字） |
  | DB_DATABASES | static List<DBDatabase> | 库索引 |
  | DB_TABLES | static List<MysqlTable> | 表索引 |
  | DB_VIEWS | static List<MysqlView> | 视图索引 |
  | DB_FUNCTIONS | static List<MysqlFunction> | 函数索引 |
  | DB_PROCEDURES | static List<MysqlProcedure> | 过程索引 |
  | DB_COLUMNS | static List<MysqlColumn> | 字段索引 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static List<String> getKeywords()` 及 `getDatabases/getTables/getViews/getFunctions/getProcedures/getColumns()` | 获取各类索引 | 返回对应静态列表 |
  | `static void updateIndex(MysqlClient[, boolean async])` | 更新索引 | 仅当 indexStatus==0 时执行：清空各列表，`client.databases()` 装载库，再对非内部库逐个 `selectTables/views/functions/procedures` 装载；async 时 `ThreadUtil.start` |
  | `static List<MysqlQueryPromptItem> initPrompts(MysqlQueryToken token, float minCorr)` | 计算候选提示 | 按 token 可能性分别对关键字/库/表/视图/函数/过程/字段计算 `TextUtil.clacCorr`，超阈值者生成 PromptItem 并加入 CopyOnWriteArrayList；`ThreadUtil.submit(tasks)` 并行执行；最后按相关度排序并反转返回 |

- 调用链：`MysqlQueryUtil.updateIndex → MysqlClient.databases/selectTables/views/functions/procedures`
- 调用链：`MysqlQueryPromptPopup.initPrompts → MysqlQueryUtil.initPrompts → TextUtil.clacCorr`

# popups 包代码审查

> 包路径：`cn.oyzh.easymysql.popups`
> 覆盖类数：9
> 说明：以下均为 JavaFX 弹窗控制器，继承 `PopupController`，通过 `@PopupAttribute(value = FXConst.POPUP_PATH + "*.fxml")` 绑定 FXML；`@FXML` 字段为界面控件，`getProp/setProp` 用于跨窗口传参。

## MysqlFieldInfoPopupController (extends PopupController)
- 职责：字段信息弹窗，只读展示字段的名称/类型/长度/值/默认值/注释与标签。
- 字段（@FXML）：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | name | TextField | 名称 |
  | size | NumberTextField | 字段长 |
  | type | TextField | 类型 |
  | value | TextField | 值 |
  | comment | TextArea | 注释 |
  | defaultValue | TextField | 默认值 |
  | sizeBox / tagsBox / valueBox / defaultValueBox | FXHBox | 各分组的容器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void close()` | 关闭窗口 | `closeWindow()` |
  | `void onWindowShowing(WindowEvent)` | 显示时填充 | 从 prop 取 `column`，按 supportSize/supportValue/supportDefaultValue 填值并显示对应 box；`DBNodeUtil.generateTags` 生成标签并设置边距；填 name/type/comment |
  | `void onPopupInitialize(PopupAdapter)` | 初始化 | 各 box `managedBindVisible()` |

- 调用链：`onWindowShowing → DBNodeUtil.generateTags → MysqlColumn.isNullable/isAutoIncrement/...`

## MysqlColumnEnumPopupController (extends PopupController)
- 职责：字段枚举值弹窗，以可编辑文本框列表增删枚举值。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | onSubmit | Runnable | 提交回调 |
  | listView | FXListView<ClearableTextField> | 枚举值列表（@FXML） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void submit()` | 提交 | 执行 onSubmit 后 close；异常 `MessageBox.exception` |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `@FXML void addRow()` / `deleteRow()` | 增/删行 | `listView.addItem(createNode(""))` / `removeSelectedItem` |
  | `private ClearableTextField createNode(String)` | 创建输入节点 | 构造 ClearableTextField 并设样式、选中行 |
  | `void onWindowShowing(WindowEvent)` | 显示 | 取 onSubmit 与 values，逐值创建行 |
  | `void onPopupInitialize(PopupAdapter)` | 初始化 | 设置 cellFactory 渲染 ClearableTextField |

- 调用链：`onWindowShowing → listView.addItem → createNode`

## MysqlColumnFieldPopupController (extends PopupController)
- 职责：字段列表选择弹窗，用于从字段列表中多选字段。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | onSubmit | Runnable | 提交回调 |
  | listView | MysqlColumnListView | 字段列表（@FXML） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void submit()` | 提交 | 执行 onSubmit 后 close |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `@FXML void moveUpRow()/moveDownRow()` | 行上移/下移 | `ListViewUtil.moveUp/moveDown` |
  | `void onWindowShowing(WindowEvent)` | 显示 | 取 onSubmit/columns/selectedColumns，`listView.init` 与 `listView.select` |

- 调用链：`onWindowShowing → MysqlColumnListView.init → select(selectedColumns)`

## MysqlIndexFieldPopupController (extends PopupController)
- 职责：索引字段配置弹窗，增删/排序索引包含的列。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | onSubmit | Runnable | 提交回调 |
  | listView | MysqlIndexColumnListView | 索引列列表（@FXML） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void submit()` | 提交 | 执行 onSubmit 后 close |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `@FXML void addRow()` / `deleteRow()` | 增/删行 | `addColumn(new MysqlIndex.IndexColumn())` / `removeSelectedItem` |
  | `@FXML void moveUpRow()/moveDownRow()` | 行上移/下移 | `ListViewUtil.moveUp/moveDown` |
  | `void onWindowShowing(WindowEvent)` | 显示 | 取 dbIndex/columnList，`listView.init(dbIndex, columnList)` |

- 调用链：`onWindowShowing → MysqlIndexColumnListView.init`

## MysqlPageSettingPopupController (extends PopupController)
- 职责：分页设置弹窗，设置并保存记录每页限制。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | limit | NumberTextField | 每页限制（@FXML） |
  | setting | MysqlSetting | 当前设置（final，取自 MysqlSettingStore.SETTING） |
  | settingStore | MysqlSettingStore | 设置存储（final） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void apply()` | 应用设置 | 取 limit 值 → `setting.setRecordPageLimit` → `settingStore.update` → `submit(limit)` → close |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `void onWindowShowing(WindowEvent)` | 显示 | limit 设为 `setting.getRecordPageLimit()` |
  | `void onWindowHidden(WindowEvent)` | 隐藏 | limit 置 null |

- 调用链：`apply → MysqlSetting.setRecordPageLimit → MysqlSettingStore.update`

## MysqlRecordEnumPopupController (extends PopupController)
- 职责：数据枚举弹窗，以复选框列表从全部枚举值中选择值。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | onSubmit | Runnable | 提交回调 |
  | listView | FXListView<CheckBox> | 枚举值复选列表（@FXML） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void submit()` | 提交 | 执行 onSubmit 后 close |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `void onWindowShowing(WindowEvent)` | 显示 | 对 allValues 逐个建 FXCheckBox，values 包含则勾选后加入列表 |
  | `void onPopupInitialize(PopupAdapter)` | 初始化 | 仅调父类 |

- 调用链：`onWindowShowing → new FXCheckBox → listView.addItem`

## MysqlColumnConfigPopupController (extends PopupController)
- 职责：字段配置弹窗，编辑字段的值/默认值/字符集/排序/无符号/补零/自增/主键长度/时间戳更新等属性。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | defaultValueBox | FXHBox | 默认值容器 |
  | defaultValue | MysqlDefaultValueTextFiled | 默认值控件 |
  | valueBox | FXHBox | 值容器 |
  | value | DBEnumTextFiled | 字段值控件 |
  | primaryKeySizeBox / primaryKeySize | FXHBox / NumberTextField | 主键长度容器/控件 |
  | zeroFillBox / zeroFill | FXHBox / FXCheckBox | 补零 |
  | autoIncrementBox / autoIncrement | FXHBox / FXCheckBox | 自动递增 |
  | unsignedBox / unsigned | FXHBox / FXCheckBox | 无符号 |
  | currentTimestampBox / currentTimestamp | FXHBox / FXCheckBox | 按时间戳更新 |
  | charsetBox / charset | FXHBox / DBCharsetComboBox | 字符集 |
  | collationBox / collation | FXHBox / DBCollationComboBox | 排序方式 |
  | dbColumn | MysqlColumn | 当前字段（非 FXML） |
  | dbClient | MysqlClient | db 客户端（非 FXML） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void submit()` | 提交 | 按各 box 可见性把控件值写回 dbColumn（值/字符集/排序/补零/无符号/默认值/自增/主键长度/时间戳），再 close |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `void bindListeners()` | 绑定监听 | 字符集选择变化时 `collation.init(newValue, dbClient)` 并选中首项 |
  | `void onWindowShowing(WindowEvent)` | 显示初始化 | 取 dbColumn/dbClient，按字段 supportXxx 显示对应 box 并回填当前值；主键且支持键长时显示主键长度 |
  | `void onPopupInitialize(PopupAdapter)` | 初始化 | 各 box `managedBindVisible()` |

- 调用链：`submit → MysqlColumn.setValue/setCharset/setCollation/setZeroFill/setUnsigned/setDefaultValue/setAutoIncrement/setPrimaryKeySize/setUpdateOnCurrentTimestamp`
- 调用链：`onWindowShowing → DBCollationComboBox.init / DBCharsetComboBox.init`

## MysqlTableRecordFilterPopupController (extends PopupController)
- 职责：表数据过滤弹窗，编辑表的记录过滤条件列表。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | filterTable | FXTableView<MysqlRecordFilter> | 过滤条件表单（@FXML） |
  | treeItem | MysqlTableTreeItem | db 表节点 |
  | columnList | List<MysqlColumn> | 字段列表缓存 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void apply()` | 应用 | `submit(filterTable.getItems())` 后 close |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `void onWindowShowing(WindowEvent)` | 显示 | 取 prop `item`/`filters`，`filterTable.setItem(filters)` |
  | `void onWindowHidden(WindowEvent)` | 隐藏 | columnList 置 null |
  | `@FXML void addFilter()` | 添加条件 | 新建 MysqlRecordFilter，懒取 `treeItem.columns()` 后 setColumns 并 addItem |
  | `@FXML void deleteFilter()` | 删除条件 | 移除选中项 |

- 调用链：`addFilter → MysqlTableTreeItem.columns → MysqlRecordFilter.setColumns`

## MysqlViewRecordFilterPopupController (extends PopupController)
- 职责：视图数据过滤弹窗，编辑视图的记录过滤条件列表（与表版逻辑一致）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | filterTable | FXTableView<MysqlRecordFilter> | 过滤条件表单（@FXML） |
  | treeItem | MysqlViewTreeItem | db 视图节点 |
  | columnList | List<MysqlColumn> | 字段列表缓存 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `@FXML void apply()` | 应用 | `submit(filterTable.getItems())` 后 close |
  | `@FXML void close()` | 关闭 | `closeWindow()` |
  | `void onWindowShowing(WindowEvent)` | 显示 | 取 prop `item`/`filters` 填充表格 |
  | `void onWindowHidden(WindowEvent)` | 隐藏 | columnList 置 null |
  | `@FXML void addFilter()` | 添加条件 | 新建 MysqlRecordFilter 并绑定 `treeItem.columns()` |
  | `@FXML void deleteFilter()` | 删除条件 | 移除选中项 |

- 调用链：`addFilter → MysqlViewTreeItem.columns → MysqlRecordFilter.setColumns`

# condition 包代码审查文档

> 包路径：`cn.oyzh.easymysql.condition`
> 说明：Mysql 记录过滤条件的抽象与实现，以及条件构建工具。所有具体条件类均为单例（`INSTANCE`）。

## MysqlCondition

- 职责：过滤条件的抽象基类，定义「名称 + 运算符值 + 是否需要条件值」三要素与条件片段包装逻辑。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | name | String | 条件显示名称（中文，如「等于」） |
  | value | String | 条件对应的 SQL 运算符片段（如 `=`、`LIKE`、`IN`） |
  | requireCondition | boolean | 是否需要用户输入条件值；为 false 时（如 IS NULL）不拼接值 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlCondition() | 空构造 | 供子类间接使用 |
  | MysqlCondition(String name, String value) | 构造，默认 requireCondition=true | 赋值 name/value |
  | MysqlCondition(String name, String value, boolean requireCondition) | 全参构造 | 赋值三要素 |
  | wrapCondition() | 无值包装 | 委托 `wrapCondition(null)` |
  | wrapCondition(Object condition) | 核心：生成条件片段 | requireCondition 为真时返回 `value + " " + DBUtil.wrapData(condition)`；condition 为 null 仅返回 value；否则直接返回 value |
  | getName/setName | 读写名称 | — |
  | getValue/setValue | 读写运算符值 | — |
  | isRequireCondition/setRequireCondition | 读写是否需要条件值 | — |

- 调用链：`MysqlRecordFilter.condition() → MysqlCondition.wrapCondition(value) → DBUtil.wrapData(value)`

## MysqlContainsCondition

- 职责：包含条件（`LIKE '%v%'`）。
- 字段：无（仅静态常量 `INSTANCE`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlContainsCondition() | 构造 | `super("包含", "LIKE")` |
  | wrapCondition(Object) | 覆盖 | 非空值包装为 `%值%` 后交父类 |

- 调用链：`MysqlConditionUtil.conditions() → MysqlContainsCondition.INSTANCE → wrapCondition`

## MysqlNotContainsCondition

- 职责：不包含条件（`NOT LIKE '%v%'`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotContainsCondition() | 构造 | `super("不包含", "NOT LIKE")` |
  | wrapCondition(Object) | 覆盖 | 非空值包装为 `%值%` 后交父类 |

- 调用链：`MysqlConditionUtil.conditions() → INSTANCE → wrapCondition`

## MysqlEqCondition

- 职责：等于条件（`=`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlEqCondition() | 构造 | `super("等于", "=")` |

- 调用链：`MysqlRecordFilter → MysqlEqCondition.wrapCondition → 父类拼接 "= 值"`

## MysqlNotEqCondition

- 职责：不等于条件（`!=`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotEqCondition() | 构造 | `super("不等于", "!=")` |

- 调用链：`MysqlRecordFilter → MysqlNotEqCondition.wrapCondition`

## MysqlGtCondition

- 职责：大于条件（`>`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlGtCondition() | 构造 | `super("大于", ">")` |

- 调用链：`MysqlRecordFilter → wrapCondition`

## MysqlGtEqCondition

- 职责：大于等于条件（`>=`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlGtEqCondition() | 构造 | `super("大于等于", ">=")` |

- 调用链：`MysqlRecordFilter → wrapCondition`

## MysqlLtCondition

- 职责：小于条件（`<`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlLtCondition() | 构造 | `super("小于", "<")` |

- 调用链：`MysqlRecordFilter → wrapCondition`

## MysqlLtEqCondition

- 职责：小于等于条件（`<=`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlLtEqCondition() | 构造 | `super("小于等于", "<=")` |

- 调用链：`MysqlRecordFilter → wrapCondition`

## MysqlNullCondition

- 职责：是 NULL 条件（`IS NULL`），无需条件值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNullCondition() | 构造 | `super("是NULL", "IS NULL", false)`，requireCondition=false |

- 调用链：`MysqlConditionUtil.generateNode() → isRequireCondition()==false → 节点禁用`

## MysqlNotNullCondition

- 职责：不是 NULL 条件（`IS NOT NULL`），无需条件值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotNullCondition() | 构造 | `super("不是NULL", "IS NOT NULL", false)` |

- 调用链：同上

## MysqlEmptyCondition

- 职责：是空字符串条件（`=''`），无需条件值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlEmptyCondition() | 构造 | `super("是空的", "=''", false)` |

- 调用链：同上

## MysqlNotEmptyCondition

- 职责：不是空字符串条件（`!=''`），无需条件值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotEmptyCondition() | 构造 | `super("不是空的", "!=''", false)` |

- 调用链：同上

## MysqlStartWithCondition

- 职责：以指定值开始条件（`LIKE '%v'`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlStartWithCondition() | 构造 | `super("开始以", "LIKE")` |
  | wrapCondition(Object) | 覆盖 | 非空值前缀 `%`：`%值` |

- 调用链：`MysqlConditionUtil.conditions() → INSTANCE → wrapCondition`

## MysqlEndWithCondition

- 职责：以指定值结束条件（`LIKE 'v%'`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlEndWithCondition() | 构造 | `super("结束以", "LIKE")` |
  | wrapCondition(Object) | 覆盖 | 非空值后缀 `%`：`值%` |

- 调用链：`MysqlConditionUtil.conditions() → INSTANCE → wrapCondition`

## MysqlNotStartWithCondition

- 职责：不以指定值开始条件（`NOT LIKE '%v'`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotStartWithCondition() | 构造 | `super("不是开始以", "NOT LIKE")` |
  | wrapCondition(Object) | 覆盖 | 非空值前缀 `%` |

- 调用链：`MysqlConditionUtil.conditions() → INSTANCE → wrapCondition`

## MysqlNotEndWithCondition

- 职责：不以指定值结束条件（`NOT LIKE 'v%'`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotEndWithCondition() | 构造 | `super("不是结束以", "NOT LIKE")` |
  | wrapCondition(Object) | 覆盖 | 非空值后缀 `%` |

- 调用链：`MysqlConditionUtil.conditions() → INSTANCE → wrapCondition`

## MysqlInListCondition

- 职责：在列表条件（`IN (v1,v2,...)`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlInListCondition() | 构造 | `super("在列表", "IN")` |
  | wrapCondition(Object) | 覆盖 | 非空值返回 `IN (DBUtil.wrapData(condition))`，支持数组/集合 |

- 调用链：`MysqlConditionUtil.generateNode() → isInCondition()判真 → ClearableTextField → wrapCondition`

## MysqlNotInListCondition

- 职责：不在列表条件（`NOT IN (v1,v2,...)`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotInListCondition() | 构造 | `super("不在列表", "NOT IN")` |
  | wrapCondition(Object) | 覆盖 | 非空值返回 `NOT IN (DBUtil.wrapData(condition))` |

- 调用链：`MysqlConditionUtil.generateNode() → isInCondition()判真 → wrapCondition`

## MysqlBetweenCondition

- 职责：介于条件（`BETWEEN a AND b`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlBetweenCondition() | 构造 | `super("介于", "BETWEEN")` |
  | wrapCondition(Object) | 覆盖 | Object[] 或 Collection 取两端值：`BETWEEN a AND b`；其余交父类 |

- 调用链：`generateNode() → isBetweenCondition()判真 → 生成两个输入节点 → wrapCondition(list)`

## MysqlNotBetweenCondition

- 职责：不介于条件（`NOT BETWEEN a AND b`）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | MysqlNotBetweenCondition() | 构造 | `super("不介于", "NOT BETWEEN")` |
  | wrapCondition(Object) | 覆盖 | Object[]/Collection 取两端：`NOT BETWEEN a AND b` |

- 调用链：`generateNode() → isBetweenCondition()判真 → wrapCondition(list)`

## MysqlConditionUtil

- 职责：条件工具类，提供条件清单、条件 SQL 构建、条件判定与 UI 节点生成/取值。
- 字段：无（纯静态工具类）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | conditions() | 返回全部 20 个条件单例列表 | 按 包含/不包含/等于/大于/小于/不等于/NULL/非NULL/空/非空/小于等于/大于等于/IN/NOT IN/BETWEEN/NOT BETWEEN/开始/结束/不开始/不结束 顺序组装 |
  | buildCondition(List&lt;MysqlRecordFilter&gt;) | 由过滤条件列表拼 SQL WHERE 片段 | 逐项取 `filter.condition()`，用 `DBUtil.wrap(column, DBDialect.MYSQL)` 包装列名，追加连接符 `filter.getJoinSymbol()` |
  | isInCondition(MysqlCondition) | 是否 IN / NOT IN | 恒等比较单例 `INSTANCE` |
  | isBetweenCondition(MysqlCondition) | 是否 BETWEEN / NOT BETWEEN | 恒等比较单例 |
  | generateNode(MysqlColumn, MysqlCondition) | 依据条件生成输入节点 | IN→单个 ClearableTextField；BETWEEN→两个字段节点；其余→一个字段节点；按 requireCondition 设置禁用 |
  | setNodeVal(List&lt;Node&gt;, Object) | 回填节点值 | 值为 List 时逐项对应，否则统一赋值，委托 `DBNodeUtil.setNodeVal` |
  | getNodeVal(List&lt;Node&gt;) | 汇集节点值 | 单节点返回值本身，多节点返回 List |

- 调用链：
  - `MysqlConditionUtil.conditions() → 各条件 INSTANCE → wrapCondition`
  - `MysqlConditionUtil.buildCondition(filters) → MysqlRecordFilter.condition() → MysqlCondition.wrapCondition → DBUtil.wrapData`
  - `MysqlConditionUtil.generateNode(column, condition) → DBNodeUtil.generateNode`

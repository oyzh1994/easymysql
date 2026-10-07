# generator 包代码审查

> 包路径：`cn.oyzh.easymysql.generator`（含 event、routine、table 子包）
> 覆盖类数：8（TableAlertSqlGenerator、TableCreateSqlGenerator 为整文件注释死代码，已跳过）
> 说明：table 子包的抽象基类已被注释为死代码，当前 table 生成器为独立实现。

## EventAlertSqlGenerator (abstract)
- 职责：事件修改 SQL 生成器抽象基类，提供按方言分派的静态入口。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dialect | DBDialect | 数据库方言 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `EventAlertSqlGenerator(DBDialect)` | 构造 | 保存方言 |
  | `abstract String generate(MysqlEvent)` | 生成 SQL | 子类实现 |
  | `static String generate(DBDialect, MysqlEvent)` | 静态分派 | switch 方言，MYSQL→`new MysqlEventAlertSqlGenerator().generate(event)`，default→null |
  | `getDialect/setDialect` | 访问器 | 读写方言 |

- 调用链：`EventAlertSqlGenerator.generate(dialect,event) → MysqlEventAlertSqlGenerator.generate`

## EventCreateSqlGenerator (abstract)
- 职责：事件创建 SQL 生成器抽象基类，提供按方言分派的静态入口。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dialect | DBDialect | 数据库方言 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `EventCreateSqlGenerator(DBDialect)` | 构造 | 保存方言 |
  | `abstract String generate(MysqlEvent)` | 生成 SQL | 子类实现 |
  | `static String generate(DBDialect, MysqlEvent)` | 静态分派 | MYSQL→`new MysqlEventCreateSqlGenerator().generate(event)` |
  | `getDialect/setDialect` | 访问器 | 读写方言 |

- 调用链：`EventCreateSqlGenerator.generate(dialect,event) → MysqlEventCreateSqlGenerator.generate`

## MysqlEventAlertSqlGenerator (extends EventAlertSqlGenerator)
- 职责：MySQL 事件修改 SQL 生成器，拼装 `ALTER EVENT` 语句。
- 字段：无新增
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlEventAlertSqlGenerator()` / `(DBDialect)` | 构造 | 分别传入 MYSQL 或指定方言 |
  | `String generate(MysqlEvent event)` | 生成 ALTER EVENT SQL | 依次拼接 `ALTER`、DEFINER、`EVENT db.name`、`ON SCHEDULE`（AT/EVERY + INTERVAL/STARTS/ENDS）、`ON COMPLETION`、状态、`COMMENT`、`DO` 定义 |

- 调用链：`EventAlertSqlGenerator.generate → MysqlEventAlertSqlGenerator.generate → DBUtil.wrap/wrapData`

## MysqlEventCreateSqlGenerator (extends EventCreateSqlGenerator)
- 职责：MySQL 事件创建 SQL 生成器，拼装 `CREATE EVENT` 语句。
- 字段：无新增
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlEventCreateSqlGenerator()` / `(DBDialect)` | 构造 | 传入方言 |
  | `String generate(MysqlEvent event)` | 生成 CREATE EVENT SQL | 逻辑与 Alert 版一致，起始关键字为 `CREATE` |

- 调用链：`EventCreateSqlGenerator.generate → MysqlEventCreateSqlGenerator.generate → DBUtil.wrap`

## MysqlFunctionSqlGenerator
- 职责：MySQL 函数创建 SQL 生成器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlFunctionSqlGenerator | 单例实例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String generate(MysqlFunction function)` | 生成 CREATE FUNCTION SQL | 拼 `CREATE [DEFINER] FUNCTION name (参数, ...)`，`StringUtil.replaceLast` 去尾逗号，追加 `RETURNS`（returnParam）、`COMMENT`、`SQL SECURITY`、特征与函数定义 |

- 调用链：`MysqlFunctionSqlGenerator.INSTANCE.generate → DBUtil.wrap/wrapData`

## MysqlProcedureSqlGenerator
- 职责：MySQL 存储过程创建 SQL 生成器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | INSTANCE | MysqlProcedureSqlGenerator | 单例实例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String generate(MysqlProcedure procedure)` | 生成 CREATE PROCEDURE SQL | 拼 `CREATE [DEFINER] PROCEDURE name (参数,...)`，去尾逗号，追加 `COMMENT`、`SQL SECURITY`、特征与过程定义 |

- 调用链：`MysqlProcedureSqlGenerator.INSTANCE.generate → DBUtil.wrap`

## MysqlTableCreateSqlGenerator
- 职责：MySQL 建表 SQL 生成器，根据参数对象拼装 `CREATE TABLE` 及字段/主键/索引/外键/检查/触发器。
- 字段：无（方法内使用局部 StringBuilder）
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String generate(MysqlCreateTableParam param)` | 生成建表 SQL | 拼 `CREATE TABLE db.table ( ... )`，依 hasColumns/hasIndex/hasForeignKey/hasCheck 调对应 handle，再追加字符集/排序/引擎/注释/行格式/自增；最后 `replaceAll` 修正 `,)`、`, )`、`,;`；hasTrigger 时触发 triggerHandle |
  | `protected void triggerHandle(StringBuilder, MysqlCreateTableParam)` | 处理触发器 | 逐触发器拼 `CREATE TRIGGER ... FOR EACH ROW ...` |
  | `protected void columnHandle(StringBuilder, MysqlCreateTableParam)` | 处理字段 | 拼字段名、类型、长度/值、UNSIGNED、ZEROFILL、字符集/排序、默认值、NULL/NOT NULL、ON UPDATE、AUTO_INCREMENT、COMMENT |
  | `protected void primaryKeyHandle(StringBuilder, MysqlCreateTableParam)` | 处理主键 | `param.primaryKeys()` 非空则拼 `PRIMARY KEY (...)` |
  | `protected void indexHandle(StringBuilder, MysqlCreateTableParam)` | 处理索引 | 逐索引拼 `[UNIQUE] INDEX name (列[子长]) USING ... COMMENT ...` |
  | `protected void foreignKeyHandle(StringBuilder, MysqlCreateTableParam)` | 处理外键 | 拼 `CONSTRAINT name FOREIGN KEY (...) REFERENCES db.table (...) ON DELETE/UPDATE` |
  | `protected void checkHandle(StringBuilder, MysqlCreateTableParam)` | 处理检查 | 拼 `CONSTRAINT name CHECK (clause)` |
  | `static String generateSql(MysqlCreateTableParam)` | 静态入口 | new 实例后 generate |

- 调用链：`MysqlTableCreateSqlGenerator.generate → columnHandle/primaryKeyHandle/indexHandle/foreignKeyHandle/checkHandle → DBUtil.wrap`
- 调用链：`MysqlTableCreateSqlGenerator.generateSql → generate`

## MysqlTableAlertSqlGenerator
- 职责：MySQL 修改表 SQL 生成器，对比对象状态生成 `ALTER TABLE` 及字段/主键/索引/外键/检查/触发器变更语句。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | sqlList | List<String> | 额外 SQL 列表（触发器、外键删除等） |
  | sqlBuilder | StringBuilder | 主 SQL 构建器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String generate(MysqlAlertTableParam param)` | 生成改表 SQL | 初始化容器；hasForeignKey 时先 `foreignKeyHandle2` 处理外键删除；拼 `ALTER TABLE db.table`；按 columnChanged/primaryKeyChanged/hasIndex/hasForeignKey/hasCheck 调对应 handle，追加表级属性；hasTrigger 时 triggerHandle；最后 `buildSql` |
  | `private String buildSql()` | 组装结果 | 拼接 sqlBuilder 与 sqlList |
  | `protected void triggerHandle(MysqlAlertTableParam)` | 处理触发器 | 删除/变更者拼 `DROP TRIGGER`，变更/新增者拼 `CREATE TRIGGER`，追加到 sqlList |
  | `protected void columnHandle(StringBuilder, MysqlAlertTableParam)` | 处理字段 | 新增→`ADD COLUMN`；改名→`CHANGE COLUMN 原名 新名`；否则 `MODIFY COLUMN`；删除→`DROP COLUMN`；中间拼字段定义 |
  | `protected void primaryKeyHandle(StringBuilder, MysqlAlertTableParam)` | 处理主键 | 存在主键先 `DROP PRIMARY KEY`，再 `ADD PRIMARY KEY (...)` 并带键长与 `USING BTREE` |
  | `protected void indexHandle(StringBuilder, MysqlAlertTableParam)` | 处理索引 | 删除/变更→`DROP INDEX`；新增/变更→`[ADD typeName] INDEX ...` |
  | `protected void foreignKeyHandle1(StringBuilder, MysqlAlertTableParam)` | 外键新增/变更 | 对 CHANGED/CREATED 的过滤结果拼 `ADD CONSTRAINT ... FOREIGN KEY ...` |
  | `protected void foreignKeyHandle2(StringBuilder, MysqlAlertTableParam)` | 外键删除/变更 | 对 DELETED/CHANGED 的过滤结果拼 `DROP FOREIGN KEY`，写入 sqlBuilder |
  | `protected void checkHandle(StringBuilder, MysqlAlertTableParam)` | 处理检查 | 删除/变更→`DROP CONSTRAINT`；新增/变更→`ADD CONSTRAINT ... CHECK (...)` |
  | `static String generateSql(MysqlAlertTableParam)` | 静态入口 | new 实例后 generate |

- 调用链：`MysqlTableAlertSqlGenerator.generate → columnHandle/indexHandle/foreignKeyHandle1/foreignKeyHandle2/checkHandle/triggerHandle → DBUtil.wrap`
- 调用链：`MysqlTableAlertSqlGenerator.generateSql → generate → buildSql`

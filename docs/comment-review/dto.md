# dto 包代码审查

> 包路径：`cn.oyzh.easymysql.dto`
> 覆盖类数：2
> 说明：数据传输对象包。注意与 `cn.oyzh.easymysql.domain.MysqlConnect`（持久化实体）区分，本包的 `MysqlConnect` 为轻量传输对象。

## MysqlConnect
- 职责：db 连接 DTO，承载主机、端口、认证与库索引，用于连接参数的传输与解析。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | host | String | 地址（默认 `127.0.0.1`） |
  | port | int | 端口（默认 `3306`） |
  | user | String | 用户 |
  | password | String | 密码 |
  | db | int | db 索引（默认 0） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String getHost()/setHost(String)` | 地址读写 | 普通访问器 |
  | `int getPort()/setPort(int)` | 端口读写 | 普通访问器 |
  | `String getUser()/setUser(String)` | 用户读写 | 普通访问器 |
  | `String getPassword()/setPassword(String)` | 密码读写 | 普通访问器 |
  | `int getDb()/setDb(int)` | db 索引读写 | 普通访问器 |

- 调用链：`DBConnectUtil.parse → new MysqlConnect(host/port/password/db)`

## MysqlInfoExport
- 职责：db 连接导出对象，封装程序版本、平台与连接列表，支持与 JSON 互转。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | version | String | 导出程序版本号 |
  | platform | String | 导出平台（os.name） |
  | connects | List\<MysqlConnect\> | 导出的连接数据 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static MysqlInfoExport fromConnects(List<MysqlConnect>)` | 由连接列表生成 | `Project.load()` 取版本，写入 connects，`System.getProperty("os.name")` 取平台 |
  | `static MysqlInfoExport fromJSON(String json)` | 由 JSON 生成 | `JSONUtil.parseObject` 后取 version，`JSONUtil.toList` 解析 connects |
  | `String toJSONString()` | 转 JSON | `JSONUtil.toJson(this)` |
  | `String getVersion()/setVersion(String)` | 版本读写 | 普通访问器 |
  | `String getPlatform()/setPlatform(String)` | 平台读写 | 普通访问器 |
  | `List<MysqlConnect> getConnects()/setConnects(List)` | 连接列表读写 | 普通访问器 |

- 调用链：`导出：MysqlInfoExport.fromConnects → toJSONString → 写文件`
- 调用链：`导入：MysqlInfoExport.fromJSON → getConnects`

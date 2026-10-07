# domain 包代码审查

> 包路径：`cn.oyzh.easymysql.domain`
> 覆盖类数：6（MysqlPageInfo、MysqlSearchHistory 为整文件注释死代码，已跳过）
> 说明：实体类使用 `cn.oyzh.store.jdbc` 的 `@Table/@Column/@PrimaryKey` 注解映射到 H2 本地库。

## MysqlConnect
- 职责：db 连接信息实体，映射表 `t_connect`，承载主机、认证、分组、SSH、只读等连接配置。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | id | String | 数据 id（@PrimaryKey） |
  | host | String | 连接地址（可含 `ip:port`） |
  | name | String | 名称 |
  | user | String | 认证用户 |
  | type | String | 类型（方言） |
  | password | String | 认证密码 |
  | remark | String | 备注信息 |
  | readonly | Boolean | 只读模式 |
  | groupId | String | 分组 id |
  | collects | List<String> | 收藏路径列表（非持久化） |
  | connectTimeOut | Integer | 连接超时（秒） |
  | sshForward | Boolean | 是否开启 ssh 转发 |
  | sshConfig | MysqlSSHConfig | ssh 配置（非持久化） |
  | sid | String | 服务 id |
  | serviceName | String | 服务名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlConnect copy(MysqlConnect)` | 复制字段 | 逐字段拷贝（不含 id），返回 this |
  | `boolean isSSHForward()` | 是否 ssh 转发 | `BooleanUtil.isTrue(sshForward)` |
  | `boolean isReadonly()` | 是否只读 | `BooleanUtil.isTrue(readonly)` |
  | `boolean isCollect(String)` | 是否已收藏路径 | collects 非空且包含 path |
  | `addCollect(String)` / `removeCollect(String)` | 添加/取消收藏 | 懒建 list 后增删 |
  | `Integer getConnectTimeOut()` | 连接超时 | 空或小于1时返回默认 5 |
  | `int connectTimeOutMs()` | 超时毫秒 | `getConnectTimeOut()*1000` |
  | `int compareTo(MysqlConnect)` | 排序比较 | 按 name 忽略大小写比较 |
  | `String hostIp()` | 取 ip | host 含 `:` 时取 `:` 前段 |
  | `int hostPort()` | 取端口 | host 不含`,` 且含 `:` 时解析 `:` 后段为 int，异常或不合规返回 -1 |
  | `boolean compare(MysqlConnect)` | 相等比较 | 按 name 判断 |
  | `String serviceName()` | 服务名称 | `sid==null` 时返回 serviceName，否则返回 sid |
  | `String checkServiceType()` | 服务类型 | `sid==null` 返回 "sid"，否则 "serviceName" |
  | 标准 getter/setter | 读写 | 直接读写字段 |

- 调用链：`MysqlConnect.isSSHForward → BooleanUtil.isTrue`
- 调用链：`DBClientUtil.newClient(info) → info.getType()`

## MysqlGroup
- 职责：db 分组实体，继承 `AppGroup`，映射表 `t_group`。
- 字段：无自有字段（继承 AppGroup 的 name/groupId/expand 等）
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlGroup()` / `MysqlGroup(String name, String groupId, boolean expand)` | 构造 | 调用父类构造 |
  | `boolean compare(MysqlGroup)` | 相等比较 | 按 name 判断 |

- 调用链：`MysqlGroupStore.replace → MysqlGroup.getName/getGid`

## MysqlQuery
- 职责：db 查询（保存的 SQL）实体，映射表 `t_query`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | uid | String | 数据 id（@PrimaryKey） |
  | iid | String | 连接 id |
  | dbName | String | 数据库名称 |
  | name | String | 名称 |
  | content | String | 内容（SQL） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `MysqlQuery copy(MysqlQuery)` | 复制字段 | 拷贝 iid/name/dbName/content，返回 this |
  | `int compareTo(MysqlQuery)` | 排序比较 | `StringUtil.compare(t1.uid, this.uid, true)` |
  | `boolean compare(MysqlQuery)` | 相等比较 | 按 uid |
  | `boolean isNew()` | 是否新数据 | `getUid() == null` |
  | 标准 getter/setter | 读写 | 直接读写字段 |

- 调用链：`MysqlQueryStore.replace → MysqlQuery.getUid → isNew`

## MysqlSSHConfig
- 职责：db SSH 配置实体，继承 `SSHConnect`，映射表 `t_ssh_config`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | iid | String | 关联的连接 id（@PrimaryKey） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getIid/setIid` | 读写连接 id | 直接读写 |

- 调用链：`MysqlConnectStore.replace → MysqlSSHConfig.setIid → MysqlSSHConfigStore.replace`

## MysqlSetting
- 职责：db 设置实体，继承 `AppSetting`，映射表 `t_setting`，增加记录分页限制配置。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | recordPageLimit | Integer | 记录每页限制 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void setRecordPageLimit(Integer)` | 设置分页大小 | 空或 ≤0 时置为 100，否则用传入值 |
  | `Integer getRecordPageLimit()` | 获取分页大小 | 空或 ≤0 时返回默认 100 |

- 调用链：`MysqlSettingStore.load → MysqlSetting.getRecordPageLimit`

## ShellTerminalHistory
- 职责：shell 终端历史实体，继承 `TerminalHistory`，映射表 `t_terminal_history`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | tid | String | 数据 id（@PrimaryKey） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getTid/setTid` | 读写数据 id | 直接读写 |

- 调用链：`ShellTerminalHistoryStore.replace → ShellTerminalHistory（insert）`

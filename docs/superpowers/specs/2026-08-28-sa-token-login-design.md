# 用户登录模块设计（sa-token 双 Token）

- 日期：2026-08-28
- 状态：已评审（用户全权委托，开发前确认需求：表不存在需建表、Redis 存储、30分钟/7天滑动续期、四个接口、标准字段）

## 1. 目标

为 consumer_center 增加用户登录模块：

- 认证框架：sa-token 1.44.0（`sa-token-spring-boot3-starter`）
- 用户表：`system_user`（新建），密码暂用明文
- 拦截器检查请求头 `token`
- 双 Token：accessToken（短时效）+ refreshToken（长时效），支持刷新
- 会话存储：Redis（`sa-token-redis-jackson`，复用现有 Redis 连接）

## 2. 双 Token 机制（基于 sa-token 1.44.0 源码验证）

采用 sa-token 原生双会话方案：同一账号登录时创建两个会话，用 `device` 区分：

| 会话 | device | Redis 绝对时效 (timeout) | 活跃窗口 (activeTimeout) | 行为 |
|---|---|---|---|---|
| accessToken | `access` | 7 天 | 30 分钟，滑动 | 每次携带请求自动续活；闲置超 30 分钟被冻结 |
| refreshToken | `refresh` | 14 天 | 7 天，滑动 | 仅刷新时校验；每次成功刷新续活并续期 |

关键机制（源码已验证）：

1. `autoRenew` 默认开启：每次 `checkLogin()` 通过后自动把 token 最后活跃时间刷为当前时间 → accessToken 天然滑动续期。
2. 活跃窗口超时 → token 被"冻结"，`checkLogin()` 抛 `NotLoginException(TOKEN_FREEZE=-6)`；伪造/记录已删除的 token 抛 `INVALID_TOKEN=-2`；未携带 token 抛 `NOT_TOKEN=-1`。据此可精确映射业务错误码。
3. `StpUtil.getLoginDeviceTypeByToken(token)` 可取回 token 的 device，拦截器用它阻止 refreshToken 被当作 accessToken 使用。
4. `StpUtil.renewTimeout(tokenValue, timeout)` 可续期 token 的全部 Redis 键（映射、Token-Session、Account-Session、活跃时间键）。
5. per-token activeTimeout 需要全局配置 `dynamic-active-timeout: true` 才生效。

### 错误码映射（复用已预置的 ErrorMessageEnum）

| 场景 | sa-token 表现 | 业务错误码 |
|---|---|---|
| 未携带 `token` 头 | NOT_TOKEN(-1) | 4002 AUTH_TOKEN_MISSING |
| accessToken 闲置超 30 分钟 | TOKEN_FREEZE(-6) | 4003 AUTH_TOKEN_EXPIRED |
| accessToken 伪造/已登出/记录清除 | INVALID_TOKEN(-2)、KICK_OUT(-5)、BE_REPLACED(-4)、TOKEN_TIMEOUT(-3) 等 | 4004 AUTH_TOKEN_INVALID |
| accessToken 位置传了 refreshToken | device 校验失败 | 4004 AUTH_TOKEN_INVALID |
| 用户名或密码错误 | 业务校验 | 4001 AUTH_USERNAME_OR_PASSWORD_INVALID |
| refreshToken 伪造/不存在 | `getLoginIdByTokenNotThinkFreeze == null` | 4005 AUTH_REFRESH_TOKEN_INVALID |
| refreshToken 闲置超 7 天 | `getTokenActiveTimeoutByToken == -2` | 4006 AUTH_REFRESH_TOKEN_EXPIRED |
| 账号被禁用 | 业务校验 | 新增 4007 AUTH_USER_DISABLED |

前端约定：收到 4003/4004 先尝试用 refreshToken 刷新，刷新失败（4005/4006）跳转登录页。

## 3. 数据库设计

`system_user`（MySQL 5.7，utf8mb4，InnoDB）：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint(20) PK AUTO_INCREMENT | 主键 |
| username | varchar(64) NOT NULL UNIQUE | 用户名 |
| password | varchar(128) NOT NULL | 密码（明文，后续升级加密） |
| real_name | varchar(64) | 真实姓名 |
| phone | varchar(32) | 手机号 |
| email | varchar(128) | 邮箱 |
| status | tinyint(4) NOT NULL DEFAULT 1 | 状态(1:启用,0:禁用) |
| deleted | tinyint(4) NOT NULL DEFAULT 0 | 逻辑删除(0:未删除,1:已删除) |
| create_time | datetime DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

初始化测试账号：`admin / 123456`。

实体与 Mapper 不手写：将 `system_user` 加入 `MyBatisCommentGenerator` 的 TABLES 数组后运行其 main 方法生成（符合 AGENTS.md 规约）。

## 4. 接口设计（RESTful，/api/auth，统一 Result 包装）

| 接口 | 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|---|
| 登录 | POST | /api/auth/login | 免鉴权 | 入参 LoginDTO{username,password}；出参 LoginVO |
| 刷新 | POST | /api/auth/refresh | 免鉴权 | 请求头 `refresh-token` 携带 refreshToken；出参 LoginVO（新 accessToken，原 refreshToken 续期） |
| 退出 | POST | /api/auth/logout | 需登录 | 作废当前 accessToken；请求头 `refresh-token` 可选，携带则一并作废 |
| 当前用户信息 | GET | /api/auth/userinfo | 需登录 | 出参 SystemUserVO（不含密码） |

### 请求头约定

- 业务接口：`token: {accessToken}`
- 刷新接口：`refresh-token: {refreshToken}`
- 退出接口：`token: {accessToken}` + 可选 `refresh-token: {refreshToken}`

### LoginVO

`accessToken`、`refreshToken`、`accessTokenExpiresIn`（1800 秒）、`refreshTokenExpiresIn`（604800 秒）。两个 expiresIn 均为"滑动闲置窗口"：期间只要有一次正常使用（业务请求/刷新）即重新计时。

## 5. 代码结构

```
config/
  WebMvcConfig.java             # excludePathPatterns 增加 /api/auth/login、/api/auth/refresh
interceptor/
  AuthInterceptor.java          # 实现 token 校验（checkLogin + device 校验 + 异常映射）
common/constant/
  AuthConstant.java             # device 类型、请求头名、时效常量
  ErrorMessageEnum.java         # 增加 AUTH_USER_DISABLED(4007)
mvc/controller/AuthController.java
mvc/service/AuthService.java + impl/AuthServiceImpl.java          # 本地账号密码校验（认证通道）
mvc/service/LoginSessionService.java + impl/LoginSessionServiceImpl.java  # 双token签发/刷新/作废（与认证方式解耦）
mvc/pojo/dto/auth/LoginDTO.java
mvc/pojo/vo/auth/LoginVO.java
mvc/pojo/vo/auth/SystemUserVO.java
mvc/pojo/entity/SystemUser.java      # 生成器生成（含 external_id/source）
mvc/mapper/SystemUserMapper.java     # 生成器生成
docs/sql/system_user.sql             # 建表 SQL 留档
docs/sql/migrate-sso-fields.sql      # SSO 映射字段迁移
```

### sa-token yaml 配置

```yaml
sa-token:
  token-name: token
  timeout: 2592000
  active-timeout: -1
  dynamic-active-timeout: true
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-read-cookie: false
  is-log: false
```

### AuthInterceptor 逻辑

```
try {
    StpUtil.checkLogin();
} catch (NotLoginException e) {
    NOT_TOKEN      -> BusinessException(4002)
    TOKEN_FREEZE   -> BusinessException(4003)
    其他           -> BusinessException(4004)
}
if (!"access".equals(StpUtil.getLoginDeviceTypeByToken(StpUtil.getTokenValue())))
    -> BusinessException(4004)
```

### 关键流程（认证与签发分层）

**AuthService（身份校验，本地账号密码通道）**
- **login**：按 username 查未删除用户 → 校验明文密码（4001）→ 校验 status=1（4007）→ 委托 `LoginSessionService.issueTokens(userId)`。
- **userinfo**：`StpUtil.getLoginIdAsLong()` 查用户 → 转 VO（排除 password、externalId、source）。

**LoginSessionService（双token生命周期，与认证方式解耦）**
- **issueTokens(userId)**：`StpUtil.login(id, access 参数)` 取 accessToken → `StpUtil.login(id, refresh 参数)` 取 refreshToken → 组装 LoginVO。任何认证通道确认身份后都从这里签发令牌。
- **refreshTokens(refreshToken)**：`getLoginIdByTokenNotThinkFreeze` 校验存在性（4005）→ device 必须为 `refresh`（4005）→ `getTokenActiveTimeoutByToken == -2` 判冻结（4006）→ 签发新 accessToken → `renewTimeout(refreshToken, 14天)` + `updateLastActiveToNow(refreshToken)` 滑动续期 → 返回 LoginVO。
- **invalidateSessions(refreshToken)**：`StpUtil.logout()` 作废当前 accessToken；若携带合法 `refresh-token`，校验归属同一账号且 device=refresh 后 `StpUtil.logoutByTokenValue(refreshToken)`。

## 6. 依赖变更（pom.xml）

```xml
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot3-starter</artifactId>
    <version>1.44.0</version>
</dependency>
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-redis-jackson</artifactId>
    <version>1.44.0</version>
</dependency>
```

## 7. 对现有功能的影响

- `AuthInterceptor` 由空壳变为实际校验：仅拦截 `/api/**` 业务接口（登录/刷新除外）。文档、静态资源等天然放行。现有接口调用方（前端）需先登录获取 token。
- WebMvcConfig 拦截范围 `addPathPatterns("/api/**")`，排除 `/api/auth/login`、`/api/auth/refresh`。
- Knife4jConfig 通过 OpenAPI SecurityScheme 声明 `token` 请求头认证，文档页提供 Authorize 入口，填充 accessToken 后即可调试受保护接口。
- Redis 新增 `satoken:*` 前缀键，与业务键无冲突。

## 8. 测试策略（由用户执行）

- 用户自行启动服务并用 knife4j/Postman 验证：登录 → 携带 token 访问业务接口 → 不带/错误 token 的报错码 → 刷新 → 退出。
- 本轮不写自动化测试（用户明确要求不做编译/运行验证，避免交付未经验证的测试代码）。

## 9. 明确不做（YAGNI）

- 密码加密（用户明确暂时明文，后续迭代）
- 验证码、登录失败次数限制、多设备管理
- 权限/角色体系

## 10. 门户 SSO 对接预留（2026-08-28 增补）

> 后续已按 OAuth2 Token Introspection 约定落地 SSO 换票流程，当前实现和门户接入方式见 `docs/sso-integration.md`。

背景：本项目后续将嵌入大型门户网站，门户登录后需免登录进入本系统。门户登录协议（CAS / OIDC / OAuth2 / 自定义 ticket）尚未确认，因此**不实现任何协议相关代码**，只做协议无关的稳定准备。

### 已落地的准备

1. **数据层**：`system_user` 新增 `external_id`（门户用户唯一标识，唯一索引，本地账号为 NULL）、`source`（账号来源，默认 `local`）。见 `docs/sql/migrate-sso-fields.sql`。
2. **代码层解耦**：抽出 `LoginSessionService` 承载双 token 的签发/刷新/作废；`AuthService` 仅做本地账号密码校验。认证（谁来证明身份）与签发（如何发令牌）互不依赖。

### 未来接入 SSO 的扩展点（协议确定后再实现）

- 新增一个认证通道服务：校验门户凭据（ticket / code / assertion），换取门户侧用户标识 `externalId`。
- 以 `external_id + source` 在本系统定位用户；若不存在，按约定决定自动建档（auto-provision）或拒绝。
- 定位到 `userId` 后，直接调用现有 `LoginSessionService.issueTokens(userId)` 签发双 token，令牌机制与前端拦截、刷新逻辑完全复用，无需改动。
- 前端预留 `/sso/callback` 承接门户回跳并换取/接收 token；是否启用由环境变量切换（本地登录页 / SSO 直跳）。

### 需门户方确认的事项

认证协议类型、传递的用户标识字段、嵌入方式（链接跳转 / iframe）、是否需要单点登出（SLO）。

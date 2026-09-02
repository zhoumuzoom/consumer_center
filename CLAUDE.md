# CLAUDE.md

本文件为 Claude Code（claude.ai/code）提供本仓库的开发指南。

## 仓库规范

请遵循 `AGENTS.md`；它是本仓库的权威开发指南。重要约束包括：

- 绝不执行数据库/数据表删除操作，也不要删除 Git 分支。
- 后端使用 Java 17、Spring Boot 3.5.16、MyBatis-Plus 3.5.17、MySQL、Redis、Sa-Token、Nacos、Knife4j 和 XXL-Job。
- 业务代码应放在 `com.iwhalecloud.consumercenter.mvc` 下，并遵循 controller → service → mapper 分层。Controller 接收 DTO，返回由 `Result` 静态工厂方法包装的 VO；不得直接暴露实体。
- 业务失败时抛出 `BusinessException`，由 `GlobalExceptionHandler` 统一生成响应。
- Service 接口不继承 MyBatis-Plus 的 `IService`；Mapper 接口继承 `BaseMapper`。优先使用 `BaseMapper` 的单表操作，仅在必要时使用 XML SQL。
- 使用字段注入配合 `@Autowired`、`java.time` 类型、显式导入、Swagger/OpenAPI 注解，以及 `mvc/pojo/convert` 下现有的转换类。
- 不要手动创建生成的实体或 Mapper。请更新 `TABLES` 数组，然后运行 `MyBatisCommentGenerator.main`。

## 命令

### 后端（仓库根目录）

```bash
# 运行 Spring Boot 应用（默认 profile 为 dev）
./mvnw spring-boot:run

# 编译/打包
./mvnw clean package

# 运行全部测试
./mvnw test

# 运行单个测试类
./mvnw -Dtest=CustomerServiceImplTest test

# 运行单个测试方法
./mvnw -Dtest=CustomerServiceImplTest#methodName test
```

`pom.xml` 中未配置后端 lint 或格式化插件。

### 前端（`frontend/`）

```bash
npm install
npm run dev
npm run build
npm run lint
npm run format
```

前端未配置测试脚本。它是一个 React 19 应用，使用 Umi Max 4、Ant Design 6、ProComponents、React Query、Axios、G6、Tailwind CSS 和 Biome。尽管过时的 `AGENTS.md` 仍引用 Vue，但仓库中实际提交的前端是 React。

## 架构

本仓库包含一个 Spring Boot API 和一个独立的前端应用。

### 后端

- `CustomerCenterApplication` 是应用入口。
- `mvc/controller` 对外提供认证、客户和概览看板、客户画像、客户树和行业树、决策者/决策链以及集团报告等 REST API。
- `mvc/service` 和 `mvc/service/impl` 负责业务编排。Service 通常会组合 Mapper 调用，通过 `mvc/pojo/convert` 转换持久化实体，并返回 DTO/VO 模型。
- `mvc/mapper` 包含 MyBatis-Plus 的 `BaseMapper` 数据访问接口；`mvc/pojo/entity` 映射数据库表，`dto` 和 `vo` 定义 API 边界。
- `common/result` 定义统一的 `Result` 和 `PageResult` 响应；`common/error` 与 `common/constant/ErrorMessageEnum` 定义集中式业务错误处理。
- `AuthInterceptor` 通过 Sa-Token 保护 API 请求。认证支持本地凭据和可选的 OAuth2 令牌 introspection；访问/刷新会话分开管理，并通过 Redis 支持的 Sa-Token 状态持久化。
- `ApiLogAspect` 记录 API 操作，`SqlCostInterceptor` 观察 SQL 执行耗时。
- `config` 负责配置 MVC 拦截、MyBatis-Plus 分页、Redis 序列化和 Knife4j/OpenAPI。
- `application.yaml` 激活 `dev`，根据 `mysql.*` 配置数据源，配置逻辑删除、Sa-Token、Swagger/Knife4j 以及可选的 SSO。`application-dev.yaml` 提供本地默认值并导入可选的 Nacos 配置。请将其中已提交的连接配置视为特定环境的配置；不要将其复制到新的代码或文档中。
- 测试包括 Spring 上下文覆盖测试，以及位于对应包路径下的 Controller 测试和 Service 实现单元测试。

### 前端

- `frontend/src/app.jsx` 提供应用运行时配置；`layouts/BasicLayout.jsx` 是主要的已认证应用外壳。
- `pages/` 包含按业务领域分组的路由级页面，与后端业务领域保持对应。
- `api/` 包含按业务领域划分的后端客户端。共享 Axios 行为位于 `utils/request.js`，令牌持久化位于 `utils/token.js`，认证状态通过 `hooks/useAuth.js` 暴露。
- `components/G6TreeCanvas.jsx` 是共享的图/树可视化组件。语言环境资源位于 `locales/` 下。
- Umi 配置位于 `frontend/config/`。不要将生成的 `frontend/src/.umi/` 或已安装的 `frontend/node_modules/` 内容当作源代码进行编辑。

通常的端到端流程为：页面 → 业务领域 API 模块 → Axios 请求/令牌处理 → Spring Controller → Service → Mapper/MySQL → 转换器/VO → 统一的 `Result` 响应。

# Consumer Center（CMI 要客运营平台）

面向要客（重点客户）运营管理场景的前后端分离应用，提供客户管理、概览看板、客户画像、客户树/行业树、决策者与决策链、集团报告等能力。后端提供 REST API，前端为独立 SPA；登录认证基于 Sa-Token，并支持按需接入 OAuth2 SSO。

## 技术栈

| 模块 | 主要技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.5、MyBatis-Plus、MySQL 5.7、Redis、Sa-Token、Nacos、Knife4j |
| 前端 | React 19、Umi Max 4、Ant Design 6、ProComponents、Tailwind CSS、Biome |

## 目录结构

```text
.
├── src/main/java/com/iwhalecloud/consumercenter   # 后端源码（controller / service / mapper 等）
├── src/main/resources                             # 应用配置
├── frontend/                                      # React 前端
├── docs/                                          # 需求文档、数据库脚本、SSO 接入说明
├── pom.xml、mvnw                                  # 后端 Maven 构建
├── AGENTS.md、CLAUDE.md                           # 开发规范
└── README.md
```

## 本地启动

以下命令均在项目根目录执行。

### 环境要求

- JDK 17+
- Node.js 18+ 与 npm
- MySQL 5.7
- Redis（Sa-Token 会话存储）
- Nacos（配置中心与服务注册）

### 1. 初始化数据库

创建 MySQL 数据库后，执行 `docs/sql` 下的脚本：

- `create-tables.sql`：客户、客户树、行业树、决策者、任务等核心业务表
- `system_user.sql`：登录用户表
- `migrate-profile-enhancement.sql`：客户画像相关表与字段
- `init-data.sql`：演示数据（可选）

### 2. 启动后端

按本地环境修改 `src/main/resources/application-dev.yaml` 中的 MySQL、Redis、Nacos 连接信息，然后启动：

```bash
./mvnw spring-boot:run
```

后端默认端口为 `8080`，接口文档（Knife4j）：<http://localhost:8080/doc.html>。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 <http://localhost:8000>，开发服务器已将 `/api` 代理到 `http://localhost:8080`。

### 常用命令

```bash
# 后端测试
./mvnw test

# 前端
cd frontend
npm run lint    # 代码检查
npm run build   # 生产构建
```

## 配置说明

- 应用默认激活 `dev` profile，配置入口为 `src/main/resources/application.yaml` 和 `application-dev.yaml`。
- `application-dev.yaml` 中已提交的连接信息仅用于当前开发环境，请按本地环境调整，不要提交个人或生产凭据。
- 后端开发规范见 `AGENTS.md` 与 `CLAUDE.md`；前端说明见 `frontend/README.md`。

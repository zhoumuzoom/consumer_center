# dsh-superagent 部署手册

> 飞书机器人 + GitHub 自主编码 Agent，从零到跑通第一个 PR。

---

## 目录

- [0. 先读这一段](#0-先读这一段)
- [1. 架构与运行形态](#1-架构与运行形态)
- [2. 前置条件](#2-前置条件)
- [3. 变量约定](#3-变量约定)
- [4. 准备工作](#4-准备工作)
- [5. 安装 dsh-superagent](#5-安装-dsh-superagent)
- [6. 配置 Source / Policy / Executor](#6-配置-source--policy--executor)
- [7. 验证](#7-验证)
- [8. 日常运维](#8-日常运维)
- [9. 故障排查](#9-故障排查)
- [10. 安全清单](#10-安全清单)
- [11. 备选接入方式（不用飞书）](#11-备选接入方式不用飞书)
- [附录](#附录-a-cordis-patchyml-模板)

## 0. 先读这一段

本文档描述如何在一台个人电脑（macOS / Linux）上部署 [`dsh-superagent`](https://github.com/deepseek-ai/dsh-superagent-prototype)，让一个 Agent 监听飞书消息，在自己的 GitHub 仓库里改代码、开 PR。

**它是什么：**

- 一个基于 [DeepSeek Harness (DSH)](https://www.deepseek.com/en/harness/) 的**主动式编码 Agent**，以 DSH bundle 的形式运行。
- 每个请求会生成一张 **Ticket**，在独立 session + 独立 git worktree 里执行，完成后推送分支、创建 PR，并回到原对话回复结果。
- 它是**研究原型**，官方声明 "not production-ready"。适合个人项目、内部试验，不建议直接接入生产链路。

**它不是什么：**

- 不是 Java/前端项目里的依赖库，不需要改业务代码。
- 不是多租户服务，一个部署实例只服务**一个 GitHub 仓库**。
- 不会自动合并 PR（默认关闭 auto-merge），最终合并权在人工。

**读完本文你能得到：**

```
飞书群 @机器人 → Ticket → 独立 worktree 改代码 → 推送分支 → 创建 PR → 飞书线程内回复
```

---

## 1. 架构与运行形态

```
┌──────────────┐   事件（长连接）   ┌─────────────────────────────┐
│  飞书 群/私聊  │ ───────────────→ │  DSH profile: superagent     │
└──────────────┘                   │  ├─ dsh-superagent bundle    │
                                   │  ├─ Source: feishu           │
┌──────────────┐   notifications    │  ├─ Source: github           │
│  GitHub 仓库  │ ←───────────────→ │  ├─ Policy（白名单/准入）     │
└──────────────┘   push/PR/comments │  ├─ Executor（worker 池）     │
                                   │  └─ SQLite 状态库             │
                                   └─────────────────────────────┘
                                              │
                                    ~/.dsh/profiles/superagent
                                    ~/.dsh/state/superagent
                                    ~/src/dsh-superagent（源码）
```

关键概念：

| 概念 | 作用 |
|---|---|
| **Source** | 请求入口。`feishu` 接收机器人消息，`github` 接收 issue/PR 评论通知、轮询 CI |
| **Policy** | 谁可以发起工作（allowed）、什么消息算明确请求（addressed/admission） |
| **Ticket** | 一次被授权的工作项，有独立目录、独立 Session、独立分支 |
| **Executor** | 执行 Ticket 的 worker，跑在独立 git worktree 里 |
| **Eval** | 判断一条消息该不该被接受。`observe` 模式下只评估"主动型"工作，明确请求照常执行 |
| **Delivery** | 把结果回复到原对话（飞书线程 / GitHub 评论） |

---

## 2. 前置条件

### 2.1 软件

| 组件 | 版本要求 | 检查命令 |
|---|---|---|
| Node.js | `^22.19.0` 或 `>=24.0.0` | `node -v` |
| pnpm | 11.x | `pnpm -v` |
| git | `>= 2.32` | `git --version` |
| Python | `>= 3.8`（跑服务脚本） | `python3 --version` |
| tmux | 任意近期版本 | `tmux -V` |
| GitHub CLI | `>= 2.48` | `gh --version` |
| lark-cli | 实测 `1.0.97`（官方建议 ≥1.0.90） | `lark-cli --version` |
| DSH | 满足 bundle `engines.dsh`（`^0.2.1-alpha.2`） | `dsh --version` |

> 如果使用 **DeepSeek Harness 桌面版**，它自带 Node 24 / pnpm / dsh CLI，可复用：
> `dsh` 位于
> `/Applications/DeepSeek Harness.app/Contents/Resources/runtime/cli/bin/dsh`
> Node 位于
> `.../runtime/primary-runtime/dependencies/node/bin/node`
> pnpm 位于
> `.../runtime/primary-runtime/dependencies/pnpm/bin/pnpm.cjs`

### 2.2 账号与权限

| 账号 | 用途 | 必需权限 |
|---|---|---|
| GitHub **Agent 账号** | Agent 以它身份提交、推送、开 PR | 目标仓库 **Write**（或 admin） |
| GitHub **提需求账号** | 可选。issue 里 @ Agent 触发工作 | 公开仓库无需权限；私有仓库需 Read |
| 飞书 **企业自建应用** | 机器人身份，接收/发送消息 | 机器人能力 + 消息读写/群成员/事件订阅 |

> ⚠️ **强烈建议 Agent 使用独立 GitHub 账号**，不要复用个人账号。所有 commit / PR / 评论都会显示为该账号。
>
> ⚠️ **不要用刚注册的新 GitHub 账号做"提需求"账号**。新注册账号在公开仓库立刻发 issue/评论，极易被风控封禁，且可能连带关联主账号。飞书方案就是为了绕开这个问题。

### 2.3 模型

二选一：

- **DeepSeek 账号登录**（推荐）：DSH profile 使用 `deepseek-account` provider，无需 API Key。
- **API Key**：设置环境变量 `DEEPSEEK_API_KEY`，或通过 DSH Web 的 Models 页面写入凭据。

---

## 3. 变量约定

下文命令统一使用以下占位符，部署时替换成你自己的值：

| 占位符 | 说明 | 示例 |
|---|---|---|
| `<REPO>` | GitHub 仓库 `owner/name` | `acme/widgets` |
| `<AGENT_GH>` | Agent 的 GitHub 登录名 | `acme-agent` |
| `<REQUESTER_GH>` | 提需求账号（可选） | `alice` |
| `<DSH_HOME>` | DSH 配置目录 | `~/.dsh` |
| `<SRC_DIR>` | dsh-superagent 源码目录 | `~/src/dsh-superagent` |
| `<WORK_CLONE>` | 用于创建 worktree 的仓库克隆 | `~/src/widgets` |
| `<TICKET_DIR>` | Ticket 工作目录根 | `~/src/widgets-tickets` |
| `<STATE_DIR>` | Agent 状态目录（绝对路径） | `~/.dsh/state/superagent` |
| `<APP_ID>` | 飞书应用 App ID | `cli_xxx` |
| `<CHAT_ID>` | 飞书群 ID | `oc_xxx` |
| `<FEISHU_USER_OPEN_ID>` | 允许提需求的飞书用户 open_id | `ou_xxx` |

---

## 4. 准备工作

### 4.1 GitHub 仓库与 Agent 账号

1. 确认仓库在 `github.com`（不支持 GitHub Enterprise Server）。
2. 创建 Agent 专用 GitHub 账号，并邀请为仓库 **Collaborator（Write）**：
   `Settings → Collaborators → Add people`。
3. 用 Agent 账号接受邀请。

> 如果仓库是公开的，提需求账号不需要任何仓库权限。

### 4.2 gh CLI 与 `gh-agent` wrapper

Agent 的 GitHub 凭据必须与个人 `gh` 隔离，避免混用：

```sh
mkdir -p ~/.local/bin
cat > ~/.local/bin/gh-agent <<'EOF'
#!/bin/sh
GH_CONFIG_DIR="$HOME/.config/gh-agent" exec gh "$@"
EOF
chmod +x ~/.local/bin/gh-agent

# 用 Agent 账号登录（会走浏览器 device code）
~/.local/bin/gh-agent auth login
~/.local/bin/gh-agent api user --jq .login
```

**必须确保 token 含 `notifications` scope**（否则 Source 读不到 @ 通知）：

```sh
~/.local/bin/gh-agent auth status        # Token scopes 里应有 notifications
# 没有则补：
~/.local/bin/gh-agent auth refresh -h github.com -s notifications
```

验证仓库权限：

```sh
~/.local/bin/gh-agent api repos/<REPO> --jq '.permissions'
```

### 4.3 飞书自建应用

在 [飞书开放平台](https://open.feishu.cn/app) 创建**企业自建应用**：

1. **创建应用**，名称可用中文（2–30 字符，企业内唯一）。
2. **添加应用能力 → 机器人**，启用。
3. **权限管理**，至少开通（以控制台中文名称为准）：

   | 权限 | 用途 |
   |---|---|
   | `im:message` 获取与发送单聊、群组消息 | 收发消息 |
   | `im:message:send_as_bot` 以应用身份发消息 | 回复用户 |
   | `im:message:readonly` 读取消息 | 补收历史 |
   | `im:chat` / `im:chat:readonly` 获取群组信息 | 列群、群名 |
   | `im:chat.members:read` 获取群成员 | 同步/配置白名单 |
   | `im:message.p2p_msg` 获取单聊消息 | 私聊触发 |
   | `im:message.group_msg` 获取群组中所有消息 | 仅当群要"全部消息"模式 |

4. **事件与回调 → 事件配置**：
   - 订阅方式选择 **使用长连接接收事件**（不要选 Webhook，免公网地址）
   - 添加事件 **`im.message.receive_v1`（接收消息）**
5. **版本管理与发布**：创建版本并发布（自建应用通常需要企业管理员审核）。
6. 把机器人**拉进目标群**：
   群设置 → 群机器人 → 添加机器人 → 选择该应用。
7. 从「凭证与基础信息」拿到 **App ID**；**App Secret 不要贴到聊天/文档里**，下一步用 stdin 输入。

### 4.4 lark-cli 机器人凭据

机器人必须使用**独立配置目录**，并锁定为 bot 身份：

```sh
export LARKSUITE_CLI_CONFIG_DIR="$HOME/.lark-cli-dsh-superagent"

lark-cli config init --app-id <APP_ID> --app-secret-stdin   # 按提示粘贴 Secret
lark-cli config strict-mode bot --global

# 包装器，供 dsh-superagent 调用
cat > ~/.local/bin/lark-bot <<'EOF'
#!/bin/sh
export LARKSUITE_CLI_CONFIG_DIR="$HOME/.lark-cli-dsh-superagent"
exec lark-cli "$@"
EOF
chmod +x ~/.local/bin/lark-bot
```

验证身份与群列表：

```sh
~/.local/bin/lark-bot api GET /open-apis/bot/v3/info
~/.local/bin/lark-bot im +chat-list --page-all --page-size 100
```

> `strict-mode bot` 是硬性要求：宿主会拒绝一个可以以用户身份行动的 CLI。
>
> 如果机器上已有个人 `lark-cli` 登录，**不要**复用它的默认配置目录，否则会串号。

---

## 5. 安装 dsh-superagent

### 5.1 获取源码并构建

> 确保 `dsh` 在 PATH 中：可以用 `npm install -g @deepseek-ai/dsh@<版本>`（版本需满足 bundle 的 `engines.dsh`），
> 或使用桌面版自带的 CLI（路径见 2.1）。

```sh
git clone https://github.com/deepseek-ai/dsh-superagent-prototype.git <SRC_DIR>
cd <SRC_DIR>
pnpm install
pnpm build
```

### 5.2 创建独立 DSH profile

```sh
cd <SRC_DIR>
dsh superagent --from-default-profile web --dump-config > /dev/null
dsh plugin --profile superagent add "link:$PWD"
```

> `superagent` 是 profile 名，与日常使用的 profile 分开，互不影响。
> 该命令只用于**首次安装**；已有 profile 时不要重复执行（会覆盖配置）。

### 5.3 机器配置（`cordis.patch.yml`）

编辑 `~/.dsh/profiles/superagent/cordis.patch.yml`：

```yaml
# 面板监听地址
- id: webserver
  config:
    host: 127.0.0.1
    port: 3082

# 状态目录（必须绝对路径，升级前备份）
- id: dsh-superagent
  config:
    stateDir: <STATE_DIR>

# 模型：使用 DeepSeek 账号登录（无需 API Key）
# 若改用 API Key，改为 provider: deepseek-official 并设置 DEEPSEEK_API_KEY
- id: agent-default-model
  name: "@deepseek-ai/dsh-agent-default-model"
  config:
    provider: deepseek-account
    model: deepseek-flash
    reasoningEffort: max

# 启用飞书 Source
- id: dsh-superagent-feishu
  disabled: false
```

### 5.4 启动与状态

```sh
cd <SRC_DIR>
python3 scripts/dsh-superagent start
python3 scripts/dsh-superagent status
```

`status` 退出码：

| 码 | 含义 |
|---|---|
| 0 | 正常运行 |
| 1 | 未运行 / 重复运行 / 不响应 / 运行时报错（可尝试 restart） |
| 2 | 运行中但某个部件报错（restart 修不好，需人工） |
| 3 | 运行中但等待 setup（缺账号/仓库/CLI 配置） |

面板地址在 `.artifacts/dsh-superagent-<profile>-*.log` 的最新日志里，形如：

```
http://127.0.0.1:3082/?token=xxxxx
```

> **桌面版 DSH 的兼容性说明**：桌面版 CLI 的进程名是 `DeepSeek`，而 `scripts/dsh-superagent` 只识别 `node`/`dsh`，会导致 `status/stop` 找不到进程。若使用桌面版 CLI，需把脚本中的
> `name in ('node', 'dsh')` 改为 `name in ('node', 'dsh', 'DeepSeek')`（两处）。
> 升级源码后可能需要重新打补丁。

---

## 6. 配置 Source / Policy / Executor

所有配置通过本地 **Management API** 写入，面板与它是同一套配置。

**请求约定：**

```sh
API=http://127.0.0.1:3082/orchestrator/api
# 必须带这个头，且 Host 必须是 127.0.0.1/localhost
-H 'x-orchestrator-request: 1'
```

读取当前配置（含每个配置块的 `revision`，保存时必须带上）：

```sh
curl -s -H 'x-orchestrator-request: 1' $API/settings
```

保存格式：

```json
{ "value": { ... }, "revision": 当前版本号 }
```

> `revision` 不匹配会返回 `409`。每次保存后重新 `GET /settings` 拿新值。

### 6.1 GitHub Source

```sh
curl -s -X PUT -H 'x-orchestrator-request: 1' -H 'content-type: application/json' \
  $API/settings/source:github \
  -d '{
    "value": {
      "enabled": true,
      "config": {
        "repository": "<REPO>",
        "cli": "/Users/<you>/.local/bin/gh-agent",
        "pollSeconds": 60,
        "failuresBeforeError": 3,
        "observeIssues": false,
        "observePulls": false,
        "observeComments": false,
        "observeCi": false
      }
    },
    "revision": 0
  }'
```

- `repository` / `cli` 变更会触发服务重启（响应里 `restarting: true`），等待 API 恢复后再继续。
- 四个 `observe*` 开关是"仓库直读"模式：
  - `observeIssues` / `observeComments`：不依赖通知，直接轮询仓库新 issue / 新评论。
  - 代价是每条都进入评估，噪音更大。
  - 默认建议只依赖通知（全部关闭）。

### 6.2 飞书 Source

```sh
curl -s -X PUT -H 'x-orchestrator-request: 1' -H 'content-type: application/json' \
  $API/settings/source:feishu \
  -d '{
    "value": {
      "enabled": true,
      "config": {
        "cli": "/Users/<you>/.local/bin/lark-bot",
        "readPageSize": 20,
        "linkTimeoutMs": 5000,
        "chats": [
          { "chatId": "<CHAT_ID>", "name": "自动化测试", "listen": "mentions" }
        ],
        "directMessages": true,
        "otherChats": "mentions"
      }
    },
    "revision": 0
  }'
```

- `listen: "mentions"`：只接收 @ 机器人的消息和已有 Ticket 的线程。
- `listen: "all"`：接收群里所有消息（需要 `im:message.group_msg` 权限），可配合规则做主动推断。
- `directMessages: true`：允许私聊触发（私聊不需要 @）。

### 6.3 Policy

```sh
curl -s -X PUT -H 'x-orchestrator-request: 1' -H 'content-type: application/json' \
  $API/settings/policy \
  -d '{
    "value": {
      "rules": [
        {
          "id": "copy",
          "name": "Copy fixes",
          "enabled": true,
          "instruction": "Explicit requests to correct a typo, wording or punctuation."
        }
      ],
      "sources": {
        "github": {
          "allowed": [{ "id": "<REQUESTER_GH>" }],
          "bots": [{ "id": "dependabot[bot]" }]
        },
        "feishu": {
          "allowed": [{ "id": "<FEISHU_USER_OPEN_ID>", "name": "你的飞书昵称" }],
          "syncChat": ""
        }
      }
    },
    "revision": 0
  }'
```

- `allowed` 是**谁能发起工作**的白名单，必须显式配置。
- `syncChat` 填 `<CHAT_ID>` 时，启动/同步会用群成员自动填充白名单（适合小团队群）。
- `rules` 只影响**推断型**（未 @ 的主动工作），明确请求不受规则限制。

获取飞书用户 `open_id`：

```sh
~/.local/bin/lark-bot im +chat-members-list \
  --chat-id <CHAT_ID> --member-types user --page-all --page-limit 0
```

### 6.4 Executor

```sh
curl -s -X PUT -H 'x-orchestrator-request: 1' -H 'content-type: application/json' \
  $API/settings/executor \
  -d '{
    "value": {
      "model": { "provider": "deepseek-account", "model": "deepseek-flash", "reasoningEffort": "max" },
      "maxWorkers": 3,
      "directory": "<TICKET_DIR>",
      "prepare": "git -C <WORK_CLONE> fetch --no-tags \"https://github.com/$DSH_SUPERAGENT_GITHUB_REPOSITORY.git\" \"${DSH_SUPERAGENT_GITHUB_PR_HEAD:-HEAD}:refs/dsh-superagent/$DSH_SUPERAGENT_TICKET\" && git -C <WORK_CLONE> worktree add -b \"agent/$DSH_SUPERAGENT_NAME\" \"$DSH_SUPERAGENT_DIR\" \"refs/dsh-superagent/$DSH_SUPERAGENT_TICKET\"",
      "blockedCommands": []
    },
    "revision": 0
  }'
```

- `directory`：每张 Ticket 的子目录根（必须是绝对路径）。
- `prepare`：为每张 Ticket 创建独立 worktree。
  - `<WORK_CLONE>` 是本地仓库克隆，`git fetch` 用 Agent 凭据拉取。
  - 建议用**独立克隆**，不要和日常开发目录混用。
- `model` 留 `null` 时使用 profile 默认模型；显式设置可避免不同 profile 的默认值差异。

### 6.5 Eval 模型（可选但建议）

```sh
curl -s -X PUT -H 'x-orchestrator-request: 1' -H 'content-type: application/json' \
  $API/settings/eval \
  -d '{
    "value": {
      "mode": "observe",
      "timeoutSeconds": 120,
      "model": { "provider": "deepseek-account", "model": "deepseek-flash", "reasoningEffort": "max" }
    },
    "revision": 0
  }'
```

`mode` 说明：

| 模式 | 行为 |
|---|---|
| `off` | 只处理明确请求（@ 机器人） |
| `observe` | 明确请求照常执行；推断型工作只评估、记录"would accept"，不执行（推荐起步） |
| `auto` | 推断型工作匹配规则即自动执行（噪音大，谨慎） |

### 6.6 Prompts（可选）

```sh
curl -s -H 'x-orchestrator-request: 1' $API/prompts
```

- 不配置时的默认行为：commit 作者使用 Agent 账号的 noreply 地址；不启用 auto-merge。
- 要指定提交身份 / 自动合并条件，编辑 `delivery` 段后 `PUT /prompts`（带 `baseRevision`）。
- **建议保持 auto-merge 关闭**：Agent 不合并自己的 PR。

---

## 7. 验证

1. 在飞书群里**从 @ 列表选中**机器人（不要手打 @ 文本），发一条小需求：

   ```
   @你的机器人 在仓库根目录新增 docs/agent-test.md，写一行测试说明即可，不需要改代码。
   ```

2. 约 1 分钟内：
   - 面板生成 Ticket
   - Agent 在飞书线程内回复"开始…"
   - 完成后回复 PR 链接

3. 在 GitHub 确认：新分支 `agent/...`、新 PR（作者是 Agent 账号）。

> **关键细节**：飞书只有**从 @ 选择器选中**机器人才会产生 `mentions` 元数据。
> 手打 `@机器人名` 只是纯文本，会被判定为 `no_mention` 忽略。
> 私聊机器人不需要 @，直接发需求即可。

---

## 8. 日常运维

```sh
cd <SRC_DIR>

python3 scripts/dsh-superagent start     # 启动
python3 scripts/dsh-superagent status    # 状态（0/1/2/3）
python3 scripts/dsh-superagent restart   # 优雅重启（会等待 worker）
python3 scripts/dsh-superagent stop      # 停止（暂停自动恢复）
```

- 日志：`<SRC_DIR>/.artifacts/dsh-superagent-<profile>-*.log`（保留最近 20 份）
- 状态库：`<STATE_DIR>/orchestrator.sqlite`（升级前务必备份）
- 面板：最新日志里的 `http://127.0.0.1:3082/?token=...`
- 升级：

  ```sh
  cd <SRC_DIR>
  git pull && pnpm install && pnpm build
  python3 scripts/dsh-superagent restart
  ```

- 开机自启（可选）：用 launchd / systemd 托管 `scripts/dsh-superagent start`；否则每次重启电脑后手动启动。

---

## 9. 故障排查

| 现象 | 原因 | 处理 |
|---|---|---|
| `MISSING_CREDENTIAL: no API key for provider route "deepseek-official"` | profile 默认模型走 API Key provider，但没有 Key | 把 `agent-default-model` / `eval.model` / `executor.model` 指向 `deepseek-account`，或设置 `DEEPSEEK_API_KEY`；已有 session 需重建才生效 |
| 飞书消息收到了但判定 `no_mention` | 手打了 @ 文本，没有真正 mention | 从 @ 列表选中机器人；或改用私聊 |
| 飞书事件收不到 | 事件订阅不是长连接 / 未发布版本 / 权限缺失 | 检查 `im.message.receive_v1` + 长连接 + 发布状态 |
| `lark-cli` 报身份错误 | 用了个人配置目录，CLI 能以用户身份行动 | 独立 `LARKSUITE_CLI_CONFIG_DIR` + `strict-mode bot` |
| GitHub 通知读不到 | token 缺 `notifications` scope | `gh-agent auth refresh -h github.com -s notifications` |
| GitHub 轮询报 `404`（某个已删除的 issue） | 已删除的 issue/PR 仍挂在订阅线程或通知里 | `POST /threads/<key>/unsubscribe` 取消订阅；必要时停服清理 `github-retry` |
| `scripts/dsh-superagent status` 找不到进程（桌面版 CLI） | 进程名是 `DeepSeek`，脚本只认 `node`/`dsh` | 打补丁加入 `DeepSeek`（见 5.4） |
| 新 GitHub 账号立刻被封 | 新号 + 同 IP 连续注册 + 公开仓库评论，触发反滥用 | 用老账号；或改用飞书入口；不要连续注册小号 |
| Agent 以错误身份提交 | `gh-agent` 配置目录串号 | 检查 `~/.config/gh-agent`，重新 `gh-agent auth login` |
| 端口 3082 被占用 | 重复实例 / 其他进程 | `lsof -nP -iTCP:3082 -sTCP:LISTEN`，停掉旧实例 |
| `revision` 保存返回 409 | 配置版本已变化 | 重新 `GET /settings` 拿最新 revision |

> 直接改 SQLite 状态库（如清理 outbox/effect）属于最后手段：**必须先停服务**，改完再启动。

---

## 10. 安全清单

- **权限模型**：worker 以当前 OS 用户、`danger-full-access` 运行，能读取 home 目录、执行任意命令。
  - 生产/多人环境请用**独立 OS 账号**或独立机器，只挂载该仓库。
- **仓库保护**：`master/main` 开启分支保护，要求 PR review；不要给 Agent 合并权限。
- **auto-merge**：保持关闭。
- **凭据隔离**：Agent 的 `gh` / `lark-cli` 使用独立配置目录，不与个人账号混用。
- **Secret 管理**：App Secret 只通过 stdin 输入；一旦出现在日志/截图中，立即在开放平台重置。
- **数据备份**：`<STATE_DIR>/orchestrator.sqlite` 包含 Ticket 历史与配置，升级前备份。
- **成本**：推理按 token 计费；先用 `observe` 模式观察，再考虑 `auto`。

---

## 11. 备选接入方式（不用飞书）

如果暂时无法创建飞书企业自建应用，也可以只用 GitHub 作为请求入口：

1. 在仓库 issue / 评论里 @ Agent 账号；
2. Source 通过通知或 `observeIssues` / `observeComments` 接收；
3. Policy 白名单加入提需求账号。

**注意事项：**

- Agent 自己不能触发自己（代码层面把 Agent 账号的消息判定为非人类）。
- 必须有两个**不同**的 GitHub 账号：Agent 账号 + 提需求账号。
- **新注册的提需求账号风险很高**，容易被 GitHub 风控封禁。建议使用注册时间较久的账号。
- 通知链路依赖 `notifications` scope；没有该 scope 时只能靠 `observe*` 轮询兜底。

---

## 附录 A：`cordis.patch.yml` 模板

```yaml
- id: webserver
  config:
    host: 127.0.0.1
    port: 3082

- id: dsh-superagent
  config:
    stateDir: <STATE_DIR>

- id: agent-default-model
  name: "@deepseek-ai/dsh-agent-default-model"
  config:
    provider: deepseek-account
    model: deepseek-flash
    reasoningEffort: max

- id: dsh-superagent-feishu
  disabled: false
```

## 附录 B：settings 模板

```jsonc
// source:github
{
  "enabled": true,
  "config": {
    "repository": "<REPO>",
    "cli": "/Users/<you>/.local/bin/gh-agent",
    "pollSeconds": 60,
    "failuresBeforeError": 3,
    "observeIssues": false,
    "observePulls": false,
    "observeComments": false,
    "observeCi": false
  }
}

// source:feishu
{
  "enabled": true,
  "config": {
    "cli": "/Users/<you>/.local/bin/lark-bot",
    "readPageSize": 20,
    "linkTimeoutMs": 5000,
    "chats": [{ "chatId": "<CHAT_ID>", "name": "<群名>", "listen": "mentions" }],
    "directMessages": true,
    "otherChats": "mentions"
  }
}

// policy.sources
{
  "github": { "allowed": [{ "id": "<REQUESTER_GH>" }], "bots": [{ "id": "dependabot[bot]" }] },
  "feishu": { "allowed": [{ "id": "<FEISHU_USER_OPEN_ID>" }], "syncChat": "" }
}

// executor
{
  "model": { "provider": "deepseek-account", "model": "deepseek-flash", "reasoningEffort": "max" },
  "maxWorkers": 3,
  "directory": "<TICKET_DIR>",
  "prepare": "git -C <WORK_CLONE> fetch --no-tags \"https://github.com/$DSH_SUPERAGENT_GITHUB_REPOSITORY.git\" \"${DSH_SUPERAGENT_GITHUB_PR_HEAD:-HEAD}:refs/dsh-superagent/$DSH_SUPERAGENT_TICKET\" && git -C <WORK_CLONE> worktree add -b \"agent/$DSH_SUPERAGENT_NAME\" \"$DSH_SUPERAGENT_DIR\" \"refs/dsh-superagent/$DSH_SUPERAGENT_TICKET\"",
  "blockedCommands": []
}
```

## 附录 C：命令速查

```sh
# 服务
cd <SRC_DIR> && python3 scripts/dsh-superagent {start|stop|restart|status}

# 配置
API=http://127.0.0.1:3082/orchestrator/api
curl -s -H 'x-orchestrator-request: 1' $API/settings
curl -s -H 'x-orchestrator-request: 1' $API/overview
curl -s -H 'x-orchestrator-request: 1' $API/tickets
curl -s -H 'x-orchestrator-request: 1' $API/sources

# GitHub
~/.local/bin/gh-agent api user --jq .login
~/.local/bin/gh-agent api repos/<REPO> --jq '.permissions'
~/.local/bin/gh-agent auth status

# 飞书
~/.local/bin/lark-bot api GET /open-apis/bot/v3/info
~/.local/bin/lark-bot im +chat-list --page-all --page-size 100
~/.local/bin/lark-bot im +chat-members-list --chat-id <CHAT_ID> --member-types user --page-all --page-limit 0
```

---

## 附录 D：本机实际部署记录（对外分享前请删除本节）

| 项 | 值 |
|---|---|
| 仓库 | `zhoumuzoom/consumer_center` |
| Agent GitHub 账号 | `zhoumuzoom` |
| 源码目录 | `~/src/dsh-superagent` |
| 工作克隆 | `~/src/consumer_center` |
| Ticket 目录 | `~/src/consumer-center-tickets` |
| stateDir | `~/.dsh/state/superagent` |
| 面板端口 | `3082` |
| 飞书应用 | `consumer-center-agent`（App ID `cli_a9215206bd389cc2`） |
| 飞书群 | `自动化测试`（`oc_3396d1458ccb5cdb700d6a90d38b9e04`） |
| 飞书用户 open_id | `ou_d882f8596a5497df92b71d409852af60` |
| 服务命令 | `cd ~/src/dsh-superagent && python3 scripts/dsh-superagent start` |

> 已跑通的验证 PR：
> - `docs: add agent-test.md test note` → `https://github.com/zhoumuzoom/consumer_center/pull/3`
> - `docs: 新增根目录 README` → `https://github.com/zhoumuzoom/consumer_center/pull/2`

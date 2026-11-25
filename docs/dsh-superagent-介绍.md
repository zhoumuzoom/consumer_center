# dsh-superagent 介绍

> 一个会自己接需求、自己改代码、自己提 PR 的编码 Agent。

---

## 一句话

把需求发到飞书群里 @ 它，它就在你的 GitHub 仓库里开分支、改代码、跑验证、提 PR，完成后回到飞书线程里回复结果。

---

## 它解决什么问题

传统 AI 编码助手是"你问一句、它答一句"，你要自己复制代码、自己提交、自己开 PR。

dsh-superagent 把这件事变成**工单化**：

| 传统方式 | dsh-superagent |
|---|---|
| 对话里给建议 | 直接在仓库里改文件 |
| 你手动复制粘贴 | 自动 commit + push |
| 你手动开 PR | 自动创建 PR，并附变更说明 |
| 上下文散落在聊天里 | 每个需求一张 Ticket，有独立目录、独立会话、独立分支 |
| 结果不可追溯 | PR、提交、回复都在原对话里可查 |

---

## 它是怎么工作的

```
飞书群 @机器人
      │
      ▼
  Policy 校验：这个人可以提需求吗？
      │
      ▼
  生成 Ticket
      │
      ▼
  Worker 在独立 git worktree 里执行
  ├─ 读代码、查文档、跑命令
  ├─ 修改实现
  ├─ 运行构建 / 测试
  └─ commit + push + 开 PR
      │
      ▼
  回到飞书线程回复：改了什么、PR 链接
      │
      ▼
  （可选）auto-merge workflow 自动合并
```

每个 Ticket 有：

- 独立的 **git worktree**，不会污染你的工作目录
- 独立的 **Agent Session**，上下文互不干扰
- 独立的 **分支**（默认 `agent/` 前缀）
- 完整的 **执行记录**（面板可查）

---

## 核心能力

- **多入口**：飞书群 @、飞书私聊、GitHub issue/评论（可选）
- **权限可控**：白名单决定谁能发起工作，规则决定哪些主动工作可以执行
- **隔离执行**：每个 Ticket 独立目录、独立分支，失败不影响主分支
- **可审计**：PR 描述、提交记录、飞书面板、状态库全链路留痕
- **人工兜底**：默认不自动合并；合并权、review 权始终在人手里
- **自动收尾**：可以被 GitHub Actions 自动合并且删除分支

---

## 适合谁

- 个人开发者：把零散小需求（改文案、加个小接口、补文档）交给它
- 小团队：统一从飞书提需求，PR 作为交付物
- 想体验"主动式 Agent"工作流的人

---

## 不适合谁

- 需要多租户、多仓库统一调度的生产平台
- 要求强合规、强审计的大型企业流程（它是研究原型）
- 完全没有 review 流程、直接上生产的场景

---

## 一个真实案例

1. 在飞书群 @机器人：`客户列表页面新增一个导出功能，纯前端导出即可`
2. 约 1 分钟内生成 Ticket，worker 开始执行
3. 自动创建分支 `agent/customer-list-export-...`，修改前端代码
4. 提交 PR，描述里写明变更内容与验证方式
5. 飞书线程收到回复和 PR 链接
6. 仓库的 auto-merge workflow 在 PR 就绪后自动 squash 合并并删除分支

整个过程不需要人工复制代码、不需要手动开 PR、不需要手动合并。

---

## 安全边界

- Agent 以**独立 GitHub 账号**提交，和工作账号隔离
- 运行在 `danger-full-access` 沙箱下，**建议使用独立 OS 账号或独立机器**
- 默认**不自动合并**；开启自动合并时建议配合分支保护和 CI
- 飞书机器人使用 **bot-only** 的 `lark-cli` 配置，不能以用户身份行动
- App Secret、GitHub token 等凭据通过独立配置目录隔离，不写入代码仓库

---

## 现状与限制

- 官方定位是**研究原型**（research prototype），不是生产级产品
- 一个部署实例只服务**一个 GitHub 仓库**
- 只支持 `github.com`，不支持 GitHub Enterprise Server
- 推理按 token 计费，复杂任务成本更高
- UI 类改动要求提供 Before/After 截图证据，否则会标记为"未完成"

---

## 快速开始

完整的安装、账号准备、飞书应用配置、Source/Policy/Executor 配置和故障排查，见配套文档：

**《dsh-superagent 部署手册》**

---

## 技术栈

| 层 | 组件 |
|---|---|
| Agent 运行时 | DeepSeek Harness (DSH) |
| Agent 形态 | DSH bundle（`dsh-superagent-prototype`） |
| 请求入口 | 飞书自建应用机器人 / GitHub |
| 代码托管 | GitHub（git worktree + PR） |
| 模型 | DeepSeek 账号（`deepseek-account`）或 API Key |
| 状态存储 | SQLite（`~/.dsh/state/superagent`） |
| 控制面板 | 本地 Web（默认 `127.0.0.1:3082`） |

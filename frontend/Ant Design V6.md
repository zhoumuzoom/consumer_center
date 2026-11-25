![](https://mdn.alipayobjects.com/huamei_fkc4p0/afts/img/A*Yqq6QIQHEVYAAAAAeuAAAAgAeobDAQ/original)

# Ant Design Pro v6 发布啦

距离 v5 发布已经过去快五年了。五年间，前端世界发生了翻天覆地的变化——React 18/19 带来了并发渲染，antd 从 v4 升级到了 v6，构建工具从 webpack 演进到了 Turbopack，CSS-in-JS 和 Tailwind 成为主流。v6 做了两件事：**全面拥抱最新技术栈**，以及**清理历史包袱**。

---

## 🌟 v6 背后的蚂蚁生态

- **[Ant Design V6](https://ant.design)** — 企业级设计系统。Pro v6 全面采用 antd 6，启用 CSS 变量模式（cssVar），支持 Default、Dark、Glass 等多种风格预设，渲染性能显著提升。
- **[Ant Design X](https://x.ant.design)** — AI 界面解决方案，基于 RICH 设计范式将 GUI 与自然语言交互有机融合。Pro v6 内置的 AI 助手页面基于 Ant Design X 构建，开箱即用。
- **[Ant Design CLI](https://github.com/ant-design/ant-design-cli)** — 官方命令行工具（`npx antd`），查询组件 API、获取示例代码、诊断项目配置一步到位。Pro v6 已内置集成，告别频繁翻文档。
- **[utoo](https://github.com/utooland/utoo)** — 基于 Turbopack 的 Rust 构建引擎，提供极速冷启动和毫秒级 HMR。Pro v6 默认使用 utoopack 构建，生产构建提速约 42%。

---

## 🚀 框架升级

### React 19 + antd 6

v6 基于 React 19 和 antd 6 构建。React 19 的并发特性与 Server Components，antd 6 的设计系统更新与 CSS 变量主题——这些在 v5 中还无法触及的能力，现在开箱即用。

### Umi Max 4

v5 已在使用 Umi Max，v6 升级到 @umijs/max 4，带来 utoopack 默认构建引擎和更完善的插件体系：

```diff
- import { useModel } from 'umi';
+ import { useModel, request, useAccess, getLocale, useIntl } from '@umijs/max';
```

### ProComponents v3

分散的 `@ant-design/pro-table`、`@ant-design/pro-form`、`@ant-design/pro-layout` 等多个包统一为 `@ant-design/pro-components` v3。一个包解决所有中后台组件需求，版本对齐不再头疼。

---

## 💎 样式体系重构

v5 的样式基于 Less，这在早期是明智的选择，但 Less 的维护活跃度持续下降。v6 全面迁移到现代 CSS 方案：

- **Tailwind CSS v4**：原子化 CSS，布局和间距不再需要写自定义样式
- **antd-style v4**：消费 antd Design Token 的 CSS-in-JS 方案，主题切换零成本
- **CSS Modules**：组件级样式隔离，避免命名冲突

同时启用 antd CSS 变量模式（cssVar），不仅支持动态主题切换，渲染性能也有提升。

---

## ⚡ 构建提速

v5 使用 mfsu（基于 webpack 5）做依赖预编译，v6 切换到 [utoopack](https://github.com/utooland/utoo)（Turbopack + Rust 核心），构建速度显著提升：

| 版本 | 构建工具 | 生产构建耗时 |
|------|---------|------------|
| v5.2.0 | webpack 5 (mfsu) | ~15.5s |
| v6.0.0 | utoopack (Turbopack) | ~9.0s |

> 测试环境：Apple M-series, Node 22

生产构建提速约 **42%**，日常开发中冷启动和 HMR 提升更为明显。同时启用了 `routePrefetch` 路由预加载，页面切换更加流畅。

---

## 🤖 AI 能力

v6 新增了 AI 助手页面，基于 [Ant Design X](https://x.ant.design/) 构建。这是一个开箱即用的聊天界面示例，展示了如何在 Pro 项目中集成 AI 对话能力。

---

## 🔧 工具链现代化

- **Biome** 替代 ESLint + Prettier — 一个工具搞定 lint + format，速度快 10 倍以上，告别多个配置文件的繁琐
- **React Query** 替代 useRequest — 缓存管理、请求去重、乐观更新、无限滚动，中后台最常见的需求都有了现成方案
- yorkie → **husky** · moment → **dayjs** · lodash → **原生 API / clsx** · Class 组件 → **函数式组件**
- Docker 配置移除 · pro-cli → **git clone + npm run simple**

---

## 🌐 Cloudflare Worker 后端

v6 的演示 API 迁移到 [Cloudflare Workers](https://workers.cloudflare.com/)（[Hono](https://hono.dev/) 框架）。`cloudflare-worker/` 目录独立于主项目，有自己的 `package.json` 和 `tsconfig.json`，可以独立部署。

---

## 📊 D3 地图可视化

监控页地图从 @antv/L7 替换为 D3 hex-tile 方案。L7 需要独立的 Mapbox token 才能工作，对演示和本地开发都不友好。D3 方案零配置，开箱即用。

---

## 📖 Cheatsheet 速查文档（取代 pro.ant.design）

v6 用项目内 Cheatsheet 文档全面取代了原有的 [pro.ant.design](https://pro.ant.design) 文档站。文档以 Markdown 文件内嵌在仓库中（`docs/cheatsheet.zh-CN.md` / `docs/cheatsheet.en-US.md`），通过 [@ant-design/x-markdown](https://x.ant.design/components/markdown) 在 Welcome 页面直接渲染，支持语法高亮和暗色模式。

覆盖内容：v6 新特性、快速开始、路由与菜单、布局、数据流、请求、权限、国际化、样式、测试与调试、FAQ——原来散布在 pro.ant.design 上的核心文档现在全部内置于项目中，开箱即查，无需跳转外部站点。

---

## 🔄 升级指南

v6 是一次大版本升级，涉及框架和依赖的全面更新，推荐**新建 v6 项目，逐步迁移业务代码**：

1. **依赖替换**：`umi` → `@umijs/max`，分散的 ProComponents → `@ant-design/pro-components`
2. **样式迁移**：Less → Tailwind + antd-style + CSS Modules
3. **导入路径**：`from 'umi'` → `from '@umijs/max'`
4. **请求方式**：`useRequest` → `@tanstack/react-query`
5. **代码检查**：ESLint + Prettier → Biome
6. **日期库**：moment → dayjs

```bash
git clone --depth=1 https://github.com/ant-design/ant-design-pro.git myapp
cd myapp
npm install
npm run simple  # 精简为最小模板
```

---

## 🙏 致谢

感谢所有为此版本做出贡献的开发者！v6 的开发跨越了将近五年，凝聚了 100 余位贡献者的智慧。特别感谢 @chenshuai2144 在 v6-beta 早期的奠基性工作，以及所有提交 PR、反馈问题、参与讨论的社区伙伴。

---

## 完整更新日志

### 核心框架升级

- 升级到 React 19 (#11474)
- 升级到 antd 6 (#11471, #11604, #11645, #11695)
- 升级到 Umi Max 4 / @umijs/max (#9956, #11589, #11686)
- ProComponents 统一为 @ant-design/pro-components v3
- @ant-design/icons 升级到 v6 (#11565)
- moment → dayjs
- lodash → 原生 API / clsx
- rc-util → @rc-component/util

### 样式体系

- Less 全部移除，迁移到 Tailwind CSS + antd-style + CSS Modules (#10938, #10941, #10970, #10971, #10972, #10973, #10975)
- Tailwind CSS v3 → Tailwind CSS v4 (#11576, #11668)
- 启用 antd CSS 变量模式 (cssVar) (#11499)
- AlibabaSans 作为默认字体 (#11499)
- 全局表单 variant 设为 filled 样式 (#11650)
- 移除 font smoothing CSS 属性 (#11663)

### 构建与工具链

- mfsu → utoopack (基于 Turbopack) (#11326)
- ESLint + Prettier → Biome (#11572, #11694)
- yorkie → husky
- umi-presets-pro → 直接使用插件 (#11651)
- TypeScript 升级到 6.x (#11637, #11696)
- Docker 配置移除
- pro-cli 移除，使用 git clone + npm run simple (#11617)
- CI 从 bun 迁移到 utoo (#11639)
- Dependabot 配置分组，忽略 patch 版本自动更新 (#11714)
- npm scripts 优化 (#11719, #11718)

### 新增功能

- AI 助手页面 (@ant-design/x) (#11681)
- React Query 数据请求 (@tanstack/react-query) (#11665)
- Cloudflare Worker API (Hono) (#11655, #11659)
- D3 hex-tile 地图替代 L7 (#11709)
- 路由预加载 (routePrefetch) (#11652)
- 骨架屏 Loading 组件 (#11505, #11510)
- Cheatsheet 速查文档 (#11712)
- 版本号和 commit hash 显示在页脚 (#11660)
- 历史版本链接 (#11725)
- exportStatic 静态导出支持 (#11512)
- 错误页面卡片包裹 (#11513)
- CLAUDE.md / AGENTS.md — 为 AI 编码助手提供项目指引 (#11727)
- Header 右上角样式统一，更新头像菜单 (#11721)
- CI 环境变量支持 (#11672)
- Header 操作项从头像下拉菜单中提取为独立顶部组件，语言切换扩展到全部 8 种语言 (#11733)
- Welcome 页面添加 V6 庆祝渐变标题 (#11731)

### 架构重构

- Class 组件 → 函数式组件 (#11616)
- 提取共享组件 (#11692)
- 提取共享 Mock 工具 (#11674)
- 请求管线和 React Query 优化 (#11693)
- 减小包体积，替换重度依赖 (#11698)
- 移除未使用页面和路由 (#11506, #11507)
- 移除 Fragment 包装，简化 Jest 配置 (#11514)

### 文档与项目

- README 更新 badges、链接和过时内容 (#11726)
- Cheatsheet 布局优化，新增 Node.js badge (#11723)
- 去除冗余 .ts wrapper，直接导入 .md (#11717)
- README 和 Cheatsheet 更新 v6 发布信息（技术栈、AI 助手、模板列表等）

### 问题修复

- 修复首次加载白屏问题 (#10347)
- 修复刷新时标题不显示 (#10455)
- 修复 antd 时间选择器中文语言包缺失 (#10440)
- 修复 Hash 路由在 getInitialState() 中被忽略 (#10352)
- 修复登录页 title={false} 不生效 (#11364)
- 修复 SearchList 标签页路由异常 (#10970)
- 修复 HeaderDropdown 响应式样式不生效 (#10669)
- 修复暗黑主题样式问题 (#11645)
- 修复 CORS 不支持多级子域名 (#11664)
- 修复 Tailwind CSS 因 Less import 顺序未生效 (#11662)
- 修复 Monitor 页面地图显示 (#11676)
- 修复 mock 路由重复注册警告 (#11657)
- 修复右侧内容区边距和边框问题 (#11646, #11698)
- 修复 ModalForm 触发器类型定义 (#11648)
- 修复安全漏洞 (#11641)
- 修复 headScripts 链接与 publicPath 不匹配 (#11289)
- 修复 404 路由 (#11528, #11096, #10007, #11730)
- 修复 404 页面白屏 (#11729)
- 修复 404 页面横向滚动条 (#10194)
- 修复 Select.Options → options (#10971)
- 修复消息提示不自动关闭 (#10209)
- 修复 RightContent navTheme 条件值 (#10210)
- 修复登录重定向原始 URL 保留 (#11722)

### 国际化

- 新增西班牙语文档 (#10999)
- 新增土耳其语翻译 (#10478)
- 404 页面多语言支持 (#11064)
- 修复 zh-TW 翻译错误 (#11093)
- 修复 SettingDrawer 翻译错误 (#11037)

### 依赖更新

- @umijs/max → 4.6.49
- @biomejs/biome → 2.4.13
- express → 5.2.1
- Jest → 30.x
- cross-env → 10.1.0
- gh-pages → 6.3.0
- highlight.js（新增）

**Full Changelog**: https://github.com/ant-design/ant-design-pro/compare/v5.2.0...v6.0.0

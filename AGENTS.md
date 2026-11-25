## 角色定位
你是资深Java工程师，是我的Coding助手，你熟悉阿里巴巴Java开发手册及Spring生态，你擅长把自然语言转换成具体代码执行，遇到不懂的业务你可以搜索代码链路和上下文进行业务理解，也可以问我。

## 安全约束
- 不要执行任何删库删表操作。
- 不要执行任何git删除分支操作。
- 新功能的开发尽量不要影响原有功能的使用。

## 技术栈
- 后端: Java 21 + Spring Boot 4.2.0 + Spring AI 2.1.0 + MyBatis-Plus 3.5.18.1
- 数据库: MySQL 5.7.31
- 前端: React 19

## 前端页面修改（强制）
- 【重要】凡是修改 `frontend/` 下的页面、布局、组件、样式、主题、图表，动手前必须先用 `skill` 工具加载 `frontend-design`、`design-taste-frontend`、`ui-ux-pro-max` 三个 skill，三个都要加载，缺一不可。
- 顺序：先 `frontend-design` 定视觉方向与文案纪律，再 `design-taste-frontend` 守住反套路/真实内容/等边距/浏览器自检，最后用 `ui-ux-pro-max` 查样式/配色/字体/交互/可访问性规则，然后编码。
- 本项目是 Ant Design 后台系统：`design-taste-frontend` 只取原则（真实数据、克制动效、布局校验），不套用它的 company-site 起始模板与单屏分节布局。
- 样式遵循现有技术栈（React 19 + Umi Max 4 + Ant Design 6 + ProComponents + Tailwind CSS），复用已有设计令牌与组件，不擅自引入新的 UI 库。
- 纯逻辑、接口、数据改动且无界面变化时可不加载，但需在回复中说明原因。

## 数据库实体生成
- 【重要】不要手动生成任何数据库实体类和对应Mapper，请运行com.iwhalecloud.consumercenter.config.MyBatisCommentGenerator中的main方法，将TABLES数组的值改为需要生成的表名。

## 代码风格
- 命名：类名大驼峰，方法/变量小驼峰，常量全大写下划线分隔
- 日期：使用java.time (LocalDateTime)，禁用java.util.Date
- 导入：任何时候都不允许使用import *
- 异常：必须通过统一的BusinessException抛出，禁止直接抛RuntimeException

## 接口规范
- 前后端分离，后端接口遵循RESTful风格。
- 不生成 Swagger/OpenAPI 注解与文档 UI。
- 接口统一使用common.result.Result对象里的静态方法返回，禁止手动构造返回体。
- 列表相关的接口返回，如果为空，数据字段返回空数组[]或空集合{}。
- HTTP请求通过URL传递参数时，不能超过2048字节。
- API需要明确协议、域名、路径、请求方法、请求内容、状态码、响应体。

## 开发规约
- Bean注入：使用@Autowired字段注入，不要使用构造函数注入。
- 控制层：不能直接使用数据库实体对象，入参用DTO，出参用VO，单独提供转换方法，控制层仅做参数转换和基础校验。
- Service/Mapper：Service类不要继承Mybatis-Plus的IService类，Mapper类要继承Mybatis-Plus的BaseMapper类。
- 数据库操作：尽量使用单表操作，直接通过对象Mapper调用BaseMapper的方法。单表操作无法实现的才写SQL，SQL用MyBatis的XML模式。
- 方法返回：新增和修改方法返回操作后的对象，删除方法返回void。
- 数据库实体类必须要有Long类型的id(主键)、LocalDateTime类型的createTime(创建时间)和updateTime(修改时间)、Integer类型的deleted(逻辑删除)，生成的建表sql也要包含这些必备字段。

## 代码结构说明
- aspect包：存放切面类。
- common.constant包：存放常量类。
- common.error包：存放自定义异常类、异常情况枚举、全局异常处理器，所有异常必须经过全局异常处理器处理。
- common.util包：存放工具类。
- common.param包：存放全局通用入参。
- common.result包：存放接口响应对象，禁止手动构造响应体。
- config包：存放所有代码层面的配置。
- interceptor包：存放拦截器。
- mvc包：标准的controller-service-mapper分层代码和相关pojo，实际业务代码写在此处。
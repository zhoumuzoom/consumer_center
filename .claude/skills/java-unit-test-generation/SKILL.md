---
name: java-unit-test-generation
description: 用于为 Spring Boot 控制器和服务生成 Java 单元测试，要求全分支覆盖、Mock 依赖、清晰标注异常测试
---

# Java 单元测试生成

## 概述

为 Spring Boot 应用生成全面的 Java 单元测试，遵循**双层测试架构**（Controller + Service），实现**全分支覆盖**并**清晰标注异常测试**。
注意：一定要按SKILL要求，生成双层测试（controller+service）。

## 何时使用

- 用户要求为 Java Spring Boot 代码创建/生成单元测试
- 用户提到测试控制器、服务或其他 Spring 组件
- 用户要求 Java 代码的全分支覆盖
- 用户需要 Mock 数据库操作或外部服务调用
- 用户要求异常测试清晰标注

## 核心模式：双层测试架构

### 第一层：控制器测试（MockMvc）

```java
@WebMvcTest(MyController.class)
@Import(GlobalExceptionHandler.class)
class MyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean  // Spring Boot 3.4+；3.3 及更早版本使用 @MockBean
    private MyService myService;

    @Test
    @DisplayName("方法名-正常场景描述")
    void methodName_success() throws Exception {
        when(myService.someMethod(any())).thenReturn(expectedResult);

        mockMvc.perform(get("/api/resource")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.field").value("expected"));
    }
}
```

**要点：**
- `@WebMvcTest` 只加载 Web 层（不加载 service、repository）
- `@Import(GlobalExceptionHandler.class)` 在测试中启用异常处理
- 使用 `@MockitoBean`（Spring Boot 3.4+）或 `@MockBean`（更早版本）Mock 服务
- 测试请求路由、参数绑定、校验、响应结构

### 第二层：服务测试（Mockito）

```java
@ExtendWith(MockitoExtension.class)
class MyServiceImplTest {
    @InjectMocks
    private MyServiceImpl myService;

    @Mock
    private MyMapper myMapper;

    @Mock
    private ExternalService externalService;

    @Test
    @DisplayName("正常场景描述")
    void methodName_success() {
        when(myMapper.selectById(1L)).thenReturn(mockEntity);

        Result result = myService.methodName(1L);

        assertNotNull(result);
        verify(myMapper).selectById(1L);
    }
}
```

**要点：**
- `@ExtendWith(MockitoExtension.class)` 启用 Mockito 注解
- `@InjectMocks` 将 Mock 注入到被测服务中
- `@Mock` 用于依赖（Mapper、外部服务、Repository）
- 测试所有业务逻辑分支和异常路径

## 分支覆盖规则（关键）

**每个 `if` 分支都需要独立的测试用例。**

对于每个 `if` 语句：
1. 一个测试让条件为 **TRUE**（进入分支）
2. 一个测试让条件为 **FALSE**（跳过分支）
3. FALSE 情况通常由"基线"测试覆盖（所有条件为 false）

### 示例：包含 3 个 if 分支的方法

```java
public Result method(QueryDTO dto) {
    if (dto.getA() != null) { ... }  // 分支 1
    if (dto.getB() != null) { ... }  // 分支 2
    if (dto.getC() != null) { ... }  // 分支 3
}
```

**必需的测试：**
- 测试 1：所有条件都不满足（全为 null）- 基线
- 测试 2：仅 A 不为 null
- 测试 3：仅 B 不为 null
- 测试 4：仅 C 不为 null
- 测试 5：所有条件都满足（可选，为了完整性）

**不要**将多个分支合并到一个测试中。每个分支需要独立测试。

## 异常测试标注（必须）

所有异常测试用例**必须**用"异常测试-"前缀清晰标注：

```java
@Test
@DisplayName("异常测试-客户不存在时抛出RuntimeException")
void getDetail_customerNotFound() {
    when(mapper.selectById(999L)).thenReturn(null);

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> service.getDetail(999L));

    assertEquals("客户不存在", ex.getMessage());
}
```

**原因：** 异常测试验证错误处理。清晰的标注便于查找和评审。

## 测试组织

使用 `@Nested` 类按方法分组测试：

```java
@Nested
@DisplayName("客户详情查询测试")
class GetDetailTest {
    @Test
    @DisplayName("客户存在-正常返回详情")
    void getDetail_success() { ... }

    @Test
    @DisplayName("异常测试-客户不存在-抛出RuntimeException")
    void getDetail_customerNotFound() { ... }
}
```

**好处：**
- IDE 测试运行器中分组清晰
- 便于运行某个方法的所有测试
- 大型测试类组织更好

## Mock 策略

| 依赖类型 | Mock 方式 | 示例 |
|---------|----------|------|
| 数据库 Mapper | `@Mock` | `@Mock CustomerMapper mapper` |
| 外部服务 | `@Mock` | `@Mock PaymentService paymentService` |
| Repository | `@Mock` | `@Mock UserRepository repo` |
| 被测类 | `@InjectMocks` | `@InjectMocks CustomerServiceImpl service` |

**不要 Mock：**
- 被测类本身（除非有复杂依赖）
- 没有外部依赖的工具方法
- 值对象或 DTO

## MyBatis-Plus Mock 模式（关键）

本项目使用 MyBatis-Plus 作为 ORM 框架，测试中需要特别注意以下模式。

### Page 对象构建

`selectPage` 返回 `Page<T>`，需要手动构建分页结果：

```java
// 构建模拟的分页结果
Page<Customer> page = new Page<>(1, 10);
page.setRecords(List.of(buildCustomer(1L, (byte) 0)));
page.setTotal(1);

when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
    .thenReturn(page);
```

**注意：**
- `new Page<>(current, size)` 参数是分页参数，不是数据库参数
- 必须调用 `setRecords()` 和 `setTotal()`，否则返回空结果
- `selectPage` 的参数签名是 `(Page, Wrapper)`，两个参数都要 mock

### LambdaQueryWrapper 匹配

`LambdaQueryWrapper` 是**具体类**（不是接口），内部状态复杂，测试中**不要尝试断言 wrapper 的具体内容**。使用 `any()` 匹配：

```java
when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
    .thenReturn(page);

// 如果需要验证 selectPage 被调用了
verify(customerMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
```

### selectById / updateById / deleteById / insert

这些是 MyBatis-Plus BaseMapper 的标准方法，直接 mock 即可：

```java
// selectById - 查询
when(customerMapper.selectById(1L)).thenReturn(mockEntity);
when(customerMapper.selectById(999L)).thenReturn(null);  // 不存在

// insert - 返回影响行数
when(customerMapper.insert(any(Customer.class))).thenReturn(1);

// updateById - 返回影响行数
when(customerMapper.updateById(any(Customer.class))).thenReturn(1);

// deleteById - 返回影响行数
when(customerMapper.deleteById(1L)).thenReturn(1);
```

### 多 Mapper 依赖

当 Service 依赖多个 Mapper 或多个 Service 时，每个依赖都需要一个 `@Mock`：

```java
@ExtendWith(MockitoExtension.class)
class DecisionMakerServiceImplTest {
    @InjectMocks
    private DecisionMakerServiceImpl service;

    @Mock
    private DecisionMakerMapper decisionMakerMapper;

    @Mock
    private DecisionChainMapper decisionChainMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private OperationLogService operationLogService;
}
```

`@InjectMocks` 会按类型匹配注入，Mock 的数量和类型必须与 `@Autowired` 字段一一对应。

## 写操作测试模式（关键）

### ArgumentCaptor 验证写入数据

对于 `create`、`update` 等写操作，需要验证传给 Mapper 的 entity 字段是否正确设置。使用 `ArgumentCaptor` 捕获参数：

```java
@Test
@DisplayName("新增客户-自动生成customerCode和默认状态")
void create_success() {
    CustomerCreateDTO createDTO = new CustomerCreateDTO();
    createDTO.setCustomerName("新测试客户");
    createDTO.setIndustry("IT");

    when(customerMapper.insert(any(Customer.class))).thenReturn(1);
    doNothing().when(operationLogService).log(
        any(), anyString(), anyString(), anyString());

    CustomerVO result = customerService.create(createDTO);

    assertNotNull(result);
    assertEquals("新测试客户", result.getCustomerName());

    // 用 ArgumentCaptor 捕获 insert 的参数，验证自动生成的字段
    ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
    verify(customerMapper).insert(captor.capture());
    Customer saved = captor.getValue();
    assertTrue(saved.getCustomerCode().startsWith("CUS"));
    assertEquals((byte) 0, saved.getStatus());
    assertEquals("手动录入", saved.getSource());
    assertNotNull(saved.getJoinTime());
}
```

**要点：**
- `ArgumentCaptor.forClass(Customer.class)` 创建捕获器
- `captor.capture()` 替代 `any()` 放在 `verify()` 中
- `captor.getValue()` 获取实际传入的对象，逐字段断言

### void 方法的 Mock 和验证

Service 调用的 `operationLogService.log(...)` 等 void 方法，需要特殊处理：

```java
import static org.mockito.Mockito.doNothing;

// Mock void 方法（通常 Mockito 对 void 方法默认 doNothing，显式声明更清晰）
doNothing().when(operationLogService).log(
    any(), anyString(), anyString(), anyString());

// 验证 void 方法被调用
verify(operationLogService).log(
    eq(customerId), eq("customer"), eq("CREATE"), anyString());

// 验证 void 方法未被调用（异常场景）
verify(operationLogService, never()).log(
    any(), anyString(), anyString(), anyString());
```

**注意：** 如果 void 方法有多个重载，使用 `any(具体类型.class)` 消除歧义。

## 私有转换方法覆盖策略

私有方法（如 `convertToVO`、`getStatusDesc`）**不要直接测试**，而是通过公共方法的路径间接覆盖。

### switch 表达式 / 枚举映射

对于 `getStatusDesc` 这类 switch 表达式：

```java
private String getStatusDesc(Byte status) {
    if (status == null) return "未知";
    return switch (status) {
        case 0 -> "未看管";
        case 1 -> "已看管";
        case 2 -> "已剔除";
        default -> "未知";
    };
}
```

**覆盖策略：** 通过不同 `status` 值的 entity 调用 `getDetail` 或 `pageList`，每个 case 分支一个测试：

- `status = null` → 覆盖 `null` 检查
- `status = 0` → 覆盖 `case 0`
- `status = 1` → 覆盖 `case 1`
- `status = 2` → 覆盖 `case 2`
- `status = 99` → 覆盖 `default`

在测试数据构建器中设置不同的 `status` 值即可：

```java
@Nested
@DisplayName("状态描述转换覆盖")
class StatusDescTest {
    @Test
    @DisplayName("status为0-状态描述为未看管")
    void getDetail_statusNull_returnsUnknown() {
        when(customerMapper.selectById(1L)).thenReturn(buildCustomer(1L, (byte) 0));
        CustomerDetailVO result = customerService.getDetail(1L);
        assertEquals("未看管", result.getStatusDesc());
    }

    @Test
    @DisplayName("status为99-状态描述为未知（default分支）")
    void getDetail_status99_returnsUnknown() {
        when(customerMapper.selectById(1L)).thenReturn(buildCustomer(1L, (byte) 99));
        CustomerDetailVO result = customerService.getDetail(1L);
        assertEquals("未知", result.getStatusDesc());
    }
}
```

## Spring Boot 版本兼容性

### Spring Boot 3.4 ~ 3.5+（当前版本）

```java
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(MyController.class)
class MyControllerTest {
    @MockitoBean
    private MyService myService;
}
```

**Spring Boot 3.5.x 注意事项：**
- `@MockitoBean` 包路径：`org.springframework.test.context.bean.override.mockito.MockitoBean`
- 3.4 引入，3.5 沿用，包路径不变
- `@MockBean`（旧版）已在 3.4 标记废弃，3.5 中仍可用但不推荐

### Spring Boot 3.3 及更早版本

```java
import org.springframework.boot.test.mock.bean.MockBean;

@WebMvcTest(MyController.class)
class MyControllerTest {
    @MockBean
    private MyService myService;
}
```

**注意：** `@MockBean` 在 Spring Boot 3.4 中被废弃，替换为 `@MockitoBean`。

## 校验依赖

如果代码使用了 `@Valid`、`@NotBlank`、`@NotNull` 等注解，确保 pom.xml 中包含 `spring-boot-starter-validation`：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**没有此依赖：**
- 校验注解不会生效
- 期望校验失败的测试会失败
- `@Valid` 不会触发 `MethodArgumentNotValidException`

## 测试用例模板

### 控制器测试 - 正常路径

```java
@Test
@DisplayName("方法名-正常场景描述")
void methodName_success() throws Exception {
    // 准备
    when(service.method(any())).thenReturn(expectedResult);

    // 执行 & 断言
    mockMvc.perform(post("/api/resource")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.field").value("expected"));
}
```

### 控制器测试 - 异常路径

```java
@Test
@DisplayName("异常测试-异常场景描述")
void methodName_exception() throws Exception {
    when(service.method(any())).thenThrow(new RuntimeException("错误信息"));

    mockMvc.perform(get("/api/resource/999"))
            .andExpect(status().isOk())  // HTTP 200，因为 @ResponseStatus
            .andExpect(jsonPath("$.code").value(5001))  // 业务错误码
            .andExpect(jsonPath("$.message").value("系统异常"));
}
```

### 服务测试 - 分支覆盖

```java
@Test
@DisplayName("仅keyword条件-进入keyword分支")
void method_withKeyword() {
    QueryDTO dto = new QueryDTO();
    dto.setKeyword("test");
    // 其他字段为 null

    when(mapper.select(any())).thenReturn(mockResult);

    Result result = service.method(dto);

    assertNotNull(result);
    verify(mapper).select(any());
}
```

### 服务测试 - 异常路径

```java
@Test
@DisplayName("异常测试-实体不存在-抛出RuntimeException")
void method_entityNotFound() {
    when(mapper.selectById(999L)).thenReturn(null);

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> service.method(999L));

    assertEquals("实体不存在", ex.getMessage());
    verify(mapper, never()).updateById(any());  // 验证没有副作用
}
```

## 常见错误

| 错误 | 修正 |
|------|------|
| 只测试正常路径 | 为每个 null/未找到检查添加异常测试 |
| 将多个分支合并到一个测试 | 拆分为独立测试，每个分支一个 |
| 在 Spring Boot 3.4+ 中使用 `@MockBean` | 改用 `@MockitoBean` |
| 忘记校验依赖 | 在 pom.xml 中添加 `spring-boot-starter-validation` |
| 不标注异常测试 | @DisplayName 前缀加上"异常测试-" |
| Mock 被测类本身 | 只 Mock 依赖，不要 Mock 被测服务 |
| 重载方法使用 `any()` | 使用 `any(具体类型.class)` 消除歧义 |
| 不验证副作用 | 使用 `verify()` 检查 mapper/service 调用 |
| 直接测试私有方法 | 通过公共方法间接测试 |
| 不验证写操作参数 | 使用 `ArgumentCaptor` 捕获 insert/update 的 entity 并断言字段 |
| 忽略 void 方法副作用 | 用 `verify(operationLogService).log(...)` 验证日志/审计调用 |
| switch 分支漏测 | 为每个 case（含 default 和 null）构造不同测试数据 |
| 多 Mapper 服务漏 Mock | 每个 `@Autowired` 依赖都需要对应 `@Mock`，否则 `@InjectMocks` 注入失败 |

## 检查清单

提交测试前，请验证：

- [ ] 控制器测试使用 `@WebMvcTest` + MockMvc
- [ ] 服务测试使用 `@ExtendWith(MockitoExtension.class)`
- [ ] 每个 `if` 分支都有独立测试用例
- [ ] 每个异常路径都有标注"异常测试-"的测试
- [ ] 所有数据库操作都已 Mock
- [ ] 所有外部服务调用都已 Mock
- [ ] `@Nested` 类按方法分组测试
- [ ] `@DisplayName` 清晰描述（中文即可）
- [ ] 测试编译通过且运行成功
- [ ] 没有不必要的 Mock（只 Mock 依赖）
- [ ] 使用 `@Valid` 时已添加校验依赖
- [ ] 写操作使用 `ArgumentCaptor` 验证传入 entity 的字段
- [ ] void 方法（日志、审计）已用 `verify()` 验证调用
- [ ] switch 表达式的每个 case（含 default 和 null）都有覆盖
- [ ] 多 Mapper 服务的每个依赖都已 `@Mock`

## 快速参考

| 内容 | 方法 |
|------|------|
| 控制器测试 | `@WebMvcTest` + `@MockitoBean` + `MockMvc` |
| 服务测试 | `@ExtendWith(MockitoExtension.class)` + `@InjectMocks` + `@Mock` |
| 分支覆盖 | 每个 `if` 分支一个测试（TRUE/FALSE） |
| 异常测试 | `@DisplayName("异常测试-...")` + `assertThrows()` |
| 校验依赖 | 添加 `spring-boot-starter-validation` |
| Spring Boot 3.4+ | 使用 `@MockitoBean`（不用 `@MockBean`） |
| 分组测试 | `@Nested` + `@DisplayName` |
| 验证调用 | `verify(mock).method(any())` |
| 验证未调用 | `verify(mock, never()).method(any())` |
| 验证写入参数 | `ArgumentCaptor.forClass(Entity.class)` + `captor.capture()` + `captor.getValue()` |
| void 方法 Mock | `doNothing().when(mock).voidMethod(args)` |
| void 方法验证 | `verify(mock).voidMethod(eq(...), anyString(), ...)` |
| MyBatis-Plus 分页 | `new Page<>(current, size)` + `setRecords()` + `setTotal()` |
| LambdaQueryWrapper | `any(LambdaQueryWrapper.class)` 匹配，不要断言 wrapper 内容 |
| switch 覆盖 | 每个 case + null + default 各一个测试，通过不同入参走公共方法 |
| 多依赖注入 | 每个 `@Autowired` 字段对应一个 `@Mock`，`@InjectMocks` 按类型注入 |

## 参考示例

本次会话中创建的测试文件：
- `CustomerControllerTest.java` - 控制器层测试（13 个用例）
- `CustomerServiceImplTest.java` - 服务层测试（24 个用例，全分支覆盖）

两个文件都展示了：
- 双层测试架构
- 分支独立测试规则
- 异常测试标注
- @Nested 组织方式
- Mock 策略
- 中文 @DisplayName

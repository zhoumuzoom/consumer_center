# Spring Boot 项目包架构模板

新建空项目时，按以下结构创建基础包（基础包路径可自定义）：

```
${basePackage}
├── aspect/                     # 切面类
├── common/                     # 通用包，包内存放各种全局需要的类或资源
│   ├── constant/               # 常量类
│   ├── error/                  # 异常类、异常枚举、全局异常处理器
│   ├── util/                   # 工具类
│   └── result/                 # 接口响应对象
├── config/                     # 配置类，存放一些需要手动配置的配置项
│   └── interceptor/            # 拦截器
└── mvc/
    ├── controller/             # 控制层
    ├── service/                # 服务接口
    │   └── impl/              # 服务实现
    ├── mapper/                # 数据访问层
    └── pojo/
        ├── context/           # 上下文对象
        ├── convert/           # 对象转换器
        ├── dto/               # 入参 DTO
        ├── entity/            # 数据库实体
        └── vo/                # 出参 VO
```

## 快速创建命令

```bash
# 设置基础包路径（示例）
BASE="src/main/java/com/example/project"

# 创建目录结构
mkdir -p $BASE/{aspect,common/{constant,error,util,result},config/interceptor,mvc/{controller,service/impl,mapper,pojo/{context,convert,dto,entity,vo}}}
```

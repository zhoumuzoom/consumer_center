package com.iwhalecloud.customer.config;

import org.jspecify.annotations.NonNull;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.ClientGeneratorConfiguration;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.ModelGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.Property;
import org.mybatis.generator.config.TableConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * MyBatisGenerator 2.0.0，适用于Java 17+
 * 启动器、注释配置、插件配置
 * 作用：自动生成实体类和Mapper接口类，自动生成表注释和字段注释，生成的实体类和Mapper接口携带MyBatis-Plus注解
 * 使用方式：
 * 1.添加mybatis-generator依赖
 * <dependency>
 *   <groupId>org.mybatis.generator</groupId>
 *   <artifactId>mybatis-generator-core</artifactId>
 *   <version>2.0.0</version>
 * </dependency>
 * 2.修改数据库链接配置
 * 3.修改生成路径配置
 * 4.修改需要生成的表名
 */
public class MyBatisCommentGenerator extends PluginAdapter implements CommentGenerator {

    // ==================== 数据库链接配置 ====================
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://119.23.9.44:3306/iwhalecloud";
    private static final String JDBC_USER = "bhome";
    private static final String JDBC_PASSWORD = "bhome123";

    // ==================== 生成路径配置 ====================
    private static final String ENTITY_PACKAGE = "com.iwhalecloud.customer.mvc.pojo.entity";
    private static final String MAPPER_PACKAGE = "com.iwhalecloud.customer.mvc.mapper";

    // ==================== 生成目标表配置 ====================
    private static final String[] TABLES = {
            "customer",
            "customer_tree_node",
            "industry_tree_node",
            "customer_industry_tag",
            "decision_maker",
            "decision_chain",
            "customer_branch",
            "customer_interaction",
            "task",
            "operation_log",
            "group_report_record",
            "report_schedule_config"
    };

    /**
     * MyBatisGenerator 启动方法 - Java配置方式
     */
    public static void main(String[] args) {
        System.out.println("MyBatisGenerator开始执行，目标表:" + String.join("、", TABLES));
        try {
            Configuration configuration = buildConfiguration();

            MyBatisGenerator myBatisGenerator = new MyBatisGenerator.Builder()
                    .withConfiguration(configuration)
                    .withOverwriteEnabled(true)
                    .build();
            myBatisGenerator.generateAndWrite();

            System.out.println("MyBatisGenerator执行成功，生成表:" + String.join("、", TABLES));
        } catch (Exception e) {
            System.err.printf("MyBatisGenerator执行失败: %s%n", e.getMessage());
        }
    }

    /**
     * 构建Configuration配置
     */
    private static Configuration buildConfiguration() {
        // 插件
        PluginConfiguration pluginConfig = new PluginConfiguration.Builder()
                .withConfigurationType(MyBatisCommentGenerator.class.getName())
                .withProperty(new Property("enabled", "true"))
                .build();

        // 配置实体类注释生成器
        CommentGeneratorConfiguration commentGeneratorConfig = new CommentGeneratorConfiguration.Builder()
                .withConfigurationType(MyBatisCommentGenerator.class.getName())
                .build();

        // 配置数据库连接参数
        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration.Builder()
                .withDriverClass(JDBC_DRIVER)
                .withConnectionURL(JDBC_URL)
                .withUserId(JDBC_USER)
                .withPassword(JDBC_PASSWORD)
                .withProperty(new Property("useInformationSchema", "true"))
                .withProperty(new Property("nullCatalogMeansCurrent", "true"))
                .build();

        // 配置实体类生成路径
        ModelGeneratorConfiguration javaModelConfig = new ModelGeneratorConfiguration.Builder()
                .withTargetProject("src/main/java")
                .withTargetPackage(ENTITY_PACKAGE)
                .build();

        // 配置生成的Mapper接口
        ClientGeneratorConfiguration javaClientGeneratorConfig = new ClientGeneratorConfiguration.Builder()
                .withTargetProject("src/main/java")
                .withTargetPackage(MAPPER_PACKAGE)
                .withLegacyClientType(ClientGeneratorConfiguration.LegacyClientType.ANNOTATED_MAPPER)
                .build();

        // 配置需要生成的表
        List<TableConfiguration> tableConfigs = Arrays.stream(TABLES)
                .map(tableName -> new TableConfiguration.Builder()
                        .withTableName(tableName)
                        .build())
                .toList();

        // 构建Context
        Context.Builder contextBuilder = new Context.Builder()
                .withId("MySQL")
                .withDefaultModelType(ModelType.FLAT)
                .withTargetRuntime("MyBatis3Simple")
                .withPluginConfiguration(pluginConfig)
                .withCommentGeneratorConfiguration(commentGeneratorConfig)
                .withJdbcConnectionConfiguration(jdbcConfig)
                .withModelGeneratorConfiguration(javaModelConfig)
                .withClientGeneratorConfiguration(javaClientGeneratorConfig);
        for (TableConfiguration tableConfig : tableConfigs) {
            contextBuilder.withTableConfiguration(tableConfig);
        }
        Context context = contextBuilder.build();

        Configuration configuration = new Configuration();
        configuration.addContext(context);
        return configuration;
    }

    @Override
    public void addConfigurationProperties(@NonNull Properties properties) {
    }

    /**
     * 实体类注释
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 注释
        String remarks = introspectedTable.getRemarks().orElse("");
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + remarks);
        topLevelClass.addJavaDocLine(" */");

        // Lombok
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");

        // MyBatis-Plus表名注解
        topLevelClass.addImportedType("com.baomidou.mybatisplus.annotation.TableName");
        topLevelClass.addAnnotation("@TableName(\"" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\")");

        // 实现Serializable接口
        topLevelClass.addImportedType("java.io.Serializable");
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType("java.io.Serializable"));

        // 添加serialVersionUID字段
        topLevelClass.addImportedType("java.io.Serial");
        Field serialUid = new Field("serialVersionUID", new FullyQualifiedJavaType("long"));
        serialUid.addJavaDocLine("");
        serialUid.addAnnotation("@Serial");
        serialUid.setVisibility(JavaVisibility.PRIVATE);
        serialUid.setStatic(true);
        serialUid.setFinal(true);
        serialUid.setInitializationString("1L");
        topLevelClass.addField(serialUid);

        // 添加所有可能需要的MyBatis-Plus导入
        topLevelClass.addImportedType("com.baomidou.mybatisplus.annotation.TableLogic");
        topLevelClass.addImportedType("com.baomidou.mybatisplus.annotation.Version");
    }

    /**
     * 实体类字段注释
     */
    @Override
    public void addFieldAnnotation(@NonNull Field field, @NonNull IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, @NonNull Set<FullyQualifiedJavaType> imports) {
        String columnName = introspectedColumn.getActualColumnName();
        String remarks = introspectedColumn.getRemarks().orElse("");

        // 添加字段javadoc注释（数据库列注释）
        if (!remarks.isEmpty()) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + remarks);
            field.addJavaDocLine(" */");
        }

        // 逻辑删除字段
        if ("deleted".equalsIgnoreCase(columnName)) {
            imports.add(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.TableLogic"));
            field.addAnnotation("@TableLogic");
        }

        // 乐观锁字段
        if ("version".equalsIgnoreCase(columnName)) {
            imports.add(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.Version"));
            field.addAnnotation("@Version");
        }

        // 主键注解
//        if (PRIMARY_KEY.equalsIgnoreCase(columnName)) {
//            field.addAnnotation("@TableId(value = \"" + columnName + "\", type = IdType.AUTO)");
//            return;
//        }
//
        // 创建时间 - 自动填充
//        if (CREATE_TIME_FIELD.equalsIgnoreCase(columnName)) {
//            field.addAnnotation("@TableField(fill = FieldFill.INSERT)");
//            return;
//        }
//
        // 更新时间 - 自动填充
//        if (UPDATE_TIME_FIELD.equalsIgnoreCase(columnName)) {
//            field.addAnnotation("@TableField(fill = FieldFill.INSERT_UPDATE)");
//        }
    }

    /*
    以下方法是阻止 MybatisGenerator 生成的实体类含有 get/set
    因为使用了 lombok，因此生成的实体类不需要 get/set
    返回 false 表示不生成该方法
     */

    @Override
    public boolean validate(@NonNull List<String> warnings) {
        return true;
    }

    /**
     * 拦截 Getter 方法的生成，返回 false 表示不生成
     */
    @Override
    public boolean modelGetterMethodGenerated(@NonNull Method method, @NonNull TopLevelClass topLevelClass, @NonNull IntrospectedColumn introspectedColumn, @NonNull IntrospectedTable introspectedTable, @NonNull ModelClassType modelClassType) {
        return false;
    }

    /**
     * 拦截 Setter 方法的生成，返回 false 表示不生成
     */
    @Override
    public boolean modelSetterMethodGenerated(@NonNull Method method, @NonNull TopLevelClass topLevelClass, @NonNull IntrospectedColumn introspectedColumn, @NonNull IntrospectedTable introspectedTable, @NonNull ModelClassType modelClassType) {
        return false;
    }

    /*
    以下方法是 MybatisGenerator 生成 Mapper 接口的基础方法
    让生成的 Mapper 接口符合 MyBatis-Plus 规范
     */

    /**
     * 拦截 Mapper 接口的生成
     */
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        // 数据表实体类引入
        String modelType = introspectedTable.getBaseRecordType();
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(modelType);
        interfaze.addImportedType(entityType);

        // 继承MyBatis-Plus的BaseMapper
        FullyQualifiedJavaType baseMapperType = new FullyQualifiedJavaType("com.baomidou.mybatisplus.core.mapper.BaseMapper");
        interfaze.addImportedType(baseMapperType);
        FullyQualifiedJavaType mpBaseType = new FullyQualifiedJavaType("com.baomidou.mybatisplus.core.mapper.BaseMapper<" + modelType + ">");
        interfaze.addSuperInterface(mpBaseType);

        // 添加@Mapper注解
        FullyQualifiedJavaType mapperAnnotationType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
        interfaze.addImportedType(mapperAnnotationType);
        interfaze.addAnnotation("@Mapper");

        // 添加类注释
        String remarks = introspectedTable.getRemarks().orElse("");
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * " + remarks + " Mapper");
        interfaze.addJavaDocLine(" */");

        // 使用反射移除多余的导入
        try {
            // 获取 importedTypes 字段
            java.lang.reflect.Field field = interfaze.getClass().getDeclaredField("importedTypes");
            field.setAccessible(true);

            // 获取字段值
            @SuppressWarnings("unchecked")
            Set<FullyQualifiedJavaType> importedTypes = (Set<FullyQualifiedJavaType>) field.get(interfaze);

            // 遍历并移除包含 "org.apache.ibatis.annotations.Select" 的类型
            importedTypes.removeIf(type -> type.getFullyQualifiedName().equals("org.apache.ibatis.annotations.Select"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("org.apache.ibatis.annotations.Select反射失败，不影响主流程，只是会多一个 import");
        }

        return true;
    }

    /*
    以下方法是阻止 MybatisGenerator 生成具体的 SQL 方法 (selectByPrimaryKey, insert 等)
    因为 MyBatis-Plus 的 BaseMapper 已经提供了这些方法，我们不需要重复生成
    返回 false 表示不生成该方法
     */

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(@NonNull Method method, @NonNull Interface interfaze, @NonNull IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(@NonNull Method method, @NonNull Interface interfaze, @NonNull IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(@NonNull Method method, @NonNull Interface interfaze, @NonNull IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(@NonNull Method method, @NonNull Interface interfaze, @NonNull IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(@NonNull Method method, @NonNull Interface interfaze, @NonNull IntrospectedTable introspectedTable) {
        return false;
    }
}

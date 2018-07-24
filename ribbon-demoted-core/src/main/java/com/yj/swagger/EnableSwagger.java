package com.yj.swagger;

import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

import java.lang.annotation.*;

/**
 * @Description 启用在线文档即文档相关基础信息
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/7
 * Time: 13:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({Swagger2DocumentationConfiguration.class,EnableSwaggerRegister.class})
public @interface EnableSwagger {

    /**
     * Controller包扫描路径
     * @return
     */
    String basePackage() default "";

    /**
     * 文档创建者
     * @return
     */
    String createName() default "admin";

    /**
     * 文档标题
     * @return
     */
    String serviceName() default "Api";

    /**
     * 文档描述
     * @return
     */
    String description() default "Description";

    /**
     * 文档版本
     * @return
     */
    String version() default "1.0.0";
}

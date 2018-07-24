package com.yj.swagger;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Map;

/**
 * 动态注册配置类EnableSwagger到Ioc容器中
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/7
 * Time: 13:50
 */
@Configuration
public class EnableSwaggerRegister implements ImportBeanDefinitionRegistrar,EnvironmentAware,BeanFactoryAware {

    /**
     * Controller包扫描路径
     */
    private String basePackage;
    /**
     * 文档创建者
     */
    private String creatName;
    /**
     * 文档标题
     */
    private String serviceName;
    /**
     * 文档描述
     */
    private String description;
    /**
     * 文档版本
     */
    private String version;

    private BeanFactory beanFactory;


    @Override
    public void setEnvironment(Environment environment) {

        /**
         * 从application.properties配置文件中读取相关配置项
         */
        this.basePackage = environment.getProperty("swagger.basepackage","");
        this.creatName = environment.getProperty("swagger.service.developer","");
        this.serviceName = environment.getProperty("swagger.service.name","");
        this.description = environment.getProperty("swagger.service.description","");
        this.version = environment.getProperty("swagger.service.version","");

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        Map<String,Object> properties = annotationMetadata.getAnnotationAttributes(EnableSwagger.class.getName());
        /**
         * application.properties配置优先级低于手动在注解上的配置内容
         */
        if(!isDefaultValue(properties,"basePackage","")){
            basePackage = (String) properties.get("basePackage");
        }

        if(!isDefaultValue(properties,"createName","admin")){
            creatName = (String) properties.get("createName");
        }
        if(!isDefaultValue(properties,"serviceName","Api")){
            serviceName = (String) properties.get("serviceName");
        }
        if(!isDefaultValue(properties,"description","Description")){
            description = (String) properties.get("description");
        }
        if(!isDefaultValue(properties,"version","1.0.0")){
            version = (String) properties.get("version");
        }

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                            .title(serviceName + " Restful APIs")
                            .contact(new Contact(creatName,"",""))
                            .description(description)
                            .version(version)
                            .build())
                .select() // 选择哪些路径和api会生成document
                .apis(RequestHandlerSelectors.basePackage(basePackage)) // 对basePackage包所有api进行监控
                .paths(PathSelectors.any()) // 包下所有路径进行监控
                .build();
/**
 * 不知道为什么通过 beanDefinitionRegistry动态注册Bean运行时会报没有找到DocumentationType的Bean
 * 改为通过实现BeanFactoryAware接口来创建Bean运行正常
 * 通过BeanDefinition动态创建Bean，需要把Docket构造函数所需要的参数全部构造全部才能正常创建
 */
        // 创建DocketBean
//        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
//        beanDefinition.setBeanClass(Docket.class);
//        beanDefinition.setSynthetic(true);
        // 注册
//        beanDefinitionRegistry.registerBeanDefinition("api",beanDefinition);

        // 直接通过Docket实例进行Bean的动态注入
        ((DefaultListableBeanFactory)this.beanFactory).registerSingleton("api",docket);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private boolean isDefaultValue(Map<String,Object> properties,String key,String defaultVal){
        if(properties.containsKey(key)){
            String temp = (String) properties.get(key);
            if(!temp.trim().equalsIgnoreCase(defaultVal)){
                return false;
            }
        }
        return true;
    }
}

package com.yj.zuul.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/7
 * Time: 16:29
 */
@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    @Autowired
    private DiscoveryClient discoveryClient;
    /**
     * 采用属性注入方式
     */
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public List<SwaggerResource> get() {
        List resource = new ArrayList<>();
        // 从注册中心提取服务，但排除网关
        List<String> serviceList = discoveryClient.getServices();
        for(String service:serviceList){

            if(service.equalsIgnoreCase(appName))
                continue;

            resource.add(swaggerResource(service,"/"+service+"/v2/api-docs","1.0"));
        }
        return resource;
    }

    private SwaggerResource swaggerResource(String name,String location,String version){
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);

        return  swaggerResource;
    }
}

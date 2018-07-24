package com.yj.provider;

import com.yj.swagger.EnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/26
 * Time: 10:25
 */

@SpringBootApplication(scanBasePackages = {"com.yj.amqp","com.yj.provider"})
@EnableDiscoveryClient
@EnableSwagger(createName = "test")
public class ProviderApplication {
    public static void main(String[] args){
        SpringApplication.run(ProviderApplication.class,args);
    }
}

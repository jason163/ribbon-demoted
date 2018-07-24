package com.yj.consumer;

import com.yj.core.CoreAutoConfiguration;
import com.yj.swagger.EnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/26
 * Time: 14:13
 */
@SpringBootApplication(scanBasePackages = {"com.yj.amqp","com.yj.consumer"})
@EnableDiscoveryClient
@EnableSwagger
public class ConsumerApplication {
    public static void main(String[] args){
        SpringApplication.run(ConsumerApplication.class,args);
    }
}

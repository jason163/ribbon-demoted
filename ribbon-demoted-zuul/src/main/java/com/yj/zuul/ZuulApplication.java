package com.yj.zuul;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.WeightedResponseTimeRule;
import com.yj.core.CoreAutoConfiguration;
import com.yj.core.DefaultRibbonConfiguration;
import com.yj.core.LabelAndWeightMetadataRule;
import com.yj.zuul.filter.ErrorFilter;
import com.yj.zuul.filter.PreFilter;
import com.yj.zuul.filter.ServerFallback;
import com.yj.zuul.filter.ThrowExceptionFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/25
 * Time: 18:00
 */

@Configuration
@EnableZuulProxy
@SpringBootApplication
@EnableZuulSwagger
//@Import(CoreAutoConfiguration.class),第三方包已通过META-INFO->spring.factories方式自动配置
public class ZuulApplication {

    public static void main(String[] args){
        SpringApplication.run(ZuulApplication.class,args);
    }

    @Bean
    public PreFilter preFilter(){
        return new PreFilter();
    }

    @Bean
    public ErrorFilter errorFilter(){
        return new ErrorFilter();
    }


//    @Bean
//    public ServerFallback serverFallback(){
//        return new ServerFallback();
//    }

//    @Bean
//    public IRule weightedMetadataRule(){
//        return new LabelAndWeightMetadataRule();
//    }
}

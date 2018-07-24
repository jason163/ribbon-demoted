package com.yj.core;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/24
 * Time: 21:50
 */
@Configuration
public class DefaultRibbonConfiguration {

//    @Value("${ribbon.client.name:#{null}")
//    private String name;
//
//    /**
//     * 配置管理客户端
//     */
//    @Autowired(required = false)
//    private IClientConfig config;
//
//    @Autowired
//    private PropertiesFactory propertiesFactory;


    @Bean
    public IRule ribbonRule(){

//        if (StringUtils.isEmpty(name)) {
//            return null;
//        }
//
//        if (this.propertiesFactory.isSet(IRule.class, name)) {
//            return this.propertiesFactory.get(IRule.class, config, name);
//        }

        LabelAndWeightMetadataRule rule = new LabelAndWeightMetadataRule();
//        rule.initWithNiwsConfig(config);

        return rule;
    }
}

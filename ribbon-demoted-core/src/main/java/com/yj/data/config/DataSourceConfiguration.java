package com.yj.data.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

/**
 * 从配置文件信息加载datasource配置项
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/7
 * Time: 20:59
 */
@Configuration
@ConfigurationProperties(prefix = "mybatis.datasource")
@PropertySource(ignoreResourceNotFound = true,value = {"classpath:mybatis/mybatis.yml"})
public class DataSourceConfiguration {
    private static Logger logger = LoggerFactory.getLogger(DataSourceAutoConfiguration.class);

    private String mapperLocations;
    private String typeAliasesPackage;
    private String configLocation;
    private List<DruidDataSource> cluster = new ArrayList<>();

    @Bean
    @ConfigurationProperties(prefix = "mybatis.datasource.master")
    public DruidDataSource writeDataSource(){
        return new DruidDataSource();
    }


    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public List<DruidDataSource> getCluster() {
        return cluster;
    }

    public void setCluster(List<DruidDataSource> cluster) {
        this.cluster = cluster;
    }
}

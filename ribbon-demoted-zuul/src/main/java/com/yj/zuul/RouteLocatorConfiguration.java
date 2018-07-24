package com.yj.zuul;

import com.netflix.discovery.converters.Auto;
import com.yj.zuul.routeLocator.DynamicRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 配置自定义路由定位器
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/2
 * Time: 11:32
 */
@Configuration
public class RouteLocatorConfiguration {
    @Autowired
    private ZuulProperties zuulProperties;
    @Autowired
    private ServerProperties server;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Bean
    public DynamicRouteLocator routeLocator(){
        DynamicRouteLocator routeLocator = new DynamicRouteLocator(this.server.getServletPath(),this.zuulProperties);

        RouteLocatorContext.setJdbcTemplate(jdbcTemplate);

        return routeLocator;
    }
}

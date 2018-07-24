package com.yj.zuul.service;

import com.yj.zuul.RouteLocatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 通过事件来更新路由定位信息
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/2
 * Time: 15:16
 */
@Service
public class RefreshRouteService {

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    RouteLocator routeLocator;

    public void refreshRoute(){
        // 清理自定义路由上下文,使之从DB重新加载路由信息
        RouteLocatorContext.clearContext();

        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(routeLocator);
        publisher.publishEvent(routesRefreshedEvent);
    }
}

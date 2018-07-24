package com.yj.zuul.routeLocator;

import com.yj.zuul.RouteLocatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 继承默认路由定位并实现路由刷新接口RefreshableRouteLocator
 * 主要重写定位路由信息即重写locateRoute()
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/2
 * Time: 10:44
 */
public class DynamicRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    public final static Logger logger = LoggerFactory.getLogger(DynamicRouteLocator.class);
    private ZuulProperties properties;

    public DynamicRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.properties = properties;
    }

    /**
     * 父类已经提供了刷新接口
     */
    @Override
    public void refresh() {
        doRefresh();
    }

    /**
     * 刷新[间隔一段时间自动刷新]一次即调用一次refresh就会调用locateRoutes()方法，重新加载路由信息
     * @return
     */
    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String,ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        // 从application.properties中加载路由信息
        routesMap.putAll(super.locateRoutes());
        // 从db中加载路由信息
        routesMap.putAll(RouteLocatorContext.getRoutes());
        //

        // 优化配置
        LinkedHashMap<String,ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String,ZuulProperties.ZuulRoute> entry:routesMap.entrySet()){
            String path = entry.getKey();
            if(!path.startsWith("/")){
                path = "/" + path;
            }
            if(StringUtils.hasText(this.properties.getPrefix())){
                path = this.properties.getPrefix() + path;
                if(!path.startsWith("/")){
                    path = "/" + path;
                }
            }
            values.put(path,entry.getValue());
        }
        return values;
    }
}

package com.yj.zuul;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/2
 * Time: 11:03
 */
public class RouteLocatorContext {

    public final static Logger logger = LoggerFactory.getLogger(RouteLocatorContext.class);
    private static JdbcTemplate jdbcTemplate;
    private static final Map<String,ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();

    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        RouteLocatorContext.jdbcTemplate = jdbcTemplate;
    }

    public static Map<String,ZuulProperties.ZuulRoute> getRoutes() {
        if(routes.isEmpty()){
            loadRouteByDB();
        }
        return routes;
    }

    /**
     * 清理自定义路由定位上下文数据
     */
    public static void clearContext(){
        if(!routes.isEmpty()){
            routes.clear();
        }
    }

    private static void loadRouteByDB(){
        // 通过jdbcTemplate从数据库中查询路由定位信息
        List<ZuulRouteVO> results = jdbcTemplate.query("select * from `gateway`.`routes` where enabled = true",new BeanPropertyRowMapper<>(ZuulRouteVO.class));
        for(ZuulRouteVO result : results){
            if(org.apache.commons.lang.StringUtils.isBlank(result.getPath())){
                continue;
            }
            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            try {
                BeanUtils.copyProperties(result,zuulRoute);
            }catch (Exception e){
                logger.error("load zuul route info from db with error!");
            }

            routes.put(zuulRoute.getPath(),zuulRoute);
        }

    }

    /**
     * 路由实体信息
     */
    public static class ZuulRouteVO {
        /**
         * The ID of the route (the same as its map key by default).
         */
        private String id;
        /**
         * The path (pattern) for the route, e.g. /foo/**.
         */
        private String path;
        /**
         * The service ID (if any) to map to this route. You can specify a physical URL or
         * a service, but not both.
         */
        private String serviceId;
        /**
         * A full physical URL to map to the route. An alternative is to use a service ID
         * and service discovery to find the physical address.
         */
        private String url;
        /**
         * Flag to determine whether the prefix for this route (the path, minus pattern
         * patcher) should be stripped before forwarding.
         */
        private boolean stripPrefix = true;
        /**
         * Flag to indicate that this route should be retryable (if supported). Generally
         * retry requires a service ID and ribbon.
         */
        private Boolean retryable;
        private Boolean enabled;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public String getServiceId() {
            return serviceId;
        }
        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public boolean isStripPrefix() {
            return stripPrefix;
        }
        public void setStripPrefix(boolean stripPrefix) {
            this.stripPrefix = stripPrefix;
        }
        public Boolean getRetryable() {
            return retryable;
        }
        public void setRetryable(Boolean retryable) {
            this.retryable = retryable;
        }
        public Boolean getEnabled() {
            return enabled;
        }
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}

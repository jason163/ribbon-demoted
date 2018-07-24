package com.yj.zuul.filter;

import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 网关熔断
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/5
 * Time: 16:17
 */
//@Component
public class ServerFallback implements ZuulFallbackProvider {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static final Logger logger = LoggerFactory.getLogger(ServerFallback.class);

    @Autowired
    private RouteLocator routeLocator;

    /**
     * "*" 或者 null 表示所有服务支持妿
     * @return
     */
    @Override
    public String getRoute() {
        return "abc";
    }

    /**
     * 请求失败，返回自定义消息给消费者客户端
     * @return
     */
    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            /**
             * 消费者客户端向网关发送的请求是OK的
             * 所以不应该返回错误代码给客户端
             * @return
             * @throws IOException
             */
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                String requestURI = URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
                Route route = routeLocator.getMatchingRoute(requestURI);
                if(null != route){
                    logger.error("调用：{} 异常: {}",route.getId(),"服务不可用");
                }else {
                    logger.error("调用：{} 异常: {}",requestURI,"服务不可用");
                }
                return new ByteArrayInputStream("{\"success\": false,\"message\": \"服务不可用\",\"data\": null}".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                // 避免出现乱码，header 和 body中内容编码保持一致
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return  headers;
            }
        };
    }
}

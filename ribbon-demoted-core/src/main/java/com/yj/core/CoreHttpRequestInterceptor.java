package com.yj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * ClientHttpRequestInterceptor是RestTemplate的拦截器接口
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/24
 * Time: 21:34
 */
public class CoreHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CoreHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpRequest);

        String header = StringUtils.collectionToDelimitedString(CoreHeaderInterceptor.lable.get(),
                CoreHeaderInterceptor.HEADER_LABLE_SPLIT);

        logger.info("label: "+ header);

        requestWrapper.getHeaders().add(CoreHeaderInterceptor.HEADER_LABEL,header);


        return clientHttpRequestExecution.execute(requestWrapper,bytes);
    }
}

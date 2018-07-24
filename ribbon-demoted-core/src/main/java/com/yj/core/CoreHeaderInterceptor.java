package com.yj.core;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * 请求都通过zuul进来，因此我们可以在zuul里面给请求打标签，基于用户，IP或其他看你的需求，然后将标签信息放入ThreadLocal中,然后在Ribbon Rule中从ThreadLocal拿出来使用就可以了
 * 然而，拿不到ThreadLocal。原因是有hystrix这个东西，回忆下hystrix的原理，为了做到故障隔离，hystrix启用了自己的线程，不在同一个线程ThreadLocal失效
 * 标签传到HystrixRequestVariableDefault这里的，如果项目中没有使用Hystrix就用不了了,这个时候需要做一个判断在restTemple里面做个判断，没有hystrix就直接threadlocal取
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/24
 * Time: 21:13
 */
public class CoreHeaderInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CoreHeaderInterceptor.class);
    public static final String  HEADER_LABEL = "x-label";
    public static final String HEADER_LABLE_SPLIT = ",";

    public static final HystrixRequestVariableDefault<List<String>> lable = new HystrixRequestVariableDefault<>();

    /**
     * 添加标签
     * @param labels
     */
    public static void initHystrixRequestContext(String labels){
        logger.info("labels: "+labels);
        if(!HystrixRequestContext.isCurrentThreadInitialized()){
            HystrixRequestContext.initializeContext();
        }

        if(!StringUtils.isEmpty(labels)){
            CoreHeaderInterceptor.lable.set(Arrays.asList(labels.split(CoreHeaderInterceptor.HEADER_LABLE_SPLIT)));;
        }else {
            CoreHeaderInterceptor.lable.set(Collections.emptyList());
        }
    }

    public static void shutdownHystrixRequestContext(){
        if(HystrixRequestContext.isCurrentThreadInitialized()){
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception{
        CoreHeaderInterceptor.initHystrixRequestContext(request.getHeader(CoreHeaderInterceptor.HEADER_LABEL));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception{
        CoreHeaderInterceptor.shutdownHystrixRequestContext();
    }
}

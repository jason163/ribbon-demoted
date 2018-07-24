package com.yj.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletResponse;

/**
 * 所有异常都会发送给SendErrorFilter进行处理，但是RequestContext中必须包含error.status_code&
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/5
 * Time: 11:11
 */
public class ThrowExceptionFilter extends ZuulFilter {
    private static Logger logger = LoggerFactory.getLogger(ThrowExceptionFilter.class);
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return  FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        logger.info("This is a pre filter,it will throw a runtimeexception");

        doSomething();

//        RequestContext ctx = RequestContext.getCurrentContext();
//        try{
//            doSomething();
//        }catch (Exception e){
//            ctx.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            ctx.set("error.exception",e);
//        }



        return null;
    }

    private void doSomething(){
        throw new RuntimeException("Exist some errors ...");
    }
}

package com.yj.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * #自定义网关异常处理、自定义异常提示信息，首先必须禁用默认的SendErrorFilter
 * zuul.SendErrorFilter.error.disable=true
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/5
 * Time: 13:37
 */
public class ErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return context.getThrowable() != null;
    }

    @Override
    public Object run() {

        try{
            RequestContext ctx = RequestContext.getCurrentContext();

            ZuulException exception = this.findZuulException(ctx.getThrowable());

            HttpServletResponse response = ctx.getResponse();
            response.setContentType("application/json; charset=utf8");
            // 消费客户端正常访问网关，所以不应该返回错误代码给客户端
            response.setStatus(HttpStatus.OK.value());

            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                String msg = exception.getCause().getMessage();
                writer.print("{\"success\":false,\"message\":\"服务不可用，请稍后重试\",\"data\":"+msg+"}");
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(writer!=null){
                    writer.close();
                }
            }
        }catch (Exception ex){
            ReflectionUtils.rethrowRuntimeException(ex);
        }

        return null;
    }

    ZuulException findZuulException(Throwable throwable){
        if(ZuulRuntimeException.class.isInstance(throwable.getCause())){
            return (ZuulException) throwable.getCause().getCause();
        }else if(ZuulException.class.isInstance(throwable.getCause())){
            return (ZuulException) throwable.getCause();
        }else {
            return ZuulException.class.isInstance(throwable)?(ZuulException)throwable
                    :new ZuulException(throwable,500,null);
        }
    }
}

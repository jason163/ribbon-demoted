package com.yj.zuul;

import com.yj.swagger.EnableSwagger;
import com.yj.zuul.swagger.DocumentationConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用网关在线文档
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableSwagger
@Import(value = {DocumentationConfig.class})
public @interface EnableZuulSwagger {

}

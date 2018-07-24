package com.yj.provider.controller;

import com.yj.provider.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;


/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/26
 * Time: 13:30
 */
@RestController
@RequestMapping("/user")
@Api(value = "API - UserController",description = "测试接口")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "查找所有用户",httpMethod = "GET",produces = "application/json")
    public List<User> query(HttpServletRequest request){
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder sbHeader = new StringBuilder();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            sbHeader.append(" [" + headerName + ":" + request.getHeader(headerName) + "] ");
        }

        logger.info("query all "+ sbHeader);

        return Arrays.asList(new User(1L, "account1", "password1"),
                new User(2L, "account2", "password2"),
                new User(3L, "account3", "password3"));
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "获取单个用户信息",httpMethod = "GET")
    public User query(@PathVariable@ApiParam(value = "用户编号",required = true) Long id){
        logger.info("query by id");

//        doSomething();

        return new User(id,"account"+id,"password"+id);
    }

    private void doSomething(){
        throw new RuntimeException("Exist some errors ...");
    }
}

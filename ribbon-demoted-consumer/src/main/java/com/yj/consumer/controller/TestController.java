package com.yj.consumer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/26
 * Time: 14:14
 */
@RestController
@RequestMapping("/test")
@Api(value = "TestController",description = "消费者测试接口")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "消费者调用生产者测试",httpMethod = "GET",produces = "application/json")
    public String test(@RequestHeader("x-label") String label){
        logger.info("label: "+ label);

        String result =restTemplate.getForObject("http://provider/user",String.class);

        return  result;
    }
}

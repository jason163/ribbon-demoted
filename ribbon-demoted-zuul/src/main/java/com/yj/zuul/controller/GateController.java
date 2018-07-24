package com.yj.zuul.controller;

import com.yj.zuul.service.RefreshRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/2
 * Time: 15:20
 */
@RestController
@RequestMapping("/gw")
public class GateController {

    @Autowired
    RefreshRouteService refreshRouteService;

    @RequestMapping(value = "refreshRoute",method = RequestMethod.GET)
    public String refreshRoute(){
        refreshRouteService.refreshRoute();
        return "SUCCESS";
    }

}

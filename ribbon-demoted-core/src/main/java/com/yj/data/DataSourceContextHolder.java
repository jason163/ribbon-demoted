package com.yj.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/7
 * Time: 21:10
 */
public class DataSourceContextHolder {
    private static Logger logger = LoggerFactory.getLogger(DataSourceContextHolder.class);

    // 线程本地环境
    private static final ThreadLocal<String> local = new ThreadLocal<>();


}

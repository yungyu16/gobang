package com.github.yungyu16.gobang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author: songjialin
 */
@MapperScan("com.github.yungyu16.gobang.dao.mapper")
@SpringBootApplication
public class WebBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(WebBootstrap.class, args);
    }
}

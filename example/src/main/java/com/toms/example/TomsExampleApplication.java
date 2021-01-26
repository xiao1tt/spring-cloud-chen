package com.toms.example;

import com.base.commons.web.configuration.EnableWebComponent;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenxiaotong
 */
@SpringBootApplication
@EnableWebComponent
@MapperScan(basePackages = "com.toms.example.dao")
public class TomsExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(TomsExampleApplication.class, args);
    }
}
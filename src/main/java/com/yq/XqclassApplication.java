package com.yq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.yq")//扫描的包，显示声明一下
public class XqclassApplication {

    public static void main(String[] args) {
        SpringApplication.run(XqclassApplication.class, args);
    }

}

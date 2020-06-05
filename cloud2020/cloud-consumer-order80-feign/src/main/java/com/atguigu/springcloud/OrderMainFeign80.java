package com.atguigu.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient //注册Eureka
@EnableFeignClients
public class OrderMainFeign80 {

    public static void main(String[] args) {
        SpringApplication.run(OrderMainFeign80.class,args);
    }
}

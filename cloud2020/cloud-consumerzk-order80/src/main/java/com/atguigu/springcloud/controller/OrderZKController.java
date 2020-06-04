package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/*
* RestTemplate提供了多种便捷访问远程Http服务的方法，
* 是一种简单便捷的访问restful服务模板类，
* 是Spring提供的用于访问Rest服务的客户端模板工具集
* */
@RestController
@Slf4j
public class OrderZKController {

    /*消费者*/
    //private  static final String PAYMENT_URL="http://localhost:8001";  写死地址
    private  static final String INVOME_URL="http://cloud-provider-payment";

    @Autowired
     private  RestTemplate restTemplate;

    @GetMapping("/consumer/payment/zk")
    public String payment (){
        String result = restTemplate.getForObject(INVOME_URL+"/payment/zk",String.class);
        return result;
    }






}

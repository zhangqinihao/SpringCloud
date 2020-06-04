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
/*
* RestTemplate提供了多种便捷访问远程Http服务的方法，
* 是一种简单便捷的访问restful服务模板类，
* 是Spring提供的用于访问Rest服务的客户端模板工具集
* */
@RestController
@Slf4j
public class OrderController {

    /*消费者*/
    //private  static final String PAYMENT_URL="http://localhost:8001";  写死地址
    private  static final String PAYMENT_URL="http://CLOUD-PAYMENT-SERVICE";

    @Autowired
     private  RestTemplate restTemplate;

    @RequestMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment){


        return  restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);
    }


    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){


        return  restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
    }





}

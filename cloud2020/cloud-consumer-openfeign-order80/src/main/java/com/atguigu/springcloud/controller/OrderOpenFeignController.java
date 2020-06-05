package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.api.PaymentServiceApi;
import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/*
* RestTemplate提供了多种便捷访问远程Http服务的方法，
* 是一种简单便捷的访问restful服务模板类，
* 是Spring提供的用于访问Rest服务的客户端模板工具集
* */
@RestController
@Slf4j
public class OrderOpenFeignController {

    @Autowired
    private PaymentServiceApi paymentServiceApi;

    @GetMapping(value = "openfeign/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id")Long id){

        return  paymentServiceApi.getPaymentById(id);
    }

}

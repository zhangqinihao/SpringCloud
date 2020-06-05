package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.api.PaymentServiceApi;
import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
* RestTemplate提供了多种便捷访问远程Http服务的方法，
* 是一种简单便捷的访问restful服务模板类，
* 是Spring提供的用于访问Rest服务的客户端模板工具集
* */
@RestController
@Slf4j
public class OrderFeignController {

    @Autowired
    private PaymentServiceApi paymentServiceApi;

    @RequestMapping(value = "Feign/payment/get",method = RequestMethod.GET)
    public CommonResult getPaymentById(@RequestParam("id")Long id){
        Payment payment = paymentServiceApi.getPaymentById(id);
        log.info("*****查询结果："+payment+id);
        if (payment!=null){  //说明有数据，能查询成功
            return new CommonResult(200,"查询成功"+payment+id,payment);
        }else {
            return new CommonResult(444,"没有对应记录，查询ID："+id,null);
        }
    }

}

package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.service.PaymentHystrixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class OrderHystrixController {

    @Resource
    private PaymentHystrixService paymentHystrixService;


   /* @Resource
    private PaymentFallbackService paymentHystrixService;*/

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id){
        String result = paymentHystrixService.paymentInfo_OK(id);
        log.info("*******result:"+result);
        return result;
    }

//    //失败
//    @HystrixCommand(fallbackMethod ="paymentInfo_TimeOutHandler",commandProperties = {
//            //指定这个线程 5秒正常
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")})
    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id){
        String result = paymentHystrixService.paymentInfo_TimeOut(id);
        log.info("*******result:"+result);
        return result;
    }




//    //失败  成功
//    @HystrixCommand(fallbackMethod ="paymentInfo_TimeOutHandlerok",commandProperties = {
//            //指定这个线程 5秒正常
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")})
    @GetMapping("/consumer/payment/hystrix/timeoutok/{id}")
    public String paymentInfo_TimeOutok(@PathVariable("id") Integer id){
        String result = paymentHystrixService.paymentInfo_TimeOutok(id);
        log.info("*******result:"+result);
        return result;
    }

    //兜底方法
    public String paymentInfo_TimeOutHandler(Integer id){
        return "线程池："+Thread.currentThread().getName()+"   系统繁忙, 请稍候再试  ,id：  "+id+"\t"+"哭了哇呜";
    }

    //兜底方法
    public String paymentInfo_TimeOutHandlerok(Integer id){
        return "线程池："+Thread.currentThread().getName()+"   系统繁忙, 请稍候再试  ,id：  "+id+"\t"+"哭了哇呜";
    }


}

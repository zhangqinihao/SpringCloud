package com.atguigu.springcloud.service;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    //成功
    public String paymentInfo_OK(Integer id){
        return "线程池："+Thread.currentThread().getName()+"   paymentInfo_OK,id：  "+id+"\t"+"哈哈哈"  ;
    }

    //失败
    @HystrixCommand(fallbackMethod ="paymentInfo_TimeOutHandler",commandProperties = {
                                     //指定这个线程  3秒正常
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")})

    public String paymentInfo_TimeOut(Integer id){
        int timeNumber = 5;//故意写5秒
        try { TimeUnit.SECONDS.sleep(timeNumber); }catch (Exception e) {e.printStackTrace();}
        return "线程池："+Thread.currentThread().getName()+"   paymentInfo_TimeOut,id：  "+id+"\t"+"呜呜呜"+" 耗时(秒)"+timeNumber;
    }
    //失败
    @HystrixCommand(fallbackMethod ="paymentInfo_TimeOutHandler",commandProperties = {
            //指定这个线程 5秒正常
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")})

    public String paymentInfo_TimeOutok(Integer id){
        int timeNumber = 2;//故意写2秒
        try { TimeUnit.SECONDS.sleep(timeNumber); }catch (Exception e) {e.printStackTrace();}
        return "线程池："+Thread.currentThread().getName()+"   paymentInfo_TimeOut,id：  "+id+"\t"+"成功"+" 耗时(秒)"+timeNumber;
    }

    //兜底方法
    public String paymentInfo_TimeOutHandler(Integer id){
        return "线程池："+Thread.currentThread().getName()+"   系统繁忙, 请稍候再试  ,id：  "+id+"\t"+"哭了哇呜";
    }



}



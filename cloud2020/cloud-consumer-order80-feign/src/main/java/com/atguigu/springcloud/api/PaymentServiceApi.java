package com.atguigu.springcloud.api;

import com.atguigu.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient(value = "cloud-payment-service")
public interface PaymentServiceApi {

    @RequestMapping(value = "/payment/get",method = RequestMethod.GET)
    public Payment getPaymentById(@RequestParam("id") Long id);  //读取
}

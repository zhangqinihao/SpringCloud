package com.atguigu.springcloud.alibaba.service.impl;

import com.atguigu.springcloud.alibaba.service.PaymentService;
import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentServiceimpl  implements PaymentService {
    @Override
    public CommonResult<Payment> paymentSQL(Long id) {

        return new CommonResult<>(44444,"服务降级返回,---PaymentServiceimpl",new Payment(id,"errorSerial"));
    }
}

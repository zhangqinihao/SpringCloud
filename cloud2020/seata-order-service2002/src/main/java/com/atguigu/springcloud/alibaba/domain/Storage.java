package com.atguigu.springcloud.alibaba.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Storage implements Serializable {

    private Long id;

    // 产品id
    private Long productId;

    //总库存
    private Integer total;


    //已用库存
    private Integer used;


    //剩余库存
    private Integer residue;
}


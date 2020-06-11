模块 cloudalibaba-sentinel-service8401

POM
<dependency>
    <groupId>com.atguigu.springcloud</groupId>
    <artifactId>cloud-api-commons</artifactId>
    <version>${project.version}</version>
</dependency>

业务类RateLimitController

配置流控规则
http://localhost:8401//byResource

1秒钟点击1下，OK
超过上述问题，疯狂点击，返回了自己定义的限流处理信息，限流发送


额外问题
此时关闭微服务8401看看
Sentinel控制台，流控规则消失了  临时的不是持久的
==================================================================================
按照Url地址限流+后续处理
测试
疯狂点击http://localhost:8401/rateLimit/byUrl
会返回Sentinel自带的限流处理结果

========================================================
上面兜底方法面临的问题
创建CustomerBlockHandler类用于自定义限流处理逻辑
自定义限流处理类
========================================
更多注解属性说明
Sentinel主要有三个核心API
SphU定义资源
Tracer定义统计
ContextUtil定义了上下文
============================================================================
服务熔断功能

Ribbon系列
sentinel整合ribbon+openFeign+fallback

启动nacos和sentinel
新建cloudalibaba-provider-payment9003/9004

测试
http://localhost:9003/paymentSQL/1
http://localhost:9004/paymentSQL/1

新建cloudalibaba-consumer-nacos-order84

业务类

ApplicationContextConfig

修改后请重启微服务
对@SentinelResource注解内属性，有时效果不好

目的
fallback管运行异常
blockHandler管配置违规

测试地址
http://localhost:84/consumer/fallback/1

没有任何配置
给客户error页面，不友好

只配置fallback
编码（那个业务类下面的CircleBreakerController的全部源码）


只配置blockHandler
编码（那个业务类下面的CircleBreakerController的全部源码）
fallback和blockHandler都配置
结果
若blockHandler和fallback 都进行了配置，则被限流降级而抛出BlockException 时只会进入blockHandler 处理逻辑。

忽略属性...
编码（那个业务类下面的CircleBreakerController的全部源码）
http://localhost:84/consumer/fallback/1

没有任何配置
给客户error页面，不友好

只配置fallback fallback 能解决运行时异常


只配置blockHandler  blockHandler只负责sentinel控制台配置违规 


fallback和blockHandler都配置  
若blockHandler 和fallback都进行了配置，则被限流降级而抛出Blodkception 时只会进入blockHandler 处理逻辑。

忽略属性...
IllegalArgumentException.class
假如报该异常，不再有fallback方法兜底，没有降级效果了。

Feign系列
pom
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

带@FeignClient注解的业务接口
测试 http://localhost:84/consumer/paymentSQL/1
测试84调用9003，此时故意关闭9003微服务提供者，看84消费侧自动降级，不会被耗死

熔断框架比较
Sentinel

Hystrix


规则持久化
是什么
一旦我们重启应用，Sentinel规则将消失，生产环境需要将配置规则进行持久化
怎么玩
将限流配置规则持久化进Nacos保存，只要刷新8401某个rest地址
，sentinel控制台的流控规则就能看到，只要Nacos里面的配置不删除，针对8401上Sentinel上的流控规则持续有效

pom
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
</dependency>


yml
 datasource:
        ds1:
          nacos:
            server-addr: localhost:8848
            dataId: cloudalibaba-sentinel-service
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: flow

添加Nacos业务规则配置

启动8401后刷新sentinel发现业务规则有了


快速访问测试接口
停止8401再看sentinel
重新启动8401再看sentinel
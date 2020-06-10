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
负载均衡
spring-cloud-starter-netflix-eureka-client 确实引入了Ribbon  不用再重复引
负载均衡+RestTemplate调用
getForObject  getForEntity区别见80模块
推荐getForObject

IRule:根据特定算法从服务列表中选取一个要访问的服务
com.netflix.loadbalancer.RoundRobinRule  轮询
com.netflix.loadbalancer.RandomRule 随机
com.netflix.loadbalancer.RetryRule  先按照轮询的策略获取服务，如果获取服务失败则在指定时间内会进行重试
WeightedResponseTimeRule  对轮询的扩展，响应速度越快的实例选择权重越大，越容易被选择
BestAvailableRule   会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，然后选择一个并发量最小的服务
AvailabilityFilteringRule  先过滤掉故障实例，再选择并发较小的实例
ZoneAvoidanceRule 默认规则，复合判断server所在区域的性能和server的可用性选择服务器 

修改cloud-consumer-order80 
配置类不能放在@ComponentScan所扫描的当前包下以及子包下
新建package  com.atguigu.myrule
主启动类
@RibbonClient(name="CLOUD-PAYMENT-SERVICE",configuration = MySelfRule.class)
测试 http://localhost/consumer/payment/get/1

负载均衡算法：rest接口第几次请求数%服务器集群总数量=实际调用服务器位置下标，每次服务重启动后rest接口计数从1开始。
8001+8002组合成为集群，它们共计2台机器，集群总数为2，按照轮询算法原理：
当总请求数为1时：1%2=1对应下标位置为1，则获得服务地址为127.0.0.1：8001
当总请求数位2时：2%2=0对应下标位置为0，则获得服务地址为127.0.0.1：8002
当总请求数位3时：3%2=1对应下标位置为1，则获得服务地址为127.0.0.1：8001
当总请求数位4时：4%2=0对应下标位置为0，则获得服务地址为127.0.0.1：8002
如此类推.…
=================================================
手写负载均衡算法 见8001 8002
@GetMapping(value = "/payment/lb")
public String getPaymentLB(){
    return serverPort;
}

新建接口 LoadBalancer
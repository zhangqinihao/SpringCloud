分布式系统面临的问题
复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免地失败。


一般情况对于服务依赖的保护主要有3中解决方案：
 
（1）熔断模式：这种模式主要是参考电路熔断，如果一条线路电压过高，保险丝会熔断，防止火灾。
     放到我们的系统中，如果某个目标服务调用慢或者有大量超时，此时，熔断该服务的调用，对于后续调用请求，
     不在继续调用目标服务，直接返回，快速释放资源。如果目标服务情况好转则恢复调用。
 
（2）隔离模式：这种模式就像对系统请求按类型划分成一个个小岛的一样，当某个小岛被火少光了，不会影响到其他的小岛。
     例如可以对不同类型的请求使用线程池来资源隔离，每种类型的请求互不影响，如果一种类型的请求线程资源耗尽，
     则对后续的该类型请求直接返回，不再调用后续资源。这种模式使用场景非常多，例如将一个服务拆开，
     对于重要的服务使用单独服务器来部署，再或者公司最近推广的多中心。
 
（3）限流模式：上述的熔断模式和隔离模式都属于出错后的容错处理机制，而限流模式则可以称为预防模式。
     限流模式主要是提前对各个类型的请求设置最高的QPS阈值，若高于设置的阈值则对该请求直接返回，
     不再调用后续资源。这种模式不能解决服务依赖的问题，只能解决系统整体资源分配问题，
     因为没有被限流的请求依然有可能造成雪崩效应。
     
============================================================================================================
服务降级
服务器忙，请稍候再试，不让客户端等待并立刻返回一个友好提示，fallback

哪些情况会触发降级?
程序运行异常
超时
服务熔断触发服务降级
线程池/信号量打满也会导致服务降级



服务熔断?
类比保险丝达到最大服务访问后，
直接拒绝访问，拉闸限电，然后调用服务降级的方法并返回友好提示

服务的降级->进而熔断->恢复调用链路

服务限流 ?
秒杀高并发等操作，严禁一窝蜂的过来拥挤，大家排队，一秒钟N个，有序进行
项目 cloud-provider-hystrix-payment8001  7001  


成功 http://localhost:8001/payment/hystrix/ok/31

失败 http://localhost:8001/payment/hystrix/timeout/31

以上述为根基平台，从正确->错误->降级熔断->恢复
=======================================================================
开启Jmeter，来20000个并发压死8001，20000个请求都去访问paymentInfo_TimeOut服务

http://jmeter.apache.org/download_jmeter.cgi
下载 Binaries zip（win版本）
找到jmeter下的bin目录，打开jmeter.properties 文件

第三十七行（新版39行）修改为

language=zh_CN

去掉前面的#，以后打开就是中文界面了

看演示结果
两个都在自己转圈圈
结论:上面还是服务提供者8001自己测试，
假如此时外部的消费者80也来访问，那消费者只能干等，
最终导致消费端80不满意，服务端8001直接被拖死
====================================
新建 
cloud-consumer-feign-hystrix-order80

正常测试 http://localhost/consumer/payment/hystrix/ok/31
2W个线程压8001
http://localhost/consumer/payment/hystrix/timeout/31
消费者80，呜呜呜


故障现象和导致原因
8001同一层次的其他接口服务被困死，因为tomcat线程里面的工作线程已经被挤占完毕
80此时调用8001，客户端访问响应缓慢，转圈圈
==================================================

超时导致服务器变慢（转圈）  超时不再等待
出错（宕机或程序运行出错） 出错要有兜底

解决：
对方服务（8001）超时了，调用者（80）不能一直卡死等待，必须有服务降级
对方服务（8001）down机了，调用者（80）不能一直卡死等待，必须有服务降级
对方服务（8001）OK，调用者（80）自己出故障或有自我要求（自己的等待时间小于服务提供者），自己处理降级

====
服务降级
  
@HystrixCommand注解 见Hystrix8001 设置自身调用超时时间的峰值，峰值内可以正常运行，超过了需要有兜底的方法处理，作服务降级fallback 

@HystrixCommand报异常后如何处理
一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法


添加新注解@EnableCircuitBreaker
测试 http://localhost:8001/payment/hystrix/timeout/31  测试抛异常
     http://localhost:8001/payment/hystrix/timeoutok/31 测试ok
     
80订单微服务，也可以更好的保护自己，自己也依样画葫芦进行客户端降级保护 ***一般都是降级放80
其题外话：我们自己配置过的热部署方式对java代码的改动明显，但对@HystrixCommand内属性的修改建议重启微服务

yml
feign:
  hystrix:
    enabled: true #如果处理自身的容错就开启。开启方式与生产端不一样。
主启动类
@EnableHystrix   这个跟是生产者启动类注解不一样

业务
@HystrixCommand注解 见Hystrix8001 设置自身调用超时时间的峰值，峰值内可以正常运行，超过了需要有兜底的方法处理，作服务降级fallback 

@HystrixCommand报异常后如何处理
一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法

测试
http://localhost/consumer/payment/hystrix/timeout/31  失败
http://localhost:8001/payment/hystrix/timeoutok/2   成功


目前问题
每个业务方法对应一个兜底的方法，代码膨胀   一个一个写兜底得方法是傻逼行为
解决 :统一和自定义的分开
见OrderHystrixquanjutongyongController
@DefaultProperties(defaultFallback = "")
测试
http://localhost/tongyong/consumer/payment/hystrix/timeout/31  全局


http://localhost/tongyong/consumer/payment/hystrix/timeoutok/31 自定义

全局fallback方法不用加参数
==============================================================================

  控制层一个一个调用指定服务名称 万一服务宕机怎么办

jieouController
 在服务层配置全局降级方法
本次案例服务降级处理是在客户端80实现完成的，
与服务端8001没有关系，
只需要为Feign客户端定义的接口添加一个服务降级处理的实现类即可实现解耦

未来我们要面对的异常
运行：代码错误
超时
宕机

根据cloud-consumer-feign-hystrix-order80已经有的PaymentHystrixService接口
，重新新建一个类（PaymentFallbackService）实现该接口，统一为接口里面的方法进行异常处理
http://localhost/jieou/consumer/payment/hystrix/ok/1
关闭生产者 测试

=======================================================================
断路器  一句话就是家里保险丝
熔断机制概述
熔断机制是应对雪崩效应的一种微服务链路保护机制。当扇出链路的某个微服务出错不可用或者响应时间太长时，会进行服务的降级，进而熔断该节点微服务的调用，快速返回错误的响应信息。
当检测到该节点微服务调用响应正常后，恢复调用链路。
你能理解为找不到东西 还记得reids雪崩吗
@HystrixCommand注解

修改cloud-provider-hystrix-payment8001
//服务熔断
@HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
        @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),  //是否开启断路器
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),   //请求次数
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),  //时间范围
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"), //失败率达到多少后跳闸
})
public String paymentCircuitBreaker(@PathVariable("id") Integer id){
    if (id < 0){
        throw new RuntimeException("*****id 不能负数");
    }
    String serialNumber = IdUtil.simpleUUID();

    return Thread.currentThread().getName()+"\t"+"调用成功,流水号："+serialNumber;
}
public String paymentCircuitBreaker_fallback(@PathVariable("id") Integer id){
    return "id 不能负数，请稍候再试,(┬＿┬)/~~     id: " +id;
}

http://localhost:8001//payment/circuit/1 成功
http://localhost:8001//payment/circuit/-1 错误
持续访问 http://localhost:8001//payment/circuit/-1
完事访问http://localhost:8001//payment/circuit/1还是错误 得等一会才正常

从正确->错误->降级熔断->恢复
熔断类型
熔断打开 请求不再进行调用当前服务，内部设置时钟一般为MTTR(平均故障处理时间)，当打开时长达到所设时钟则进入熔断状态
熔断关闭 熔断关闭不会对服务进行熔断
熔断半开 部分请求根据规则调用当前服务，如果请求成功且符合规则则认为当前服务恢复正常，关闭熔断

涉及到断路器的三个重要参数：快照时间窗、请求总数阀值、错误百分比阀值。


断路器开启或者关闭的条件
当满足一定阀值的时候（默认10秒内超过20个请求次数）
当失败率达到一定的时候（默认10秒内超过50%请求失败）
到达以上阀值，断路器将会开启
当开启的时候，所有请求都不会进行转发
一段时间之后（默认是5秒），这个时候断路器是半开状态，会让其中一个请求进行转发。如果成功，断路器会关闭，若失败，继续开启。重复4和5
====================================
限流 alibaba的Sentinel说明


==========================================================================
服务监控hystrixDashboard  ：除了隔离依赖服务的调用以外，Hystrix还提供了准实时的调用监控（Hystrix Dashboard），Hystrix会持续地记录所有通过Hystrix发起的请求的执行信息，
                    并以统计报表和图形的形式展示给用户，包括每秒执行多少请求多少成功，多少失败等。
                    Netflix通过hystrix-metrics-event-stream项目实现了对以上指标的监控。Spring Cloud也提供了Hystrix Dashboard的整合，对监控内容转化成可视化界面。




新建cloud-consumer-hystrix-dashboard9001

HystrixDashboardMain9001+新注解@EnableHystrixDashboard



所有Provider微服务提供类（8001/8002/8003）都需要监控依赖配置
 
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>


http://localhost:9001/hystrix
修改cloud-provider-hystrix-payment8001

注意：新版本Hystrix需要在主启动类MainAppHystrix8001中指定监控路径 要不报错
Unable to connect to Command Metric Stream   404

仪表盘配置见ReadMe仪表盘9001.jpg

添写监控地址
http://localhost:8001/hystrix.stream

访问http://localhost:8001//payment/circuit/9 查看仪表盘

如何看
7色
1圈：实心圆：共有两种含义。它通过颜色的变化代表了实例的健康程度，它的健康度从绿色<黄色<橙色<红色递减。
   该实心圆除了颜色的变化之外，它的大小也会根据实例的请求流量发生变化，流量越大该实心圆就越大。
   所以通过该实心圆的展示，就可以在大量的实例中快速的发现故障实例和高压力实例。
1线 ：曲线：用来记录2分钟内流量的相对变化，可以通过它来观察到流量的上升和下降趋势。

整图 见 熔断仪表盘.jpg
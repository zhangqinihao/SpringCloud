问题
在微服务框架中，一个由客户端发起的请求在后端系统中会经过多个不同的的服务节点调用来协同产生最后的请求结果，
每一个前段请求都会形成一条复杂的分布式服务调用链路，链路中的任何一环出现高延时或错误都会引起整个请求最后的失败。

spring Cloud Sleuth提供了一套完整的服务跟踪的解决方案
在分布式系统中提供追踪解决方案并且兼容支持了zipkin
SpringCloud从F版起已不需要自己构建Zipkin server了，只需要调用jar包即可
https://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/ 下载
zipkin-server-2.12.9.exec.jar
运行jar
运行控制台
http://localhost:9411/zipkin/
名词解释
Trace:类似于树结构的Span集合，表示一条调用链路，存在唯一标识
span:表示调用链路来源，通俗的理解span就是一次请求信息

cloud-provider-payment8002

pom
<!--包含了sleuth+zipkin-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
        
yml
server:
  port: 8002


spring:
  application:
    name: cloud-payment-service
  zipkin:
     base-url: http://localhost:9411
  sleuth:
     sampler:
     probability: 1 # 0-1之间 1代表全部采集
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.gjt.mm.mysql.Driver
    url: jdbc:mysql://localhost:3306/db2019?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai
    username: root
    password: 1234
    #以下为新增
    druid:
      # 指明是否在从池中取出连接前进行检验,如果检验失败, 则从池中去除连接并尝试取出另一个，
      #注意: 设置为true后如果要生效,validationQuery参数必须设置为非空字符串
      test-on-borrow: false
      # 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除.
      #注意: 设置为true后如果要生效,validationQuery参数必须设置为非空字符串
      test-while-idle: true
      # 指明是否在归还到池中前进行检验，注意: 设置为true后如果要生效,
      #validationQuery参数必须设置为非空字符串
      test-on-return: false
      # SQL查询,用来验证从连接池取出的连接,在将连接返回给调用者之前.
      #如果指定,则查询必须是一个SQL SELECT并且必须返回至少一行记录
      validation-query: select 1
eureka:
  client:
    register-with-eureka: true #注册eureka 默认true
    fetchRegistry: true  #从eureka抓取已有的节点 默认 true 。 单节点无所谓  集群必须设置true 才能负载均衡
    service-url:
      #defaultZone: http://localhost:7001/eureka
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka  #集群版
  instance:
    instance-id: payment8002
    prefer-ip-address: true #ip显示
mybatis:
  mapperLocations: classpath:mapper/*.xml
  type-aliases-package: com.atguigu.springcloud.entities


controller
 @GetMapping("/payment/zipkin")
    public String paymentZipkin()
    {
        return "hi ,i'am paymentzipkin server fall back，welcome to atguigu，O(∩_∩)O哈哈~";
    }


cloud-consumer-order80
pom
 <!--包含了sleuth+zipkin-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
yml
server:
  port: 80
 
spring:
    application:
        name: cloud-order-service
    zipkin:
      base-url: http://localhost:9411
    sleuth:
      sampler:
        probability: 1
 
eureka:
  client:
    #表示是否将自己注册进EurekaServer默认为true。
    register-with-eureka: false
    #是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      #单机
      #defaultZone: http://localhost:7001/eureka
      # 集群
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka  # 集群版
      
      
 controller
 // ====================> zipkin+sleuth
     @GetMapping("/consumer/payment/zipkin")
     public String paymentZipkin()
     {
         String result = restTemplate.getForObject("http://localhost:8001"+"/payment/zipkin/", String.class);
         return result;
     }

 
依次启动eureka7001/8002/80 
 80调用8002几次测试下   
 5.打开浏览器访问:http:localhost:9411
 出现页面
 就能看到消费者跟生产者得依赖关系了    
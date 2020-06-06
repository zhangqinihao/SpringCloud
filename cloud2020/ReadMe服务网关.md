zuul  
127.0.0.1  myzuul.com host映射
Zuul包含了对请求的路由和过滤两个最主要的功能：
其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础.Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中获得其他微服务的消息，也即以后的访问微服务都是通过Zuul跳转后获得。
注意：Zuul服务最终还是会注册进Eureka
提供=代理+路由+过滤三大功能
新建Module模块microservicecloud-zuul-gateway-9527

注解
@EnableDiscoveryClient
@EnableZuulProxy

yml
server:
  port: 9527 #服务端口


spring:
  application:
    name: microservicecloud-zuul-gateway #指定服务名

#配置路由
zuul:
  routes:
    payment8001: # 这里是路由id，随意写
      path: /** # 这里是映射路径
      url: http://127.0.0.1:8001 # 映射路径对应的实际url地址 (url跟serviceId选一个)
      #serviceId : cloud-provider-hystrix-payment
eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
  instance:
    instance-id: gateway-9527.com
    prefer-ip-address: true


info: #可写可不写
  app.name: atguigu-microcloud
  company.name: www.atguigu.com
  build.artifactId: $project.artifactId$
  build.version: $project.version$
  
路由访问映射规则
工程microservicecloud-zuul-guizegateway-9527
zuul: 
  routes: 
    mydept.serviceId: cloud-provider-hystrix-payment #a
    mydept.path: /mydept/**  #b

b路径代替a路径
before
http://myzuul.com:9527/cloud-provider-hystrix-payment/dept/get/2
after
http://myzuul.com:9527/mydept/dept/get/1


此时问题 上面两个路径都ok
zuul: 
  ignored-services: cloud-provider-hystrix-payment 
  （单个具体，多个可以用"*"）
zuul: 
  ignored-services: *
  隐藏a路径
  http://myzuul.com:9527/cloud-provider-hystrix-payment/dept/get/2 失效了
设置统一前缀  
zuul: 
  prefix: /api
http://localhost:9527/api/mydept/payment/hystrix/ok/31
http://myzuul.com:9527/api/mydept/payment/hystrix/ok/31
==================================================================================================
Gateway
Gateway是在Spring生态系统之上构建的API网关服务，基于Spring 5，Spring Boot 2和Project Reactor等技术。
Gateway旨在提供一种简单而有效的方式来对API进行路由，以及提供一些强大的过滤器功能，例如：熔断、限流、重试等
SpringCloud Gateway 是Spring Cloud 的一个全新项目，基于Spring 5.0+Spring Boot 2.0和Project Reactor等技术开发的网关，它旨在为微服务架构提供一种简单有效的统一的API路由管理方式。
SpringCloud Gateway作为Spring Cloud 生态系统中的网关，目标是替代Zuul，在Spring Cloud2.0以上版本中，没有对新版本的Zuul2.0以上最新高性能版本进行集成，仍然还是使用的Zuul 1.x非Reactor模式的老版本。而为了提升网关的性能，SpringCloud Gateway是基于WebFlux框架实现的，而WebFlux框架底层则使用了高性能的Reactor模式通信框架Netty。
Spring Cloud Gateway的目标提供统一的路由方式且基于Filter链的方式提供了网关基本的功能，例如：安全，监控/指标，和限流。
Spring Cloud Gateway 使用的Webflux中的reactor-netty响应式编程组件，底层使用了Netty通讯框架

能干嘛？
反向代理，鉴权，流量控制，熔断，日志监控
我们为什么选择Gatway?
1.neflix不太靠谱，zuul2.0一直跳票,迟迟不发布
2.SpringCloud Gateway具有如下特性
动态路由：能够匹配任何请求属性；
可以对路由指定 Predicate（断言）和Filter（过滤器）；
集成Hystrix的断路器功能；
集成Spring Cloud 服务发现功能；
易于编写的Predicate（断言）和Filter（过滤器）；
请求限流功能；
支持路径重写。
3.SpringCloud Gateway与Zuul的区别
1、Zuul 1.x，是一个基于阻塞I/O的API Gateway
2、Zuul 1.x基于Servlet 2.5使用阻塞架构它不支持任何长连接（如WebSocket）Zuul的设计模式和Nginx较像，每次I/O操作都是从工作线程中选择一个执行，请求线程被阻塞到工作线程完成，但是差别是Nginx用C++实现，Zuul用Java实现，而JVM本身会有第一次加载较慢的情况，使得Zuul的性能相对较差。
3、Zuul2.x理念更先进，想基于Netty非阻塞和支持长连接，但SpringCloud目前还没有整合。Zuul2.x的性能较Zuul1.x有较大提升
。在性能方面，根据官方提供的基准测试，Spring Cloud Gateway的RPS（每秒请求数）是Zuul的1.6倍。
4、Spring Cloud Gateway 建立在Spring Framework 5、Project Reactor和Spring Boot2之上，使用非阻塞API。
5、Spring Cloud Gateway 还支持WebSocket，并且与Spring紧密集成拥有更好的开发体验

WebFlux是什么?
传统的Web框架，比如说：struts2，springmvc等都是基于Servlet APl与Servlet容器基础之上运行的。
但是
在Servlet3.1之后有了异步非阻塞的支持。而WebFlux是一个典型非阻塞异步的框架，它的核心是基于Reactor的相关API实现的。相对于传统的web框架来说，它可以运行在诸如Netty，Undertow及支持Servlet3.1的容器上。非阻塞式+函数式编程（Spring5必须让你使用java8）
Spring WebFlux是Spring 5.0引入的新的响应式框架，区别于Spring MVC，它不需要依赖ServletAPI，它是完全异步非阻塞的，并且基于Reactor来实现响应式流规范。

三大核心概念
Route(路由) :路由是构建网关的基本模块，它由ID，目标URI，一系列的断言和过滤器组成，如果断言为true则匹配该路由
Predicate（断言）:参考的是java8的java.util.function.Predicate开发人员可以匹配HTTP请求中的所有内容（例如请求头或请求参数），如果请求与断言相匹配则进行路由
Filter(过滤) :指的是Spring框架中GatewayFilter的实例，使用过滤器，可以在请求被路由前或者之后对请求进行修改。

核心逻辑
路由转发+执行过滤器链
创建工程
cloud-gateway-gateway9527

yml
server:
  port: 9527
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true  #开启从注册中心动态创建路由的功能，利用微服务名进行路由
      routes:
        - id: payment_routh #路由的ID，没有固定规则但要求唯一，建议配合服务名
          #uri: http://localhost:8001   #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service
          predicates:
            - Path=/payment/get/**   #断言,路径相匹配的进行路由

        - id: payment_routh2
          #uri: http://localhost:8001   #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service
          predicates:
            - Path=/payment/lb/**   #断言,路径相匹配的进行路由


eureka:
  instance:
    hostname: cloud-gateway-service
  client:
    service-url:
      register-with-eureka: true
      fetch-registry: true
      defaultZone: http://eureka7001.com:7001/eureka

注意
不需要 spring-boot-starter-web dependency


9527网关如何做路由映射那？？？
cloud-provider-payment8002看看controller的访问地址
我们目前不想暴露8001端口，希望在8001外面套一层9527
添加网关前  http://localhost:8002/payment/get/1
添加网关后 http://localhost:9527/payment/get/1

Gateway网关路由有两种配置方式
1在配置文件yml中配置 以上配置
2代码中注入RouteLocator的Bean
百度国内新闻网址，需要外网  http://news.baidu.com/guoji

自己写一个

业务需求 通过9527网关访问到外网的百度新闻网址
config
http://localhost:9527/guonei

http://localhost:9527/guoji


通过微服务名实现动态路由
cloud-provider-payment8002
cloud-provider-payment8003
yml
server:
  port: 9527
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true  #开启从注册中心动态创建路由的功能，利用微服务名进行路由
      routes:
        - id: payment_routh #路由的ID，没有固定规则但要求唯一，建议配合服务名
          #uri: http://localhost:8001   #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service
          predicates:
            - Path=/payment/get/**   #断言,路径相匹配的进行路由

        - id: payment_routh2
          #uri: http://localhost:8001   #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service
          predicates:
            - Path=/payment/lb/**   #断言,路径相匹配的进行路由


eureka:
  instance:
    hostname: cloud-gateway-service
  client:
    service-url:
      register-with-eureka: true
      fetch-registry: true
      defaultZone: http://eureka7001.com:7001/eureka


测试
http://localhost:9527/payment/lb  端口来回切换

=====================================================================
常用的Route Predicate
All
yml
server:
  port: 9527
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true  #开启从注册中心动态创建路由的功能，利用微服务名进行路由
      routes:
        - id: payment_routh #路由的ID，没有固定规则但要求唯一，建议配合服务名
          #uri: http://localhost:8001   #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service
          predicates:
            - Path=/payment/get/**   #断言,路径相匹配的进行路由
 
        - id: payment_routh2
          #uri: http://localhost:8001   #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service
          predicates:
            - Path=/payment/lb/**   #断言,路径相匹配的进行路由
            #- After=2020-03-08T10:59:34.102+08:00[Asia/Shanghai]
            #- Cookie=username,zhangshuai #并且Cookie是username=zhangshuai才能访问
            #- Header=X-Request-Id, \d+ #请求头中要有X-Request-Id属性并且值为整数的正则表达式
            #- Host=**.atguigu.com
            #- Method=GET
            #- Query=username, \d+ #要有参数名称并且是正整数才能路由
 
 
eureka:
  instance:
    hostname: cloud-gateway-service
  client:
    service-url:
      register-with-eureka: true
      fetch-registry: true
      defaultZone: http://eureka7001.com:7001/eureka
 ========================================================================================
 filter
 路由过滤器可用于修改进入的HTTP请求和返回的HTTP响应，路由过滤器只能指定路由进行使用。
 
 编写filter
 启动
 http://localhost:9527/payment/lb?username=z3 成功
 http://localhost:9527/payment/lb 错误





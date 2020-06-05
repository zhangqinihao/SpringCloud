Feign
Feign是一个声明式WebService客户端。
使用Feign能让编写Web Service客户端更加简单, 
它的使用方法是定义一个接口，然后在上面添加注解，
同时也支持JAX-RS标准的注解。
Feign是一个声明式的Web服务客户端，使得编写Web服务客户端变得非常容易，
只需要创建一个接口，然后在上面添加注解即可。

Feign集成了Ribbon
利用Ribbon维护了MicroServiceCloud-Dept的服务列表信息，
并且通过轮询实现了客户端的负载均衡。
而与Ribbon不同的是，通过feign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用
=============================================================
项目搭建 参考8001 feign80 模块
cloud-consumer-order80-feign
 <!--feign接口-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>
1消费者启动类
@EnableFeignClients//开启服务调用
2消费者创建service

@FeignClient(name="shengchan")//生产者服务名
@Service
public interface EmppService {

    @RequestMapping("user/show")//（user是生产者项目名）生产者方法名
    public List<Teacher> aaa(Teacher teacher);

}

3控制层调用service方法
####
####一个关于Feign调用的巨坑，能坑死人  参数传递为null值
#### https://blog.csdn.net/wk52525/article/details/79183113 
#### https://blog.csdn.net/weixin_41595700/article/details/86508352?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase

 1.当参数比较复杂时，feign即使声明为get请求也会强行使用post请求

 2.不支持@GetMapping类似注解声明请求，需使用@RequestMapping(value = "url",method = RequestMethod.GET)

 3.使用@RequestParam注解时必须要在后面加上参数名

============================================================================================
OpenFeign
Feign是一个声明式WebService客户端。使用Feign能让编写Web Service客户端更加简单。
它的使用方法是定义一个服务接口然后在上面添加注解。Feign也支持可拔插式的编码器和解码器。Spring Cloud对Feign进行了封装，
使其支持了Spring MVC标准注解和HttpMessageConverters。Feign可以Eureka和Ribbon组合使用以支持负载均衡

Feign能于什么
Feign旨在使编写Java Http客户端变得更容易。
前面在使用Ribbon+RestTemplate时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。
所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它（以前是Dao接口上面标注Mapper注解，现在是一个微服务接口上面标注一个Feign注解即可），即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。
Feign集成了Ribbon利用Ribbon维护了Payment的服务列表信息，并且通过轮询实现了客户端的负载均衡。而与Ribbon不同的是，通过feign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用

新建项目 见openfeign-order80 8002   8003 模块
测试 http://localhost/openfeign/payment/get/1


OpenFeign 就不存在 Feign死坑的问题 yml也不用配置数据库
天生自带负载均衡功能  
推荐 OpenFeign

OpenFeign超时控制 了解
OpenFeign默认等待一秒钟，超过后报错
配置yml
ribbon:
  ReadTimeout:  5000
  ConnectTimeout: 5000

===========================================================================

OpenFeign日志打印功能 
Feign 提供了日志打印功能，我们可以通过配置来调整日志级别，从而了解 Feign中Http请求的细节。
说白了就是对Feign接口的调用情况进行监控和输出
日志级别
NONE：默认的，不显示任何日志；
BASIC：仅记录请求方法、URL、响应状态码及执行时间；
HEADERS：除了BASIC中定义的信息之外，还有请求和响应的头信息；
FULL：除了HEADERS中定义的信息之外，还有请求和响应的正文及元数据。

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}

yml
logging:
  level:
    #feign日志以什么级别监控哪个接口
    com.atguigu.springcloud.api.PaymentServiceApi: debug``

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




版本采用springboot 2.x+springcloud H
***sql文件存放在 commons模块
===========================================================

RestTemplate提供了多种便捷访问远程Http服务的方法，
是一种简单便捷的访问restful服务模板类，
是Spring提供的用于访问Rest服务的客户端模板工具集


单机注册中心
生产者
消费者


===============================================================
eureka用到了高可用集群搭建  可以用SwitchHosts!修改本地端口号
两个注册中心互相注册
127.0.0.1  eureka7001.com
127.0.0.1  eureka7002.com
两个生产者

基础
集群搭建加分负载均衡

===========================================================
细节修改生生产者服务信息
instance:
    instance-id: payment8001/8002
    prefer-ip-address: true //ip显示 点击服务信息显示ip
查看服务和状态
http://localhost:8001(端口号)/actuator/health

服务发现
@Resource
private DiscoveryClient discoveryClient;
主启动类
@EnableDiscoveryClient
======================================================
eureka自我保护  高可用设计思想
保护模式主要用于一组客户端和Eureka Server之间存在网络分区场景下的保护。
一旦进入保护模式，Eureka Server将会尝试保护其服务注册表中的信息，
不再删除服务注册表中的数据，也就是不会注销任何微服务。

为什么会产生Eureka自我保护机制？
为了防止EurekaClient可以正常运行，但是与EurekaServer网络不通情况下，Eurekaserver不会立刻将EurekaClient服务剔除

禁用自我保护模式 见端口7001
server:
  enable-self-preservation: false
  eviction-interval-timer-in-ms: 2000

见8001端口   发生意外立即停止注册
   向客户端发送心跳时间间隔 默认30秒
    lease-renewal-interval-in-seconds: 1
    #Eureka服务端在收到最后一心跳后等得时间上限，单位为（默认是90秒），超时将剔除服务
    lease-expiration-duration-in-seconds: 2

===========================================================================================================
进入Dubbo文件夹

zooker

进入 zookeeper-3.4.13/bin  cmd zkServer.cmd
测试连接 进入zookeeper-3.4.13/bin  cmd zkCli.cmd     再点回车
端口号127.0.0.1:2181
可视化工具 账户密码root
cmd java -jar  dubbo-admin-0.0.1-SNAPSHOT.jar 端口号 7001

生产者连接可能出现问题
   <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-zookeeper-discovery -->
        
测试是否进入
 cmd zkCli.cmd     再点回车 ls /services
 Zookeeper做集群的不多
========================================================================================
注册中心Consul go语言写的
解压后有一个exe文件，在当前文件夹运行cdm 
consul 查看版本
consul agent -dev  运行
可视化页面  http://localhost:8500
========================================================================

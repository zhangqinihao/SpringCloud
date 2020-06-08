为什么叫Nacos
前四个字母分别为Naming和Configuration的前两个字母，最后的s为Service
是什么？
一个更易于构建云原生应用的动态服务发现，配置管理和服务管理中心
Nacos就是注册中心+配置中心的组合
等价于 Nacos = Eureka+Config+Bus
去哪下
https://github.com/alibaba/Nacos
文档
https://nacos.io/zh-cn/index.html

下载 1.1.4 https://github.com/alibaba/nacos/releases/tag/1.1.4
我下载得 1.2.1  zip win版
本地Java8+Maven环境已经OK


解压安装包，直接运行bin目录下的startup.cmd
访问http://localhost:8848/nacos
默认账号密码都是nacos
==============================================================================
基于Nacos的服务提供者
cloudalibaba-provider-payment9001
访问  http://localhost:9001/payment/nacos/1
为了下一章节演示nacos的负载均衡，参照9001新建9002
不想建就虚拟映射

cloudalibaba-provider-payment9002
访问  http://localhost:9002/payment/nacos/1

cloudalibaba-consumer-nacos-order83
测试
http://localhost:83/consumer/payment/nacos/13
83访问9001/9002，轮询负载OK
Nacos支持AP和CP模式的切换
C是所有节点在同一时间看到的数据是一致的；而A的定义是所有的请求都会收到响应。

一般来说，如果不需要存储服务级别的信息目服务实例是通过nacos-client主册，并能够保持心跳上报，那么就可以选择AP模式。当前主流的服务如 Spring cloud和Dubbo服务，都适用于AP模式，AP模式为了服务的可能性而减弱了一致性，因此AP模式下只支持注册临时实例。
如果需要在服务级别编辑或者存储配置信息，那么CP是必须，K8S服务和DNS服务则适用于CP模式。
CP模式下则支持注册持久化实例，此时则是以Raft 协议为集群运行模式，该模式下注册实例之前必须先注册服务，如果服务不存在，则会返回错误。
=================================================================================================================

Nacos作为服务配置中心演示
Nacos作为配置中心-基础配置
cloudalibaba-config-nacos-client3377
两个yml
为什么两个
Nacos同springcloud-config一样，在项目初始化时，要保证先从配置中心进行配置拉取，拉取配置之后，才能保证项目的正常启动。
springboot中配置文件的加载是存在优先级顺序的，bootstrap优先级高于application

在8848端口写
nacos-config-client-dev.yaml（不是yml）
指定yaml 格式  
内容:
config:
    info: from nacos config center, nacos-config-client-dev.yaml, version=1


测试 http://localhost:3377/config/info
    
Nacos作为配置中心-分类配置
 问题1：
 实际开发中，通常一个系统会准备
 dev开发环境
 test测试环境
 prod生产环境。
 I如何保证指定环境启动时服务能正确读取到Nacos上相应环境的配置文件呢？
 问题2：
 一个大型分布式微服务系统会有很多微服务子项目，每个微服务项目又都会有相应的开发环境、测试环境、预发环境、正式环境……
 那怎么对这些微服务配置进行管理呢？
 DataID方案    默认空间+默认分组+新建dev和test两个DataID、
 
 Group方案      通过Group实现环境区分  新建Group
 
 在nacos图形界面控制台上面新建配置文件DataID  
 
 bootstrap+application   在config下增加一条group的配置即可。可配置为DEV_GROUP或TEST_GROUP
 http://localhost:3377/config/info  配置变了
 
 Namespace方案
  http://localhost:3377/config/info 
 
 ===========================================================================================================
 Nacos集群和持久化配置（重要）
 nacos-server nacos\conf目录下找到application.properties
 最后一行加
 spring.datasource.platform=mysql
  
 db.num=1
 db.url.0=jdbc:mysql://localhost:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
 db.user=root
 db.password=1234
 
 
 重新改管理列表信息  发现数据库也有信息 存入数据库了
 
 Linux版Nacos+MySQL生产环境配置
 
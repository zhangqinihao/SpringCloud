是什么
屏蔽底层消息中间件的差异，降低切换版本，统一消息的编程模型


Stream标准流程套路
Binder 绑顶器 很方便的连接中间件，屏蔽差异
Channel  频道  通道，是队列Queue的一种抽象，在消息通讯系统中就是实现存储和转发的媒介，通过对Channel对队列进行配置 
Source和Sink   Stream自身，从Stream发布消息就是输出，接受消息就是输入

RabbitMQ环境已经OK

工程中新建三个子模块
cloud-stream-rabbitmq-provider8801,作为生产者进行发消息模块
yml 就复制我的 报错也别怀疑 能启动
server:
  port: 8801

spring:
  application:
    name: cloud-stream-provider
  cloud:
    stream:
      binders: # 在此处配置要绑定的rabbitmq的服务信息；
        defaultRabbit: # 表示定义的名称，用于于binding整合
          type: rabbit # 消息组件类型
          environment: # 设置rabbitmq的相关的环境配置
            spring:
              rabbitmq:
                host: localhost
                port: 5672
                username: guest
                password: guest
      bindings: # 服务的整合处理
        output: # 这个名字是一个通道的名称
          destination: studyExchange # 表示要使用的Exchange名称定义
          content-type: application/json # 设置消息类型，本次为json，文本则设置“text/plain”
          binder: defaultRabbit  # 设置要绑定的消息服务的具体设置

eureka:
  client: # 客户端进行Eureka注册的配置
    service-url:
      defaultZone: http://localhost:7001/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 # 设置心跳的时间间隔（默认是30秒）
    lease-expiration-duration-in-seconds: 5 # 如果现在超过了5秒的间隔（默认是90秒）
    instance-id: send-8801.com  # 在信息列表时显示主机名称
    prefer-ip-address: true     # 访问的路径变为IP地址












 访问 http://localhost:8801/sendMessage

cloud-stream-rabbitmq-consumer8802,作为消息接收模块
server:
  port: 8802

spring:
  application:
    name: cloud-stream-consumer
  cloud:
    stream:
      binders: # 在此处配置要绑定的rabbitmq的服务信息；
        defaultRabbit: # 表示定义的名称，用于于binding整合
          type: rabbit # 消息组件类型
          environment: # 设置rabbitmq的相关的环境配置
            spring:
              rabbitmq:
                host: localhost
                port: 5672
                username: guest
                password: guest
      bindings: # 服务的整合处理
        input: # 这个名字是一个通道的名称
          destination: studyExchange # 表示要使用的Exchange名称定义
          content-type: application/json # 设置消息类型，本次为json，文本则设置“text/plain”
          binder: defaultRabbit  # 设置要绑定的消息服务的具体设置

eureka:
  client: # 客户端进行Eureka注册的配置
    service-url:
      defaultZone: http://localhost:7001/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 # 设置心跳的时间间隔（默认是30秒）
    lease-expiration-duration-in-seconds: 5 # 如果现在超过了5秒的间隔（默认是90秒）
    instance-id: receive-8802.com  # 在信息列表时显示主机名称
    prefer-ip-address: true     # 访问的路径变为IP地址

测试8801发送8802接收消息
这里启动服务可能会报连接拒绝的异常，说一下原因：
ream-rabbit依赖里面有amgp的自动配置，老师用的是本机的mq，“如果有人用虚拟机的mq，就会抛出异常
http://localhost:8801/sendMessage



分组消费与持久化
cloud-stream-rabbitmq-consumer8803,作为消息接收模块
http://localhost:8801/sendMessage
消费 目前是8802/8803同时都收到了，存在重复消费问题
微服务应用放置于同一个group中，就能够保证消息只会被其中一个应用消费一次。
不同的组是可以消费的，同一个组内会发生竞争关系，只有其中一个可以消费。

如何解决
分组和持久化属性group
group: groupA、groupB
8802修改YML group:  groupA
8803修改YML group:  groupB  见yml
结论  还是重复消费

8802/8803实现了轮询分组，每次只有一个消费者 
8801模块的发的消息只能被8802或8803其中一个接收到，这样避免了重复消费
yml 
8802修改YML group:  groupA
8803修改YML group:  groupA
结论
同一个组的多个微服务实例，每次只会有一个拿到
==================================================================
通过上述，解决了重复消费问题，再看看持久化
停止8802/8803并去除掉8802的分组group:groupA
8803的分组group:groupA没有去掉
先启动8802，无分组属性配置，后台没有打出来消息
先启动8803，有分组属性配置，后台打出来了MQ上的消息
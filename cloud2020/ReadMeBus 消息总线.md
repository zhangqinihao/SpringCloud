Spring Cloud Bus配合Spring Cloud Config使用可以实现配置的动态刷新

Bus支持两种消息代理：RabbitMQ和Kafka
安装RabbitMQ，下载地址
新建 cloud-config-client-3366

2) 利用消息总线触发一个服务端ConfigServer的/bus/refresh端点,而刷新所有客户端的配置（更加推荐）

给cloud-config-center-3344配置中心服务端添加消息总线支持 
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>

rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    
3366
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>

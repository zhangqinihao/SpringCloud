是什么?
一句话解释，之前我们讲解过的Hystrix

Hystrix
1需要我们程序员自己手工搭建监控平台
2没有一套web界面可以给我们进行更加细粒度化得配置流控、速率控制、服务熔断、服务降级。。...。

去哪下
https://github.com/alibaba/Sentinel/releases

服务使用中的各种问题?
服务雪崩
服务降级
服务熔断
服务限流

安装Sentinel控制台
sentinel组件由2部分组成
后台
前台8080

下载
https://github.com/alibaba/Sentinel/releases
下载到本地sentinel-dashboard-1.7.0.jar


运行命令

前提 java8环境OK   8080端口不能被占用

命令  java -jar sentinel-dashboard-1.7.0.jar
访问sentinel管理界面
http://localhost:8080
登录账号密码均为sentinel

初始化演示工程
启动Nacos8848成功
模块 cloudalibaba-sentinel-service8401
启动Sentinel8080
启动微服务8401

启动8401微服务后查看sentienl控制台
空空如也，啥都没有
Sentinel采用的懒加载说明
执行一次访问即可
http://localhost:8401/testA
http://localhost:8401/testB
结论
sentinel8080正在监控微服务8401
==============================================================================
流控模式
直接（默认）
设置每秒访问一次
QPS：每秒查询率QPS是对一个特定的查询服务器在规定时间内所处理流量多少的衡量标准
快速点击访问http://localhost:8401/testA  结果报错 Blocked by Sentinel (flow limiting) 只能一秒点一次

思考？？？ 直接调用默认报错信息，技术方面OK but，是否应该有我们自己的后续处理？
关联
多线程，是指从软件或者硬件上实现多个线程并发执行的技术
是什么？
当关联的资源达到阈值时，就限流自己
当与A关联的资源B达到阈值后，就限流自己
B惹事，A挂了
配置A
postman模拟并发密集访问testB
访问testB成功
postman里新建多线程集合组
将访问地址添加进新线程组
Run
http://localhost:8401/testB 
大批量线程高并发访问B，导致A失效了

运行后发现testA挂了
点击访问http://localhost:8401/testA
结果  Blocked by Sentinel (flow limiting)

QPS（每秒钟的请求数量）：当调用该ap的QPS达到调值的时候，进行限流

线程数：当调用该api的线程数达到阀值的时候，进行限流
测试  访问被拒绝

流控效果
直接->快速失败（默认的流控处理）   直接失败，抛出异常     Blocked by Sentinel (flow limiting)

预热 说明 公式：阈值除以coldFactor（默认值为3），经过预热时长后才会达到阈值    Warmup配置 多次点击http://localhost:8401/testB
(说人话就是一开始多次点击报错，时间过后访问就不报错了)


排队等待
匀速排队，阈值必须设置为QPS


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
====================================================================
降级规则
降级策略实战
RT
平均响应时间（DEGRADE_GRADE_RT）：当15内持续进入5个请求，对应时刻的平均响应时间（秒级）均超过阀值（count，以ms为单位），那么在接下的时间窗口（DegradeRule中的timewindow，以s为单位）之内，
对这个方法的调用都会自动地熔断（抛出DegradeException）。注意Sentinel默认统计的RT上限是4900ms，超出此阀值的都会算作4900ms，若需要变更此上限可以通过启动配置项-Dcsp.sentinel.statistic.max.rt=xxx来配置。

代码



    @GetMapping("/testD")
    public String testD()
    {
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        log.info("testD 测试RT");

        return "------testD";
    }

jmeter压测
=================================
异常比例
异常比例（DEGRADE_GRADE_EXCEPTION_RATIO）：当资源的每秒请求量>=5，并且每秒异常总数占通过量的比值超过阈值（DegradeRule中的count）之后，
资源进入降级状态，即在接下的时间窗口（DegradeRu1e中的timelindow，以s为单位）之内，对这个方法的调用都会自动地返回。异常比率的阀值范围是[e.e，1.e]，代表0%-100%。

结论
按照上述配置，单独访问一次，必然来一次报错一次（int age=10/0），调一次错一次；

开启jmeter后，直接高并发发送请求，多次调用达到我们的配置条件了。
断路器开启（保险丝跳闸），微服务不可用了，不再报错error而是服务降级了。
=============================================
异常数
异常数（DEGRADE_GRADE_EXCEPTTON_COUNT）：当资源近1分钟的异常数目超过阀值之后会进行熔断。注意由于统计时间窗口是分钟级别的，
若timewindow小于60s，则结束熔断状态后仍可能再进入熔断状态。

代码
@GetMapping("/testE")
public String testE()
{
    log.info("testE 测试异常数");
    int age = 10/0;
    return "------testE 测试异常数";
}

访问  http://localhost:8401/testE   五次  降级ok


======================================================================================
热点key限流

官网
https://github.com/alibaba/Sentinel/wiki/热点参数限流
·商品ID为参数，统计一段时间内最常购买的商品 ID并进行限制
·用户1D为参数，针对一段时间内频繁访问的用户ID进行限流、热点参数限流会统计传入参数中的热点参数，并根据配置的限流满值与模式，对包含热点参数的资源调用进行限流。
热点参数限流可以看做是一种特殊的流量控制，仅对包含热点参数的资源澜用生效。

访问 ：http://localhost:8401/testHotKey

访问   http://localhost:8401/testHotKey?p1=abc&p2=33

配置
加速访问   http://localhost:8401/testHotKey?p1=abc&p2=33 出问题了
但是加速访问   http://localhost:8401/testHotKey?p2=33 不出问题了 因为没设置p2

=====================================================
参数例外项
上述案例演示了第一个参数p1,当QPS超过1秒1次点击后马上被限流
特殊情况
普通 超过1秒钟一个后，达到阈值1后马上被限流
我们期望p1参数当它是某个特殊值时，它的限流值和平时不一样
特例 假如当p1的值等于5时，它的阈值可以达到200

前提条件
热点参数的注意点，参数必须是基本类型或者String

以上针对Sentinel配置的异常
其他
手贱添加异常看看....  controller  不管用 运行时出错不管
=================================================================================
系统规则
系统规则支持以下的模式：
·Load 自适应（仅对Linux/Unix-like机器生效）：系统的load1作为启发指标，进行自适应系统保护。当系统load1超过设定的启发值，且系统当前的并发线程数超过估算的系统容量时才会触发系统保护（BBR阶段）。系统容墨由系统的maxQp5*minRt估算得出。设定参考值一般是CPU cores*2.5。I
·CPU usage（1.5.0+版本）：当系统CPU使用率超过闽值即触发系统保护（取值范围0.0-
1.0），比较灵敏。
·平均RT：当单台机器上所有入口流星的平均RT达到网值即触发系统保护，单位是毫秒。
·并发线程数：当单台机器上所有入口流量的并发线程数达到闽值即触发系统保护。
·入口QPS：当单台机器上所有入口流量的QPS达到闽值即触发系统保护。

不建议使用系统规则















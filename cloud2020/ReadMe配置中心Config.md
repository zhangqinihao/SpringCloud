微服务意味着要将单体应用中的业务拆分成一个个子服务，每个服务的粒度相对较小，因此系统中会出现大量的服务。由于每个服务都需要必要的配置信息才能运行，所以一套集中式的、动态的配置管理设施是必不可少的。
SpringCloud提供了ConfigServer来解决这个问题，我们每一个微服务自己带着一个application.yml，上百个配置文件的管理……

SpringCloud Config为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供了一个中心化的外部配置。

用你自己的账号在Github上新建一个名为sprincloud-config的新Repository 我这里放在我脚手架里面了

127.0.0.1 config-3344.com
新建Module模块cloud-config-center-3344它既为Cloud的配置中心模块cloudConfig Center
启动类
@EnableConfigServer
写一个 application-config.yml
访问 http://config-3344.com:3344/master/application-config.yml

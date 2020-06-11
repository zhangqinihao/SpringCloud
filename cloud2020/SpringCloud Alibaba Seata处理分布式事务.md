自己学习把
Seata简介
地址 http://seata.io/zh-cn/



发布说明:https://github.com/seata/seata/releases

怎么玩

Spring 本地@Transactional
全局@GlobalTransactional
3.seata-server-0.9.0.zip解压到指定目录并修改conf目录下的file.conf配置文件
先备份原始file.conf文件
主要修改：自定义事务组名称+事务日志存储模式为db+数据库连接信息
file.conf
service模块
vgroup_mapping.my_test_tx_group = "fsp_tx_group"


store模块

mode = "db"
 
  url = "jdbc:mysql://127.0.0.1:3306/seata"
  user = "root"
  password = "你自己的密码"


这里我们会创建三个服务，一个订单服务，一个库存服务，一个账户服务。
当用户下单时，会在订单服务中创建一个订单，然后通过远程调用库存服务来扣减下单商品的库存，再通过远程调用账户服务来扣减用户账户里面的余额，T最后在订单服务中修改订单状态为已完成。
该操作跨越三个数据库，有两次远程调用，很明显会有分布式事务问题。

seata_order: 存储订单的数据库
seata_storage:存储库存的数据库
seata_account: 存储账户信息的数据库

CREATE DATABASE seata_order;
 
 CREATE TABLE t_order(
     `id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
     `user_id` BIGINT(11) DEFAULT NULL COMMENT '用户id',
     `product_id` BIGINT(11) DEFAULT NULL COMMENT '产品id',
     `count` INT(11) DEFAULT NULL COMMENT '数量',
     `money` DECIMAL(11,0) DEFAULT NULL COMMENT '金额',
     `status` INT(1) DEFAULT NULL COMMENT '订单状态：0：创建中; 1：已完结'
 ) ENGINE=INNODB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
  
 SELECT * FROM t_order;
 
 
CREATE DATABASE seata_storage;

CREATE TABLE t_storage(
    `id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT(11) DEFAULT NULL COMMENT '产品id',
   `total` INT(11) DEFAULT NULL COMMENT '总库存',
    `used` INT(11) DEFAULT NULL COMMENT '已用库存',
    `residue` INT(11) DEFAULT NULL COMMENT '剩余库存'
) ENGINE=INNODB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
 
INSERT INTO seata_storage.t_storage(`id`,`product_id`,`total`,`used`,`residue`)
VALUES('1','1','100','0','100');
 
 
SELECT * FROM t_storage;


 
CREATE DATABASE seata_account;

INSERT INTO seata_account.t_account(`id`,`user_id`,`total`,`used`,`residue`) VALUES('1','1','1000','0','1000')

按照上述3库分别建对应的回滚日志表

CREATE TABLE `undo_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `branch_id` BIGINT(20) NOT NULL,
  `xid` VARCHAR(100) NOT NULL,
  `context` VARCHAR(128) NOT NULL,
  `rollback_info` LONGBLOB NOT NULL,
  `log_status` INT(11) NOT NULL,
  `log_created` DATETIME NOT NULL,
  `log_modified` DATETIME NOT NULL,
  `ext` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



新建订单Order-Module
1.seata-order-service2001



新建库存Storage-Module
1.seata-order-service2002



新建账户Account-Module
1.seata-order-service2003



正常下单
http://localhost:2001/order/create?userId=1&productId=1&count=10&money=100


seata-order-service2003 impl 取消sleep 


order模块 impl不加@GlobalTransactional  数据库重复添加数据
order模块 impl加@GlobalTransactional  数据库不重复添加数据 事务一直
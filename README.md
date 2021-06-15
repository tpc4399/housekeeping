# housekeeping
生产服务器相关信息：  
服务器供应商网址 ：https://myh.godaddy.com/  
网址登入账户 ：080smile@gmail.com  
网址登入密码：ss11300ss  
服务器ip ： 184.168.125.103  
服务器域名：5807733.xyz  
服务器ssl证书：有  
服务器ssh登入端口：22   
服务器ssh登入账户：root  
服务器ssh登入密码：Hkservice123$$  
宝塔：  
    Bt-Panel: http://184.168.125.103:8888  
    username: tyuwike7  
    password: 24a4627b  
  
ps.生产服务器账户属于客户，登入账户手机绑定是林柏志  
  
  
测试服务器相关信息：  
服务器供应商网址 ：https://account.aliyun.com/  
网址登入账户 ：eggcarton  
网址登入密码：EggCarton2013  
服务器ip ： 47.56.146.107  
服务器域名：无域名  
服务器ssl证书：无    
服务器ssh登入端口：22  
服务器ssh登入账户：root  
服务器ssh登入密码：EggCarton2013  
宝塔：  
    Bt-Panel: http://47.56.146.107:8888/  
    username: eggcarton  
    password: EggCarton2013  
      
ps.测试服务器属于公司，登入账户手机绑定是Eden  
  
//项目结构   
housekeeping     
|--admin            //主要的接口，接口大多数放在这里  
     |--interface    //admin的实体类、DTO等等信息，分离出来目的主要可供别的模块依赖  
     |--service      //admin的接口、服务、mapper等代码  
|--auth             //用户登录模块  
     |--interface    //auth的实体类、DTO等等信息，分离出来目的主要可供别的模块依赖  
     |--service      //auth的接口、服务、mapper等代码  
|--common           //公共模块，用于存放工具类，配置公共参数、注入公共Bean进容器  
|--ecpay            //三方支付、信用卡支付的三方工具库  
|--gateway          //网关  
|--im               //聊天模块  
|--interfaces       //公共变量模块，原本是给各个interface去继承的，但是由于原先服务器内存不够，所以放弃该模块的使用(可以删掉或者继续使用)  
|--order            //订单模块，由于原先服务器内存不够，所以放弃该模块的使用(可以删掉或者继续使用)  
|--register         //注册中心模块  

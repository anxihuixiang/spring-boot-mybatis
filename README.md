# Spring Boot MyBatis
### Spring Boot 整合常用框架组成完整可用的快速开发模板。
  
响应式管理界面改自zhengAdmin，基于Bootstrap、JQuery、Layui等，前后端是独立的，可分开部署和开发、自行更换前端框架。  
  
##### 首次启动前请修改mybatis-generator.xml和application.yml中的数据库信息并执行init-data.sql。  

## 集成的功能：
Spring Boot：Spring Boot和Spring基础框架，提供容器、定时任务、异步调用和其他常用功能支持。  
  
Spring Security：Spring安全框架，可满足复杂场景下的安全需求，已实现基于注解的基本权限体系。  
  
Spring MVC：Web访问接口与控制器，Restful，全局异常，自动转换时间，静态资源访问，动态国际化支持等。  
  
MyBatis：使用MyBatis框架访问数据库，已配置MyBatis的生成插件，MyBatis分页插件，并提供了模块案例。  
  
OkHttpUtils：OkHttp3具有很高的性能和简单易用的API，OkHttpUtils再次简化封装，一行代码即可实现Http请求。  
  
Spring Jdbc：Spring Jdbc事务及异常支持、JdbcTemplate作为特殊情况下的后备支持，确保无后顾之忧。  
  
GlobalIdWorker：全局ID生成器，支持多机多实例运行，趋势递增且尾数均匀，对分表分库非常友好，也可用于唯一命名。  
  
Spring Cache：基于注解的缓存，默认使用EhCache作为本地缓存，resources目录中提供了Redis参考。  
  
Protostuff 序列化：Protostuff 序列化可以大幅提高时间及空间性能，适合传输对象，比如存储到Redis等。  
  
Spring AOP：使用AOP对方法日志进行统一处理，也可用做收集信息、事务处理、权限校验等。  
  
RSA 和 AES：RSA 非对称可逆加密可用于登录加密等， AES 对称可逆加密可用于内部存储数据。  
  
Swagger2：扫描Controller及标记注解，生成接口文档，访问路径：/swagger-ui.html。  
  
Spring Test：Mock测试用例参考，测试驱动开发有助于编写简洁可用和高质量的代码，并加速开发过程。 
   
MySql数据库：默认使用MySql，驱动和配置参考 pom.xml 和 application.yml 中的数据库连接信息。  
  
其他支持：OkHttpUtils、RSA和AES加密、JWT、Spring Boot DevTools、Logback配置。  

### 文件目录与包的划分规范
尽量把同一个功能模块的文件放一起，可减少目录或包的交叉访问以提高开发效率、增加隔离性以降低耦合有利于水平扩展。公共资源可以放一起。

### Spring Security说明：
Spring Security中的Role和Authority是同一个概念，但hasRole默认带前缀，建议使用hasAuthority。  
  
项目中已实现基于注解的功能权限控制（hasAuthority）和资源许可授权控制（hasPermission，若不需要删除Permission相关代码即可）。  

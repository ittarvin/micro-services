
# 微服务架构体系
## 1.架构图及技术栈
![8cd8ee66713616bf7ca3c8c0c43f803c.png](https://static001.geekbang.org/infoq/45/454c2af0a3fe2896cac9ed1e5422a19e.png)


| 中间件 |
| --- | 
|  Nginx|  
|  Spring Cloud Gateway|  
|  NetFlix Eureka|  
|  携程 Apollo|  
|  Spring Boot|  
|  Alibaba Druid|  
|  Mysql|  
|  |  

## 2.微服务介绍
### 2.1.项目代码管理目录结构
![fdafa263b9a072c74b20af20a7bee4ca.png](https://static001.geekbang.org/infoq/1e/1e88e7fae6f3acf9cc7795e018a355ab.png)

> 项目采用Maven进行项目代码管理。

#### 2.1.1.代码结构介绍

- zjcw-gateway
>服务网关服务
- zjcw-${占位符}-micro-services
> 按模块划分的微服务
- zjcw-notifcation
>通知服务（短信，钉钉，推送等）
- zjcw-statistics
> 定时任务执行服务
- zjcw-oauth（待定）
> 微服务的安全体系
- zjcw-plugin
> 插件管理
- zjcw-utils
> 公共工具类


### 3.服务网关（Gateway）
>Gateway 作为更底层的微服务网关，通常是作为外部 Nginx 网关和内部微服务系统之间的桥梁，起了这么一个承上启下的作用。

#### 3.1.简介
>网关服务基于Spring Cloud Gateway 实现。实现功能包括 请求路由，服务负载，请求降级等。（[文档](https://spring.io/projects/spring-cloud-gateway)）


#### 3.1.2.扩展
- 实现基于Apollo的路由动态配置更新 。
- 集成车旺链路追踪（Zipkin）。

### 4.业务微服务（zjcw-${}-micro-services）
#### 4.1.微服务应用分层
![fcd596f82ed10b10823bc81e7efaa678.png](https://static001.geekbang.org/infoq/fd/fd83030e5ca3e06fd24107ca677f1bcd.png)
> **Controler** 业务请求控制层【不包括业务逻辑】
> **Service**  服务层【与 domain 共同实现业务逻辑，主要担任 domain 层的业务协调工作】
> **Domain** 层负责关键业务计算
> **Proxy** 服务代理与其他服务的交互（如：Fegin Client）
> **DAO** 数据库持久层（Mybatis）

#### 4.2.微服务应用测试

##### 4.2.1.测试覆盖原则

![33fb41908be4f64fce403143b763cf87.png](https://static001.geekbang.org/infoq/59/5951849cfbf0c9bb5b7eee3cb2445250.png)

- 单元测试（UT）
  ![c96b75dcd91d1b93c9ef52b51777a47f.png](https://static001.geekbang.org/infoq/54/54aef54d1df997fe4e836e6cb3c662e4.png)
> 使用技术：Junit，Spring Mock MVC，[Mpckito](https://site.mockito.org)
- 集成测试（IT）
  ![e53fe14281d99b804d995ca942696c27.png](https://static001.geekbang.org/infoq/38/382a814c429e5da5e6556b3b117e72af.png)
> 主要针对外部依赖进行测试。
- 组件测试（CT）
  ![96948323edc2d01eca16c4341e321cfc.png](https://static001.geekbang.org/infoq/a3/a37c49146d90aa2fc0b807b1c0c28011.png)
>  内部 Mock **AND** 外部 Mock
>  内部：Spring MockBean，外部工具 [WireMock](http://wiremock.org)
>  外部： [hoverfly](https://hoverfly.io)
>  将外部依赖进行 Mock
- 端到端（End-to-End Test）
> API 测试工具 [Rest-assured](http://rest-assured.io)
- 契约测试（不推荐）
> 国内应用较少

##### 4.2.2.测试收益

| 分类 |功能  |
| --- | --- |
| 单元测试 | 保证类，模块功能的正常 |
| 集成测试 | 确保组件之间接口，交互，链路的正常 |
| 组件测试 | 确保服务作为独立整体，接口的功能正常 |
| 端到端测试 | 确保整个引用满足用户的需求 |
| 契约测试（不推荐） | 确保服务提供方和消费放都遵守契约规范（成本较高） |


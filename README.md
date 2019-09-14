# Meeting-films 在线影院

<!-- GFM-TOC -->
* [Meeting-films 在线影院](#meeting-films-在线影院)
    * [系统架构图](#系统架构图)
    * [业务模块](#业务模块)
    * [其他模块](#其他模块)
    * [Dubbo特性的应用](#dubbo特性的应用)
        * [异步调用](#异步调用)
        * [结果缓存](#结果缓存)
        * [本地伪装](#本地伪装)
        * [隐式参数](#隐式参数)
    * [限流与熔断](#限流与熔断)
        * [服务限流](#服务限流)
        * [服务熔断](#服务熔断)
    * [分布式事务](#分布式事务)
        * [简介](#简介)
        * [TCC补偿性事务与Tcc-Transaction](#tcc补偿性事务与tcc-transaction)
        * [使用场景](#使用场景)
    * [FTP文件服务器](#ftp文件服务器)
<!-- GFM-TOC -->


## 系统架构图

![系统架构图.jpg](https://i.loli.net/2019/09/08/ZU3VwGKxFntyTN4.png)

## 业务模块

-  **用户模块** 

    提供用户登录验证、注册、个人信息查询修改等功能

-  **影片模块** 

    提供首页影片的展示数据（正在热映，即将上映，首页轮播图，票房排行榜），影片的条件查询（根据影片类型、片源、年代），影片信息的查询（影片简介，影片图集，导演与演员信息）

-  **影院模块** 

    提供影院的条件查询（根据品牌，区域，影厅类型）、影院场次的详情信息和在线选座服务

-  **订单模块** 

    提供创建选座订单和查询订单的服务

-  **支付模块** 

    与支付宝沙箱版对接，实现支付功能和订单支付结果查询

## 其他模块

-  **API网关模块** 

    主要作用是服务聚合、身份验证和结果缓存。他是业务系统的唯一入口，在RPC调用中扮演消费者的角色。实际开发过程中，所有ServiceImpl都在业务模块，API网关根据发过来的请求调用相应的ServiceAPI，封装成VO后返回。API网关的引入，让系统架构更为清晰，也更容易做安全控制。

-  **Uid生成模块** 

    在常规的分布式系统中，订单模块不可能只部署在一台机器上，而订单号的生成要保证全局唯一性、有序递增、带时间、高可用等多种特性，目前来讲主流的方案有以下几种：

    - 数据库自动生成

        - 优点：保证唯一递增，实现简单
        - 缺点：性能较低；在读写分离的主从架构中，主库的写速度决定Uid的生成速度

    - 时间戳+UUID 

        - 优点：保证唯一递增（单纯的时间戳在并发量>1000时会重复）
        - 缺点：长度过长，且以字符串形式存储，占用空间大

    - 第三方（Redis）集中生成

        - 优点：Redis的单线程特性和incr指令很适合此场景
        - 缺点：增加系统复杂度

    - Snowflake算法

        由Twitter开源的Snowflake算法，核心思想是生成一个64位长long类型的ID，其中41bit毫秒时间戳，10bit机器ID，12bit序列号，理论上最多支持1024台机器每秒生成4096*1000个序列号

        ![snowflake.png](https://i.loli.net/2019/09/09/kQGmqwKRp4aWc3s.png)

        

    本项目使用百度开源的[UidGenerator](<https://github.com/baidu/uid-generator>)，是基于Snowflake算法的一种实现

## Dubbo特性的应用

###  异步调用

异步调用是基于 NIO 非阻塞实现的并行调用，客户端不需要启动多线程即可完成并行调用多个远程服务。

![dubbo异步调用](http://dubbo.apache.org/docs/zh-cn/user/sources/images/future.jpg)

针对影片模块的获取首页信息接口、订单模块的创建订单接口等对RT要求比较高的场景，使用异步调用进行优化。以首页信息接口为例: 

```java
@RequestMapping(value = "getIndex", method = RequestMethod.GET)
public ResponseVO getIndex() {
    long start = System.currentTimeMillis();
    FilmIndexVo indexVo = new FilmIndexVo();
    //获取banner信息  
    indexVo.setBanners(filmServiceAPI.getBanners());
    //获取正在热映的电影    
    indexVo.setHotFilms(filmServiceAPI.getHotFilms(true, 10));
    //即将上映的电影    
    indexVo.setSoonFilms(filmServiceAPI.getSoonFilms(true, 10));
    //票房排行榜    
    indexVo.setBoxRanking(filmServiceAPI.getBoxRanking());
    //获我受欢迎的榜单    
    indexVo.setExpectRanking(filmServiceAPI.getExpectRanking());
    //获取Top100的前10   
    indexVo.setTop100(filmServiceAPI.getTop());
    log.info("同步调用获取首页数据耗时：{}ms", System.currentTimeMillis()-start);
    return ResponseVO.success(indexVo, IMG_PRE);
}
```

每调用一次filmServiceAPI的方法，当前线程必须等待返回结果才能继续执行；

而使用异步调用，当前方法立即返回结果(null)。随后执行Future.get()后线程才陷入阻塞，直到对应方法执行完成并取得返回值。改造后的代码如下：

```java
@RequestMapping(value = "getIndex", method = RequestMethod.GET)
public ResponseVO getIndex() {
    long start = System.currentTimeMillis();
    //获取banner信息    
    Future<List<BannerVo>> bannerListFuture = RpcContext.getContext().asyncCall(()->filmServiceAsynAPI.getBanners());    
    //获取正在热映的电影    
    Future<FilmVo> hotFilmsFuture = RpcContext.getContext().asyncCall(()->filmServiceAsynAPI.getHotFilms(true, 10));    
    //即将上映的电影    
    Future<FilmVo> soonFilmFuture = RpcContext.getContext().asyncCall(() -> filmServiceAsynAPI.getSoonFilms(true, 10));    
    //票房排行榜    
    Future<List<FilmInfoVo>> boxRankingFuture = RpcContext.getContext().asyncCall(() -> filmServiceAsynAPI.getBoxRanking());    
    //获取受欢迎的榜单    
    Future<List<FilmInfoVo>> expectRankingFuture = RpcContext.getContext().asyncCall(() -> filmServiceAsynAPI.getExpectRanking());    
    //获取Top100的前10    
    Future<List<FilmInfoVo>> topFuture = RpcContext.getContext().asyncCall(() -> filmServiceAsynAPI.getTop());    
    
    FilmIndexVo indexVo = new FilmIndexVo();   
    try {
        indexVo.setBanners(bannerListFuture.get());    
        indexVo.setHotFilms(hotFilmsFuture.get());        
        indexVo.setSoonFilms(soonFilmFuture.get());        
        indexVo.setBoxRanking(boxRankingFuture.get());        
        indexVo.setExpectRanking(expectRankingFuture.get());        
        indexVo.setTop100(topFuture.get());    
    } catch (InterruptedException | ExecutionException e) {
        log.error("FutureTask获取失败：{}", e.getMessage());        
        e.printStackTrace();    
    }    
    log.info("异步调用获取首页数据耗时：{}ms", System.currentTimeMillis()-start);
    return ResponseVO.success(indexVo, IMG_PRE);
}
```

使用异步调用前后，在API网关层的分别进行5次调用测试，调用时间对比如下所示：

> 2019-09-09 16:12:24.439  INFO 19868 --- [p-nio-80-exec-2] c.s.g.rest.modular.film.FilmController   : 同步调用获取首页数据耗时：712ms
> 2019-09-09 16:12:30.616  INFO 19868 --- [p-nio-80-exec-3] c.s.g.rest.modular.film.FilmController   : 同步调用获取首页数据耗时：570ms
> 2019-09-09 16:12:33.867  INFO 19868 --- [p-nio-80-exec-4] c.s.g.rest.modular.film.FilmController   : 同步调用获取首页数据耗时：569ms
> 2019-09-09 16:12:37.160  INFO 19868 --- [p-nio-80-exec-5] c.s.g.rest.modular.film.FilmController   : 同步调用获取首页数据耗时：509ms
> 2019-09-09 16:12:41.727  INFO 19868 --- [p-nio-80-exec-6] c.s.g.rest.modular.film.FilmController   : 同步调用获取首页数据耗时：579ms

平均耗时： **587.8ms** 

> 2019-09-09 15:54:53.354  INFO 18120 --- [p-nio-80-exec-1] c.s.g.rest.modular.film.FilmController   : 异步调用获取首页数据耗时：255ms
> 2019-09-09 15:55:00.518  INFO 18120 --- [p-nio-80-exec-3] c.s.g.rest.modular.film.FilmController   : 异步调用获取首页数据耗时：169ms
> 2019-09-09 15:55:03.324  INFO 18120 --- [p-nio-80-exec-4] c.s.g.rest.modular.film.FilmController   : 异步调用获取首页数据耗时：128ms
> 2019-09-09 15:55:09.425  INFO 18120 --- [p-nio-80-exec-5] c.s.g.rest.modular.film.FilmController   : 异步调用获取首页数据耗时：144ms
> 2019-09-09 15:55:13.357  INFO 18120 --- [p-nio-80-exec-6] c.s.g.rest.modular.film.FilmController   : 异步调用获取首页数据耗时：138ms

平均耗时： **166.8ms** 



### 结果缓存

首页的数据，往往是一个网站的热点数据，针对首页信息接口引入缓存，将大大优化RT和QPS。

项目中备选方案有 **Redis** 和**Dubbo结果缓存**，两者区别在于Redis可以储存多种数据类型，可搭建集群横向拓展，而Dubbo的缓存只是本地缓存。两者实现原理都是先访问缓存，查询不到结果再访问数据库，然后将查询结果存入缓存，效果相差不大，因此项目中使用Dubbo本地缓存。

Dubbo缓存有3中：`LRUCache`、`JCache`、`ThreadLocalCache`，其中LRUCache就当操作系统中的LRU就行了，最近最少使用的内容会从缓存中清除；而ThreadLocalCache适用于涉及用户信息相关的结果缓存；JCache不了解。

本项目中采用LRU进行结果缓存。

**疑问** ：Dubbo的LruCache似乎并没有提供过期时间的设置，本想定时更新一次缓存的没法做到...



### 本地伪装

本地伪装通常用于服务降级，比如某验权服务，当服务提供方全部挂掉后，客户端不抛出异常，而是通过 Mock 数据返回授权失败。注意，他只会对`RpcException`和其子类进行服务降级，在功能上是 **本地存根的一个子集** 。

有时候，当出现网络延迟导致服务间通信超时，在服务消费方会收到RpcException。以本项目的获取支付结果为例，如果采用本地存根，代码实现如下：

```java
public class AlipayServiceStub implements AlipayServiceAPI {
    private final AlipayServiceAPI alipayServiceAPI;

    public AlipayServiceStub(AlipayServiceAPI alipayServiceAPI){
        this.alipayServiceAPI = alipayServiceAPI;
    }

    @Override
    public PayResultVO getPayResult(String orderId) {
        try{
            return alipayServiceAPI.getPayResult(orderId);
        }catch (RpcException e){
            PayResultVO payResultVO = new PayResultVO();
            payResultVO.setOrderId(orderId);
            payResultVO.setOrderMsg("查询支付结果失败(Mock)！");
            return payResultVO;
        }
    }
}
```

采用本地伪装，可以减少冗余代码：

```java
public class AlipayServiceMock implements AlipayServiceAPI {
    @Override
    public PayResultVO getPayResult(String orderId) {
        PayResultVO payResultVO = new PayResultVO();
        payResultVO.setOrderId(orderId);
        payResultVO.setOrderMsg("查询支付结果失败(Mock)！");
        return payResultVO;
    }
}
```



### 隐式参数

服务调用者向消费者传递的参数，不仅仅局限于API中的方法参数，还可以通过隐式参数传递一些”次要的“信息。

比如支付模块的查询支付结果功能，API中只有OrderId一个参数，若订单号的排列规则被知晓，用户可以爬取所有的订单的交易结果，这显然是无法接受的。因此需要引入当前用户的UserId这一参数，在消费者（API网关）调用前，将UserId放入RpcContext，提供者（AlipayService）在查询订单时取出并作为SQL的查询条件，从而避免上述情况的发生。

```java
RpcContext.getContext().setAttachment("userId", userId);	//API网关
String userId = RpcContext.getContext().getAttachment("userId");	//支付模块
```

## 限流与熔断

当订单模块大量请求的涌入时（比如某影片首映），往往会给调用链上的每一个服务造成不小的压力。若下游的某一个服务突然 **变得不可用** 或**响应过慢**，上游服务的调用将发生阻塞，在一段时间未得到返回结果后同样抛出异常。于是不可用不断传递，造成整条链路和相关消费者的不可用，称之**服务雪崩**。

为了预防以上局面的发生，可以采用 **服务降级** 的手段，对一些服务采取有策略的放弃，从而缓解系统压力，保证核心服务可用。比如双十一期间，一个电商系统可以暂时关闭广告服务以减轻秒杀系统的压力。

此外可采取 **服务限流** 和**服务熔断**的措施，下文将详细介绍（附上调用参考图）：

![订单调用链.jpg](https://i.loli.net/2019/09/10/Csg8ye67dcFqGJW.png)

### 服务限流

Dubbo自带有 **并发控制** 与**连接控制**（服务端可配置`executes` / `accepts`，客户端可配置`actives` / `connections`），可限制流量，但是这种方式不够灵活。通常情况下会选用限流算法来限制流量，常见的有**漏桶法**和**令牌桶法**。

漏桶法实现的原理是将所有请求放入一个队列，以固定频率处理这些请求。如果队列满了，直接拒绝后续请求。缺点显而易见，就是无法应对突发流量。

令牌桶法则是以一定的速率产生令牌，每个请求需获取令牌后才能进入业务系统。当令牌桶内数量为0时，拒绝后续请求。与漏桶法相反，它正好适合应对流量突发。

![tokenBucket.png](https://i.loli.net/2019/09/09/TPdhqLmbOryiFHA.png)

项目里使用的是个人实现的简易版令牌桶

```java
// 令牌桶法限流工具
public class TokenBucket {
    private int capacity = 100; //桶大小
    private int rate = 1;       //桶令牌增添速率（每毫秒）
    private int nowTokens;  	//当前令牌数
    private long timestamp;     //时间戳
    public TokenBucket() {
        nowTokens = this.capacity;        
        timestamp = getNowTime();    
    }
    private long getNowTime() {        
        return System.currentTimeMillis();    
    }
    public synchronized boolean getToken() {        
        long addTokens = (getNowTime() - timestamp) * rate;        
        nowTokens += addTokens;        
        timestamp = getNowTime();        
        nowTokens = Math.min(capacity, nowTokens);        
        if (nowTokens > 0) {            
            nowTokens--;            
            return true;        
        } else {           
            return false;        
        }   
    }
}
```

### 服务熔断

服务熔断，指的是当某一服务出现响应超时或不可用的情况时，暂停对该服务的调用，以防服务雪崩。在Dubbo中，可以通过本地存根的方式实现，但需要对API包进行修改，且需要手动实现相关逻辑（比如：何时启动熔断，线程池配置等）。而SpringCloud的组件Hystrix，大大简化了服务熔断的实现。仅需注解的方式配置相关参数即可，入侵性极小。

本项目针对用户大规模下单的场景，对Order模块的createOrder方法进行熔断配置，相关代码如下：

```java
public ResponseVO buyTicketsError(int fieldId, String soldSeats, String seatsName) {  
    return ResponseVO.serviceFail("抱歉，下单的人太多，请稍候重试！");
}

@HystrixCommand(
    fallbackMethod = "buyTicketsError", 
    commandProperties = {     
        @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),     
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),     
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
}, threadPoolProperties = {        
        @HystrixProperty(name = "coreSize", value = "5"),        
        @HystrixProperty(name = "maxQueueSize", value = "10"),        
        @HystrixProperty(name = "keepAliveTimeMinutes", value = "1"),        
        @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),        
        @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),        
        @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "6000")})
@RequestMapping(value = "/buyTickets", method = RequestMethod.POST)
public ResponseVO buyTickets(int fieldId, String soldSeats,String seatsName){
    ...
}
```

各参数含义如下：

**fallbackMethod** : 方法执行时熔断、错误、超时时会调用的回退方法，需要保持此方法与 Hystrix 方法的参数和返回值一致。

**commandProperties** : 命令属性, 由 `@HystrixProperty` 注解数组构成的。

- execution.isolation.strategy：请求的隔离策略，默认Thread，代表并发的请求在单独的线程上运行；
- execution.isolation.thread.timeoutInMilliseconds：设置调用者执行的超时时间，默认1000，这里由于网络原因放宽到4000
- circuitBreaker.requestVolumeThreshold：断路器启动的阈值（窗口时间内达到多少请求才开启断路器功能）
- circuitBreaker.errorThresholdPercentage：窗口时间内请求失败百分比到达该值时，将启动断路器。默认50

**threadPoolProperties** ：线程池属性，与commandProperties用法相同。
Hystrix的线程池是基于java内置ThreadPool的简单包装，参数可对应着来配。

- coreSize: 核心线程池的大小。默认10，下订单很显然是IO密集型任务，线程池大小可配置成2N+1（N为CPU核心数）
- maxQueueSize： 阻塞队列大小，当核心线程池内的线程处于忙碌状态时，会把新加入的任务放到阻塞队列中，待核心线程空闲时再执行。超出队列大小的任务将被拒绝。默认10
- keepAliveTimeMinutes： 线程池中超出核心线程池大小的线程存活多久后被回收，单位分钟。默认2
- queueSizeRejectionThreshold： 阻塞队列的大小超过该值时，将拒绝新任务的加入，可作为maxQueueSize的“动态配置”。默认5，设置maxQueueSize可得把这个参数也带上呀！
- metrics.rollingStats.timeInMilliseconds： 统计响应时间百分比的滑动窗口，单位毫秒，默认60000，即1分钟
- metrics.rollingStats.numBuckets： 响应时间统计窗口所对应的桶数量，默认6（滑动窗口每滑过一个桶的时间间隔，就统计一次）

## 分布式事务

### 简介
分布式事务就是将多个节点的事务看成一个整体处理，一般由事务参与者、资源服务器、事务管理器等组成。事务参与者可以理解为各个参与分布式事务的分布式节点，资源服务器用于控制事务中涉及的资源，事务管理器起协调和监控所有事务的作用。
常见的分布式事务解决方案有两段式/三段式事务（2PC/3PC）、基于消息的最终一致性方案、TCC编程式补偿性事务。相较于基于消息的最终一致性方案，TCC柔性补偿事务效率较高，开启后每一个节点上的事务可以并发执行，不存在彼此等待的情况，适用于资源“可控性强”、时效性要求较高的场景。
> 注：修改订单状态 VS 资金转出，订单状态和资金都是资源，前者有撤回的余地，可控性强；而后者撤回较为困难，可控性差。

### TCC补偿性事务与Tcc-Transaction

TCC分别对应Try、Confirm和Cancel三种操作（三个阶段），含义如下：

- Try：预留业务资源
- Confirm：确认执行业务操作
- Cancel：取消执行业务操作

Try、Confirm、Cancel的相应方法都需要人工实现，增加了开发的工作量，且需要注意Confirm与Cancel操作的幂等性

本项目使用的TCC框架为tcc-transaction：https://github.com/changmingxie/tcc-transaction

### 使用场景

结合本项目，我们可以在订单模块应用TCC分布式事务来保证创建订单的原子性，使用场景如下：
- 调用方：Gateway模块的buyTicket方法
- 被调用方：Order模块的isTrueSeat、isNotSold、createOrder方法
其中isTrueSeat、isNotSold用于验证当前场次的座位是否可用，在前端控制不出错的前提下均会返回true，createOrder才执行真正创建订单的业务。为了缩短响应时间，3个方法采用异步调用，这时如果createOrder先于isTrueSeat执行，且isTrueSeat返回false，会造成订单中出现虚假座位的情况。因此，有必要将本次调用作为一个全局事务进行执行，一旦其中之一执行“失败”，所有参与者将进行cancel操作。

> 注：为了配合TCC框架，在service实现类中一旦isTrueSeat或isNotSold返回false，则抛出一个自定义异常，触发事务管理器执行cance操作。

**预留资源** ：try阶段执行createOrder的操作，并设置订单状态为draft。confirm操作会修改为order_success，cancel操作则修改为order_fail。

**幂等性** ：createOrder的confirm/cancel方法都加入幂等性控制，在执行前会查询订单是否存在且状态是否为draft，满足以上条件才接着执行。

相关代码如下：
```java
// 调用方buyTickets
@Compensable(confirmMethod = "buyTicketsConfirm", cancelMethod = "buyTicketsCancel", transactionContextEditor = DubboTransactionContextEditor.class)
public ResponseVO buyTickets(int fieldId, String soldSeats,String seatsName){
    ...
	orderServiceAsyncAPI.isTrueSeats(fieldId, seatsInt);
	orderServiceAsyncAPI.isNotSold(fieldId, seatsInt);
	orderServiceAsyncAPI.createOrder(uuid, fieldId, seatsInt, userId, seatsName);
	...
}

public ResponseVO buyTicketsConfirm(int fieldId, String soldSeats, String seatsName) {
	log.info("整个下订单事务提交");
	return null;
}
public ResponseVO buyTicketsCancel(int fieldId, String soldSeats, String seatsName) {
	log.info("整个下订单事务取消");
	return null;
}
```

```java
// 被调用方（仅createOrder为例）
@Compensable(confirmMethod = "orderConfirm", cancelMethod = "orderCancel", transactionContextEditor = DubboTransactionContextEditor.class)
public OrderInfoVO createOrder(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
	// 创建订单业务，设置订单状态draft
}

public OrderInfoVO orderConfirm(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
	OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
	if(orderInfoVO!=null && DRAFT.equals(orderInfoVO.getOrderStatus())) {
		// 更新订单状态为 order_success
		...
		
		log.info("创建订单确认");
	}
	return null;
}

public OrderInfoVO orderCancel(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
	OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
	if(orderInfoVO!=null && DRAFT.equals(orderInfoVO.getOrderStatus())) {
		// 更新订单状态 order_fail
		...
		
		log.info("创建订单取消");
	}
	return null;
}
```


## FTP文件服务器
顾名思义，是以FTP为通信协议，用于文件的传输与管理的应用服务器。
在本项目中有两处使用到FTP服务器，一处是根据影厅类型，在数据库中的对应的位置分布图的文件路径，通过该路径从FTP服务器上获取文件；另一处是支付模块生成的二维码图片，需要保存在FTP服务器中。
在CentOS环境下，我选用vsftpd作为FTP服务器应用，注意配置文件中要加上如下内容并在云服务器上设置对应端口的安全组：
```shell
pasv_enable=YES      #被动模式
pasv_min_port=50000  #开放的端口
pasv_max_port=50009
```


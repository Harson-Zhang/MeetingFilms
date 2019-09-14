# Meeting-films ����ӰԺ

<!-- GFM-TOC -->
* [Meeting-films ����ӰԺ](#meeting-films-����ӰԺ)
    * [ϵͳ�ܹ�ͼ](#ϵͳ�ܹ�ͼ)
    * [ҵ��ģ��](#ҵ��ģ��)
    * [����ģ��](#����ģ��)
    * [Dubbo���Ե�Ӧ��](#dubbo���Ե�Ӧ��)
        * [�첽����](#�첽����)
        * [�������](#�������)
        * [����αװ](#����αװ)
        * [��ʽ����](#��ʽ����)
    * [�������۶�](#�������۶�)
        * [��������](#��������)
        * [�����۶�](#�����۶�)
    * [�ֲ�ʽ����](#�ֲ�ʽ����)
        * [���](#���)
        * [TCC������������Tcc-Transaction](#tcc������������tcc-transaction)
        * [ʹ�ó���](#ʹ�ó���)
    * [FTP�ļ�������](#ftp�ļ�������)
<!-- GFM-TOC -->


## ϵͳ�ܹ�ͼ

![ϵͳ�ܹ�ͼ.jpg](https://i.loli.net/2019/09/08/ZU3VwGKxFntyTN4.png)

## ҵ��ģ��

-  **�û�ģ��** 

    �ṩ�û���¼��֤��ע�ᡢ������Ϣ��ѯ�޸ĵȹ���

-  **ӰƬģ��** 

    �ṩ��ҳӰƬ��չʾ���ݣ�������ӳ��������ӳ����ҳ�ֲ�ͼ��Ʊ�����а񣩣�ӰƬ��������ѯ������ӰƬ���͡�ƬԴ���������ӰƬ��Ϣ�Ĳ�ѯ��ӰƬ��飬ӰƬͼ������������Ա��Ϣ��

-  **ӰԺģ��** 

    �ṩӰԺ��������ѯ������Ʒ�ƣ�����Ӱ�����ͣ���ӰԺ���ε�������Ϣ������ѡ������

-  **����ģ��** 

    �ṩ����ѡ�������Ͳ�ѯ�����ķ���

-  **֧��ģ��** 

    ��֧����ɳ���Խӣ�ʵ��֧�����ܺͶ���֧�������ѯ

## ����ģ��

-  **API����ģ��** 

    ��Ҫ�����Ƿ���ۺϡ������֤�ͽ�����档����ҵ��ϵͳ��Ψһ��ڣ���RPC�����а��������ߵĽ�ɫ��ʵ�ʿ��������У�����ServiceImpl����ҵ��ģ�飬API���ظ��ݷ����������������Ӧ��ServiceAPI����װ��VO�󷵻ء�API���ص����룬��ϵͳ�ܹ���Ϊ������Ҳ����������ȫ���ơ�

-  **Uid����ģ��** 

    �ڳ���ķֲ�ʽϵͳ�У�����ģ�鲻����ֻ������һ̨�����ϣ��������ŵ�����Ҫ��֤ȫ��Ψһ�ԡ������������ʱ�䡢�߿��õȶ������ԣ�Ŀǰ���������ķ��������¼��֣�

    - ���ݿ��Զ�����

        - �ŵ㣺��֤Ψһ������ʵ�ּ�
        - ȱ�㣺���ܽϵͣ��ڶ�д��������Ӽܹ��У������д�ٶȾ���Uid�������ٶ�

    - ʱ���+UUID 

        - �ŵ㣺��֤Ψһ������������ʱ����ڲ�����>1000ʱ���ظ���
        - ȱ�㣺���ȹ����������ַ�����ʽ�洢��ռ�ÿռ��

    - ��������Redis����������

        - �ŵ㣺Redis�ĵ��߳����Ժ�incrָ����ʺϴ˳���
        - ȱ�㣺����ϵͳ���Ӷ�

    - Snowflake�㷨

        ��Twitter��Դ��Snowflake�㷨������˼��������һ��64λ��long���͵�ID������41bit����ʱ�����10bit����ID��12bit���кţ����������֧��1024̨����ÿ������4096*1000�����к�

        ![snowflake.png](https://i.loli.net/2019/09/09/kQGmqwKRp4aWc3s.png)

        

    ����Ŀʹ�ðٶȿ�Դ��[UidGenerator](<https://github.com/baidu/uid-generator>)���ǻ���Snowflake�㷨��һ��ʵ��

## Dubbo���Ե�Ӧ��

###  �첽����

�첽�����ǻ��� NIO ������ʵ�ֵĲ��е��ã��ͻ��˲���Ҫ�������̼߳�����ɲ��е��ö��Զ�̷���

![dubbo�첽����](http://dubbo.apache.org/docs/zh-cn/user/sources/images/future.jpg)

���ӰƬģ��Ļ�ȡ��ҳ��Ϣ�ӿڡ�����ģ��Ĵ��������ӿڵȶ�RTҪ��Ƚϸߵĳ�����ʹ���첽���ý����Ż�������ҳ��Ϣ�ӿ�Ϊ��: 

```java
@RequestMapping(value = "getIndex", method = RequestMethod.GET)
public ResponseVO getIndex() {
    long start = System.currentTimeMillis();
    FilmIndexVo indexVo = new FilmIndexVo();
    //��ȡbanner��Ϣ  
    indexVo.setBanners(filmServiceAPI.getBanners());
    //��ȡ������ӳ�ĵ�Ӱ    
    indexVo.setHotFilms(filmServiceAPI.getHotFilms(true, 10));
    //������ӳ�ĵ�Ӱ    
    indexVo.setSoonFilms(filmServiceAPI.getSoonFilms(true, 10));
    //Ʊ�����а�    
    indexVo.setBoxRanking(filmServiceAPI.getBoxRanking());
    //�����ܻ�ӭ�İ�    
    indexVo.setExpectRanking(filmServiceAPI.getExpectRanking());
    //��ȡTop100��ǰ10   
    indexVo.setTop100(filmServiceAPI.getTop());
    log.info("ͬ�����û�ȡ��ҳ���ݺ�ʱ��{}ms", System.currentTimeMillis()-start);
    return ResponseVO.success(indexVo, IMG_PRE);
}
```

ÿ����һ��filmServiceAPI�ķ�������ǰ�̱߳���ȴ����ؽ�����ܼ���ִ�У�

��ʹ���첽���ã���ǰ�����������ؽ��(null)�����ִ��Future.get()���̲߳�����������ֱ����Ӧ����ִ����ɲ�ȡ�÷���ֵ�������Ĵ������£�

```java
@RequestMapping(value = "getIndex", method = RequestMethod.GET)
public ResponseVO getIndex() {
    long start = System.currentTimeMillis();
    //��ȡbanner��Ϣ    
    Future<List<BannerVo>> bannerListFuture = RpcContext.getContext().asyncCall(()->filmServiceAsynAPI.getBanners());    
    //��ȡ������ӳ�ĵ�Ӱ    
    Future<FilmVo> hotFilmsFuture = RpcContext.getContext().asyncCall(()->filmServiceAsynAPI.getHotFilms(true, 10));    
    //������ӳ�ĵ�Ӱ    
    Future<FilmVo> soonFilmFuture = RpcContext.getContext().asyncCall(() -> filmServiceAsynAPI.getSoonFilms(true, 10));    
    //Ʊ�����а�    
    Future<List<FilmInfoVo>> boxRankingFuture = RpcContext.getContext().asyncCall(() -> filmServiceAsynAPI.getBoxRanking());    
    //��ȡ�ܻ�ӭ�İ�    
    Future<List<FilmInfoVo>> expectRankingFuture = RpcContext.getContext().asyncCall(() -> filmServiceAsynAPI.getExpectRanking());    
    //��ȡTop100��ǰ10    
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
        log.error("FutureTask��ȡʧ�ܣ�{}", e.getMessage());        
        e.printStackTrace();    
    }    
    log.info("�첽���û�ȡ��ҳ���ݺ�ʱ��{}ms", System.currentTimeMillis()-start);
    return ResponseVO.success(indexVo, IMG_PRE);
}
```

ʹ���첽����ǰ����API���ز�ķֱ����5�ε��ò��ԣ�����ʱ��Ա�������ʾ��

> 2019-09-09 16:12:24.439  INFO 19868 --- [p-nio-80-exec-2] c.s.g.rest.modular.film.FilmController   : ͬ�����û�ȡ��ҳ���ݺ�ʱ��712ms
> 2019-09-09 16:12:30.616  INFO 19868 --- [p-nio-80-exec-3] c.s.g.rest.modular.film.FilmController   : ͬ�����û�ȡ��ҳ���ݺ�ʱ��570ms
> 2019-09-09 16:12:33.867  INFO 19868 --- [p-nio-80-exec-4] c.s.g.rest.modular.film.FilmController   : ͬ�����û�ȡ��ҳ���ݺ�ʱ��569ms
> 2019-09-09 16:12:37.160  INFO 19868 --- [p-nio-80-exec-5] c.s.g.rest.modular.film.FilmController   : ͬ�����û�ȡ��ҳ���ݺ�ʱ��509ms
> 2019-09-09 16:12:41.727  INFO 19868 --- [p-nio-80-exec-6] c.s.g.rest.modular.film.FilmController   : ͬ�����û�ȡ��ҳ���ݺ�ʱ��579ms

ƽ����ʱ�� **587.8ms** 

> 2019-09-09 15:54:53.354  INFO 18120 --- [p-nio-80-exec-1] c.s.g.rest.modular.film.FilmController   : �첽���û�ȡ��ҳ���ݺ�ʱ��255ms
> 2019-09-09 15:55:00.518  INFO 18120 --- [p-nio-80-exec-3] c.s.g.rest.modular.film.FilmController   : �첽���û�ȡ��ҳ���ݺ�ʱ��169ms
> 2019-09-09 15:55:03.324  INFO 18120 --- [p-nio-80-exec-4] c.s.g.rest.modular.film.FilmController   : �첽���û�ȡ��ҳ���ݺ�ʱ��128ms
> 2019-09-09 15:55:09.425  INFO 18120 --- [p-nio-80-exec-5] c.s.g.rest.modular.film.FilmController   : �첽���û�ȡ��ҳ���ݺ�ʱ��144ms
> 2019-09-09 15:55:13.357  INFO 18120 --- [p-nio-80-exec-6] c.s.g.rest.modular.film.FilmController   : �첽���û�ȡ��ҳ���ݺ�ʱ��138ms

ƽ����ʱ�� **166.8ms** 



### �������

��ҳ�����ݣ�������һ����վ���ȵ����ݣ������ҳ��Ϣ�ӿ����뻺�棬������Ż�RT��QPS��

��Ŀ�б�ѡ������ **Redis** ��**Dubbo�������**��������������Redis���Դ�������������ͣ��ɴ��Ⱥ������չ����Dubbo�Ļ���ֻ�Ǳ��ػ��档����ʵ��ԭ�����ȷ��ʻ��棬��ѯ��������ٷ������ݿ⣬Ȼ�󽫲�ѯ������뻺�棬Ч�����������Ŀ��ʹ��Dubbo���ػ��档

Dubbo������3�У�`LRUCache`��`JCache`��`ThreadLocalCache`������LRUCache�͵�����ϵͳ�е�LRU�����ˣ��������ʹ�õ����ݻ�ӻ������������ThreadLocalCache�������漰�û���Ϣ��صĽ�����棻JCache���˽⡣

����Ŀ�в���LRU���н�����档

**����** ��Dubbo��LruCache�ƺ���û���ṩ����ʱ������ã����붨ʱ����һ�λ����û������...



### ����αװ

����αװͨ�����ڷ��񽵼�������ĳ��Ȩ���񣬵������ṩ��ȫ���ҵ��󣬿ͻ��˲��׳��쳣������ͨ�� Mock ���ݷ�����Ȩʧ�ܡ�ע�⣬��ֻ���`RpcException`����������з��񽵼����ڹ������� **���ش����һ���Ӽ�** ��

��ʱ�򣬵����������ӳٵ��·����ͨ�ų�ʱ���ڷ������ѷ����յ�RpcException���Ա���Ŀ�Ļ�ȡ֧�����Ϊ����������ñ��ش��������ʵ�����£�

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
            payResultVO.setOrderMsg("��ѯ֧�����ʧ��(Mock)��");
            return payResultVO;
        }
    }
}
```

���ñ���αװ�����Լ���������룺

```java
public class AlipayServiceMock implements AlipayServiceAPI {
    @Override
    public PayResultVO getPayResult(String orderId) {
        PayResultVO payResultVO = new PayResultVO();
        payResultVO.setOrderId(orderId);
        payResultVO.setOrderMsg("��ѯ֧�����ʧ��(Mock)��");
        return payResultVO;
    }
}
```



### ��ʽ����

����������������ߴ��ݵĲ�����������������API�еķ���������������ͨ����ʽ��������һЩ����Ҫ�ġ���Ϣ��

����֧��ģ��Ĳ�ѯ֧��������ܣ�API��ֻ��OrderIdһ���������������ŵ����й���֪�����û�������ȡ���еĶ����Ľ��׽��������Ȼ���޷����ܵġ������Ҫ���뵱ǰ�û���UserId��һ�������������ߣ�API���أ�����ǰ����UserId����RpcContext���ṩ�ߣ�AlipayService���ڲ�ѯ����ʱȡ������ΪSQL�Ĳ�ѯ�������Ӷ�������������ķ�����

```java
RpcContext.getContext().setAttachment("userId", userId);	//API����
String userId = RpcContext.getContext().getAttachment("userId");	//֧��ģ��
```

## �������۶�

������ģ����������ӿ��ʱ������ĳӰƬ��ӳ������������������ϵ�ÿһ��������ɲ�С��ѹ���������ε�ĳһ������ͻȻ **��ò�����** ��**��Ӧ����**�����η���ĵ��ý�������������һ��ʱ��δ�õ����ؽ����ͬ���׳��쳣�����ǲ����ò��ϴ��ݣ����������·����������ߵĲ����ã���֮**����ѩ��**��

Ϊ��Ԥ�����Ͼ���ķ��������Բ��� **���񽵼�** ���ֶΣ���һЩ�����ȡ�в��Եķ������Ӷ�����ϵͳѹ������֤���ķ�����á�����˫ʮһ�ڼ䣬һ������ϵͳ������ʱ�رչ������Լ�����ɱϵͳ��ѹ����

����ɲ�ȡ **��������** ��**�����۶�**�Ĵ�ʩ�����Ľ���ϸ���ܣ����ϵ��òο�ͼ����

![����������.jpg](https://i.loli.net/2019/09/10/Csg8ye67dcFqGJW.png)

### ��������

Dubbo�Դ��� **��������** ��**���ӿ���**������˿�����`executes` / `accepts`���ͻ��˿�����`actives` / `connections`�����������������������ַ�ʽ������ͨ������»�ѡ�������㷨��������������������**©Ͱ��**��**����Ͱ��**��

©Ͱ��ʵ�ֵ�ԭ���ǽ������������һ�����У��Թ̶�Ƶ�ʴ�����Щ��������������ˣ�ֱ�Ӿܾ���������ȱ���Զ��׼��������޷�Ӧ��ͻ��������

����Ͱ��������һ�������ʲ������ƣ�ÿ���������ȡ���ƺ���ܽ���ҵ��ϵͳ��������Ͱ������Ϊ0ʱ���ܾ�����������©Ͱ���෴���������ʺ�Ӧ������ͻ����

![tokenBucket.png](https://i.loli.net/2019/09/09/TPdhqLmbOryiFHA.png)

��Ŀ��ʹ�õ��Ǹ���ʵ�ֵļ��װ�����Ͱ

```java
// ����Ͱ����������
public class TokenBucket {
    private int capacity = 100; //Ͱ��С
    private int rate = 1;       //Ͱ�����������ʣ�ÿ���룩
    private int nowTokens;  	//��ǰ������
    private long timestamp;     //ʱ���
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

### �����۶�

�����۶ϣ�ָ���ǵ�ĳһ���������Ӧ��ʱ�򲻿��õ����ʱ����ͣ�Ը÷���ĵ��ã��Է�����ѩ������Dubbo�У�����ͨ�����ش���ķ�ʽʵ�֣�����Ҫ��API�������޸ģ�����Ҫ�ֶ�ʵ������߼������磺��ʱ�����۶ϣ��̳߳����õȣ�����SpringCloud�����Hystrix�������˷����۶ϵ�ʵ�֡�����ע��ķ�ʽ������ز������ɣ������Լ�С��

����Ŀ����û����ģ�µ��ĳ�������Orderģ���createOrder���������۶����ã���ش������£�

```java
public ResponseVO buyTicketsError(int fieldId, String soldSeats, String seatsName) {  
    return ResponseVO.serviceFail("��Ǹ���µ�����̫�࣬���Ժ����ԣ�");
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

�������������£�

**fallbackMethod** : ����ִ��ʱ�۶ϡ����󡢳�ʱʱ����õĻ��˷�������Ҫ���ִ˷����� Hystrix �����Ĳ����ͷ���ֵһ�¡�

**commandProperties** : ��������, �� `@HystrixProperty` ע�����鹹�ɵġ�

- execution.isolation.strategy������ĸ�����ԣ�Ĭ��Thread���������������ڵ������߳������У�
- execution.isolation.thread.timeoutInMilliseconds�����õ�����ִ�еĳ�ʱʱ�䣬Ĭ��1000��������������ԭ��ſ�4000
- circuitBreaker.requestVolumeThreshold����·����������ֵ������ʱ���ڴﵽ��������ſ�����·�����ܣ�
- circuitBreaker.errorThresholdPercentage������ʱ��������ʧ�ܰٷֱȵ����ֵʱ����������·����Ĭ��50

**threadPoolProperties** ���̳߳����ԣ���commandProperties�÷���ͬ��
Hystrix���̳߳��ǻ���java����ThreadPool�ļ򵥰�װ�������ɶ�Ӧ�����䡣

- coreSize: �����̳߳صĴ�С��Ĭ��10���¶�������Ȼ��IO�ܼ��������̳߳ش�С�����ó�2N+1��NΪCPU��������
- maxQueueSize�� �������д�С���������̳߳��ڵ��̴߳���æµ״̬ʱ������¼��������ŵ����������У��������߳̿���ʱ��ִ�С��������д�С�����񽫱��ܾ���Ĭ��10
- keepAliveTimeMinutes�� �̳߳��г��������̳߳ش�С���̴߳���ú󱻻��գ���λ���ӡ�Ĭ��2
- queueSizeRejectionThreshold�� �������еĴ�С������ֵʱ�����ܾ�������ļ��룬����ΪmaxQueueSize�ġ���̬���á���Ĭ��5������maxQueueSize�ɵð��������Ҳ����ѽ��
- metrics.rollingStats.timeInMilliseconds�� ͳ����Ӧʱ��ٷֱȵĻ������ڣ���λ���룬Ĭ��60000����1����
- metrics.rollingStats.numBuckets�� ��Ӧʱ��ͳ�ƴ�������Ӧ��Ͱ������Ĭ��6����������ÿ����һ��Ͱ��ʱ��������ͳ��һ�Σ�

## �ֲ�ʽ����

### ���
�ֲ�ʽ������ǽ�����ڵ�����񿴳�һ�����崦��һ������������ߡ���Դ���������������������ɡ���������߿������Ϊ��������ֲ�ʽ����ķֲ�ʽ�ڵ㣬��Դ���������ڿ����������漰����Դ�������������Э���ͼ��������������á�
�����ķֲ�ʽ����������������ʽ/����ʽ����2PC/3PC����������Ϣ������һ���Է�����TCC���ʽ��������������ڻ�����Ϣ������һ���Է�����TCC���Բ�������Ч�ʽϸߣ�������ÿһ���ڵ��ϵ�������Բ���ִ�У������ڱ˴˵ȴ����������������Դ���ɿ���ǿ����ʱЧ��Ҫ��ϸߵĳ�����
> ע���޸Ķ���״̬ VS �ʽ�ת��������״̬���ʽ�����Դ��ǰ���г��ص���أ��ɿ���ǿ�������߳��ؽ�Ϊ���ѣ��ɿ��Բ

### TCC������������Tcc-Transaction

TCC�ֱ��ӦTry��Confirm��Cancel���ֲ����������׶Σ����������£�

- Try��Ԥ��ҵ����Դ
- Confirm��ȷ��ִ��ҵ�����
- Cancel��ȡ��ִ��ҵ�����

Try��Confirm��Cancel����Ӧ��������Ҫ�˹�ʵ�֣������˿����Ĺ�����������Ҫע��Confirm��Cancel�������ݵ���

����Ŀʹ�õ�TCC���Ϊtcc-transaction��https://github.com/changmingxie/tcc-transaction

### ʹ�ó���

��ϱ���Ŀ�����ǿ����ڶ���ģ��Ӧ��TCC�ֲ�ʽ��������֤����������ԭ���ԣ�ʹ�ó������£�
- ���÷���Gatewayģ���buyTicket����
- �����÷���Orderģ���isTrueSeat��isNotSold��createOrder����
����isTrueSeat��isNotSold������֤��ǰ���ε���λ�Ƿ���ã���ǰ�˿��Ʋ������ǰ���¾��᷵��true��createOrder��ִ����������������ҵ��Ϊ��������Ӧʱ�䣬3�����������첽���ã���ʱ���createOrder����isTrueSeatִ�У���isTrueSeat����false������ɶ����г��������λ���������ˣ��б�Ҫ�����ε�����Ϊһ��ȫ���������ִ�У�һ������֮һִ�С�ʧ�ܡ������в����߽�����cancel������

> ע��Ϊ�����TCC��ܣ���serviceʵ������һ��isTrueSeat��isNotSold����false�����׳�һ���Զ����쳣���������������ִ��cance������

**Ԥ����Դ** ��try�׶�ִ��createOrder�Ĳ����������ö���״̬Ϊdraft��confirm�������޸�Ϊorder_success��cancel�������޸�Ϊorder_fail��

**�ݵ���** ��createOrder��confirm/cancel�����������ݵ��Կ��ƣ���ִ��ǰ���ѯ�����Ƿ������״̬�Ƿ�Ϊdraft���������������Ž���ִ�С�

��ش������£�
```java
// ���÷�buyTickets
@Compensable(confirmMethod = "buyTicketsConfirm", cancelMethod = "buyTicketsCancel", transactionContextEditor = DubboTransactionContextEditor.class)
public ResponseVO buyTickets(int fieldId, String soldSeats,String seatsName){
    ...
	orderServiceAsyncAPI.isTrueSeats(fieldId, seatsInt);
	orderServiceAsyncAPI.isNotSold(fieldId, seatsInt);
	orderServiceAsyncAPI.createOrder(uuid, fieldId, seatsInt, userId, seatsName);
	...
}

public ResponseVO buyTicketsConfirm(int fieldId, String soldSeats, String seatsName) {
	log.info("�����¶��������ύ");
	return null;
}
public ResponseVO buyTicketsCancel(int fieldId, String soldSeats, String seatsName) {
	log.info("�����¶�������ȡ��");
	return null;
}
```

```java
// �����÷�����createOrderΪ����
@Compensable(confirmMethod = "orderConfirm", cancelMethod = "orderCancel", transactionContextEditor = DubboTransactionContextEditor.class)
public OrderInfoVO createOrder(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
	// ��������ҵ�����ö���״̬draft
}

public OrderInfoVO orderConfirm(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
	OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
	if(orderInfoVO!=null && DRAFT.equals(orderInfoVO.getOrderStatus())) {
		// ���¶���״̬Ϊ order_success
		...
		
		log.info("��������ȷ��");
	}
	return null;
}

public OrderInfoVO orderCancel(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
	OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
	if(orderInfoVO!=null && DRAFT.equals(orderInfoVO.getOrderStatus())) {
		// ���¶���״̬ order_fail
		...
		
		log.info("��������ȡ��");
	}
	return null;
}
```


## FTP�ļ�������
����˼�壬����FTPΪͨ��Э�飬�����ļ��Ĵ���������Ӧ�÷�������
�ڱ���Ŀ��������ʹ�õ�FTP��������һ���Ǹ���Ӱ�����ͣ������ݿ��еĶ�Ӧ��λ�÷ֲ�ͼ���ļ�·����ͨ����·����FTP�������ϻ�ȡ�ļ�����һ����֧��ģ�����ɵĶ�ά��ͼƬ����Ҫ������FTP�������С�
��CentOS�����£���ѡ��vsftpd��ΪFTP������Ӧ�ã�ע�������ļ���Ҫ�����������ݲ����Ʒ����������ö�Ӧ�˿ڵİ�ȫ�飺
```shell
pasv_enable=YES      #����ģʽ
pasv_min_port=50000  #���ŵĶ˿�
pasv_max_port=50009
```


# 基于Spring框架的问答网站开发

标签（空格分隔）： Java Spring myBatis redis pyspider solr IKAnalyzer

---


## 简介 ##
该项目主要是为了熟悉基于Spring框架的网页后台开发流程，此md文档也是学习过程的一个总结。项目中主要包括Spring框架、iBatis集成、用户注册、敏感词过滤、站内信、Redis实现赞踩功能、异步设计、邮件通知、timeline实现、爬虫、站内搜索、项目测试等内容。
## 版本说明 ##
IDE IntelliJ、jdk 1.8.0_25、Spring 4.0、MySQL 5.1.73、mybatis 1.2.2、redis 2.9.0、solr 6.2.2

----------

## 学习笔记 ##

 **1. Spring框架**
 - **IoC（控制反转）**：
 依赖注入（DI），无需关注对象初始化，对象的创建交给IoC容器处理。实现方式@Autowired
 - **AOP（面向切面编程）**：
 面向切面编程，将那些影响了多个类的公共行为封装到一个可重用模块，并将其命名为@Aspect，即切面。减少重复代码，应用于日志、权限认证等。
*切面（Aspect）：*一个关注点的模块化，这个关注点可能会横切多个对象。
*切入点（Pointcut）：*匹配连接点的断言。
*通知（Advice）：*
  - 前置通知（@Before）：在某连接点之前执行的通知，但这个通知不能阻止连接点之前的执行流程（除非它抛出一个异常）。
  - 后置通知（@AfterReturning）：在某连接点正常完成后执行的通知：例如，一个方法没有抛出任何异常，正常返回。
  - 异常通知（@AfterThrowing）：在方法抛出异常退出时执行的通知。
  - 最终通知（@After）：当某连接点退出的时候执行的通知（不论是正常返回还是异常退出）。
  - 环绕通知（@Around）：包围一个连接点的通知，如方法调用。这是最强大的一种通知类型。环绕通知可以在方法调用前后完成自定义的行为。它也会选择是否继续执行连接点或直接返回它自己的返回值或抛出异常来结束执行。
 - **controller/service/DAO**
通过注解方式表明对象的作用
常见注解：

@Component：泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。

@Service：用于标注业务层组件。

@Controller：用于标注控制层组件。

@Repository：用于标注数据访问组件，即DAO组件。

 - @RequestMapping
 
RequestMapping是一个用来处理请求地址映射的注解，可用于类或方法上。用于类上，表示类中的所有响应请求的方法都是以该地址作为父路径。RequestMapping注解有六个属性，分成三类进行说明：

*value*：     指定请求的实际地址，指定的地址可以是URI Template 模式（后面将会说明）；

*method*：  指定请求的method类型， GET、POST、PUT、DELETE等；

*consumes*： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;

*produces*:    指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回；

*params*： 指定request中必须包含某些参数值是，才让该方法处理。

*headers*： 指定request中必须包含某些指定的header值，才能让该方法处理请求。

```java
//示例
@RequestMapping(path = {"/user"},method = {RequestMethod.POST},params = "myParam=myValue",headers = "content-type=text/*")
//请求地址为/user，请求方法为POST，请求参数为myParam=myValue，设置了headers
```

 **2. Velocity**
 - 实现前端和后台解耦
常见语法说明：

**2.1 注释：**

单行'##'，多行'#* *#'

**2.2 引用：**
 - 引用变量：
```html
<html>
<body>
<pre>
    Hello Lily Velocity
    username: $!{username}
    ##使用!{}表示变量不存在强制转换为空字符串
</pre>
</body>
</html>
```
 - 引用属性：
Controller:
```java
model.addAttribute("user",new User("Winter",25));
```
.vm模板：
```html
UserName:${!user.name}
UserAge:${!user.age}
```
 - 引用方法
```html
method:${user.getName()}
method2:${user.setName("summer")}
method3:${user.getName()}
```
**2.3 指令**
 - set
```html
#set( $monkey = $bill ) ## variable reference
#set( $monkey.Friend = "monica" ) ## string literal
#set( $monkey.Blame = $whitehouse.Leak ) ## property reference
#set( $monkey.Plan = $spindoctor.weave($web) ) ## method reference
#set( $monkey.Number = 123 ) ##number literal
#set( $monkey.Say = ["Not", $my, "fault"] ) ## ArrayList
#set( $monkey.Map = {"banana" : "good", "roast beef" : "bad"}) ## Map
```
 - Conditionals
if/elseif/else
```html
#if( $foo < 10 )
    **Go North**
#elseif( $foo == 10 )
    **Go East**
#elseif( $bar == 6 )
    **Go South**
#else
    **Go West**
#end
```
 - 关系和逻辑运算符:==,&&,||,！
 - foreach循环
当对象是array时：

Controller：
```java
List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
model.addAttribute("colors", colors);
```
.vm模板：
```html
#foreach ($color in $colors)
    Color $!{foreach.index}/$!{foreach.count}: $!{color}
#end
```
当对象是map时

Controller：
```java
Map<String, String> map = new HashMap<String, String>();
for (int i = 0; i < 4; ++i) {
    map.put(String.valueOf(i), String.valueOf(i * i));
}
model.addAttribute("map", map);
```
.vm模板：
```html
#foreach($key in $map.keySet())
    Number $!{foreach.index}/$!{foreach.count}: $!{key} $map.get($key)
#end
```
 - include 与 parse
 
 允许导入本地文件，区别在于include不做VTL语法解析，parse做VTL语法解析。
 
 **3. 数据库与iBatis集成**
 - **数据库**：数据库采用MySQL，在项目中共创建了6个数据库表单，分别为`question`、`user`、`login_ticket`、`comment`、`message`、`feed`，分别表示发布问题、注册用户信息、登录状态信息、问题评论、站内信、时间线消息
 - **iBatis集成**：
 可以通过注解或者XML的方式映射SQL语句
 
*注解@Mapper*：@select，@update，@delete，@insert

*XML*：
```xml
<select id="selectPerson" parameterType="int" resultType="hashmap">
  SELECT * FROM PERSON WHERE ID = #{id}
</select>

<insert id="insertAuthor">
  insert into Author (id,username,password,email,bio)
  values (#{id},#{username},#{password},#{email},#{bio})
</insert>

<update id="updateAuthor">
  update Author set
    username = #{username},
    password = #{password},
    email = #{email},
    bio = #{bio}
  where id = #{id}
</update>

<delete id="deleteAuthor">
  delete from Author where id = #{id}
</delete>
```

 - sql这个元素可以被用来定义可重用的 SQL 代码段，可以包含在其他语句中。它可以被静态地(在加载参数) 参数化. 不同的属性值通过包含的实例变化.示例代码:
```XML
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.winter.demo3.dao.QuestionDAO">
    <sql id="table">question</sql>
    <sql id="selectFields">id, title, content, comment_count,created_date,user_id
    </sql>
    <select id="selectLatestQuestions" resultType="com.winter.demo3.model.Question">
        SELECT
        <include refid="selectFields"/>
        FROM
        <include refid="table"/>

        <if test="userId != 0">
            WHERE user_id = #{userId}
        </if>
        ORDER BY id DESC
        LIMIT #{offset},#{limit}
    </select>
</mapper>
```
 - 动态SQL：
通常使用动态 SQL 不可能是独立的一部分,MyBatis 当然使用一种强大的动态 SQL 语言来改进这种情形,这种语言可以被用在任意的 SQL 映射语句中。包括
 - if
 - choose (when, otherwise)
 - trim (where, set)
 - foreach
 
例 *if* 判断:
```XML
<select id="findActiveBlogWithTitleLike"
     resultType="Blog">
  SELECT * FROM BLOG 
  WHERE state = ‘ACTIVE’ 
  <if test="title != null">
    AND title like #{title}
  </if>
</select>
```
  **4. 注册及登录**
 - token登记：与用户信息关联，session共享
用数据库表单关联用户登录状态，表单设计如下：
```SQL
DROP TABLE IF EXISTS `login_ticket`;
  CREATE TABLE `login_ticket` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `ticket` VARCHAR(45) NOT NULL,
    `expired` DATETIME NOT NULL,
    `status` INT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `ticket_UNIQUE` (`ticket` ASC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
status=0表示已登录，1表示登出

 - Interceptor：
 实现途径：实现HandlerInterceptor接口，或者实现WebRequestInterceptor接口。HandlerInterceptor接口代码：
```java
package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerInterceptor {

    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception;

    void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception;

    void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception;
}
```
**preHandle**:在请求处理之前进行调用。SpringMVC 中的 Interceptor 是链式调用的，在一个应用中或者说是在一个请求中可以同时存在多个 Interceptor 。每个 Interceptor 的调用会依据它的声明顺序依次执行，而且最先执行的都是 Interceptor 中的 preHandle 方法，所以可以在这个方法中进行一些前置初始化操作或者是对当前请求做一个预处理，也可以在这个方法中进行一些判断来决定请求是否要继续进行下去。该方法的返回值是布尔值 Boolean 类型的，当它返回为 false 时，表示请求结束，后续的 Interceptor 和 Controller 都不会再执行；当返回值为 true 时，就会继续调用下一个 Interceptor 的 preHandle 方法，如果已经是最后一个 Interceptor 的时候，就会是调用当前请求的 Controller 中的方法。

**postHandle**:需要当前对应的 Interceptor 的 preHandle 方法的返回值为 true 时才会执行,在当前请求进行处理之后，也就是在 Controller 中的方法调用之后执行，但是它会在 DispatcherServlet 进行视图返回渲染之前被调用，所以咱们可以在这个方法中对 Controller 处理之后的 ModelAndView 对象进行操作。

**afterCompletion**:需要当前对应的 Interceptor 的 preHandle 方法的返回值为 true 时才会执行。因此，该方法将在整个请求结束之后，也就是在 DispatcherServlet 渲染了对应的视图之后执行，这个方法的主要作用是用于进行资源清理的工作。

配置Interceptor方式：继承WebMvcConfigurerAdapter类，重写addInterceptors方法，完成拦截器的注册，或者通过配置XML文档。方法一示例代码：
```java
@Component
public class DemoWebConfiguration extends WebMvcConfigurerAdapter{

    @Autowired
    PassportInterceptor passportInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        super.addInterceptors(registry);
    }
}
```

 - 用户密码加密：MD5+salt
 
 **5. 敏感词过滤**[待补充]
 
 - 前缀树：复杂度
 
 **6. 评论中心及站内信**[待补充]
 
 **7. Redis**
 
常见集合及命令（Jedis）：[参考](https://www.cnblogs.com/3chi/p/7209457.html)

1.对value操作的命令

     exists(key)：确认一个key是否存在

     del(key)：删除一个key

     type(key)：返回值的类型

     keys(pattern)：返回满足给定pattern的所有key

     randomkey：随机返回key空间的一个key

     rename(oldname, newname)：将key由oldname重命名为newname，若newname存在则删除newname表示的key

     dbsize：返回当前数据库中key的数目

     expire：设定一个key的活动时间（s）

     ttl：获得一个key的活动时间

     select(index)：按索引查询

     move(key, dbindex)：将当前数据库中的key转移到有dbindex索引的数据库

     flushdb：删除当前选择数据库中的所有key

     flushall：删除所有数据库中的所有key

2.对String操作的命令

     set(key, value)：给数据库中名称为key的string赋予值value

     get(key)：返回数据库中名称为key的string的value

     getset(key, value)：给名称为key的string赋予上一次的value

     mget(key1, key2,…, key N)：返回库中多个string（它们的名称为key1，key2…）的value

     setnx(key, value)：如果不存在名称为key的string，则向库中添加string，名称为key，值为value

     setex(key, time, value)：向库中添加string（名称为key，值为value）同时，设定过期时间time

     mset(key1, value1, key2, value2,…key N, value N)：同时给多个string赋值，名称为key i的string赋值value i

     msetnx(key1, value1, key2, value2,…key N, value N)：如果所有名称为key i的string都不存在，则向库中添加string，名称key i赋值为value i

     incr(key)：名称为key的string增1操作

     incrby(key, integer)：名称为key的string增加integer

     decr(key)：名称为key的string减1操作

     decrby(key, integer)：名称为key的string减少integer

     append(key, value)：名称为key的string的值附加value

     substr(key, start, end)：返回名称为key的string的value的子串

3.对List操作的命令

     rpush(key, value)：在名称为key的list尾添加一个值为value的元素

     lpush(key, value)：在名称为key的list头添加一个值为value的 元素

     llen(key)：返回名称为key的list的长度

     lrange(key, start, end)：返回名称为key的list中start至end之间的元素（下标从0开始，下同）

     ltrim(key, start, end)：截取名称为key的list，保留start至end之间的元素

     lindex(key, index)：返回名称为key的list中index位置的元素

     lset(key, index, value)：给名称为key的list中index位置的元素赋值为value

     lrem(key, count, value)：删除count个名称为key的list中值为value的元素。count为0，删除所有值为value的元素，count>0      从头至尾删除count个值为value的元素，count<0从尾到头删除|count|个值为value的元素。

     lpop(key)：返回并删除名称为key的list中的首元素

     rpop(key)：返回并删除名称为key的list中的尾元素

     blpop(key1, key2,… key N, timeout)：lpop 命令的block版本。即当timeout为0时，若遇到名称为key i的list不存在或该list为空，则命令结束。如果 timeout>0，则遇到上述情况时，等待timeout秒，如果问题没有解决，则对key i+1开始的list执行pop操作。

     brpop(key1, key2,… key N, timeout)：rpop的block版本。参考上一命令。

     rpoplpush(srckey, dstkey)：返回并删除名称为srckey的list的尾元素，并将该元素添加到名称为dstkey的list的头部

4.对Set操作的命令

     sadd(key, member)：向名称为key的set中添加元素member

     srem(key, member) ：删除名称为key的set中的元素member

     spop(key) ：随机返回并删除名称为key的set中一个元素

     smove(srckey, dstkey, member) ：将member元素从名称为srckey的集合移到名称为dstkey的集合

     scard(key) ：返回名称为key的set的基数

     sismember(key, member) ：测试member是否是名称为key的set的元素

     sinter(key1, key2,…key N) ：求交集

     sinterstore(dstkey, key1, key2,…key N) ：求交集并将交集保存到dstkey的集合

     sunion(key1, key2,…key N) ：求并集

     sunionstore(dstkey, key1, key2,…key N) ：求并集并将并集保存到dstkey的集合

     sdiff(key1, key2,…key N) ：求差集

     sdiffstore(dstkey, key1, key2,…key N) ：求差集并将差集保存到dstkey的集合

     smembers(key) ：返回名称为key的set的所有元素

     srandmember(key) ：随机返回名称为key的set的一个元素

5.对zset（sorted set）操作的命令

     zadd(key, score, member)：向名称为key的zset中添加元素member，score用于排序。如果该元素已经存在，则根据score更新该元素的顺序。

     zrem(key, member) ：删除名称为key的zset中的元素member

     zincrby(key, increment, member) ：如果在名称为key的zset中已经存在元素member，则该元素的score增加increment；否则向集合中添加该元素，其score的值为increment

     zrank(key, member) ：返回名称为key的zset（元素已按score从小到大排序）中member元素的rank（即index，从0开始），若没有member元素，返回“nil”

     zrevrank(key, member) ：返回名称为key的zset（元素已按score从大到小排序）中member元素的rank（即index，从0开始），若没有member元素，返回“nil”

     zrange(key, start, end)：返回名称为key的zset（元素已按score从小到大排序）中的index从start到end的所有元素

     zrevrange(key, start, end)：返回名称为key的zset（元素已按score从大到小排序）中的index从start到end的所有元素

     zrangebyscore(key, min, max)：返回名称为key的zset中score >= min且score <= max的所有元素

     zcard(key)：返回名称为key的zset的基数

     zscore(key, element)：返回名称为key的zset中元素element的score

     zremrangebyrank(key, min, max)：删除名称为key的zset中rank >= min且rank <= max的所有元素

     zremrangebyscore(key, min, max) ：删除名称为key的zset中score >= min且score <= max的所有元素

     zunionstore / zinterstore(dstkeyN, key1,…,keyN, WEIGHTS w1,…wN, AGGREGATE SUM|MIN|MAX)：对N个zset求并集和交集，并将最后的集合保存在dstkeyN中。对于集合中每一个元素的score，在进行AGGREGATE运算前，都要乘以对于的WEIGHT参数。如果没有提供WEIGHT，默认为1。默认的AGGREGATE是SUM，即结果集合中元素的score是所有集合对应元素进行 SUM运算的值，而MIN和MAX是指，结果集合中元素的score是所有集合对应元素中最小值和最大值。

6.对Hash操作的命令

     hset(key, field, value)：向名称为key的hash中添加元素field<—>value

     hget(key, field)：返回名称为key的hash中field对应的value

     hmget(key, field1, …,field N)：返回名称为key的hash中field i对应的value

     hmset(key, field1, value1,…,field N, value N)：向名称为key的hash中添加元素field i<—>value i

     hincrby(key, field, integer)：将名称为key的hash中field的value增加integer

     hexists(key, field)：名称为key的hash中是否存在键为field的域

     hdel(key, field)：删除名称为key的hash中键为field的域

     hlen(key)：返回名称为key的hash中元素个数

     hkeys(key)：返回名称为key的hash中所有键

     hvals(key)：返回名称为key的hash中所有键对应的value

     hgetall(key)：返回名称为key的hash中所有的键（field）及其对应的value
 - 应用：排序，异步队列，排行榜
 
 **8. 异步队列实现点赞与邮件发送**
 
 - 异步队列：尽快反馈给用户，Redis队列实现。包含以下内容
  - EventHandler:具体事件的接口类，包含事件处理函数doHandler及获取事件类型列表函数getSupportEventTypes
  - EventType：事件类型枚举
  - EventModel：事件模型，事件分发fireEvent及事件处理doHandLer中的具体事件。事件模型中的set函数返回值均为该对象本身，即链式设置法则，精简代码量。
  - EventProducer：事件分发，将事件以Json格式序列化后写入Redis的list中。
  - EventConsumer：处理事件。实现InitializingBean接口，在初始化的时候自动注册所有事件。开启新的线程，不断查询是否有新的事件需要处理。通过异步队列避免了主线程的阻塞，提高程序效率。
  - handler包：实现EventHandler接口，重写doHandler方法，执行具体的事件。
 - 邮件发送：[待补充]
 
 **9. 关注服务**[待补充]
 
 **10. timeline实现**[待补充]
 - 推拉模式
 
 **11. pyspider爬取网页内容**[待补充]
 - 效率
 - 防止ban
 - 解析方式
 
 **12. solr实现全文搜索**
  - 配置managed-schema文档，添加IKAnanlyzer分词配置，代码如下：
```XML
  <field name="question_title" type="text_ik" indexed="true" stored="true" multiValued="true"/>
<field name="question_content" type="text_ik" indexed="true" stored="true" multiValued="true"/>

<fieldTypename="text_ik" class="solr.TextField">
<!--索引时候的分词器-->
<analyzer type="index">
<tokenizerclass="org.wltea.analyzer.util.IKTokenizerFactory" useSmart=“false"/>
<filter class="solr.LowerCaseFilterFactory"/>
</analyzer>
<!--查询时候的分词器-->
<analyzer type="query">
<tokenizerclass="org.wltea.analyzer.util.IKTokenizerFactory" useSmart=“true"/>
</analyzer>
</fieldType>
```
  - 配置数据库库导入，包括solrconfig.xml和data-config.xml,代码如下:
```XML
<!--solrconfig.xml-->
<requestHandlername="/dataimport" class="org.apache.solr.handler.dataimport.DataImportHandler">
<lstname="defaults">
<strname="config">data-config.xml</str>
</lst>
</requestHandler>
```
```XML
<!--data-config.xml-->
<dataConfig>
<dataSource type="JdbcDataSource"
driver="com.mysql.jdbc.Driver"
url="jdbc:mysql://localhost/wenda"
user="root"
password="nowcoder"/>
<document>
<entity name="question" query="select id,title,content from question">
<field column="content" name="question_content"/>
<field column="title" name="question_title"/>
</entity>
</document>
</dataConfig>
```
  - 通过solrj将solr集成到工程中
在工程配置文档中导入solrj库，代码如下：
```XML
<!-- https://mvnrepository.com/artifact/org.apache.solr/solr-solrj -->
		<dependency>
			<groupId>org.apache.solr</groupId>
			<artifactId>solr-solrj</artifactId>
			<version>6.6.2</version>
		</dependency>
```
程序框架如下：service层为`SearchService`，controller层为`SearchController`。SearchService中的searchQuestion函数返回搜索结果并加亮，indexQuestion函数实现新建索引。在QuestionController的addQuestion函数中实现`添加问题索引`异步事件的分发，具体事件为AddQuestionHandler。
 - IKAnalyzer：中文分词
 
 **13. 单元测试**[待补充]

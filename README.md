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

@RequestMapping
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

@Mapper
@Autowired


 **2. Velocity**
 - 实现前端和后台解耦
常见语法说明：
**2.1 注释：**单行##，多行#* *#
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
关系和逻辑运算符:==,&&,||,！
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
 - Interceptor：
 - 用户密码加密：
 **5. 敏感词过滤**
 - 前缀树：复杂度
 **6. 评论中心及站内信**
 **7. Redis实现赞踩**
 - 数据结构
 - 应用：排序，异步队列，排行榜
 **8. 异步队列与邮件发送**
 - 异步队列：尽快反馈给用户，Redis队列实现
 - 邮件发送：
 **9. 关注服务**
 **10. timeline实现**
 - 推拉模式
 **11. pyspider爬取网页内容**
 - 效率
 - 防止ban
 - 解析方式
 **12. solr实现全文搜索**
 - solr：导入数据，去重（敏感hash）
 - IKAnalyzer：中文分词
 **13. 单元测试**

package com.winter.demo3.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.winter.demo3.controller.CommentController;
import com.winter.demo3.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis;//localhost:6379/10");
    }

    public long sadd(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch(Exception e){
            logger.error("数据添加失败"+e.getMessage());
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch(Exception e){
            logger.error("数据移除失败"+e.getMessage());
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch(Exception e){
            logger.error("返回集合数量失败"+e.getMessage());
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }
    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }catch(Exception e){
            logger.error("判断元素是否存在失败"+e.getMessage());
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
        return false;
    }


    public static void print(int index,Object obj){
        System.out.println(String.format("%d,%s",index,obj.toString()));
    }
    public static void main(String[] args){
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();
        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.rename("hello","newhello");
        print(1,jedis.get("newhello"));
        jedis.setex("hello2",20,"world2");

        jedis.set("pv","100");
        jedis.incr("pv");
        jedis.incrBy("pv",5);
        jedis.decrBy("pv",3);
        print(2,jedis.get("pv"));

        print(3,jedis.keys("*"));

        String listName = "list";
        jedis.del(listName);
        for(int i = 0; i < 10; ++i) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(4,jedis.lrange(listName,0,12));
        print(5,jedis.llen(listName));
        print(6,jedis.lpop(listName));
        print(7,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xxx"));
        print(8,jedis.lrange(listName,0,12));

        String userKey = "userxx";
        jedis.hset(userKey,"name","winter");
        jedis.hset(userKey,"age","18");
        jedis.hset(userKey,"sex","male");
        print(12,jedis.hget(userKey,"name"));
        print(13,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"age");
        print(14,jedis.hgetAll(userKey));
        jedis.hsetnx(userKey,"school","zju");
        jedis.hsetnx(userKey,"name","summer");
        print(15,jedis.hgetAll(userKey));

        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for(int i = 0; i < 10; ++i){
            jedis.sadd(likeKey1,String.valueOf(i));
            jedis.sadd(likeKey2,String.valueOf(i*i));
        }
        print(20,jedis.smembers(likeKey1));
        print(21,jedis.smembers(likeKey2));
        print(22,jedis.sunion(likeKey1,likeKey2));
        print(23,jedis.sdiff(likeKey1,likeKey2));
        print(24,jedis.sinter(likeKey1,likeKey2));
        jedis.smove(likeKey2,likeKey1,"25");
        print(25,jedis.smembers(likeKey1));

        String rankKey = "rankKey";
        jedis.zadd(rankKey,15,"Jim");
        jedis.zadd(rankKey,30,"Tom");
        jedis.zadd(rankKey,45,"Jerry");
        jedis.zadd(rankKey,60,"Song");
        jedis.zadd(rankKey,75,"Winter");
        jedis.zadd(rankKey,90,"Summer");
        print(30,jedis.zcard(rankKey));
        print(31,jedis.zcount(rankKey,60,100));
        print(32,jedis.zscore(rankKey,"Winter"));
        jedis.zincrby(rankKey,20,"Winter");
        print(33,jedis.zrange(rankKey,0,100));
        print(34,jedis.zrevrange(rankKey,1,3));
        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey,"60","100")){
            print(35,tuple.getElement()+":"+String.valueOf(tuple.getScore()));
        }
        print(36,jedis.zrevrank(rankKey,"Winter"));

        String setKey = "zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"e");
        print(37,jedis.zlexcount(setKey,"[b","(d"));
        jedis.zremrangeByLex(setKey,"(c","+");
        print(38,jedis.zrange(setKey,0,2));

//        JedisPool pool = new JedisPool();
//        for (int i = 0; i < 7; ++i) {
//            Jedis j = pool.getResource();
//            print(45, j.get("pv"));
//            j.close();
//        }

        User user = new User();
        user.setName("xx");
        user.setPassword("pass");
        user.setSalt("salt");
        user.setId(1);
        user.setHeadUrl("a.png");
        jedis.set("user1", JSONObject.toJSONString(user));
        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value,User.class);
        print(47,user2);

    }

}

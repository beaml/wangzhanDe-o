package com.newcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=Application.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey="test:count";
        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));

    }
    @Test
    public void testHashes(){
        String redisKey="test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }
    @Test
    public void testLists(){
        String redisKey="test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));

    }
    @Test
    public void testSets(){
        String redisKey="test:teachers";

        redisTemplate.opsForSet().add(redisKey,"刘备","关羽","张飞");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));

    }
    @Test
    public void testSortedSets(){
        String redisKey="test:students";
        redisTemplate.opsForZSet().add(redisKey,"唐僧",80);
        redisTemplate.opsForZSet().add(redisKey,"悟空",90);
        redisTemplate.opsForZSet().add(redisKey,"猪八戒",60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"猪八戒"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"猪八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"猪八戒"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,1));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,1));
    }
    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
    }
    //现象：多次访问同一个key,通过绑定key来减少重复代码
    @Test
    public void testBoundOperations(){
        String redisKey="test:count";
        BoundValueOperations operations=redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }
    //将操作放在队列里统一执行。
    // 注意：不要将查询操作放在事务里，要么之间要么之后，因为查询操作不会立马执行，会造成时延。
    //编程式事务
    @Test
    public void testTransactional(){
        Object obj=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
               String redisKey="test:tx";
               //启用事务
               operations.multi();

               operations.opsForSet().add(redisKey,"zhangsan");
               operations.opsForSet().add(redisKey,"lisi");
               operations.opsForSet().add(redisKey,"wangwu");
               //此时，只是将操作放在队列里，并没有执行.查看是否有数据
                System.out.println(operations.opsForSet().members(redisKey));
               //提交事务
                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}

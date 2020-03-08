package com.newcoder.community.service;

import com.newcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    //注入redis
    @Autowired
    private RedisTemplate redisTemplate;
    //点赞
    public void like(int userId,int entityType,int entityId){
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        //判断该实体是否被该用户点过赞
        boolean isMember=redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        if(isMember){
            //已点赞，再次点赞为取消点赞
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else{
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }
    //查询某实体点赞的数量
    public long findEntityLikeCout(int entityType,int entityId){
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }
    //查询某人对某实体的点赞状态
    public int  findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0;
    }
}

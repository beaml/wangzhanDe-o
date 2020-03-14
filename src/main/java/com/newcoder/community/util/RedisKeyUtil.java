package com.newcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT=":";

    //帖子和评论统称为实体
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    //某用户获得的赞
    private static final String PREFIX_USER_LIKE="like:user";
    //粉丝
    private static final String PREFIX_FOLLOWEE="followee";
    //目标人
    private static final String PREFIX_FOLLOWER="follower";

    //某个实体的赞
    //like:entity:entityType:entityId->set(userId)
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    //某用户所获得的赞
    //like:user:userId->int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }
    //某个用户关注的实体（用户或者帖子）,userId表示是谁关注的.某个用户userId关注了某个实体entityType.以时间作为value
    //followee:userId:entityType->zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }
    //某个用户拥有的粉丝量（关注他的人）
    //follower:entityType:entityId->zset(userId,now)
    public static  String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
}

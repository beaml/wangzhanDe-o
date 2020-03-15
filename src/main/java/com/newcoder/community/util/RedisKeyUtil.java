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
    //验证码
    private static  final String PREFIX_KAPTCHA="kapacha";
    //是否已登录的凭证
    private static final String PREFIX_TICKET="ticket";
    //用户信息
    private static final String PREFIX_USER="user";

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
    //提供一个方法来拼出验证码的key。还没登录，还没有用户id，但是需要对固定的用户进行验证。加一个用户临时凭证
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }
    //提供一个拼出登录凭证的key的方法
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }
    //用户key
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }
}

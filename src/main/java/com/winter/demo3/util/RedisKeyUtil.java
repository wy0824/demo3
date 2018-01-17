package com.winter.demo3.util;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";

    private static String BIZ_FOLLOWER = "FOLLOWER";
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_TIMELINE = "TIMELINE";

    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT +String.valueOf(entityId);
    }

    public static String getDislikeKey(int entityType,int entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT +String.valueOf(entityId);
    }

    public static String getEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }
    public static String getFollowerKey(int entitytype,int entityId){
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entitytype)+SPLIT+String.valueOf(entityId);
    }


    public static String getFolloweeKey(int userId,int entityType){
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId)+SPLIT+String.valueOf(entityType);
    }
    public static String getTimelineKey(int userId){
        return BIZ_TIMELINE + SPLIT +String.valueOf(userId);
    }

}

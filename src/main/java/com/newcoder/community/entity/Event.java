package com.newcoder.community.entity;

import com.sun.org.apache.regexp.internal.RE;

import java.util.HashMap;
import java.util.Map;

public class Event {
    //主题，事件类型
    private String topic;
    //事件的触发的人
    private int userId;
    //事件发生的实体
    private int entityType;
    private int entityId;
    //实体作者
    private int entityUserId;
    //额外数据存到map里，使得程序具有扩展性
    private Map<String,Object> data=new HashMap<>();

    public String getTopic() {
        return topic;
    }
    //修改set方法，可以多次set
    public Event setTopic(String topic) {
        this.topic = topic;
        return  this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object value) {
        this.data .put(key,value);
        return this;
    }
}

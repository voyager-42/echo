package com.voyager.community.entity;

/**
 * @author Voyager1
 * @create 2022-03-18 20:56
 */

import java.util.HashMap;
import java.util.Map;

/**
 * 封装事件（用于系统通知）
 */
public class Event {
    private String topic; // 事件类型
    private int userId; // 事件由谁触发
    private int entityType; // 实体类型
    private int entityId; // 实体 id
    private int entityUserId; // 实体的作者(该通知发送给他）
    private Map<String, Object> data = new HashMap<>(); // 存储未来可能需要用到的数据

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}

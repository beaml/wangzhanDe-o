package com.newcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.entity.Event;
import com.newcoder.community.entity.Message;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.util.CommuityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommuityConstant {
    //记录处理消息时的日志，声明记录日志的组件
    private static final Logger logger=LoggerFactory.getLogger(EventConsumer.class);
    //处理事件是往message表里插入数据
    @Autowired
    private MessageService messageService;
    //消费事件的方法,一个方法处理三个主题，因为三个主题的处理结果是类似的
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOE})
    public void handleCommentMessage(ConsumerRecord record){
        if(record==null||record.value()==null){
            logger.error("消息的内容为空");
            return;
        }
        //消息的内容是json字符串，需要恢复成对象
        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误");
            return;
        }
        //发送站内通知，构造message数据插到message表里
        Message message=new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        //将event剩下的字段放到map里，之后转成json字符串存到message表的content下
        Map<String,Object> content=new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()){
            //遍历map，每次遍历得到一个entry(k,v)
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}

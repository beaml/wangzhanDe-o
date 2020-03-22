package com.newcoder.community.service;

import com.newcoder.community.dao.MessageMapper;
import com.newcoder.community.entity.Message;
import com.newcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversation(userId,offset,limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId,int odffset,int limit){
        return messageMapper.selectLetters(conversationId,odffset,limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }
    //添加一条消息
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    //将消息变成已读，支持一次读多条
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }
    //查询某主题下最新的通知
    public Message findLatestNotice(int userId,String topic){
        return messageMapper.selectLatestNotice(userId,topic);
    }
   //查询某个主题下通知的数量（包括已读和未读）
    public int findNoticeCount(int userId,String topic){
        return messageMapper.selectNoticeCount(userId,topic);
    }
    //找到某主题下未读消息数量，或者（topic参数为空时）查询所有主题未读消息的和
    public int findNoticeUnreadCount(int userId,String topic){
        return messageMapper.selectLetterUnreadCount(userId,topic);
    }
}


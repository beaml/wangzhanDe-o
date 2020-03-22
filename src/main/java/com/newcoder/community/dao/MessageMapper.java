package com.newcoder.community.dao;

import com.newcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
@Mapper
@Component
public interface MessageMapper {
    //查询当前用户的会话列表，每个会话下面，只返回一条最新的私信
    List<Message> selectConversation(int userId, int offset, int limit);
    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读的私信的数量
    int selectLetterUnreadCount(int userId,String conversationId);
    //增加私信
    int insertMessage(Message message);
    //修改消息阅读状态
    int updateStatus(List<Integer> ids,int status);
    //某一个主题下面的最新一条的通知
    Message selectLatestNotice(int userId,String topic);
    //查询某个主题所包含的通知的数量
    int selectNoticeCount(int userId,String topic);
    //查询未读的通知的数量
    int selectNoticeUnread(int userId,String topic);

}

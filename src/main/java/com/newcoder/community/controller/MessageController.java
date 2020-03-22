package com.newcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.entity.Message;
import com.newcoder.community.entity.Page;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommuityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommuityConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    //私信列表
    @RequestMapping(path="/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        //Integer.valueOf("abc");
        User user=hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList=messageService.findConversations(
                user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> convasations=new ArrayList<>();
        if(conversationList!=null){
            for(Message message:conversationList){
                Map<String,Object> map=new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                int targetId=user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));
                convasations.add(map);
            }
        }
        model.addAttribute("conversations",convasations);
        //查询未读消息(私信)数量
        int letterUnreadCount =messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        //未读通知
        int noticeUnreadCount=messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";
    }
    @RequestMapping(path="/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //私信列表
        List<Message> letterList=messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters=new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                Map<String,Object> map=new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //私信目标
        model.addAttribute("target",getLetterTarget(conversationId));
        //将未读的消息设置为已读
        List<Integer> ids=getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }
    //传入私信列表
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids=new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                if(hostHolder.getUser().getId()==message.getToId()&&message.getStatus()==0) {
                    //接收者身份，且消息未读
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    private User getLetterTarget(String conversationId){
        String[] ids=conversationId.split("_");
        int d0=Integer.parseInt(ids[0]);
        int d1=Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==d0){
            return userService.findUserById(d1);
        }else{
            return userService.findUserById(d0);
        }
    }
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        //Integer.valueOf("acc");
        User targetUser=userService.findUserByName(toName);
        if(targetUser==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }
        Message message=new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(targetUser.getId());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
    //显示通知列表
    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user=hostHolder.getUser();
        //查询评论类的通知
        Message message=messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        Map<String,Object> messageVO=new HashMap<>();
        if(message!=null){
            messageVO.put("message",message);
            //从数据库取出来的字符串需要转义
            String content= HtmlUtils.htmlUnescape(message.getContent());
            //json字符串转为对象
            Map<String,Object> data=JSONObject.parseObject(content,HashMap.class);
            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entotyType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count=messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVO.put("count",count);

            int unread=messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVO.put("unread",unread);

        }
        model.addAttribute("commentNotice",messageVO);
        //查询点赞类的通知
         message=messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        messageVO=new HashMap<>();
        if(message!=null){
            messageVO.put("message",message);
            //从数据库取出来的字符串需要转义
            String content= HtmlUtils.htmlUnescape(message.getContent());
            //json字符串转为对象
            Map<String,Object> data=JSONObject.parseObject(content,HashMap.class);
            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entotyType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count=messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            messageVO.put("count",count);

            int unread=messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVO.put("unread",unread);

        }
        model.addAttribute("likeNotice",messageVO);
        //查询关注类的通知
        message=messageService.findLatestNotice(user.getId(),TOPIC_FOLLOE);
        messageVO=new HashMap<>();
        if(message!=null){
            messageVO.put("message",message);
            //从数据库取出来的字符串需要转义
            String content= HtmlUtils.htmlUnescape(message.getContent());
            //json字符串转为对象
            Map<String,Object> data=JSONObject.parseObject(content,HashMap.class);
            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entotyType"));
            messageVO.put("entityId",data.get("entityId"));

            int count=messageService.findNoticeCount(user.getId(),TOPIC_FOLLOE);
            messageVO.put("count",count);

            int unread=messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOE);
            messageVO.put("unread",unread);

        }
        model.addAttribute("followNotice",messageVO);

        //查询未读消息的数量
        int letterUnreadCount=messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        int noticeUnreadCount=messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/notice";
    }

}


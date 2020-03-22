package com.newcoder.community.controller;

import com.newcoder.community.entity.Event;
import com.newcoder.community.entity.User;
import com.newcoder.community.event.EventProducer;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.util.CommuityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommuityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path="/like",method = RequestMethod.POST)
    //异步请求
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user=hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //点赞数量
        long likeCount=likeService.findEntityLikeCout(entityType,entityId);
        //点赞状态
        int likeStatus=likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        //将点赞数量和状态传给页面
        Map<String,Object> mapLike=new HashMap<>();
        mapLike.put("likeCount",likeCount);
        mapLike.put("likeStatus",likeStatus);

        //触发点赞事件,点赞的时候提醒，取消赞的时候就不提醒了
        if(likeStatus==1){
            Event event=new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);

        }
        return CommunityUtil.getJSONString(0,null,mapLike);
    }

}

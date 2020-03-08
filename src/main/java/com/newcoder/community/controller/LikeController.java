package com.newcoder.community.controller;

import com.newcoder.community.entity.User;
import com.newcoder.community.service.LikeService;
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
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path="/like",method = RequestMethod.POST)
    //异步请求
    @ResponseBody
    public String like(int entityType,int entityId){
        User user=hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId);
        //点赞数量
        long likeCount=likeService.findEntityLikeCout(entityType,entityId);
        //点赞状态
        int likeStatus=likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        //将点赞数量和状态传给页面
        Map<String,Object> mapLike=new HashMap<>();
        mapLike.put("likeCount",likeCount);
        mapLike.put("likeStatus",likeStatus);
        return CommunityUtil.getJSONString(0,null,mapLike);
    }

}

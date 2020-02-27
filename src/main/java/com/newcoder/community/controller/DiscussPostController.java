package com.newcoder.community.controller;

import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @RequestMapping(path="/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        //发帖的前提是登录
        User user=hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403,"你还没用登录");
        }
        DiscussPost discussPost=new DiscussPost();
        discussPost.setUserid(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        //报错的情况，将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }
    //查询帖子详情
    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        //帖子
        DiscussPost discussPost=discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);
        //作者
        User user=userService.findUserById(discussPost.getUserid());
        model.addAttribute("user",user);
        return "/site/discuss-detail";
    }
}


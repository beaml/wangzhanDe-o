package com.newcoder.community.controller;

import com.newcoder.community.entity.Comment;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.Event;
import com.newcoder.community.event.EventProducer;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.util.CommuityConstant;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommuityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //通知被评论人，他被评论了，触发评论事件
        Event event=new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        //评论的目标有可能是帖子有可能是评论
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            //评论的是帖子，entityId是帖子的id，需要到帖子的表里去查相关信息
            DiscussPost target=discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            //评论的是评论，需要到评论表里查
            Comment target=commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        //将评论事件放到eventProducer中，不用等待，可以直接return，kafka会去自动处理。
        //在处理后面业务的同时，消息也被处理了，可以异步执行
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/"+discussPostId;
    }
}

package com.newcoder.community.service;

import com.newcoder.community.dao.CommentMapper;
import com.newcoder.community.entity.Comment;
import com.newcoder.community.util.CommuityConstant;
import com.newcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommuityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentByEntity(entityType,entityId,offset,limit);
    }
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }
    //添加评论(进行事务管理，希望添加评论和更改评论总数这两个操作，要么全成功，要么全失败)
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        //使用敏感词过滤
        if(comment==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows=commentMapper.insertComment(comment);
        //更新帖子的评论总数.评论的回复不算
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            int count=commentMapper.selectCountByEntity(ENTITY_TYPE_POST,comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }
        return rows;
    }
    //根据评论id查找评论
    public Comment  findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }
}

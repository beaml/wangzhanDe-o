package com.newcoder.community.dao;

import com.newcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CommentMapper {
    //根据entityType判断查询帖子的评论、课程的评论还是评论的评论
    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);
    //查询评论的条目数
    int selectCountByEntity(int entityType,int entityId);

    //增加评论
    int insertComment(Comment comment);


}

package com.newcoder.community.dao;

import com.newcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface DiscussPostMapper {
    //当userId为0时，不查询指定id，显示所有的，当userId非0，即进行赋值时，查找对应用户发的帖子。
    //offset指当前页的起始行的行号，limit指该页能够显示的条数
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);
    //帖子总行数，可以求得总页数.
    // 需要动态的传递一个参数条件，并且这个方法有且只有一个参数，且在<if>中使用。此时应当使用Param注解给userId参数取一个别名
    int selectDiscussPostRows(@Param("userId") int userId);
    //增加帖子
    int insertDiscussPost(DiscussPost discussPost);
    //查询帖子详情
    DiscussPost selectDiscussPostById(int id);


}

package com.newcoder.community.dao;

import com.newcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface LoginTicketMapper {
    //插入方法
    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    //设置自动生成主键
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    //查询方法，根据ticket进行查询
    @Select({
            "select id,user_id,ticket,status,expired from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改凭证的状态，退出时失效
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateLoginStatus(String ticket,int status);

}

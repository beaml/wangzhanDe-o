//package com.newcoder.community.dao;
//
//import com.newcoder.community.entity.User;
//import org.apache.ibatis.annotations.Mapper;
//import org.springframework.stereotype.Component;
//@Deprecated
//@Component
////加注解让spring容器装配这个bean
//@Mapper
////接口不需要实现,需要声明增删改查的方法
//public interface UserMapper {
//
//    //根据id查询用户
//    User selectById(int id);
//    //根据用户名查询用户
//    User selectByName(String name);
//    //根据邮箱查询用户
//    User selectByEmail(String email);
//
//    //增加一个用户，返回插入成功的行数
//    int insertUser(User user);
//
//    //根据id对用户状态进行修改，返回修改的条数
//    int updateState(int id,int status);
//    //根据id更新用户头像路径
//    int updateHeader(int id,String headerUrl);
//    //更新密码
//    int updatePassword(int id,String password);
//}

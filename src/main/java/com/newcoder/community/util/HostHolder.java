package com.newcoder.community.util;

import com.newcoder.community.entity.User;
import org.springframework.stereotype.Component;
/*
  持有用户信息，用来代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users=new ThreadLocal<>();
    //线程安全的存取操作
    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    //请求结束之后，清理用户信息
    public void clear(){
        users.remove();
    }

}

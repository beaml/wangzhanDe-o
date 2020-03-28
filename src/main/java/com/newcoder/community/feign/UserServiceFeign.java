package com.newcoder.community.feign;

import com.newcoder.community.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="ZUUL-GATEWQY")
public interface UserServiceFeign {
    @GetMapping("/spring-user-my/user/getUserById")
    User getUserById(@RequestParam("id") int id);
    @PostMapping("/spring-user-my/user/updateHeaderByuserId")
    int updateHeaderByuserId(@RequestParam("userId") int userId,@RequestParam("headUrl")String headUrl);
    @PostMapping("/spring-user-my/user/updatePwdById")
    int updatePwdById(@RequestParam("id") int id,@RequestParam("newPwd") String newPwd);
    @GetMapping("/spring-user-my/user/findUserByName")
    User findUserByName(@RequestParam("name") String name);
    @PostMapping("/spring-user-my/user/registerByUser")
    Map<String,Object> registerByUser(User user);
    @PostMapping("/spring-user-my/user/updateStateById")
    void updateStateById(@RequestParam("userId")int userId,@RequestParam("state")int state);
    @PostMapping("/spring-user-my/user/login")
    Map<String,Object> login(@RequestParam("username")String username,@RequestParam("password")String password);
}

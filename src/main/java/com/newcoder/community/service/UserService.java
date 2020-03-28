package com.newcoder.community.service;

import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.feign.UserServiceFeign;
import com.newcoder.community.util.CommuityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.MailClient;
import com.newcoder.community.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommuityConstant {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserServiceFeign userServiceFeign;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    private static final Logger logger= LoggerFactory.getLogger(UserService.class);
    public User findUserById(int id){
        //return  userMapper.selectById(id);
        //优化：redis缓存中获取数据
        User user=getCache(id);
        if(user==null){
           user= initCache(id);
        }
        return user;
    }
    //注册
    public Map<String,Object> register(User user){
        Map<String,Object> map=userServiceFeign.registerByUser(user);
        if(!map.containsKey("userMsg")){
            return map;
        }
        //给用户发送激活邮件
        //把要传给模板引擎的变量存到context里
        Context context=new Context();
        Map<String,Object> userMap= (Map<String, Object>) map.get("userMsg");
        context.setVariable("email",userMap.get("email"));
        //http://localhost:8080/community/activation/101/code
        //101是用户id，code是激活码.调用user.insert之后就有id了，一开始是没有的，有了之后会对user进行回填（配置文件进行了配置）
        String url=domain+contextPath+"/activation/"+userMap.get("id")+"/"+userMap.get("activationCode");
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(userMap.get("email").toString(),"项目激活邮件",content);
        return null;
    }
    //激活，返回激活状态
    public int activation(int userId,String code){
        //微服务优化User user=userMapper.selectById(userId);
        User user=userServiceFeign.getUserById(userId);
        if(user.getStatus()==1){
            //已经被激活
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            //激活成功
            //微服务优化userMapper.updateState(userId,1);
            userServiceFeign.updateStateById(userId,1);
            clearCache(userId);
            return  ACTIVATION_SUCCESS;
        }else{
            return  ACTIVATION_FAILURE;
        }
    }
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map=userServiceFeign.login(username,password);
        if(!map.containsKey("null")){
            return map;
        }
        //以上都通过之后，发放登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(userServiceFeign.findUserByName(username).getId());

        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));

       // loginTicketMapper.insertLoginTicket(loginTicket);

        //优化，将loginTicket存到redis中
        String redisKey= RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //redis会将loginTicket对象序列化为json格式的字符串存到redis内
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    //退出登录
    public void loginOut(String ticket){
        //loginTicketMapper.updateLoginStatus(ticket,1);

        //优化，退出时更改redis里的值
        String redisKey=RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket=(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }
    //查询凭证
    public LoginTicket findLoginTicket(String ticket){
        //return loginTicketMapper.selectByTicket(ticket);

        //优化。在redis中查询
        String redisKey=RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }
    //修改头像路径
    public int updateHeader(int userId,String headUrl){
        //访问redis和mysql没有办法放到一个事务之内，他们之间的事务是分开的
        //先更新mysql再删除redis缓存

        //return userMapper.updateHeader(userId,headUrl);
        //微服务优化int rows=userMapper.updateHeader(userId,headUrl);
        int rows=userServiceFeign.updateHeaderByuserId(userId,headUrl);
        clearCache(userId);
        return rows;
    }
//修改密码
    public int updatePwd(int id,String newPwd){
        return userServiceFeign.updatePwdById(id,newPwd);
    }
    //根据用户名查用户id
    public User findUserByName(String name){
        return userServiceFeign.findUserByName(name);
    }
    //1、优先从缓存中取值
    private User getCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2、取不到时，初始化缓存数据
    private User initCache(int userId){

        //微服务优化User user=userMapper.selectById(userId);

        User user=userServiceFeign.getUserById(userId);
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3、当数据变更时，清楚缓存数据
    private void clearCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}

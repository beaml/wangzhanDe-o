package com.newcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommuityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class LoginController implements CommuityConstant {
    private static final Logger logger=LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    //获取注册页面。这个方法的目的是给浏览器返回html
    public String getRegisterPage() {
        return "/site/register";
    }
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    //获取登录页面
    public String getLoginPage() {
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            //注册成功,提示注册成功，跳转到首页，激活之后跳转到登录页面
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            //注册失败，向注册页面返回信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }
    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    //PathVariable注解的作用的从路径中取值
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code")String code){
        int result=userService.activation(userId,code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作！该账号已经激活过，无需重复激活");
            model.addAttribute("target", "/index");
        }else{
            model.addAttribute("msg", "激活失败，您提供的激活码不正确");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }
    //验证码功能
    @RequestMapping(path="/kaptcha",method=RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        //生成验证码
        String text=kaptchaProducer.createText();
        BufferedImage image=kaptchaProducer.createImage(text);

        //将验证码存入session
       // session.setAttribute("kaptcha",text);

        //优化：将验证码存入redis中
        //验证码的归属者，一个随机生成的字符串
        String kaptchaOwner= CommunityUtil.generateUUID();
        //将凭证发给客户端，客户端用cookie保存
        Cookie cookie=new Cookie("kaptchaOwner",kaptchaOwner);
        //设置cookie的生存时间，很短就可以
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        //发送给客户端
        response.addCookie(cookie);
        //将验证码存入redis
        String redisKey= RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);


        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os=response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败："+e.getMessage());
        }
    }
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberme,
                        Model model,/*HttpSession httpSession,*/HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        //首先判断验证码是否正确
       // String kaptcha= (String) httpSession.getAttribute("kaptcha");
        //优化：从redis中取kaptcha
        String kaptcha=null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey=RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha=(String)redisTemplate.opsForValue().get(redisKey);
        }

        
        if(StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        //检查账号密码
        int exporedSends=rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map=userService.login(username,password,exporedSends);
        if(map.containsKey("ticket")){
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(exporedSends);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }
    @RequestMapping(path = "logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.loginOut(ticket);
        return "redirect:/login";//默认重定向get请求的login页面
    }

}

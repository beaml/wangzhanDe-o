package com.newcoder.community.controller;

import com.newcoder.community.annotation.LoginRequired;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger looger= LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private  String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    @LoginRequired

    @RequestMapping(path="/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path="upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage==null) {
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        //为避免图片文件名重复，存储时给文件起一个随机的名字
        //先从文件名中读取文件后缀名
        String fileName=headerImage.getOriginalFilename();
        String suffix=fileName.substring(fileName.lastIndexOf('.'));
        if(!StringUtils.equals(suffix,".png")){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //生成随机文件名
        fileName=CommunityUtil.generateUUID()+suffix;
        //确定文件存放的路径
        File dest=new File(uploadPath+"/"+fileName);
        //当前文件写到目标路径去
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            looger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常!",e);
        }
        //存成功的话，更新当前用户的头像的路径（web访问路径：http://localhost:8080/community/...）
        //获取当前用户
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //获取头像
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器上存放的路径
        fileName=uploadPath+"/"+fileName;
        //向浏览器输出图片格式的文件
        String suffix=fileName.substring(fileName.lastIndexOf(".")+1);
        //响应图片
        response.setContentType("image/"+suffix);
        //图片是二进制数据
        try (
            //mvc不会管理这个输入流，是我们自己创建的。需要手动关闭
            FileInputStream fis = new FileInputStream(fileName);
            //输出流是MVC帮忙管理的，会自动关闭
            OutputStream os=response.getOutputStream();
            ){
            byte[] buffer=new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            looger.error("读取头像失败："+e.getMessage());
        }
    }

    @LoginRequired

    @RequestMapping(path="/changepwd",method = RequestMethod.POST)
    public String changepwd(String oldPwd,String newPwd,Model model){
        looger.info("密码信息{}",oldPwd);
        if (StringUtils.isBlank(oldPwd)||StringUtils.isBlank(newPwd)) {
            model.addAttribute("error1","密码不能为空！");
            return "/site/setting";
        }
        User user=hostHolder.getUser();

        if(!user.getPassword().equals(CommunityUtil.md5(oldPwd+user.getSalt()))){
            model.addAttribute("error1","您输入的原始密码不正确！");
            return "/site/setting";
        }
        String pwd=CommunityUtil.md5(newPwd+user.getSalt());
        user.setPassword(pwd);
        model.addAttribute("result","保存成功！");
        userService.updatePwd(user.getId(),pwd);
        return "/site/setting";
    }
    //个人主页(当前用户的主页或者任意用户的主页)
    @RequestMapping(path="/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user=userService.findUserById(userId);
        if(user==null){
            throw  new RuntimeException("该用户不存在");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount=likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        return "/site/profile";
    }
}

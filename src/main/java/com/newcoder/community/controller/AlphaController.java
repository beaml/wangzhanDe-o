package com.newcoder.community.controller;

import com.newcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//写注解
@Controller
@RequestMapping("/alpha")//浏览器通过这个名字访问这个类
public class AlphaController {
    @Autowired
    private AlphaService alphaService;
    //处理浏览器请求的方法
    //能够被访问到的前提是也有注解
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring!!!";
    }
    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    //怎么获得请求数据和响应数据.封装之前的写法
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumaration=request.getHeaderNames();
        while(enumaration.hasMoreElements()){
            String name=enumaration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+" : "+value);
        }
        System.out.println(request.getParameter("code"));
        //response向浏览器返回响应数据
        response.setContentType("text/html;charset=utf-8");//响应的类型
        PrintWriter writer=response.getWriter();//获取输出流
        //通过writer向浏览器打印一个网页
        writer.write("<h1>牛客网 </h1>");
        writer.close();
    }
    //get请求，默认是get. /students?current&limit=20 传参
    //有时参数是没有的，这时可以用一个注解来处理
    @RequestMapping(path="/students",method= RequestMethod.GET)
    @ResponseBody
    //通过注解对参数的注入做更详细的规范，是否必须有定义为false，若没有传值时的默认值为1
    public String getStudents(
            @RequestParam(name="current",required =false,defaultValue = "1") int current,
            @RequestParam(name="limit",required =false,defaultValue = "10")int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }
    //根据学生的编号查询一个学生  /student/123
    @RequestMapping(path="/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    //路径变量注解
    public String getSudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }
    //post请求。浏览器向服务器提交数据
    @RequestMapping(path="/student",method = RequestMethod.POST)
    @ResponseBody
    //定义的变量名称与表单中的名称相同
    public String saveStu(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }
    //如何向浏览器返回响应数据(一个动态的html数据)
    @RequestMapping(path="/teacher",method = RequestMethod.GET)
    //不加ResponseBody注解，默认返回的就是html，加了返回的是String
    public ModelAndView getTeacher(){
        ModelAndView mav=new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age","30");
        //设置模板的路径和名字，放在templates路径下。templates不用写
        mav.setViewName("/demo/viewself");
        return  mav;
    }
    @RequestMapping(path="/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/demo/viewself";
    }

    //响应json数据（通常是在异步请求中），异步请求：当前网页不刷新，部分组件访问服务器进行部分内容刷新，如用来局部验证
    //将java对象返回给浏览器，浏览器用js解析对象，js也是面向对象的语言，希望能得到一个js对象。
    // java对象不好直接转换为js对象，故使用json（具有固定格式的字符串）实现。
    // 将java对象转换成json格式的字符串传给浏览器，浏览器再把json字符串转换为js对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","刘五");
        emp.put("age",35);
        emp.put("salary",1000);
        return  emp;
    }
    //查询所有的员工
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String,Object>> list=new ArrayList();
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","刘五");
        emp.put("age",35);
        emp.put("salary",1000);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","朱七");
        emp.put("age",49);
        emp.put("salary",9000);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","吴三");
        emp.put("age",32);
        emp.put("salary",5600);
        list.add(emp);

        return  list;
    }

}

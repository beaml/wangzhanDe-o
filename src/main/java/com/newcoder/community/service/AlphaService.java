package com.newcoder.community.service;

import com.newcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//默认是单例模式。通常使用单例模式，也可以设定成多例，每次bean调用时实例化一个新的。
//@Scope("pretotype")
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    //这个是构造器
    public AlphaService(){
        System.out.println("实例化（构造方法）alphaService");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化 alphaService");
    }


    @PreDestroy
    public void destroy(){
        System.out.println("销毁  AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}

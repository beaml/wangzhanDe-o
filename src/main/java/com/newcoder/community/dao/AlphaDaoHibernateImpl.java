package com.newcoder.community.dao;


import org.springframework.stereotype.Repository;

//加了这个注解之后，spring会自动扫描这个类并把他放到容器里
@Repository("alphaHibernate")
//每一个bean都是有名字的,默认是类名的首字母小写，也可以自定义
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "hibernate";
    }
}

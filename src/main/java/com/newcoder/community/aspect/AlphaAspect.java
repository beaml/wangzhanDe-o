package com.newcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    //切点pointCut
    @Pointcut("execution(* com.newcoder.community.service.*.*(..))")
    public void pointCut(){

    }
    //通知advice
    //在连接点之前
    @Before("pointCut()")
    public void before(){
        System.out.println("before pointcut");
    }

    @After("pointCut()")
    public void after(){
        System.out.println("after pointcut");
    }
    //有了返回值之后织入代码
    @AfterReturning("pointCut()")
    public void afterRetutn(){
        System.out.println("afterReturn");
    }
    //抛异常时织入
    @AfterThrowing("pointCut()")
    public void afterThrow(){
        System.out.println("afterThrow");
    }
    //ProceedingJoinPoint连接点
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        //既在前面又在后面织入
        //在之前
        System.out.println("around before");
        Object ob=joinPoint.proceed();//调用要处理的目标业务组件的方法
        //在之后
        System.out.println("around after");
        return ob;
    }
}

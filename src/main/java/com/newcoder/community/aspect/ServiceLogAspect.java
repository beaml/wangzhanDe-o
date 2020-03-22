package com.newcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger= LoggerFactory.getLogger(ServiceLogAspect.class);
    //切点pointCut
    @Pointcut("execution(* com.newcoder.community.service.*.*(..))")
    public void pointCut(){

    }
    //在连接点之前
    @Before("pointCut()")
    public void before(JoinPoint joinPoint){
        //日志格式：用户IP[1.2.3.4]在[xxx]时间，访问了[com.newcoder.community.xx]方法
        ServletRequestAttributes attributes= (ServletRequestAttributes) RequestContextHolder .getRequestAttributes();
        //是一个特殊的调用，比如kafka的consumer，不是常规的页面的调用
        if(attributes==null){
           return;
        }
        HttpServletRequest request=attributes.getRequest();
        String ip=request.getRemoteHost();
        String now=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //得到类名+方法名
        String target=joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }


}

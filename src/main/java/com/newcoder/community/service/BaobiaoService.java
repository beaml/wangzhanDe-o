package com.newcoder.community.service;

import com.newcoder.community.dao.BaobiaoMapper;
import com.newcoder.community.util.MailClient;
import com.newcoder.community.util.ZookeeperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service

public class BaobiaoService {
    @Autowired
    private BaobiaoMapper baobiaoDao;
    @Autowired
    private MailClient mailClient;
    @Autowired  ZookeeperUtil zookeeperUtil;
    @Scheduled(cron="0 15 16 ? * *")
    public void fixTimeExecution(){
        if(!zookeeperUtil.acquireDistributedLock()){
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //当前时间
        Date current=new Date();
        Calendar cal=Calendar.getInstance();
        cal.setTime(current);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        //获取当天的零点
        Date end=cal.getTime();
        //获取前一天的零点
        cal.add(Calendar.DATE,-1);
        Date start=cal.getTime();
        int PostCount=baobiaoDao.getDayDisscussPostCount(start,end);
        //将返回结果以邮件的形式发到管理员邮箱
        String content="发帖数量情况：自北京时间"+start+"到"+end+"时间内，用户发帖总数量为："+PostCount+"条。";
        mailClient.sendMail("beamjl@163.com","发帖情况统计",content);
        zookeeperUtil.releaseDistributedLock();
       // System.out.println(start+"到"+end);
    }
}

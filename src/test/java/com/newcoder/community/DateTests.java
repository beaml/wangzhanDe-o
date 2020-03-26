package com.newcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest

public class DateTests {
    @Test
    public void timeTest(){
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
        System.out.println(start+"到"+end);

//        System.out.println("在指定时间"+dateFormat.format(new Date())+"执行");
    }
}

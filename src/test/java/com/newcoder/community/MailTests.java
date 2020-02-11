package com.newcoder.community;

import com.newcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=Application.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testTextMail(){
        mailClient.sendMail("1033025319@qq.com","TEST","ni shi zhu");
    }
    @Test
    public void testHtmlMail(){
        //把要传给模板引擎的变量存到context里
        Context context=new Context();
        context.setVariable("username","小李子");

        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("1033025319@qq.com","TestHtml",content);
    }
}

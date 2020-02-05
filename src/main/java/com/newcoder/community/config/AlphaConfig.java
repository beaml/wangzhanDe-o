package com.newcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

//普通配置类
@Configuration
public class AlphaConfig {
    //使用配置类装载第三方库，这个方法返回的对象将被装到容器里
    //方法名就是bean的名字
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
